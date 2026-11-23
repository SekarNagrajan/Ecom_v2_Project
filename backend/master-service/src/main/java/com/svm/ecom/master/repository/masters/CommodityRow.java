package com.svm.ecom.master.repository.masters;

/**
 * Row projection from commodity self-join for v2 commodity list.
 */
public class CommodityRow {

    private String comdtyCode;
    private String comdtyName;
    private String comdtyType;
    private String restricted;
    private String exemptComdty;
    private String grpCode;
    private String groupName;

    public String getComdtyCode() { return comdtyCode; }
    public void setComdtyCode(String comdtyCode) { this.comdtyCode = comdtyCode; }

    public String getComdtyName() { return comdtyName; }
    public void setComdtyName(String comdtyName) { this.comdtyName = comdtyName; }

    public String getComdtyType() { return comdtyType; }
    public void setComdtyType(String comdtyType) { this.comdtyType = comdtyType; }

    public String getRestricted() { return restricted; }
    public void setRestricted(String restricted) { this.restricted = restricted; }

    public String getExemptComdty() { return exemptComdty; }
    public void setExemptComdty(String exemptComdty) { this.exemptComdty = exemptComdty; }

    public String getGrpCode() { return grpCode; }
    public void setGrpCode(String grpCode) { this.grpCode = grpCode; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
}
