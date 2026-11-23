package net.ecom.vo;

import java.io.Serializable;
import java.util.Map;

public class ContractSurchargeCalculationResult implements Serializable {

    private Map<String, Map<String, ContractSurchargeResultVo>> chargesByPairId;

    public Map<String, Map<String, ContractSurchargeResultVo>> getChargesByPairId() {
        return chargesByPairId;
    }

    public void setChargesByPairId(Map<String, Map<String, ContractSurchargeResultVo>> chargesByPairId) {
        this.chargesByPairId = chargesByPairId;
    }
}
