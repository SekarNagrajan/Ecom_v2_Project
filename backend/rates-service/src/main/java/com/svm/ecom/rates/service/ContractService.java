package com.svm.ecom.rates.service;

import com.svm.ecom.common.master.cache.CustomerCacheService;
import com.svm.ecom.rates.dto.*;
import com.svm.ecom.rates.repository.ContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ContractService {

    private static final Logger logger = LoggerFactory.getLogger(ContractService.class);

    private final ContractRepository contractRepository;
    private final CustomerCacheService customerCacheService;

    @Autowired
    public ContractService(ContractRepository contractRepository, CustomerCacheService customerCacheService) {
        this.contractRepository = contractRepository;
        this.customerCacheService = customerCacheService;
    }

    /**
     * Get all contracts for a customer
     */
    public List<CustomerContractListItem> getCustomerContracts(String customerCode) {
        List<Map<String, Object>> contractRows = contractRepository.findContractsByCustomer(customerCode);
        
        List<CustomerContractListItem> contracts = new ArrayList<>();
        
        for (Map<String, Object> row : contractRows) {
            try {
                CustomerContractListItem contract = mapToContractListItem(row);
                contracts.add(contract);
            } catch (Exception e) {
                logger.error("Error mapping contract row to DTO: {}", e.getMessage(), e);
            }
        }
        
        return contracts;
    }

    /**
     * Get detailed contract information by contract ID
     */
    public CustomerContract getCustomerContractById(String contractId, String customerCode) {
        List<Map<String, Object>> contractRows = contractRepository.findContractsByCustomer(customerCode);
        
        // Find the specific contract
        Map<String, Object> contractRow = contractRows.stream()
                .filter(row -> contractId.equals(row.get("contractNo")))
                .findFirst()
                .orElse(null);
        
        if (contractRow == null) {
            return null;
        }
        
        try {
            return mapToContractDetail(contractRow);
        } catch (Exception e) {
            logger.error("Error mapping contract detail for contractId {}: {}", contractId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get detailed contract information by contract ID and amendment number
     */
    public CustomerContract getCustomerContractByIdAndAmendment(String contractId, Integer amendmentNumber, String customerCode) {
        Map<String, Object> contractRow = contractRepository.findContractByIdAndAmendment(contractId, amendmentNumber, customerCode);
        
        if (contractRow == null) {
            return null;
        }
        
        try {
            return mapToContractDetailWithAmendment(contractRow);
        } catch (Exception e) {
            logger.error("Error mapping contract detail for contractId {}, amendmentNumber {}: {}", 
                        contractId, amendmentNumber, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Map database row to CustomerContractListItem
     */
    private CustomerContractListItem mapToContractListItem(Map<String, Object> row) {
        CustomerContractListItem contract = new CustomerContractListItem();
        
        String contractNo = (String) row.get("contractNo");
        Integer amendmentNo = convertToInteger(row.get("amendmentNo"));
        
        contract.setId(contractNo);
        contract.setName((String) row.get("ContractName"));
        contract.setAmendmentNumber(amendmentNo);
        
        // Agreement party
        String custCode = (String) row.get("custCode");
        contract.setAgreementParty(createContractCustomer(custCode));
        
        // Named accounts
        List<String> namedAccountCodes = contractRepository.getNamedAccountCustomers(contractNo, amendmentNo);
        contract.setNamedAccounts(namedAccountCodes.stream()
                .map(this::createContractCustomer)
                .collect(Collectors.toList()));
        
        // Dates
        contract.setValidFrom(convertToLocalDate(row.get("ValidFrom")));
        contract.setValidTo(convertToLocalDate(row.get("ValidTo")));
        contract.setEffectiveDate(convertToLocalDate(row.get("effectivedate")));
        
        // Status — use contractNo to distinguish Draft (D) from WorkInProgress (WIP-prefixed)
        contract.setStatus(ContractStatus.fromCode((String) row.get("status"), contractNo));
        
        // Port pairs count
        LocalDate effectiveDate = convertToLocalDate(row.get("effectivedate"));
        Integer portPairsCount = contractRepository.getPortPairsCount(contractNo, amendmentNo, effectiveDate);
        contract.setPortPairsCount(portPairsCount != null ? portPairsCount : 0);
        
        return contract;
    }

    /**
     * Map database row to CustomerContract (detailed view)
     */
    private CustomerContract mapToContractDetail(Map<String, Object> row) {
        CustomerContract contract = new CustomerContract();
        
        String contractNo = (String) row.get("contractNo");
        Integer amendmentNo = convertToInteger(row.get("amendmentNo"));
        
        contract.setId(contractNo);
        contract.setName((String) row.get("ContractName"));
        contract.setAmendmentNumber(amendmentNo);
        
        // Agreement party
        String custCode = (String) row.get("custCode");
        contract.setAgreementParty(createContractCustomer(custCode));
        
        // Named accounts
        List<String> namedAccountCodes = contractRepository.getNamedAccountCustomers(contractNo, amendmentNo);
        contract.setNamedAccounts(namedAccountCodes.stream()
                .map(this::createContractCustomer)
                .collect(Collectors.toList()));
        
        // Dates
        contract.setValidFrom(convertToLocalDate(row.get("ValidFrom")));
        contract.setValidTo(convertToLocalDate(row.get("ValidTo")));
        contract.setEffectiveDate(convertToLocalDate(row.get("effectivedate")));
        
        // Status — use contractNo to distinguish Draft (D) from WorkInProgress (WIP-prefixed)
        contract.setStatus(ContractStatus.fromCode((String) row.get("status"), contractNo));

        // Port pairs with totalFreight in EUR
        LocalDate effectiveDate = convertToLocalDate(row.get("effectivedate"));
        contract.setPortPairs(buildPortPairs(contractNo, amendmentNo, effectiveDate));

        return contract;
    }

    /**
     * Map database row to CustomerContract for specific amendment (detailed view with amendment parameter)
     */
    private CustomerContract mapToContractDetailWithAmendment(Map<String, Object> row) {
        CustomerContract contract = new CustomerContract();

        String contractNo = (String) row.get("contractNo");
        Integer amendmentNo = convertToInteger(row.get("amendmentNo"));

        contract.setId(contractNo);
        contract.setName((String) row.get("ContractName"));
        contract.setAmendmentNumber(amendmentNo);

        // Agreement party
        String custCode = (String) row.get("custCode");
        contract.setAgreementParty(createContractCustomer(custCode));

        // Named accounts
        List<String> namedAccountCodes = contractRepository.getNamedAccountCustomers(contractNo, amendmentNo);
        contract.setNamedAccounts(namedAccountCodes.stream()
                .map(this::createContractCustomer)
                .collect(Collectors.toList()));

        // Dates
        contract.setValidFrom(convertToLocalDate(row.get("ValidFrom")));
        contract.setValidTo(convertToLocalDate(row.get("ValidTo")));
        contract.setEffectiveDate(convertToLocalDate(row.get("effectivedate")));

        // Status — use contractNo to distinguish Draft (D) from WorkInProgress (WIP-prefixed)
        contract.setStatus(ContractStatus.fromCode((String) row.get("status"), contractNo));

        // Port pairs with totalFreight in EUR
        LocalDate effectiveDate = convertToLocalDate(row.get("effectivedate"));
        contract.setPortPairs(buildPortPairs(contractNo, amendmentNo, effectiveDate));

        return contract;
    }

    /**
     * Fetch port pair rows, surcharges (batch), and today's ROE rates, then build the
     * full list of CustomerContractPortPair objects with oceanFreight and totalFreight in EUR.
     */
    private List<CustomerContractPortPair> buildPortPairs(String contractNo, Integer amendmentNo, LocalDate effectiveDate) {
        List<Map<String, Object>> portPairsRows =
                contractRepository.getPortPairsDetailsWithStatus(contractNo, amendmentNo, effectiveDate);

        // All surcharges for the contract fetched in one query, then grouped by itemno in memory
        List<Map<String, Object>> surchargeRows =
                contractRepository.getSurchargesForContract(contractNo, amendmentNo, effectiveDate);
        Map<String, List<Map<String, Object>>> surchargesByItem = groupSurchargesByItemNo(surchargeRows);

        // ROE fetched once and reused for every port pair
        Map<String, BigDecimal> roeMap = buildRoeMap(contractRepository.getCurrentRoeRates());

        return portPairsRows.stream()
                .map(r -> mapToPortPairFromDetailedRow(r, surchargesByItem, roeMap))
                .collect(Collectors.toList());
    }

    /**
     * Groups surcharge rows by itemno (String key) for O(1) lookup during port pair mapping.
     */
    private Map<String, List<Map<String, Object>>> groupSurchargesByItemNo(List<Map<String, Object>> surchargeRows) {
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        for (Map<String, Object> row : surchargeRows) {
            String key = String.valueOf(row.get("itemno"));
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        return map;
    }

    /**
     * Create ContractCustomer from customer code using cache
     */
    private ContractCustomer createContractCustomer(String customerCode) {
        ContractCustomer customer = new ContractCustomer();
        customer.setCode(customerCode);
        
        // Get customer name from cache
        String customerName = customerCacheService.getCustomerName(customerCode);
        customer.setName(customerName != null ? customerName : "Unknown Customer");
        
        return customer;
    }

    /**
     * Map a port pair DB row to CustomerContractPortPair.
     * oceanFreight is converted to EUR via the ROE map.
     * totalFreight (EUR) = oceanFreight (EUR) + sum of non-all-in surcharges (EUR).
     * currency field is always returned as "EUR".
     */
    private CustomerContractPortPair mapToPortPairFromDetailedRow(
            Map<String, Object> row,
            Map<String, List<Map<String, Object>>> surchargesByItem,
            Map<String, BigDecimal> roeMap) {

        CustomerContractPortPair portPair = new CustomerContractPortPair();

        portPair.setOrigin(convertToString(row.get("origin")));
        portPair.setPol(convertToString(row.get("pol")));
        portPair.setPod(convertToString(row.get("pod")));
        portPair.setDelivery(convertToString(row.get("delivery")));
        portPair.setEquipmentType(convertToString(row.get("eqptype")));
        portPair.setTransportService(TransportService.fromCode(convertToString(row.get("transportService"))));
        portPair.setServiceCode(convertToString(row.get("serviceCode")));

        portPair.setIsDG(convertToBoolean(row.get("dg")));
        portPair.setDgDetails(convertToString(row.get("dgDetail")));
        portPair.setIsOOG(convertToBoolean(row.get("oog")));
        portPair.setOogDetails(convertToString(row.get("oogDetail")));
        portPair.setIsSOC(convertToBoolean(row.get("soc")));
        portPair.setIsNOR(convertToBoolean(row.get("nor")));

        portPair.setOfr(OfrType.fromCode(convertToString(row.get("ofr"))));

        BigDecimal oceanFreightRaw  = convertToBigDecimal(row.get("oceanFreight"));
        String     originalCurrency = convertToString(row.get("currency"));

        portPair.setOceanFreight(convertToEur(oceanFreightRaw, originalCurrency, roeMap));
        portPair.setBaseRate(convertToEur(convertToBigDecimal(row.get("baseRate")), originalCurrency, roeMap));
        portPair.setQuantity(convertToBigDecimal(row.get("quantity")));
        portPair.setCurrency("EUR");
        portPair.setRemarks(convertToString(row.get("remarks")));
        portPair.setCommodityClass(convertToString(row.get("commodityClass")));

        portPair.setValidFrom(convertToLocalDate(row.get("lkvalidfrm")));
        portPair.setValidTo(convertToLocalDate(row.get("lkvalidto")));
        portPair.setStatus(ContractStatus.fromCode(convertToString(row.get("status"))));

        portPair.setExportContainerRent(convertToInteger(row.get("exportContainerRent")));
        portPair.setImportContainerRent(convertToInteger(row.get("importContainerRent")));
        portPair.setExportTerminalStorage(convertToInteger(row.get("exportTerminalStorage")));
        portPair.setImportTerminalStorage(convertToInteger(row.get("importTerminalStorage")));

        String itemNoKey = String.valueOf(row.get("itemno"));
        List<Map<String, Object>> surcharges = surchargesByItem.getOrDefault(itemNoKey, java.util.Collections.emptyList());
        portPair.setTotalFreight(calculateTotalFreight(oceanFreightRaw, originalCurrency, surcharges, roeMap));

        return portPair;
    }

    // -------------------------------------------------------------------------
    // Currency conversion & totalFreight helpers
    // -------------------------------------------------------------------------


    /**
     * Builds a currency-to-ROE map from ROE DB rows.
     * Convention (USD-based): 1 USD = roe[currency] units of that currency.
     *   e.g. USD=1.0000, EUR=0.8666, CNY=7.0000
     */
    private Map<String, BigDecimal> buildRoeMap(List<Map<String, Object>> roeRows) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Map<String, Object> row : roeRows) {
            String code = convertToString(row.get("currencycode"));
            BigDecimal rate = convertToBigDecimal(row.get("roe"));
            if (code != null && rate != null && rate.compareTo(BigDecimal.ZERO) > 0) {
                map.put(code.toUpperCase(), rate);
            }
        }
        return map;
    }

    /**
     * Converts an amount from the given currency to EUR using a two-step USD-pivot:
     *
     *   Step 1 — source → USD : usdAmount  = amount / roe[sourceCurrency]
     *   Step 2 — USD   → EUR  : eurAmount  = usdAmount × roe["EUR"]
     *   Combined               : EUR = (amount / roe[sourceCurrency]) × roe["EUR"]
     *
     * Example (ROE: USD=1.0, EUR=0.8666, CNY=7.0):
     *   700 CNY → (700 / 7.0) × 0.8666 = 86.66 EUR
     *   100 USD → (100 / 1.0) × 0.8666 = 86.66 EUR
     *
     * Returns null if amount is null or a required rate is unavailable/zero.
     */
    private BigDecimal convertToEur(BigDecimal amount, String currency, Map<String, BigDecimal> roeMap) {
        if (amount == null) return null;
        if (currency == null || "EUR".equalsIgnoreCase(currency)) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal sourceRoe = roeMap.get(currency.toUpperCase());
        if (sourceRoe == null || sourceRoe.compareTo(BigDecimal.ZERO) == 0) {
            logger.warn("ROE not found for source currency '{}', skipping conversion", currency);
            return null;
        }

        BigDecimal eurRoe = roeMap.get("EUR");
        if (eurRoe == null || eurRoe.compareTo(BigDecimal.ZERO) == 0) {
            logger.warn("ROE not found for EUR, skipping conversion");
            return null;
        }

        // Step 1: source → USD
        BigDecimal usdAmount = amount.divide(sourceRoe, 10, RoundingMode.HALF_UP);
        // Step 2: USD → EUR
        return usdAmount.multiply(eurRoe).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates totalFreight in EUR for one port pair item:
     *   totalFreight = convertToEur(oceanFreight) + sum(convertToEur(surcharge.amount) for each surcharge row)
     */
    private BigDecimal calculateTotalFreight(BigDecimal oceanFreight, String oceanFreightCurrency,
                                              List<Map<String, Object>> surcharges,
                                              Map<String, BigDecimal> roeMap) {
        BigDecimal total = BigDecimal.ZERO;

        BigDecimal oceanEur = convertToEur(oceanFreight, oceanFreightCurrency, roeMap);
        if (oceanEur != null) {
            total = total.add(oceanEur);
        }

        for (Map<String, Object> surcharge : surcharges) {
            BigDecimal surchargeAmt = convertToBigDecimal(surcharge.get("amount"));
            String surchargeCurrency = (String) surcharge.get("currency");
            BigDecimal surchargeEur = convertToEur(surchargeAmt, surchargeCurrency, roeMap);
            if (surchargeEur != null) {
                total = total.add(surchargeEur);
            }
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    // -------------------------------------------------------------------------

    /**
     * Safely converts any DB value to String, returning null for null input.
     * Prevents ClassCastException when a column is numeric but used as a string field.
     */
    private String convertToString(Object val) {
        if (val == null) return null;
        if (val instanceof String) return (String) val;
        return val.toString();
    }

    /**
     * Convert DB flag value ('Y'/'N', 1/0, Boolean) to Boolean.
     */
    private Boolean convertToBoolean(Object val) {
        if (val == null) return null;
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof Number) return ((Number) val).intValue() != 0;
        String s = val.toString().trim();
        return "Y".equalsIgnoreCase(s) || "1".equals(s) || "true".equalsIgnoreCase(s);
    }

    /**
     * Convert DB numeric value to BigDecimal safely.
     */
    private java.math.BigDecimal convertToBigDecimal(Object val) {
        if (val == null) return null;
        if (val instanceof java.math.BigDecimal) return (java.math.BigDecimal) val;
        if (val instanceof Number) return new java.math.BigDecimal(val.toString());
        try {
            return new java.math.BigDecimal(val.toString().trim());
        } catch (NumberFormatException e) {
            logger.warn("Cannot convert '{}' to BigDecimal", val);
            return null;
        }
    }

    /**
     * Convert database date object to LocalDate
     */
    private LocalDate convertToLocalDate(Object dateObj) {
        if (dateObj == null) {
            return null;
        }
        
        if (dateObj instanceof java.sql.Date) {
            return ((java.sql.Date) dateObj).toLocalDate();
        } else if (dateObj instanceof java.util.Date) {
            return ((java.util.Date) dateObj).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        } else if (dateObj instanceof java.time.LocalDate) {
            return (LocalDate) dateObj;
        }
        
        logger.warn("Unexpected date type: {}", dateObj.getClass());
        return null;
    }

    /**
     * Convert database object to Integer safely
     */
    private Integer convertToInteger(Object intObj) {
        if (intObj == null) {
            return null;
        }
        
        if (intObj instanceof Integer) {
            return (Integer) intObj;
        } else if (intObj instanceof Number) {
            return ((Number) intObj).intValue();
        } else if (intObj instanceof String) {
            try {
                return Integer.parseInt((String) intObj);
            } catch (NumberFormatException e) {
                logger.warn("Cannot convert string '{}' to Integer", intObj);
                return null;
            }
        }
        
        logger.warn("Unexpected integer type: {}", intObj.getClass());
        return null;
    }
}
