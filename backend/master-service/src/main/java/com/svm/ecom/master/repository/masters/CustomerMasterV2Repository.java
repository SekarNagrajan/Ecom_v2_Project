package com.svm.ecom.master.repository.masters;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for Customer Master v2 (Masters DB).
 * Uses mastersPersistenceUnit for read-only native queries.
 */
@Repository
public class CustomerMasterV2Repository {

    /* SQL Server: use WITH (NOLOCK). Main customer + address only (no billing) so query works if billing columns missing. */
    private static final String SQL_ALL_ACTIVE = "SELECT CustCode, CustName, custposition, Phone, Email, custtype, dtofCreation, persontype, mobileno, EORI, fillingtype, CrUsr, CrDate, ChgUsr, ChgDate, Address1, Address2, City, ZipCode, CountryID "
            + "FROM customermaster WITH (NOLOCK) WHERE status = 'A' ORDER BY CustCode";

    /* Billing columns from same table; if table has no Bill* columns, this query will fail - service catches and skips billing. */
    private static final String SQL_ALL_BILLING = "SELECT CustCode, BillAddressee, BillPhone, billEmail, BillmobileNo, BillAddress1, BillAddress2, BillCity, BillZipCode, BillCountry "
            + "FROM customermaster WITH (NOLOCK) WHERE status = 'A' ORDER BY CustCode";

    private static final String SQL_ALL_CONTACTS = "SELECT custcode, ContactName, Email, Phone, Designation, mobile, contactType, webid "
            + "FROM customercontacts WITH (NOLOCK) WHERE status = 'A' AND contactType = 'Online' ORDER BY custcode";

    /* Table name per Excel: Customerfinalcialinformation (typo). If actual DB table is Customerfinancialinformation, change below. */
    private static final String SQL_ALL_FINANCIAL = "SELECT custcode, [Type], Currency, validityfrom, validityto, creditperiod, CreditLimit "
            + "FROM Customerfinalcialinformation WITH (NOLOCK) WHERE status = 'A' AND GETDATE() BETWEEN validityfrom AND validityto ORDER BY custcode";

    private static final String SQL_ALL_NOTIFICATIONS = "SELECT custcode, NotificationType, [Name], Email, Fax, Mobile "
            + "FROM CustomerNotifications WITH (NOLOCK) WHERE status = 'A' ORDER BY custcode";

    private static final String SQL_ALL_SALESREP = "SELECT s.customercode, s.SalesRepCode, s.ValidFrom, s.ValidTill, sr.[Name], sr.city "
            + "FROM CustomerSalesRep s WITH (NOLOCK) INNER JOIN salesrep sr WITH (NOLOCK) ON sr.[Code] = s.SalesRepCode AND sr.status = 'A' "
            + "WHERE s.status = 'A' ORDER BY s.customercode";

    private static final String SQL_ALL_ENS = "SELECT custcode, ensname, ensemail, enseori, ensdefaultdec "
            + "FROM customers_EnsDetails WITH (NOLOCK) WHERE status = 'A' ORDER BY custcode";

