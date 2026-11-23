package net.ecom.vo;

import java.io.Serializable;

public class ContractSurchargeResultVo implements Serializable {

    private String chargeCode;
    /** Billing / local currency amount (surVO.amount). */
    private String amount;
    private String currency;
    private String amountUsd;
    private String vatos;
    private String amtType;
    private String eqpType;

    public String getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmountUsd() {
        return amountUsd;
    }

    public void setAmountUsd(String amountUsd) {
        this.amountUsd = amountUsd;
    }

    public String getVatos() {
        return vatos;
    }

    public void setVatos(String vatos) {
        this.vatos = vatos;
    }

    public String getAmtType() {
        return amtType;
    }

    public void setAmtType(String amtType) {
        this.amtType = amtType;
    }

    public String getEqpType() {
        return eqpType;
    }

    public void setEqpType(String eqpType) {
        this.eqpType = eqpType;
    }
}
