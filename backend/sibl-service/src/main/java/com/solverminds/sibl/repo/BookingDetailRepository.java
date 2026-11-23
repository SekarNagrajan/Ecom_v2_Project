package com.solverminds.sibl.repo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Repository for fetching booking details from the booking table (report DB).
 * Same pattern as booking-service's RoutingDetailRepository: get by key and map to a detail DTO.
 * Used by SIBL EdiExportService to populate editempbooking from Booking table per spec.
 */
@Repository("siblBookingDetailRepository")
public class BookingDetailRepository {

    private static final String SQL_GET_BOOKING_BY_BOOK_NO =
        "SELECT book_no, agencyid, oceanVslCode, oceanVoyage, oceanSerCode, oceanBound, " +
        "serviceCode, originPortId, finalPortId, polTerminal, polAt, podTerminal, podAt, " +
        "bookDate, bookReady, firstSvcType, preCarriage, onCarriage, documentalAgencyId, agencyIssueId, agencypolid, " +
        "originPortDesc, finalPortDesc, agencyRefNo, inTransitPort, inTransitPortName, " +
        "transportServiceId, transportTermId, firstSvcType " +
        "FROM booking (nolock) WHERE book_no = ?";

    /** booking_cust columns: same as EdiTempBooking takes from booking (AP, BP for EDI temp cust). */
    private static final String SQL_GET_BOOKING_CUST_BY_BOOK_NO_AP_BP =
        "SELECT book_no, custTypeId, custId, countryId, stateId, name, address1, address2, POBox, " +
        "city, zipCode, telephone, fax, contact, email, carriercode, isedited, persontype, eori " +
        "FROM booking_cust (nolock) WHERE book_no = ? AND custTypeId IN ('AP','BP')";

    private final JdbcTemplate reportJdbcTemplate;

