package com.svm.ecom.master.service;

import com.svm.ecom.common.enums.CustomerType;
import com.svm.ecom.common.enums.EnsFillingType;
import com.svm.ecom.master.dto.v2.*;
import com.svm.ecom.master.enums.CustomerStatus;
import com.svm.ecom.master.repository.masters.BillingRow;
import com.svm.ecom.master.repository.masters.CustomerContactRow;
import com.svm.ecom.master.repository.masters.CustomerMasterRow;
import com.svm.ecom.master.repository.masters.CustomerMasterV2Repository;
import com.svm.ecom.master.repository.CustomerSearchRepository;
import com.svm.ecom.master.repository.masters.EnsRow;
import com.svm.ecom.master.repository.masters.FinancialInfoRow;
import com.svm.ecom.master.repository.masters.NotificationRow;
import com.svm.ecom.master.repository.masters.SalesRepRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for Customer Master v2 (Masters DB).
 * Loads all Excel tabs: Master (+ address, billing), Contacts, Financial Information, Notifications, Sales Rep, ENS.
 */
@Service
public class CustomerMasterV2Service {

    private static final Logger logger = LoggerFactory.getLogger(CustomerMasterV2Service.class);
    /** ISO 8601 date-time for API (e.g. 2025-09-10T00:00:00Z). Used for date-only DB fields. */
    private static final SimpleDateFormat DATE_FORMAT;
    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'");
        DATE_FORMAT.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
    }

    @Autowired
    private CustomerMasterV2Repository customerMasterV2Repository;

    @Autowired
    private CustomerSearchRepository customerSearchRepository;

    @Autowired
    private CustomerMasterV2LoadHelper loadHelper;

    @Transactional(readOnly = true, transactionManager = "mastersTransactionManager")
    public List<CustomerResponse> findAllCustomers() {
        return findAllCustomers(null);
    }

    public List<CustomerResponse> findAllCustomers(LocalDateTime updatedAtAfter) {
        logger.debug("Fetching all customer data from Masters DB (master, contacts, financial, notifications, salesrep, ens), updatedAtAfter: {}", updatedAtAfter);
        List<CustomerMasterRow> rows;
        if (updatedAtAfter != null) {
            Date filterDate = Date.from(updatedAtAfter.atZone(ZoneId.systemDefault()).toInstant());
            rows = customerMasterV2Repository.findAllActiveCustomersUpdatedAfter(filterDate);
        } else {
            rows = customerMasterV2Repository.findAllActiveCustomers();
        }
        return buildCustomerResponses(rows);
    }

    /**
     * Returns customer info only for customers previously used in transactions
     * by the given login customer code.
     */
    @Transactional(readOnly = true, transactionManager = "mastersTransactionManager")
    public List<CustomerResponse> findCustomersByLoginCustomer(String loginCustomerCode) {
        return findCustomersByLoginCustomer(loginCustomerCode, null);
    }

    /**
     * Returns customer info only for customers previously used in transactions
     * by the given login customer code, optionally filtered by update date.
     */
    @Transactional(readOnly = true, transactionManager = "mastersTransactionManager")
    public List<CustomerResponse> findCustomersByLoginCustomer(String loginCustomerCode, LocalDateTime updatedAtAfter) {
        logger.info("Fetching previously used customer codes for login customer: {}, updatedAtAfter: {}", loginCustomerCode, updatedAtAfter);

        List<String> usedCustCodes = customerSearchRepository.findPreviouslyUsedCustomerCodes(loginCustomerCode);
        if (usedCustCodes.isEmpty()) {
            logger.info("No previously used customers found for login customer: {}", loginCustomerCode);
            return Collections.emptyList();
        }

        logger.info("Found {} previously used customer codes, loading customer info from Masters DB", usedCustCodes.size());
        List<CustomerMasterRow> rows;
        if (updatedAtAfter != null) {
            Date filterDate = Date.from(updatedAtAfter.atZone(ZoneId.systemDefault()).toInstant());
            rows = customerMasterV2Repository.findActiveCustomersByCodesUpdatedAfter(usedCustCodes, filterDate);
        } else {
            rows = customerMasterV2Repository.findActiveCustomersByCodes(usedCustCodes);
        }
        return buildCustomerResponses(rows);
    }

    private List<CustomerResponse> buildCustomerResponses(List<CustomerMasterRow> rows) {
        if (rows.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, BillingDto> billingByCustCode = loadBillingSafe();
        Map<String, List<CustomerContactDto>> contactsByCustCode = loadContactsSafe();
        Map<String, List<FinancialInfoDto>> financialByCustCode = loadFinancialSafe();
        Map<String, List<NotificationDto>> notificationsByCustCode = loadNotificationsSafe();
        Map<String, List<SalesRepDto>> salesRepByCustCode = loadSalesRepSafe();
        Map<String, List<EnsDto>> ensByCustCode = loadEnsSafe();

        return rows.stream()
                .map(row -> toCustomerResponse(
                        row,
                        billingByCustCode.get(normalizeKey(row.getCustCode())),
                        contactsByCustCode.getOrDefault(normalizeKey(row.getCustCode()), Collections.emptyList()),
                        financialByCustCode.getOrDefault(normalizeKey(row.getCustCode()), Collections.emptyList()),
                        notificationsByCustCode.getOrDefault(normalizeKey(row.getCustCode()), Collections.emptyList()),
                        salesRepByCustCode.getOrDefault(normalizeKey(row.getCustCode()), Collections.emptyList()),
                        ensByCustCode.getOrDefault(normalizeKey(row.getCustCode()), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    private Map<String, BillingDto> loadBillingSafe() {
        try {
            List<BillingRow> list = loadHelper.loadBilling();
            return list.stream().filter(b -> hasText(b.getCustCode()))
                    .collect(Collectors.toMap(b -> b.getCustCode().trim(), this::toBillingDto, (a, b) -> a));
        } catch (Exception e) {
            logger.warn("Could not load billing from Masters DB: {} - billing will be null for all customers", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Map<String, List<CustomerContactDto>> loadContactsSafe() {
        try {
            return groupContactsByCustCode(loadHelper.loadContacts());
        } catch (Exception e) {
            logger.warn("Could not load contacts from Masters DB: {} - contacts will be empty", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Map<String, List<FinancialInfoDto>> loadFinancialSafe() {
        try {
            return groupFinancialByCustCode(loadHelper.loadFinancial());
        } catch (Exception e) {
            logger.warn("Could not load financial info from Masters DB: {} - financialInformation will be empty", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Map<String, List<NotificationDto>> loadNotificationsSafe() {
        try {
            return groupNotificationsByCustCode(loadHelper.loadNotifications());
        } catch (Exception e) {
            logger.warn("Could not load notifications from Masters DB: {} - notifications will be empty", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Map<String, List<SalesRepDto>> loadSalesRepSafe() {
        try {
            return groupSalesRepByCustCode(loadHelper.loadSalesReps());
        } catch (Exception e) {
            logger.warn("Could not load sales rep from Masters DB: {} - salesRep will be empty", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Map<String, List<EnsDto>> loadEnsSafe() {
        try {
            return groupEnsByCustCode(loadHelper.loadEns());
        } catch (Exception e) {
            logger.warn("Could not load ENS from Masters DB: {} - ens will be empty", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private CustomerResponse toCustomerResponse(CustomerMasterRow row,
                                                BillingDto billing,
                                                List<CustomerContactDto> contacts,
                                                List<FinancialInfoDto> financialInformation,
                                                List<NotificationDto> notifications,
                                                List<SalesRepDto> salesRep,
                                                List<EnsDto> ens) {
        CustomerResponse r = new CustomerResponse();
        r.setCustomerCode(emptyToNull(row.getCustCode()));
        r.setCustomerName(emptyToNull(row.getCustName()));
        r.setStatus(CustomerStatus.fromCode(row.getCustposition()));
        r.setAddress(toAddressDto(row));
        r.setTelephone(emptyToNull(row.getPhone()));
        r.setEmail(emptyToNull(row.getEmail()));
        r.setCustomerTypes(toCustomerTypes(row.getCusttype()));
        r.setActiveFrom(formatDate(row.getDtofCreation()));
        r.setTypeOfPerson(emptyToNull(row.getPersontype()));
        r.setMobile(emptyToNull(row.getMobileno()));
        r.setEori(emptyToNull(row.getEori()));
        r.setEnsFilingType(parseEnsFilingType(row.getFillingtype()));
        r.setCreatedUser(emptyToNull(row.getCrUsr()));
        r.setCreatedDate(formatDate(row.getCrDate()));
        r.setUpdatedUser(emptyToNull(row.getChgUsr()));
        r.setUpdatedDate(formatDate(row.getChgDate()));
        r.setBilling(billing);
        r.setFinancialInformation(financialInformation != null ? financialInformation : Collections.emptyList());
        r.setNotifications(notifications != null ? notifications : Collections.emptyList());
        r.setContacts(contacts != null ? contacts : Collections.emptyList());
        r.setSalesRep(salesRep != null ? salesRep : Collections.emptyList());
        r.setEns(ens != null ? ens : Collections.emptyList());
        return r;
    }

    private BillingDto toBillingDto(BillingRow row) {
        BillingDto b = new BillingDto();
        b.setName(emptyToNull(row.getBillAddressee()));
        b.setPhone(emptyToNull(row.getBillPhone()));
        b.setEmail(emptyToNull(row.getBillEmail()));
        b.setMobile(emptyToNull(row.getBillmobileNo()));
        List<String> lines = new ArrayList<>();
        if (hasText(row.getBillAddress1())) lines.add(row.getBillAddress1());
        if (hasText(row.getBillAddress2())) lines.add(row.getBillAddress2());
        b.setLines(lines.isEmpty() ? null : lines);
        b.setLocality(emptyToNull(row.getBillCity()));
        b.setPostalCode(emptyToNull(row.getBillZipCode()));
        b.setCountryCode(emptyToNull(row.getBillCountry()));
        return b;
    }

    private Map<String, List<CustomerContactDto>> groupContactsByCustCode(List<CustomerContactRow> contactRows) {
        return contactRows.stream()
                .filter(c -> hasText(c.getCustcode()))
                .collect(Collectors.groupingBy(
                        c -> c.getCustcode().trim(),
                        Collectors.mapping(this::toContactDto, Collectors.toList())
                ));
    }

    private Map<String, List<FinancialInfoDto>> groupFinancialByCustCode(List<FinancialInfoRow> rows) {
        return rows.stream()
                .filter(f -> hasText(f.getCustcode()))
                .collect(Collectors.groupingBy(
                        f -> f.getCustcode().trim(),
                        Collectors.mapping(this::toFinancialDto, Collectors.toList())
                ));
    }

    private Map<String, List<NotificationDto>> groupNotificationsByCustCode(List<NotificationRow> rows) {
        return rows.stream()
                .filter(n -> hasText(n.getCustcode()))
                .collect(Collectors.groupingBy(
                        n -> n.getCustcode().trim(),
                        Collectors.mapping(this::toNotificationDto, Collectors.toList())
                ));
    }

    private Map<String, List<SalesRepDto>> groupSalesRepByCustCode(List<SalesRepRow> rows) {
        return rows.stream()
                .filter(s -> hasText(s.getCustcode()))
                .collect(Collectors.groupingBy(
                        s -> s.getCustcode().trim(),
                        Collectors.mapping(this::toSalesRepDto, Collectors.toList())
                ));
    }

    private Map<String, List<EnsDto>> groupEnsByCustCode(List<EnsRow> rows) {
        return rows.stream()
                .filter(e -> hasText(e.getCustcode()))
                .collect(Collectors.groupingBy(
                        e -> e.getCustcode().trim(),
                        Collectors.mapping(this::toEnsDto, Collectors.toList())
                ));
    }

    private CustomerContactDto toContactDto(CustomerContactRow row) {
        CustomerContactDto dto = new CustomerContactDto();
        dto.setId(emptyToNull(row.getWebid()));
        dto.setName(emptyToNull(row.getContactName()));
        dto.setPhone(emptyToNull(row.getPhone()) != null ? emptyToNull(row.getPhone()) : emptyToNull(row.getMobile()));
        dto.setEmail(emptyToNull(row.getEmail()));
        dto.setDesignation(emptyToNull(row.getDesignation()));
        dto.setContactType(emptyToNull(row.getContactType()));
        return dto;
    }

    private FinancialInfoDto toFinancialDto(FinancialInfoRow row) {
        FinancialInfoDto dto = new FinancialInfoDto();
        dto.setCreditType(emptyToNull(row.getType()));
        dto.setCurrency(emptyToNull(row.getCurrency()));
        dto.setValidFrom(row.getValidityfrom());
        dto.setValidTo(row.getValidityto());
        dto.setCreditDays(row.getCreditperiod());
        dto.setCreditLimit(row.getCreditLimit());
        return dto;
    }

    private NotificationDto toNotificationDto(NotificationRow row) {
        NotificationDto dto = new NotificationDto();
        dto.setNotificationType(emptyToNull(row.getNotificationType()));
        dto.setName(emptyToNull(row.getName()));
        dto.setEmail(emptyToNull(row.getEmail()));
        dto.setFax(emptyToNull(row.getFax()));
        dto.setMobile(emptyToNull(row.getMobile()));
        return dto;
    }

    private SalesRepDto toSalesRepDto(SalesRepRow row) {
        SalesRepDto dto = new SalesRepDto();
        dto.setCode(emptyToNull(row.getSalesRepCode()));
        dto.setName(emptyToNull(row.getName()));
        dto.setCity(emptyToNull(row.getCity()));
        dto.setActiveFrom(row.getValidFrom());
        dto.setActiveTill(row.getValidTill());
        return dto;
    }

    private EnsDto toEnsDto(EnsRow row) {
        EnsDto dto = new EnsDto();
        dto.setDeclarantName(emptyToNull(row.getEnsname()));
        dto.setEmail(emptyToNull(row.getEnsemail()));
        dto.setEoriId(emptyToNull(row.getEnseori()));
        dto.setDefaultDeclarant(row.getEnsdefaultdec());
        return dto;
    }

    private AddressDto toAddressDto(CustomerMasterRow row) {
        AddressDto a = new AddressDto();
        List<String> lines = new ArrayList<>();
        if (hasText(row.getAddress1())) lines.add(row.getAddress1());
        if (hasText(row.getAddress2())) lines.add(row.getAddress2());
        a.setLines(lines.isEmpty() ? Collections.singletonList("") : lines);
        a.setLocality(emptyToNull(row.getCity()));
        a.setPostalCode(emptyToNull(row.getZipCode()));
        a.setCountryCode(emptyToNull(row.getCountryId()));
        return a;
    }

    private static List<CustomerType> toCustomerTypes(String custtype) {
        if (custtype == null || custtype.trim().isEmpty()) return Collections.emptyList();
        return Arrays.stream(custtype.split("-"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(token -> {
                    try {
                        return CustomerType.fromValue(token);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(type -> type != null)
                .collect(Collectors.toList());
    }

    private static EnsFillingType parseEnsFilingType(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return EnsFillingType.fromValue(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String formatDate(java.util.Date date) {
        if (date == null) return null;
        synchronized (DATE_FORMAT) {
            return DATE_FORMAT.format(date);
        }
    }

    private static String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String normalizeKey(String s) {
        return (s == null || s.trim().isEmpty()) ? "" : s.trim();
    }
}