    @PersistenceContext(unitName = "mastersPersistenceUnit")
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<CustomerMasterRow> findAllActiveCustomers() {
        Query query = entityManager.createNativeQuery(SQL_ALL_ACTIVE);
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapRow).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<CustomerMasterRow> findAllActiveCustomersUpdatedAfter(Date updatedAtAfter) {
        String sql = "SELECT CustCode, CustName, custposition, Phone, Email, custtype, dtofCreation, persontype, mobileno, EORI, fillingtype, CrUsr, CrDate, ChgUsr, ChgDate, Address1, Address2, City, ZipCode, CountryID "
                + "FROM customermaster WITH (NOLOCK) "
                + "WHERE status = 'A' AND (ChgDate >= ? OR (ChgDate IS NULL AND CrDate >= ?)) "
                + "ORDER BY CustCode";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, updatedAtAfter);
        query.setParameter(2, updatedAtAfter);
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapRow).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<CustomerMasterRow> findActiveCustomersByCodes(List<String> custCodes) {
        if (custCodes == null || custCodes.isEmpty()) {
            return Collections.emptyList();
        }
        String inClause = custCodes.stream()
                .map(c -> "'" + c.replace("'", "''") + "'")
                .collect(Collectors.joining(","));
        String sql = "SELECT CustCode, CustName, custposition, Phone, Email, custtype, dtofCreation, "
                + "persontype, mobileno, EORI, fillingtype, CrUsr, CrDate, ChgUsr, ChgDate, "
                + "Address1, Address2, City, ZipCode, CountryID "
                + "FROM customermaster WITH (NOLOCK) "
                + "WHERE status = 'A' AND CustCode IN (" + inClause + ") ORDER BY CustCode";
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapRow).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<CustomerMasterRow> findActiveCustomersByCodesUpdatedAfter(List<String> custCodes, Date updatedAtAfter) {
        if (custCodes == null || custCodes.isEmpty()) {
            return Collections.emptyList();
        }
        String inClause = custCodes.stream()
                .map(c -> "'" + c.replace("'", "''") + "'")
                .collect(Collectors.joining(","));
        String sql = "SELECT CustCode, CustName, custposition, Phone, Email, custtype, dtofCreation, "
                + "persontype, mobileno, EORI, fillingtype, CrUsr, CrDate, ChgUsr, ChgDate, "
                + "Address1, Address2, City, ZipCode, CountryID "
                + "FROM customermaster WITH (NOLOCK) "
                + "WHERE status = 'A' AND CustCode IN (" + inClause + ") "
                + "AND (ChgDate >= ? OR (ChgDate IS NULL AND CrDate >= ?)) ORDER BY CustCode";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, updatedAtAfter);
        query.setParameter(2, updatedAtAfter);
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapRow).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<BillingRow> findAllBilling() {
        Query query = entityManager.createNativeQuery(SQL_ALL_BILLING);
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapBillingRow).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<CustomerContactRow> findAllActiveContacts() {
        Query query = entityManager.createNativeQuery(SQL_ALL_CONTACTS);
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapContactRow).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<FinancialInfoRow> findAllFinancialInfo() {
        Query query = entityManager.createNativeQuery(SQL_ALL_FINANCIAL);
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapFinancialRow).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<NotificationRow> findAllNotifications() {
        Query query = entityManager.createNativeQuery(SQL_ALL_NOTIFICATIONS);
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapNotificationRow).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<SalesRepRow> findAllSalesReps() {
        Query query = entityManager.createNativeQuery(SQL_ALL_SALESREP);
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapSalesRepRow).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<EnsRow> findAllEns() {
        Query query = entityManager.createNativeQuery(SQL_ALL_ENS);
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapEnsRow).collect(Collectors.toList());
    }

    private CustomerContactRow mapContactRow(Object[] row) {
        CustomerContactRow r = new CustomerContactRow();
        r.setCustcode(getString(row, 0));
        r.setContactName(getString(row, 1));
        r.setEmail(getString(row, 2));
        r.setPhone(getString(row, 3));
        r.setDesignation(getString(row, 4));
        r.setMobile(getString(row, 5));
        r.setContactType(getString(row, 6));
        r.setWebid(getString(row, 7));
        return r;
    }

    private CustomerMasterRow mapRow(Object[] row) {
        CustomerMasterRow r = new CustomerMasterRow();
        r.setCustCode(getString(row, 0));
        r.setCustName(getString(row, 1));
        r.setCustposition(getString(row, 2));
        r.setPhone(getString(row, 3));
        r.setEmail(getString(row, 4));
        r.setCusttype(getString(row, 5));
        r.setDtofCreation(getDate(row, 6));
        r.setPersontype(getString(row, 7));
        r.setMobileno(getString(row, 8));
        r.setEori(getString(row, 9));
        r.setFillingtype(getString(row, 10));
        r.setCrUsr(getString(row, 11));
        r.setCrDate(getDate(row, 12));
        r.setChgUsr(getString(row, 13));
        r.setChgDate(getDate(row, 14));
        r.setAddress1(getString(row, 15));
        r.setAddress2(getString(row, 16));
        r.setCity(getString(row, 17));
        r.setZipCode(getString(row, 18));
        r.setCountryId(getString(row, 19));
        return r;
    }

    private BillingRow mapBillingRow(Object[] row) {
        BillingRow r = new BillingRow();
        r.setCustCode(getString(row, 0));
        r.setBillAddressee(getString(row, 1));
        r.setBillPhone(getString(row, 2));
        r.setBillEmail(getString(row, 3));
        r.setBillmobileNo(getString(row, 4));
        r.setBillAddress1(getString(row, 5));
        r.setBillAddress2(getString(row, 6));
        r.setBillCity(getString(row, 7));
        r.setBillZipCode(getString(row, 8));
        r.setBillCountry(getString(row, 9));
        return r;
    }

    private FinancialInfoRow mapFinancialRow(Object[] row) {
        FinancialInfoRow r = new FinancialInfoRow();
        r.setCustcode(getString(row, 0));
        r.setType(getString(row, 1));
        r.setCurrency(getString(row, 2));
        r.setValidityfrom(getDate(row, 3));
        r.setValidityto(getDate(row, 4));
        r.setCreditperiod(getInt(row, 5));
        r.setCreditLimit(getDouble(row, 6));
        return r;
    }

    private NotificationRow mapNotificationRow(Object[] row) {
        NotificationRow r = new NotificationRow();
        r.setCustcode(getString(row, 0));
        r.setNotificationType(getString(row, 1));
        r.setName(getString(row, 2));
        r.setEmail(getString(row, 3));
        r.setFax(getString(row, 4));
        r.setMobile(getString(row, 5));
        return r;
    }

    private SalesRepRow mapSalesRepRow(Object[] row) {
        SalesRepRow r = new SalesRepRow();
        r.setCustcode(getString(row, 0));
        r.setSalesRepCode(getString(row, 1));
        r.setValidFrom(getDate(row, 2));
        r.setValidTill(getDate(row, 3));
        r.setName(getString(row, 4));
        r.setCity(getString(row, 5));
        return r;
    }

    private EnsRow mapEnsRow(Object[] row) {
        EnsRow r = new EnsRow();
        r.setCustcode(getString(row, 0));
        r.setEnsname(getString(row, 1));
        r.setEnsemail(getString(row, 2));
        r.setEnseori(getString(row, 3));
        r.setEnsdefaultdec(getBoolean(row, 4));
        return r;
    }

    private static String getString(Object[] row, int i) {
        if (row == null || i >= row.length || row[i] == null) return null;
        return String.valueOf(row[i]).trim();
    }

    private static java.util.Date getDate(Object[] row, int i) {
        if (row == null || i >= row.length || row[i] == null) return null;
        Object o = row[i];
        if (o instanceof java.util.Date) return (java.util.Date) o;
        if (o instanceof java.sql.Date) return new java.util.Date(((java.sql.Date) o).getTime());
        if (o instanceof java.sql.Timestamp) return new java.util.Date(((java.sql.Timestamp) o).getTime());
        return null;
    }

    private static Integer getInt(Object[] row, int i) {
        if (row == null || i >= row.length || row[i] == null) return null;
        Object o = row[i];
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(String.valueOf(o)); } catch (NumberFormatException e) { return null; }
    }

    private static Double getDouble(Object[] row, int i) {
        if (row == null || i >= row.length || row[i] == null) return null;
        Object o = row[i];
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); } catch (NumberFormatException e) { return null; }
    }

    private static Boolean getBoolean(Object[] row, int i) {
        if (row == null || i >= row.length || row[i] == null) return null;
        Object o = row[i];
        if (o instanceof Boolean) return (Boolean) o;
        String s = String.valueOf(o).trim();
        return "1".equals(s) || "Y".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s);
    }
}
