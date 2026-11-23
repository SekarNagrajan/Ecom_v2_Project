package com.svm.ecom.master.model;

/**
 * Implementation class for VesselView interface
 * Used with native SQL queries
 */
public class VesselViewImpl implements VesselView {
    
    private String vesselCode;
    private String vesselName;
    private String imoNumber;
    private String builtYear;
    private String builtMonth;
    private String status;
    private Integer grossTonnage;
    private Double loa;
    private Double breadth;
    private Double draft;
    private Double deadWeight;
    private Integer reeferPlugs;
    private Integer capacityTeu;
    private String flag;
    private String callSign;
    
    public VesselViewImpl(
            String vesselCode,
            String vesselName,
            String imoNumber,
            String builtYear,
            String builtMonth,
            String status,
            Integer grossTonnage,
            Double loa,
            Double breadth,
            Double draft,
            Double deadWeight,
            Integer reeferPlugs,
            Integer capacityTeu,
            String flag,
            String callSign) {
        this.vesselCode = vesselCode;
        this.vesselName = vesselName;
        this.imoNumber = imoNumber;
        this.builtYear = builtYear;
        this.builtMonth = builtMonth;
        this.status = status;
        this.grossTonnage = grossTonnage;
        this.loa = loa;
        this.breadth = breadth;
        this.draft = draft;
        this.deadWeight = deadWeight;
        this.reeferPlugs = reeferPlugs;
        this.capacityTeu = capacityTeu;
        this.flag = flag;
        this.callSign = callSign;
    }
    
    @Override
    public String getVesselCode() {
        return vesselCode;
    }
    
    @Override
    public String getVesselName() {
        return vesselName;
    }

    @Override
    public String getImoNumber() {
        return imoNumber;
    }

    @Override
    public String getBuiltYear() {
        return builtYear;
    }

    @Override
    public String getBuiltMonth() {
        return builtMonth;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public Integer getGrossTonnage() {
        return grossTonnage;
    }

    @Override
    public Double getLoa() {
        return loa;
    }

    @Override
    public Double getBreadth() {
        return breadth;
    }

    @Override
    public Double getDraft() {
        return draft;
    }

    @Override
    public Double getDeadWeight() {
        return deadWeight;
    }

    @Override
    public Integer getReeferPlugs() {
        return reeferPlugs;
    }

    @Override
    public Integer getCapacityTeu() {
        return capacityTeu;
    }

    @Override
    public String getFlag() {
        return flag;
    }

    @Override
    public String getCallSign() {
        return callSign;
    }
}
