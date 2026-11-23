package com.solverminds.bl.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing the bl table in Documentation database.
 * Bill of Lading master data.
 * Primary key: bl_no (only this table uses bl_no as PK; child tables use bl_no as FK).
 */
@Entity
@Table(name = "bl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "bl_no", length = 17, nullable = false)
    private String blNo;

    @Column(name = "agencyid", length = 6)
    private String agencyId;

    @Column(name = "book_no", length = 16)
    private String bookNo;

    @Column(name = "custRefNo", length = 10)
    private String custRefNo;

    @Column(name = "custrefonBL", length = 15)
    private String custRefOnBL;

    @Column(name = "custrefoninv", length = 20)
    private String custRefOnInv;

    @Column(name = "agencyRefNo", length = 17)
    private String agencyRefNo;

    @Column(name = "blType", length = 50)
    private String blType;

    @Column(name = "shipmentStatus", columnDefinition = "char(1)")
    private String shipmentStatus;

    @Column(name = "switchBL", columnDefinition = "char(1)")
    private String switchBL;

    @Column(name = "slotType", length = 20)
    private String slotType;

    @Column(name = "partLot", columnDefinition = "char(1)")
    private String partLot;

    @Column(name = "preCarriage", columnDefinition = "char(1)")
    private String preCarriage;

    @Column(name = "onCarriage", columnDefinition = "char(1)")
    private String onCarriage;

    @Column(name = "thirdParty", columnDefinition = "char(1)")
    private String thirdParty;

    @Column(name = "thirdPartyOper", length = 10)
    private String thirdPartyOper;

    @Column(name = "transportTermid", length = 10)
    private String transportTermId;

    @Column(name = "carrierCode", length = 3)
    private String carrierCode;

    @Column(name = "firstsvctype", columnDefinition = "char(1)")
    private String firstSvcType;

    @Column(name = "oceanVoyage", length = 6)
    private String oceanVoyage;

    @Column(name = "oceanVslCode", length = 6)
    private String oceanVslCode;

    @Column(name = "oceanSerCode", length = 4)
    private String oceanSerCode;

    @Column(name = "oceanBound", length = 1)
    private String oceanBound;

    @Column(name = "serviceCode", length = 4)
    private String serviceCode;

    @Column(name = "vesselCode", length = 6)
    private String vesselCode;

    @Column(name = "voyageCode", length = 6)
    private String voyageCode;

    @Column(name = "bound", length = 1)
    private String bound;

    @Column(name = "originPortId", length = 5)
    private String originPortId;

    @Column(name = "finalPortid", length = 5)
    private String finalPortId;

    @Column(name = "loadPortId", length = 5)
    private String loadPortId;

    @Column(name = "dischargePortid", length = 5)
    private String dischargePortId;

    @Column(name = "originPortDesc", length = 150)
    private String originPortDesc;

    @Column(name = "finalPortDesc", length = 150)
    private String finalPortDesc;

    @Column(name = "polTerminal", length = 10)
    private String polTerminal;

    @Column(name = "polAt")
    private LocalDateTime polAt;

    @Column(name = "podTerminal", length = 10)
    private String podTerminal;

    @Column(name = "podAt")
    private LocalDateTime podAt;

    @Column(name = "bookDate")
    private LocalDateTime bookDate;

    @Column(name = "bookReady")
    private LocalDateTime bookReady;

    @Column(name = "bookDeadline")
    private LocalDateTime bookDeadline;

    @Column(name = "remarks", columnDefinition = "varchar(max)")
    private String remarks;

    @Column(name = "status", columnDefinition = "char(1)")
    private String status;

    @Column(name = "actStatus", length = 2)
    private String actStatus;

    @Column(name = "originals")
    private Integer originals;

    @Column(name = "copies")
    private Integer copies;

    @Column(name = "printedOriginals")
    private Integer printedOriginals;

    @Column(name = "printedCopies")
    private Integer printedCopies;

    @Column(name = "adminOriginals")
    private Integer adminOriginals;

    @Column(name = "bookUpDate")
    private LocalDateTime bookUpDate;

    @Column(name = "backdate", columnDefinition = "char(1)")
    private String backDate;

    @Column(name = "securityNo", length = 10)
    private String securityNo;

    @Column(name = "isFCN", columnDefinition = "char(1)")
    private String isFCN;

    @Column(name = "isvoyageclosed", columnDefinition = "char(1)")
    private String isVoyageClosed;

    @Column(name = "receiptShipDate")
    private LocalDateTime receiptShipDate;

    @Column(name = "OnboardDate")
    private LocalDateTime onBoardDate;

    @Column(name = "grpcallid", length = 50)
    private String grpCallId;

    @Column(name = "PrintedDate")
    private LocalDateTime printedDate;

    @Column(name = "PrintedUser", length = 15)
    private String printedUser;

    @Column(name = "usercr", length = 15)
    private String userCr;

    @Column(name = "datecr")
    private LocalDateTime dateCr;

    @Column(name = "userup", length = 15)
    private String userUp;

    @Column(name = "dateup")
    private LocalDateTime dateUp;

    @Column(name = "ratetype", length = 50)
    private String rateType;

    @Column(name = "raterefno", length = 20)
    private String rateRefNo;

    @Column(name = "shipperRefNo", length = 20)
    private String shipperRefNo;

    @Column(name = "plaagencyid", length = 6)
    private String plaAgencyId;

    @Column(name = "switchAgency", length = 6)
    private String switchAgency;

    @Column(name = "isDGbook", columnDefinition = "char(1)")
    private String isDGBook;

    @Column(name = "crossBook", columnDefinition = "char(1)")
    private String crossBook;

    @Column(name = "systemBLType", length = 20)
    private String systemBLType;

    @Column(name = "newswblno", columnDefinition = "char(1)")
    private String newSwBlNo;

    @Column(name = "booksource", length = 5)
    private String bookSource;

    @Column(name = "shipmenttype", columnDefinition = "char(2)")
    private String shipmentType;

    @Column(name = "sbltype", length = 2)
    private String sblType;

    @Column(name = "invoiced", columnDefinition = "char(1)")
    private String invoiced;

    @Column(name = "ratevaliditydate")
    private LocalDateTime rateValidityDate;

    @Column(name = "remarks_cancel", length = 500)
    private String remarksCancel;

    @Column(name = "template", length = 1)
    private String template;

    @Column(name = "ecomPrntstatus", length = 1)
    private String ecomPrntStatus;

    @Column(name = "ecomPrintdate")
    private LocalDateTime ecomPrintDate;

    @Column(name = "slotExchange", length = 4)
    private String slotExchange;

    @Column(name = "uniqueid", length = 60)
    private String uniqueId;

    @Column(name = "inTransitOriginPort", length = 6)
    private String inTransitOriginPort;

    @Column(name = "inTransitPort", length = 6)
    private String inTransitPort;

    @Column(name = "inTransitPortName", length = 100)
    private String inTransitPortName;

    @Column(name = "salesAgent", length = 10)
    private String salesAgent;

    @Column(name = "tos", length = 7)
    private String tos;

    @Column(name = "extModDate")
    private LocalDateTime extModDate;

    @Column(name = "ConfirmedUser", length = 15)
    private String confirmedUser;

    @Column(name = "ConfirmedDate")
    private LocalDateTime confirmedDate;

    @Column(name = "ediprovider", length = 2)
    private String ediProvider;

    @Column(name = "custremarks", columnDefinition = "varchar(max)")
    private String custRemarks;

    @Column(name = "isSwitchApplicable", length = 1)
    private String isSwitchApplicable;

    @Column(name = "user_remarks", columnDefinition = "varchar(max)")
    private String userRemarks;

    @Column(name = "podagency", length = 6)
    private String podAgency;

    @Column(name = "pldagency", length = 6)
    private String pldAgency;

    @Column(name = "isOpsUpdate", columnDefinition = "char(1)")
    private String isOpsUpdate;

    @Column(name = "ecomPrintuser", length = 15)
    private String ecomPrintUser;

    @Column(name = "cargoprotectid")
    private Integer cargoProtectId;

    @Column(name = "ensfiling", columnDefinition = "char(1)")
    private String ensFiling;

    @Column(name = "feedertype", columnDefinition = "char(1)")
    private String feederType;

    @Column(name = "t2lfiling", columnDefinition = "char(1)")
    private String t2lFiling;

    @Column(name = "enstype", columnDefinition = "char(1)")
    private String ensType;

    @Column(name = "vsloperator", length = 3)
    private String vslOperator;

    @Column(name = "printformat", length = 50)
    private String printFormat;
}
