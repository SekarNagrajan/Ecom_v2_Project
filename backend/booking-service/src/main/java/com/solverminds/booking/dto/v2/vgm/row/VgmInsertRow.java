package com.solverminds.booking.dto.v2.vgm.row;

import lombok.Builder;
import lombok.Data;

/**
 * One row for ecom_vgmsolas and vgmsolas (booking path, bl_no null).
 */
@Data
@Builder
public class VgmInsertRow {

    private String bookNo;
    private String blNo;
    private String eqptype;
    private String eqpid;
    private Double vgmWeight;
    private String vgmUnit;
    private Integer versionNo;
    private String vgmSource;
    private String vgmType;
    private String vgmRefNumber;
    private String vgmOrderNumber;
    private String vgmObtainedDate;
    private String vgmObtainedMethod;
    private String vgmCompanyName;
    private String vgmAddress1;
    private String vgmAddress2;
    private String vgmZipcode;
    private String vgmCity;
    private String vgmCountry;
    private String vgmAuthorizedPerson;
    private String telephone;
    private String email;
    private String status;
    private String userCr;
    private String userUp;
    private String oprStatus;
}
