package com.svm.ecom.rates.service;

import com.svm.ecom.common.enums.EquipmentType;
import com.svm.ecom.rates.dto.estimate.RateEstimateCommodityRequest;
import com.svm.ecom.rates.dto.estimate.RateEstimateContainerRequest;
import com.svm.ecom.rates.dto.estimate.RateEstimateRequest;
import com.svm.ecom.rates.dto.estimate.RateEstimateResponse;
import com.svm.ecom.rates.mapper.EstimateToSurchargeVOMapper;
import com.svm.ecom.rates.mapper.RateEstimateResponseMapper;
import com.svm.ecom.rates.repository.RouteDetailRepository.RouteDetail;
import com.svm.ecom.rates.surcharge.CommonSurchargeEngineAdapter;
import com.svm.lrp.pnc.common.surcharge.shared.vo.surchargeVO;
import net.ecom.vo.ContractSurchargeCalculationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Estimate Auto Rate 
 * ({@code btnAutoTariff} → {@code autoOFRNew2} + {@code newAutoSurcharge}).
 * <pre>
 * Booking                          Estimate
 * -------                          --------
 * 1) autoOFRNew2 (flag O)     →    OfrContractChargeService.resolve
 *    per cargo rateType/eqp        per container; LP once; non-LP × qty
 * 2) CommonSurcharge (flag A) →    CommonSurchargeEngineAdapter.calculate
 *    + getOFRByRef per eqp    →    seed OFR/FRT per equipment type
 * 3) charges grid merge       →    mergeContractCharges
 * </pre>
 * Surcharge tariff load is unchanged; OFR is additive only.
 */
@Service
public class RateEstimateService {

    private static final Logger logger = LoggerFactory.getLogger(RateEstimateService.class);

    private final EstimateToSurchargeVOMapper surchargeVOMapper;
    private final RateEstimateLookupCacheService lookupCache;
    private final CommonSurchargeEngineAdapter surchargeEngine;
    private final OfrContractChargeService ofrContractChargeService;
    private final RateEstimateResponseMapper responseMapper;

    public RateEstimateService(EstimateToSurchargeVOMapper surchargeVOMapper,
                               RateEstimateLookupCacheService lookupCache,
                               CommonSurchargeEngineAdapter surchargeEngine,
                               OfrContractChargeService ofrContractChargeService,
                               RateEstimateResponseMapper responseMapper) {
        this.surchargeVOMapper = surchargeVOMapper;
        this.lookupCache = lookupCache;
        this.surchargeEngine = surchargeEngine;
        this.ofrContractChargeService = ofrContractChargeService;
        this.responseMapper = responseMapper;
    }

    public RateEstimateResponse estimate(RateEstimateRequest request) {
        List<String> validationErrors = validateBusinessRules(request);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", validationErrors));
        }

        RouteDetail route = lookupCache.findRouteDetail(request.getRouteId()).orElse(null);
        if (route == null) {
            logger.warn("Routedetail not found for routeId={} — terminals/traship/MainSvc left empty",
                    request.getRouteId());
        }

        surchargeVO vo = surchargeVOMapper.toSurchargeVO(request, route);
        logger.info("Auto Rate estimate: agrPartyCode={}, routeId={}, containers={}",
                request.getAgrPartyCode(),
                request.getRouteId(),
                vo != null && vo.eqpid != null ? vo.eqpid.length : 0);

        // 1)   Contract OFR engine (AutoRateFlag = O)
        ContractOfrResult ofr = ofrContractChargeService.resolve(request);

        // 2) CommonSurcharge (AutoRateFlag = A)
        //    + getOFRByRef seed (unit OFR per equipment type → formula elements); tariff load unchanged
        ContractSurchargeCalculationResult calculationResult =
                surchargeEngine.calculate(vo, ofr.getOfrUnitByEquipmentType());
        RateEstimateResponse response = responseMapper.toResponse(calculationResult);

        // 3) Merge O-flag lines into response (Booking charges grid)
        return responseMapper.mergeContractCharges(response, ofr.getChargesForResponse());
    }

    private List<String> validateBusinessRules(RateEstimateRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null || request.getContainers() == null) {
            errors.add("containers is required");
            return errors;
        }
        int index = 0;
        for (RateEstimateContainerRequest container : request.getContainers()) {
            if (container == null) {
                index++;
                continue;
            }
            String prefix = "containers[" + index + "]";
            boolean isReefer = EquipmentType.R.equals(container.getType());
            boolean isNor = Boolean.TRUE.equals(container.getIsNOR());
            if (isReefer && !isNor && container.getReeferSettings() == null) {
                errors.add(prefix + ".reeferSettings is required when type is R and isNOR is false");
            }
            if (Boolean.TRUE.equals(container.getIsDG())) {
                if (container.getCommodities() != null) {
                    int cIndex = 0;
                    for (RateEstimateCommodityRequest commodity : container.getCommodities()) {
                        if (commodity != null && commodity.getDangerousGoodsInfo() == null) {
                            errors.add(prefix + ".commodities[" + cIndex
                                    + "].dangerousGoodsInfo is required when isDG is true");
                        }
                        cIndex++;
                    }
                }
            }
            index++;
        }
        return errors;
    }
}
