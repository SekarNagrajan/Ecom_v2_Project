package com.solverminds.sibl.service;

import com.solverminds.sibl.dto.SIBLRequestDTO;
import com.solverminds.sibl.dto.SICreateResponse;
import com.solverminds.sibl.entity.EcomSi;
import com.solverminds.sibl.repo.*;
import com.solverminds.sibl.service.SIValidationService.ValidationResult;
import com.svm.ecom.common.enums.SiStatus;
import com.solverminds.sibl.exception.SIValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;

/**
 * SI Update Service – Handles SI update with same save-type validation and ecomSI/editempbooking
 * behaviour as SI create.
 * <ul>
 *   <li>Valid SI number: must exist in ecomSI table.</li>
 *   <li>Save type "D" (Draft): DraftUpdate – update ecomSI main row and re-insert children.</li>
 *   <li>Save type "S" (Submit): delete all ecomSI data for this SI and insert newly (same as create);
 *       caller then updates edidocstatus and runs EDI export.</li>
 * </ul>
 * Validations and ecomSI/editempbooking insert values are the same as SI create (delegated to SiCreateService).
 */
@Service
@Transactional(transactionManager = "ecomTransactionManager")
public class SiUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(SiUpdateService.class);

    @Autowired
    private EcomSiRepository ecomSiRepository;

    @Autowired
    private SIValidationService validationService;

    @Autowired
    private SiCreateService siCreateService;

    @Autowired
    private EcomSiCustomerRepository ecomSiCustomerRepository;

    @Autowired
    private EcomSiContainerRepository ecomSiContainerRepository;

    @Autowired
    private EcomSiCommodityRepository ecomSiCommodityRepository;

    @Autowired(required = false)
    private EcomSiReeferRepository ecomSiReeferRepository;

    @Autowired(required = false)
    private EcomSiDgRepository ecomSiDgRepository;

    @Autowired(required = false)
    private EcomSiOogRepository ecomSiOogRepository;

    @Autowired(required = false)
    private EcomSiChargesRepository ecomSiChargesRepository;

    @Autowired(required = false)
    private EcomSiIcsNewRepository ecomSiIcsNewRepository;

    @Autowired(required = false)
    @Qualifier("siblEcomSiRequestMasterRepository")
    private EcomSiRequestMasterRepository ecomSiRequestMasterRepository;

    @PersistenceContext(unitName = "ecomPersistenceUnit")
    private EntityManager entityManager;

    /**
     * Update an existing SI. Save type determines behaviour:
     * <ul>
     *   <li>status "D" (Draft): DraftUpdate – update ecomSI main row and re-insert children from DTO.</li>
     *   <li>status "S" (Submit): delete all ecomSI data for this SI and insert newly (same as SI create);
     *       caller must then update edidocstatus and run EDI export.</li>
     * </ul>
     * SI number (onlineSINo) must exist in ecomSI. Validations and ecomSI/editempbooking insert
     * values are the same as SI create.
     *
     * @param customerCode customer code from JWT
     * @param loginName    login username from JWT (for ChgUser)
     * @param onlineSINo   SI number (path) – must exist in ecomSI
     * @param request      request DTO (same shape as SI create)
     * @return SICreateResponse with onlineSINo and message
     */
    public SICreateResponse updateSi(String customerCode, String loginName, String onlineSINo, SIBLRequestDTO request) {
        try {
            logger.info("[ECOM] SiUpdateService - Starting SI update for eSINo: {}, status: {}", onlineSINo, request.getStatus());

            request.setCustomerCodeFromJWT(customerCode);
            request.setOnlineSINo(onlineSINo.trim());

            // Same validations as SI create
            ValidationResult validationResult = validationService.validateSIRequest(request, customerCode, false);
            if (!validationResult.isValid()) {
                throw new SIValidationException(validationResult.getErrors());
            }

            // Valid SI number: must exist in ecomSI table
            EcomSi existingSi = ecomSiRepository.findByeSINo(onlineSINo.trim())
                    .orElseThrow(() -> new IllegalArgumentException("SI_NOT_FOUND:" + onlineSINo));
            if (customerCode != null && !customerCode.equalsIgnoreCase(existingSi.getCustcode())) {
                throw new IllegalArgumentException("SI does not belong to customer");
            }

            LocalDateTime now = LocalDateTime.now();
            SiStatus statusEnum = request.getStatus() != null ? request.getStatus() : SiStatus.D;
            request.setStatus(statusEnum);
            String status = statusEnum.getValue();
            request.setChangeUser(loginName != null ? loginName : customerCode);
            request.setChangeDate(now);
            request.setCreatedUser(existingSi.getCrUser());
            request.setCreatedDate(existingSi.getCrDate());

            if (request.getOriginPortId() == null || request.getOriginPortId().trim().isEmpty()) {
                request.setOriginPortId(request.getLoadPortId());
            }
            if (request.getFinalPortId() == null || request.getFinalPortId().trim().isEmpty()) {
                request.setFinalPortId(request.getDischargePortId());
            }

            String eSINo = existingSi.geteSINo();

            if (SiStatus.S == statusEnum) {
                // Submit: delete all ecomSI data via native SQL (same as booking update – delete then insert)
                deleteAllEcomSiDataByeSINo(eSINo);
                if (entityManager != null) {
                    entityManager.detach(existingSi);
                    entityManager.flush();
                }
                EcomSi si = siCreateService.createMainSiRecord(request);
                ecomSiRepository.save(si);
                logger.info("[ECOM] SiUpdateService - Submit: re-inserted main SI record: {}", onlineSINo);
                siCreateService.insertAllChildren(si, customerCode, loginName, request);
                logger.info("[ECOM] SiUpdateService - SI update (Submit) completed: {}", onlineSINo);
                return new SICreateResponse(onlineSINo, SiStatus.S, "SI submitted successfully");
            }

            // Draft: same pattern as booking update – delete child records via native SQL, flush, then update main + insert children
            deleteAllEcomSiChildDataByeSINo(eSINo);
            // CRITICAL: Flush deletes to database before inserting new records (prevents PK violations on ecomSI_oog etc.)
            if (entityManager != null) {
                entityManager.flush();
            }
            logger.debug("[ECOM] SiUpdateService - Flushed delete operations for eSINo: {}", eSINo);

            siCreateService.mapRequestToEcomSi(request, existingSi);
            ecomSiRepository.save(existingSi);
            logger.info("[ECOM] SiUpdateService - DraftUpdate: updated main SI record: {}", onlineSINo);

            siCreateService.insertAllChildren(existingSi, customerCode, loginName, request);

            logger.info("[ECOM] SiUpdateService - SI update (Draft) completed: {}", onlineSINo);
            return new SICreateResponse(onlineSINo, SiStatus.D, "SI updated successfully");

        } catch (SIValidationException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            logger.warn("[ECOM] SiUpdateService - Update validation/not-found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("[ECOM] SiUpdateService - Error updating SI: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update SI: " + e.getMessage(), e);
        }
    }

    /** All ecomSI-related tables to delete by eSINo (child tables first, then main ecomSI). */
    private static final String[] ECOM_SI_DELETE_SQL = {
        "DELETE FROM dbo.ecomSI_oog WHERE eSINo = ?1",
        "DELETE FROM dbo.ecomSI_dg WHERE eSINo = ?1",
        "DELETE FROM dbo.ecomSI_reefer WHERE eSINo = ?1",
        "DELETE FROM dbo.ecomSI_commodity WHERE eSINo = ?1",
        "DELETE FROM dbo.ecomSI_cargo WHERE eSINo = ?1",
        "DELETE FROM dbo.ecomSI_cust WHERE eSINo = ?1",
        "DELETE FROM dbo.ecomsi_charges WHERE esino = ?1",
        "DELETE FROM dbo.ecomsi_ics_new WHERE eSINo = ?1",
        "DELETE FROM dbo.ecomSI_files WHERE eSINo = ?1",
        "DELETE FROM dbo.ecomSIRequestMaster WHERE reqID = ?1",
        "DELETE FROM dbo.ecomSI WHERE eSINo = ?1"
    };

    /**
     * Delete child-table rows for the given eSINo using native SQL (Draft path).
     * Does NOT delete the main ecomSI row (same loop as full delete, excludes last).
     */
    private void deleteAllEcomSiChildDataByeSINo(String eSINo) {
        executeDeletesByeSINo(eSINo, ECOM_SI_DELETE_SQL.length - 1);
    }

    /**
     * Delete all ecomSI-related rows for the given eSINo using native SQL (Submit path).
     * Same loop as child delete, includes ecomSI.
     */
    private void deleteAllEcomSiDataByeSINo(String eSINo) {
        executeDeletesByeSINo(eSINo, ECOM_SI_DELETE_SQL.length);
        if (entityManager != null) {
            entityManager.flush();
        }
    }

    /** Run native DELETEs for eSINo: first {@code count} statements from {@link #ECOM_SI_DELETE_SQL}. */
    private void executeDeletesByeSINo(String eSINo, int count) {
        if (entityManager == null) {
            throw new IllegalStateException("EntityManager not available for SI delete");
        }
        logger.debug("[ECOM] SiUpdateService - Deleting records for eSINo: {} (native SQL, {} tables)", eSINo, count);
        for (int i = 0; i < count; i++) {
            String sql = ECOM_SI_DELETE_SQL[i];
            try {
                Query q = entityManager.createNativeQuery(sql).setParameter(1, eSINo);
                int n = q.executeUpdate();
                if (n > 0) {
                    logger.debug("[ECOM] SiUpdateService - Deleted {} row(s): {}", n, sql.substring(0, Math.min(55, sql.length())));
                }
            } catch (Exception e) {
                logger.warn("[ECOM] SiUpdateService - Delete failed for {} (table may not exist): {} - {}", eSINo, sql.substring(0, Math.min(50, sql.length())), e.getMessage());
            }
        }
    }
}