    public BookingDetailRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Fetches booking row by book_no (same as booking-service gets routing by routeId).
     *
     * @param bookNo Booking number (book_no in booking table)
     * @return Optional containing booking detail for editempbooking mapping, or empty if not found
     */
    public Optional<BookingDetail> getBookingByBookNo(String bookNo) {
        if (bookNo == null || bookNo.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return reportJdbcTemplate.query(
                SQL_GET_BOOKING_BY_BOOK_NO,
                ps -> ps.setString(1, bookNo.trim()),
                rs -> rs.next() ? Optional.of(mapRowToBookingDetail(rs)) : Optional.empty()
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Fetches booking customer rows (AP, BP) from booking_cust for the given book_no.
     * Used by SIBL EdiExportService to populate editempbooking_cust from booking_cust (same as EdiTempBooking from booking).
     *
     * @param bookNo Booking number (book_no in booking_cust)
     * @return List of customer details (AP and BP only); empty if none or on error
     */
    public List<BookingCustDetail> getBookingCustomersByBookNoApBp(String bookNo) {
        if (bookNo == null || bookNo.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return reportJdbcTemplate.query(
                SQL_GET_BOOKING_CUST_BY_BOOK_NO_AP_BP,
                ps -> ps.setString(1, bookNo.trim()),
                (rs, rowNum) -> mapRowToBookingCustDetail(rs)
            );
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static BookingCustDetail mapRowToBookingCustDetail(ResultSet rs) throws SQLException {
        BookingCustDetail d = new BookingCustDetail();
        d.setBookNo(getString(rs, "book_no"));
        d.setCustTypeId(getString(rs, "custTypeId"));
        d.setCustId(getString(rs, "custId"));
        d.setCountryId(getString(rs, "countryId"));
        d.setStateId(getString(rs, "stateId"));
        d.setName(getString(rs, "name"));
        d.setAddress1(getString(rs, "address1"));
        d.setAddress2(getString(rs, "address2"));
        d.setPoBox(getString(rs, "POBox"));
        d.setCity(getString(rs, "city"));
        d.setZipCode(getString(rs, "zipCode"));
        d.setTelephone(getString(rs, "telephone"));
        d.setFax(getString(rs, "fax"));
        d.setContact(getString(rs, "contact"));
        d.setEmail(getString(rs, "email"));
        d.setCarriercode(getString(rs, "carriercode"));
        d.setIsedited(getString(rs, "isedited"));
        d.setPersontype(getString(rs, "persontype"));
        d.setEori(getString(rs, "eori"));
        return d;
    }

    private static BookingDetail mapRowToBookingDetail(ResultSet rs) throws SQLException {
        BookingDetail d = new BookingDetail();
        d.setBookNo(getString(rs, "book_no"));
        d.setAgencyid(getString(rs, "agencyid"));
        d.setOceanVslCode(getString(rs, "oceanVslCode"));
        d.setOceanVoyage(getString(rs, "oceanVoyage"));
        d.setOceanSerCode(getString(rs, "oceanSerCode"));
        d.setOceanBound(getString(rs, "oceanBound"));
        d.setServiceCode(getString(rs, "serviceCode"));
        d.setOriginPortId(getString(rs, "originPortId"));
        d.setFinalPortId(getString(rs, "finalPortId"));
        d.setPolTerminal(getString(rs, "polTerminal"));
        d.setPolAt(getLocalDateTime(rs, "polAt"));
        d.setPodTerminal(getString(rs, "podTerminal"));
        d.setPodAt(getLocalDateTime(rs, "podAt"));
        d.setBookDate(getLocalDateTime(rs, "bookDate"));
        d.setBookReady(getLocalDateTime(rs, "bookReady"));
        d.setPreCarriage(getString(rs, "preCarriage"));
        d.setOnCarriage(getString(rs, "onCarriage"));
        d.setDocumentalAgencyId(getString(rs, "documentalAgencyId"));
        d.setAgencyIssueId(getString(rs, "agencyIssueId"));
        d.setAgencypolid(getString(rs, "agencypolid"));
        d.setOriginPortDesc(getString(rs, "originPortDesc"));
        d.setFinalPortDesc(getString(rs, "finalPortDesc"));
        d.setAgencyRefNo(getString(rs, "agencyRefNo"));
        d.setInTransitPort(getString(rs, "inTransitPort"));
        d.setInTransitPortName(getString(rs, "inTransitPortName"));
        d.setTransportServiceId(getString(rs, "transportServiceId"));
        d.setTransportTermId(getString(rs, "transportTermId"));
        d.setFirstSvcType(getString(rs, "firstSvcType"));
        return d;
    }

    private static String getString(ResultSet rs, String col) throws SQLException {
        String s = rs.getString(col);
        return (s != null && !s.trim().isEmpty()) ? s.trim() : null;
    }

    private static LocalDateTime getLocalDateTime(ResultSet rs, String col) throws SQLException {
        Timestamp ts = rs.getTimestamp(col);
        if (ts == null) return null;
        return ts.toLocalDateTime();
    }

    /**
     * DTO for a row from the booking table. Used to populate editempbooking from Booking table per spec.
     */
    public static class BookingDetail {
        private String bookNo;
        private String agencyid;
        private String oceanVslCode;
        private String oceanVoyage;
        private String oceanSerCode;
        private String oceanBound;
        private String serviceCode;
        private String originPortId;
        private String finalPortId;
        private String polTerminal;
        private LocalDateTime polAt;
        private String podTerminal;
        private LocalDateTime podAt;
        private LocalDateTime bookDate;
        private LocalDateTime bookReady;
        private String preCarriage;
        private String onCarriage;
        private String documentalAgencyId;
        private String agencyIssueId;
        private String agencypolid;
        private String originPortDesc;
        private String finalPortDesc;
        private String agencyRefNo;
        private String inTransitPort;
        private String inTransitPortName;
        private String transportServiceId;
        private String transportTermId;
        private String firstSvcType;

        public String getBookNo() { return bookNo; }
        public void setBookNo(String bookNo) { this.bookNo = bookNo; }
        public String getAgencyid() { return agencyid; }
        public void setAgencyid(String agencyid) { this.agencyid = agencyid; }
        public String getOceanVslCode() { return oceanVslCode; }
        public void setOceanVslCode(String oceanVslCode) { this.oceanVslCode = oceanVslCode; }
        public String getOceanVoyage() { return oceanVoyage; }
        public void setOceanVoyage(String oceanVoyage) { this.oceanVoyage = oceanVoyage; }
        public String getOceanSerCode() { return oceanSerCode; }
        public void setOceanSerCode(String oceanSerCode) { this.oceanSerCode = oceanSerCode; }
        public String getOceanBound() { return oceanBound; }
        public void setOceanBound(String oceanBound) { this.oceanBound = oceanBound; }
        public String getServiceCode() { return serviceCode; }
        public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }
        public String getOriginPortId() { return originPortId; }
        public void setOriginPortId(String originPortId) { this.originPortId = originPortId; }
        public String getFinalPortId() { return finalPortId; }
        public void setFinalPortId(String finalPortId) { this.finalPortId = finalPortId; }
        public String getPolTerminal() { return polTerminal; }
        public void setPolTerminal(String polTerminal) { this.polTerminal = polTerminal; }
        public LocalDateTime getPolAt() { return polAt; }
        public void setPolAt(LocalDateTime polAt) { this.polAt = polAt; }
        public String getPodTerminal() { return podTerminal; }
        public void setPodTerminal(String podTerminal) { this.podTerminal = podTerminal; }
        public LocalDateTime getPodAt() { return podAt; }
        public void setPodAt(LocalDateTime podAt) { this.podAt = podAt; }
        public LocalDateTime getBookDate() { return bookDate; }
        public void setBookDate(LocalDateTime bookDate) { this.bookDate = bookDate; }
        public LocalDateTime getBookReady() { return bookReady; }
        public void setBookReady(LocalDateTime bookReady) { this.bookReady = bookReady; }
        public String getPreCarriage() { return preCarriage; }
        public void setPreCarriage(String preCarriage) { this.preCarriage = preCarriage; }
        public String getOnCarriage() { return onCarriage; }
        public void setOnCarriage(String onCarriage) { this.onCarriage = onCarriage; }
        public String getDocumentalAgencyId() { return documentalAgencyId; }
        public void setDocumentalAgencyId(String documentalAgencyId) { this.documentalAgencyId = documentalAgencyId; }
        public String getAgencyIssueId() { return agencyIssueId; }
        public void setAgencyIssueId(String agencyIssueId) { this.agencyIssueId = agencyIssueId; }
        public String getAgencypolid() { return agencypolid; }
        public void setAgencypolid(String agencypolid) { this.agencypolid = agencypolid; }
        public String getOriginPortDesc() { return originPortDesc; }
        public void setOriginPortDesc(String originPortDesc) { this.originPortDesc = originPortDesc; }
        public String getFinalPortDesc() { return finalPortDesc; }
        public void setFinalPortDesc(String finalPortDesc) { this.finalPortDesc = finalPortDesc; }
        public String getAgencyRefNo() { return agencyRefNo; }
        public void setAgencyRefNo(String agencyRefNo) { this.agencyRefNo = agencyRefNo; }
        public String getInTransitPort() { return inTransitPort; }
        public void setInTransitPort(String inTransitPort) { this.inTransitPort = inTransitPort; }
        public String getInTransitPortName() { return inTransitPortName; }
        public void setInTransitPortName(String inTransitPortName) { this.inTransitPortName = inTransitPortName; }
        public String getTransportServiceId() { return transportServiceId; }
        public void setTransportServiceId(String transportServiceId) { this.transportServiceId = transportServiceId; }
        public String getTransportTermId() { return transportTermId; }
        public void setTransportTermId(String transportTermId) { this.transportTermId = transportTermId; }
        public String getFirstSvcType() { return firstSvcType; }
        public void setFirstSvcType(String firstSvcType) { this.firstSvcType = firstSvcType; }
    }

    /**
     * DTO for a row from booking_cust (AP, BP). Used to populate editempbooking_cust from booking_cust per spec.
     * Columns: book_no, custTypeId, custId, countryId, stateId, name, address1, address2, POBox, city, zipCode,
     * telephone, fax, contact, email, carriercode, isedited, persontype, eori.
     */
    public static class BookingCustDetail {
        private String bookNo;
        private String custTypeId;
        private String custId;
        private String countryId;
        private String stateId;
        private String name;
        private String address1;
        private String address2;
        private String poBox;
        private String city;
        private String zipCode;
        private String telephone;
        private String fax;
        private String contact;
        private String email;
        private String carriercode;
        private String isedited;
        private String persontype;
        private String eori;

        public String getBookNo() { return bookNo; }
        public void setBookNo(String bookNo) { this.bookNo = bookNo; }
        public String getCustTypeId() { return custTypeId; }
        public void setCustTypeId(String custTypeId) { this.custTypeId = custTypeId; }
        public String getCustId() { return custId; }
        public void setCustId(String custId) { this.custId = custId; }
        public String getCountryId() { return countryId; }
        public void setCountryId(String countryId) { this.countryId = countryId; }
        public String getStateId() { return stateId; }
        public void setStateId(String stateId) { this.stateId = stateId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAddress1() { return address1; }
        public void setAddress1(String address1) { this.address1 = address1; }
        public String getAddress2() { return address2; }
        public void setAddress2(String address2) { this.address2 = address2; }
        public String getPoBox() { return poBox; }
        public void setPoBox(String poBox) { this.poBox = poBox; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        public String getTelephone() { return telephone; }
        public void setTelephone(String telephone) { this.telephone = telephone; }
        public String getFax() { return fax; }
        public void setFax(String fax) { this.fax = fax; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getCarriercode() { return carriercode; }
        public void setCarriercode(String carriercode) { this.carriercode = carriercode; }
        public String getIsedited() { return isedited; }
        public void setIsedited(String isedited) { this.isedited = isedited; }
        public String getPersontype() { return persontype; }
        public void setPersontype(String persontype) { this.persontype = persontype; }
        public String getEori() { return eori; }
        public void setEori(String eori) { this.eori = eori; }
    }
}
