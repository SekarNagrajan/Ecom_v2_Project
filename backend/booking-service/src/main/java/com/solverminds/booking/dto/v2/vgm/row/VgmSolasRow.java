package com.solverminds.booking.dto.v2.vgm.row;

import lombok.Data;

@Data
public class VgmSolasRow {
    private String eqpid;
    /** equipment type from vgmsolas / ecom_vgmsolas (eqptype) */
    private String eqptype;
    private String vgmWeight;
    private String vgmUnit;
    private String vgmObtainedDate;
    private String vgmObtainedMethod;
    private String vgmAuthorizedPerson;
    private String vgmCompanyName;
    private String vgmAddress1;
    private String vgmAddress2;
    private String vgmCity;
    private String vgmZipcode;
    private String vgmCountry;
    private String telephone;
    private String email;
    /** vgm_ref_number */
    private String vgmRefNumber;
    /** VGM_order_Number */
    private String vgmOrderNumber;
    private String status;
    private Integer versionNo;
    private String source;
}
