package com.solverminds.booking.service;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.CommodityDTO;
import com.solverminds.booking.dto.v2.DangerousGoodsInfoV2;
import com.solverminds.booking.dto.v2.DgPackagingReferenceV2;
import com.solverminds.booking.validation.DangerousGoodsValidator;
import com.svm.ecom.common.master.repo.DgPackagingMasterRepository;
import com.svm.ecom.common.master.repo.DgPackagingMasterRepository.DgPackagingRow;
import com.svm.ecom.common.master.repo.ImdgMasterRepository;
import com.svm.ecom.common.master.repo.ImdgMasterRepository.ImdgRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Fills read-only IMDG fields and packaging names from Documentation masters.
 * Also defaults segregation and stamps imdgAmendment / variant used at save.
 */
@Service
public class DangerousGoodsEnrichmentService {

    private static final Logger logger = LoggerFactory.getLogger(DangerousGoodsEnrichmentService.class);

    private final ImdgMasterRepository imdgMasterRepository;
    private final DgPackagingMasterRepository dgPackagingMasterRepository;

    public DangerousGoodsEnrichmentService(ImdgMasterRepository imdgMasterRepository,
                                           DgPackagingMasterRepository dgPackagingMasterRepository) {
        this.imdgMasterRepository = imdgMasterRepository;
        this.dgPackagingMasterRepository = dgPackagingMasterRepository;
    }

    public void enrichRequest(BookingRequestDTO request) {
        if (request == null || request.getCommodities() == null) {
            return;
        }
        String amendment = null;
        try {
            amendment = imdgMasterRepository.findCurrentAmendment();
        } catch (Exception e) {
            logger.warn("Could not resolve current IMDG amendment: {}", e.getMessage());
        }
        for (CommodityDTO c : request.getCommodities()) {
            if (c == null || c.getDangerousGoods() == null) {
                continue;
            }
            if (!DangerousGoodsValidator.hasDangerousGoodsPayload(c.getDangerousGoods())) {
                continue;
            }
            enrich(c.getDangerousGoods(), amendment);
        }
    }

    public void enrich(DangerousGoodsInfoV2 dgi) {
        if (dgi == null) {
            return;
        }
        String amendment = null;
        try {
            amendment = imdgMasterRepository.findCurrentAmendment();
        } catch (Exception e) {
            logger.warn("Could not resolve current IMDG amendment: {}", e.getMessage());
        }
        enrich(dgi, amendment);
    }

    private void enrich(DangerousGoodsInfoV2 dgi, String amendment) {
        String unNo = trim(dgi.getUnNo());
        if (unNo == null) {
            enrichPackagingNames(dgi);
            return;
        }
        List<ImdgRow> rows = imdgMasterRepository.findByUnNo(unNo, trim(dgi.getVariant()));
        if (rows.isEmpty() && trim(dgi.getVariant()) != null) {
            rows = imdgMasterRepository.findByUnNo(unNo, null);
        }
        if (!rows.isEmpty()) {
            ImdgRow row = selectRow(rows, dgi);
            if (trim(dgi.getVariant()) == null && row.getVariant() != null) {
                dgi.setVariant(row.getVariant());
            }
            if (trim(dgi.getImoClass()) == null && row.getImoClass() != null) {
                dgi.setImoClass(row.getImoClass());
            }
            if (trim(dgi.getTechnicalName()) == null && row.getTechnicalName() != null) {
                dgi.setTechnicalName(row.getTechnicalName());
            }
            dgi.setSubRisk1(row.getSubRisk1());
            dgi.setSubRisk2(row.getSubRisk2());
            dgi.setPackagingGroup(row.getPackagingGroup());
            dgi.setEms(row.getEms());
            dgi.setState(row.getState());
            dgi.setProperShippingName(row.getProperShippingName());
            dgi.setImoStow(row.getImoStow());
            dgi.setStowageInstruction(imdgMasterRepository.resolveStowageInstruction(row.getDglPhrase()));
            if (trim(dgi.getSegregation()) == null) {
                dgi.setSegregation(imdgMasterRepository.findSegregationByUnNo(unNo));
            }
        }
        if (amendment != null) {
            dgi.setImdgAmendment(amendment);
        }
        enrichPackagingNames(dgi);
    }

    private void enrichPackagingNames(DangerousGoodsInfoV2 dgi) {
        fillPackName(dgi.getOuterPacking());
        fillPackName(dgi.getInnerPacking());
    }

    private void fillPackName(DgPackagingReferenceV2 ref) {
        if (ref == null || trim(ref.getCode()) == null) {
            return;
        }
        DgPackagingRow row = dgPackagingMasterRepository.findByCode(ref.getCode());
        if (row != null && row.getName() != null) {
            ref.setName(row.getName());
        }
    }

    private static ImdgRow selectRow(List<ImdgRow> rows, DangerousGoodsInfoV2 dgi) {
        String imoClass = trim(dgi.getImoClass());
        if (imoClass != null) {
            for (ImdgRow row : rows) {
                if (imoClass.equalsIgnoreCase(row.getImoClass())) {
                    return row;
                }
            }
        }
        return rows.get(0);
    }

    private static String trim(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return s.trim();
    }
}
