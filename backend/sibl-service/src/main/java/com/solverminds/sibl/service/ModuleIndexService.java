package com.solverminds.sibl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Service for generating sequential IDs using the ecomMdlIndex table.
 * Same pattern as booking: IDs are prefix + index (size-safe for edidocstatus.edireaderid length 15).
 * Uses native SQL to avoid entity casting when booking and SIBL are on the same classpath.
 * Uses ECOM transaction manager for ecomMdlIndex access (same as SiCreateService.generateSINumber()).
 */
@Service("siblModuleIndexService")
@Transactional(transactionManager = "ecomTransactionManager")
public class ModuleIndexService {

    private static final Logger logger = LoggerFactory.getLogger(ModuleIndexService.class);

    /** Module id in ecomMdlIndex for EDI reader ID (same row as booking: mdlId = 'EDI'). */
    private static final String EDI_MODULE_ID = "EDI";

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Generate EDI reader ID (size-safe for edidocstatus.edireaderid length 15).
     * Uses mdlId = "EDI" from ecomMdlIndex, same as booking-service.
     * Uses native SQL like SiCreateService.generateSINumber() to avoid ClassCastException.
     *
     * @return Generated EDI reader ID (e.g. "EBKON1234")
     */
    public String generateEdiReaderId() {
        return generateNextId(EDI_MODULE_ID);
    }

    /**
     * Generate next sequential ID for a module using native SQL (no JPA entity).
     * Same pattern as SiCreateService.generateSINumber().
     */
    private String generateNextId(String moduleId) {
        if (entityManager == null) {
            throw new IllegalStateException("EntityManager not available for module index");
        }

        Query selectQuery = entityManager.createNativeQuery(
                "SELECT mdlPrefix, mdlIndex FROM ecomMdlIndex WHERE mdlId = ?");
        selectQuery.setParameter(1, moduleId);

        Object[] result;
        try {
            result = (Object[]) selectQuery.getSingleResult();
        } catch (Exception e) {
            logger.error("Module index not found for module: {}", moduleId);
            throw new IllegalStateException(
                    "Module index not found for module: " + moduleId
                            + ". Please ensure ecomMdlIndex table has entry for this module.",
                    e);
        }

        if (result == null || result.length < 2) {
            throw new IllegalStateException(
                    "Module index not found for module: " + moduleId
                            + ". Please ensure ecomMdlIndex table has entry for this module.");
        }

        String prefix = result[0] != null ? result[0].toString() : moduleId;
        Integer currentIndex = result[1] != null ? Integer.parseInt(result[1].toString()) : 0;
        int newIndex = currentIndex + 1;

        Query updateQuery = entityManager.createNativeQuery(
                "UPDATE ecomMdlIndex SET mdlIndex = ? WHERE mdlId = ?");
        updateQuery.setParameter(1, newIndex);
        updateQuery.setParameter(2, moduleId);
        int updateCount = updateQuery.executeUpdate();

        if (updateCount == 0) {
            throw new IllegalStateException("Failed to update module index for module: " + moduleId);
        }

        String generatedId = prefix + newIndex;
        logger.debug("Generated {} ID: {}", moduleId, generatedId);
        return generatedId;
    }
}
