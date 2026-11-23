package com.solverminds.booking.common.entity;

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
import java.util.Objects;

/**
 * Entity representing the booking table.
 * To maintain all booking master data.
 */
@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Booking Number - uniquely identifies location code with unique serial numbers
     */
    @Id
    @Column(name = "book_no", length = 17, nullable = false)
    private String bookNo;

    /**
     * Agency that created the Booking
     */
    @Column(name = "agencyid", length = 6, nullable = false)
    private String agencyId;

    /**
     * Booking AP Customer SCAC code
     */
    @Column(name = "custRefNo", length = 10)
    private String custRefNo;

    /**
     * Not in use
     */
    @Column(name = "custrefNo1", length = 20)
    private String custRefNo1;

    /**
     * Not in use
     */
    @Column(name = "agencyRefNo", length = 10)
    private String agencyRefNo;

    /**
     * Agency id which is for documentation
     */
    @Column(name = "documentalAgencyId", length = 6, nullable = false)
    private String documentalAgencyId;

    /**
     * BL Issued Agency id
     */
    @Column(name = "agencyIssueId", length = 6, nullable = false)
    private String agencyIssueId;

    /**
     * Not in use
     */
    @Column(name = "slotType", length = 20)
    private String slotType;

    /**
     * Whether the booking rates (SRR, normal, CMS, ..)
     */
    @Column(name = "bookType", length = 10, nullable = false)
    private String bookType;

    /**
     * Booking type reference number
     */
    @Column(name = "typeNo", length = 20)
    private String typeNo;

    /**
     * Booking Third party agency (Y/N)
     */
    @Column(name = "thirdParty", columnDefinition = "char(1)", nullable = false)
    private String thirdParty;

    /**
     * Booking Third party Carrier code
     */
    @Column(name = "thirdPartyOper", length = 10)
    private String thirdPartyOper;

    /**
     * Transport terms reference id for the Liner
     */
    @Column(name = "transportTermId", length = 2)
    private String transportTermId;

    /**
     * Transport services for the port Pair
     */
    @Column(name = "transportServiceId", length = 3)
    private String transportServiceId;

    /**
     * The Carrier Code
     */
    @Column(name = "carrierCode", length = 3, nullable = false)
    private String carrierCode;

    /**
     * Not in use
     */
    @Column(name = "oceanCallId", length = 50)
    private String oceanCallId;

    /**
     * Ocean vessel code (main-ocean)
     */
    @Column(name = "oceanVslCode", length = 6)
    private String oceanVslCode;

    /**
     * Main voyage code
     */
    @Column(name = "oceanVoyage", length = 6)
    private String oceanVoyage;

    /**
     * Main service code
     */
    @Column(name = "oceanSerCode", length = 4)
    private String oceanSerCode;

    /**
     * Main bound
     */
    @Column(name = "oceanBound", columnDefinition = "char(1)")
    private String oceanBound;

    /**
     * Load Port schedule Group call ID
     */
    @Column(name = "callId", length = 50, nullable = false)
    private String callId;

    /**
     * First service type whether Feeder / Main
     */
    @Column(name = "firstSvcType", columnDefinition = "char(1)", nullable = false)
    private String firstSvcType;

    /**
     * First port Service code
     */
    @Column(name = "serviceCode", length = 4, nullable = false)
    private String serviceCode;

    /**
     * First port vessel code
     */
    @Column(name = "vesselCode", length = 6, nullable = false)
    private String vesselCode;

    /**
     * First port voyage code
     */
    @Column(name = "voyageCode", length = 6, nullable = false)
    private String voyageCode;

    /**
     * First port bound
     */
    @Column(name = "bound", columnDefinition = "char(1)", nullable = false)
    private String bound;

    /**
     * Port ID of Origin location
     */
    @Column(name = "originPortId", length = 5, nullable = false)
    private String originPortId;

    /**
     * Destination Port Id
     */
    @Column(name = "finalPortId", length = 5, nullable = false)
    private String finalPortId;

    /**
     * Port Id in which loaded
     */
    @Column(name = "loadPortId", length = 5, nullable = false)
    private String loadPortId;

    /**
     * Port id In which discharged
     */
    @Column(name = "dischargePortId", length = 5, nullable = false)
    private String dischargePortId;

    /**
     * Description or location of Origin port
     */
    @Column(name = "originPortDesc", length = 150, nullable = false)
    private String originPortDesc;

    /**
     * Description or location of destination port
     */
    @Column(name = "finalPortDesc", length = 150, nullable = false)
    private String finalPortDesc;

    /**
     * Terminal port code where Loading takes place
     */
    @Column(name = "polTerminal", length = 10, nullable = false)
    private String polTerminal;

    /**
     * Load port Date on which loaded in schedule
     */
    @Column(name = "polAt", nullable = false)
    private LocalDateTime polAt;

    /**
     * Terminal port code where will be delivered
     */
    @Column(name = "podTerminal", length = 50)
    private String podTerminal;

    /**
     * Discharge port Date on which Delivered
     */
    @Column(name = "podAt")
    private LocalDateTime podAt;

    /**
     * Date on which it is booked
     */
    @Column(name = "bookDate", nullable = false)
    private LocalDateTime bookDate;

    /**
     * Date on which booking is confirmed ready
     */
    @Column(name = "bookReady")
    private LocalDateTime bookReady;

    /**
     * Booking Cut off Date which POL ETD and config hours
     */
    @Column(name = "bookDeadline")
    private LocalDateTime bookDeadline;

    /**
     * Whether it is Haulage / merchant for precarriage
     */
    @Column(name = "preCarriage", columnDefinition = "char(1)", nullable = false)
    private String preCarriage;

    /**
     * Whether it is Haulage / merchant for Oncarriage
     */
    @Column(name = "onCarriage", columnDefinition = "char(1)", nullable = false)
    private String onCarriage;

    /**
     * Remarks on Booking
     */
    @Column(name = "remarks", columnDefinition = "varchar(max)")
    private String remarks;

    /**
     * Whether booking in Draft or confirmed
     */
    @Column(name = "status", columnDefinition = "char(1)", nullable = false)
    private String status;

    /**
     * Not in use
     */
    @Column(name = "actStatus", columnDefinition = "char(2)", nullable = false)
    private String actStatus;

    /**
     * User who has created
     */
    @Column(name = "usercr", length = 15, nullable = false)
    private String usercr;

    /**
     * Date of creation
     */
    @Column(name = "datecr", nullable = false)
    private LocalDateTime datecr;

    /**
     * User who has updated
     */
    @Column(name = "userup", length = 15)
    private String userup;

    /**
     * Date of updation
     */
    @Column(name = "dateup")
    private LocalDateTime dateup;

    /**
     * Not in use
     */
    @Column(name = "isDGBook", columnDefinition = "char(1)", nullable = false)
    private String isDgBook;

    /**
     * Not in use
     */
    @Column(name = "shipperRefNo", length = 20)
    private String shipperRefNo;

    /**
     * Whether operation is required or not for booking
     */
    @Column(name = "prepareOPS", columnDefinition = "char(1)", nullable = false)
    private String prepareOps;

    /**
     * Whether BL doc is required or not for booking
     */
    @Column(name = "prepareBL", columnDefinition = "char(1)", nullable = false)
    private String prepareBl;

    /**
     * POL Agency code
     */
    @Column(name = "agencypolid", length = 10)
    private String agencyPolId;

    /**
     * Whether it is CrossBooking
     */
    @Column(name = "crossBook", columnDefinition = "char(1)", nullable = false)
    private String crossBook;

    /**
     * Whether booking is update or not
     */
    @Column(name = "Isupdate", columnDefinition = "char(1)", nullable = false)
    private String isUpdate;

    /**
     * Not in use
     */
    @Column(name = "paymenttermsId", columnDefinition = "char(3)")
    private String paymentTermsId;

    /**
     * Booking created Manual/Intra/EDI
     */
    @Column(name = "booksource", length = 5, nullable = false)
    private String bookSource;

    /**
     * High/Medium/Low priority
     */
    @Column(name = "priority", columnDefinition = "char(1)", nullable = false)
    private String priority;

    /**
     * Booking shipment type Container / Non container
     */
    @Column(name = "shipment_type", length = 50, nullable = false)
    private String shipmentType;

    /**
     * Whether the booking is edited or not
     */
    @Column(name = "isedited", columnDefinition = "char(1)")
    private String isEdited;

    /**
     * Rate validity date of rate request
     */
    @Column(name = "ratevaliditydate")
    private LocalDateTime rateValidityDate;

    /**
     * Remarks on booking cancel
     */
    @Column(name = "remarks_cancel", length = 500)
    private String remarksCancel;

    /**
     * Not in use
     */
    @Column(name = "optionalpod", length = 5)
    private String optionalPod;

    /**
     * Not in use
     */
    @Column(name = "ediprovider", length = 2)
    private String ediProvider;

    /**
     * To store history of remarks updated
     */
    @Column(name = "remarkshistory", columnDefinition = "varchar(max)")
    private String remarksHistory;

    /**
     * Not in use
     */
    @Column(name = "slotExchange", length = 4)
    private String slotExchange;

    /**
     * Not in use
     */
    @Column(name = "sendToEcom", columnDefinition = "char(1)")
    private String sendToEcom;

    /**
     * Not in use
     */
    @Column(name = "intranLoadPort", length = 6)
    private String intranLoadPort;

    /**
     * Not in use
     */
    @Column(name = "intraLoadPortname", length = 100)
    private String intraLoadPortName;

    /**
     * Not in use
     */
    @Column(name = "inTransitPort", length = 6)
    private String inTransitPort;

    /**
     * Not in use
     */
    @Column(name = "inTransitPortName", length = 100)
    private String inTransitPortName;

    /**
     * Not in use
     */
    @Column(name = "salesAgent", length = 6)
    private String salesAgent;

    /**
     * Not in use
     */
    @Column(name = "fixedPOD", columnDefinition = "char(1)")
    private String fixedPod;

    /**
     * Not in use
     */
    @Column(name = "twoleg", columnDefinition = "char(1)")
    private String twoLeg;

    /**
     * To save terms of ship details
     */
    @Column(name = "tos", length = 7)
    private String tos;

    /**
     * To store customer remarks
     */
    @Column(name = "custremarks", columnDefinition = "varchar(max)")
    private String custRemarks;

    /**
     * User who confirmed the Booking
     */
    @Column(name = "ConfirmedUser", length = 15)
    private String confirmedUser;

    /**
     * Date when confirmed the Booking
     */
    @Column(name = "ConfirmedDate")
    private LocalDateTime confirmedDate;

    /**
     * To store version details Performance purpose
     */
    @Column(name = "user_remarks", columnDefinition = "varchar(max)")
    private String userRemarks;

    /**
     * To save the podagency
     */
    @Column(name = "podagency", length = 6)
    private String podAgency;

    /**
     * To save the polagency
     */
    @Column(name = "pldagency", length = 6)
    private String pldAgency;

    /**
     * Cargo protect reference id
     */
    @Column(name = "cargoprotectid")
    private Integer cargoProtectId;

    /**
     * Initial POL ETD date
     */
    @Column(name = "poletd_initial")
    private LocalDateTime polEtdInitial;

    /**
     * Initial POD ETA date
     */
    @Column(name = "podeta_initial")
    private LocalDateTime podEtaInitial;

    /**
     * ENS Filing flag (Y/N)
     */
    @Column(name = "ensfiling", columnDefinition = "char(1)")
    private String ensFiling;

    /**
     * Feeder type indicator
     */
    @Column(name = "feedertype", columnDefinition = "char(1)")
    private String feederType;

    /**
     * Booking freeze flag (Y/N)
     */
    @Column(name = "bkg_freeze", columnDefinition = "char(1)")
    private String bkgFreeze;

    /**
     * T2L Filing flag (Y/N)
     */
    @Column(name = "t2lfiling", columnDefinition = "char(1)")
    private String t2lFiling;

    /**
     * ENS type indicator
     */
    @Column(name = "enstype", columnDefinition = "char(1)")
    private String ensType;

    /**
     * Vessel operator code
     */
    @Column(name = "vsloperator", length = 3)
    private String vslOperator;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookNo, booking.bookNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookNo);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookNo='" + bookNo + '\'' +
                ", agencyId='" + agencyId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
