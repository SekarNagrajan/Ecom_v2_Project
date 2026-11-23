package com.svm.ecom.rates.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repository for contract data access using ReportDB
 */
@Repository
public class ContractRepository {

    private static final Logger logger = LoggerFactory.getLogger(ContractRepository.class);

    private final JdbcTemplate reportJdbcTemplate;

    public ContractRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Get all contracts for a specific customer
     */
    public List<Map<String, Object>> findContractsByCustomer(String customerCode) {
        String sql = "select distinct sm.contractNo,sm.ContractName,sm.custCode,sm.ValidFrom,sm.ValidTo,sd.amendmentNo,sd.effectivedate," +
                    "case when (sm.ValidTo<getdate()) then 'E' else sd.contractstatus end as status " +
                    "from svcratetmaster(Nolock) sm " +
                    "inner join svcratedetail(nolock) sd on sm.contractNo=sd.contractNo " +
                    "where sm.custcode=? and sd.quotestatus='A' and sd.contractstatus in ('A','D') and CAST(sd.effectivedate AS DATE) <= CAST(getdate() AS DATE) " +
                    "order by sd.effectivedate desc";
        
        return reportJdbcTemplate.queryForList(sql, customerCode);
    }

    /**
     * Get port pairs count for a specific contract
     */
    public Integer getPortPairsCount(String contractNo, Integer amendmentNo, LocalDate effectiveDate) {
        String sql = "select count(1) as portpaircount from (" +
                    "select loadport,dischport,eqptype,lkvalidfrm,lkvalidto from svcratedetail(Nolock) " +
                    "where contractNo=? and amendmentNo=? and lkvalidto>? and quotestatus='A' and contractstatus  in ('A','D') " +
                    ") t";
        
        try {
            Integer count = reportJdbcTemplate.queryForObject(sql, Integer.class, contractNo, amendmentNo, effectiveDate);
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.warn("Error getting port pairs count for contract {}: {}", contractNo, e.getMessage());
            return 0;
        }
    }

    /**
     * Get named account customer codes for a contract
     */
    public List<String> getNamedAccountCustomers(String contractNo, Integer amendmentNo) {
        String sql = "select custcode from svcratecustomer(nolock) " +
                    "where nac='Y' and contractNo=? and amendmentNo=?";
        
        return reportJdbcTemplate.queryForList(sql, String.class, contractNo, amendmentNo);
    }

    /**
     * Get specific contract by contractNo and amendmentNo for individual contract endpoint
     */
    public Map<String, Object> findContractByIdAndAmendment(String contractNo, Integer amendmentNo, String customerCode) {
        String sql = "select distinct sm.contractNo,sm.ContractName,sm.custCode,sm.ValidFrom,sm.ValidTo,sd.amendmentNo,sd.effectivedate," +
                    "case when (sm.ValidTo<getdate()) then 'E' else sd.contractstatus end as status " +
                    "from svcratetmaster(Nolock) sm " +
                    "inner join svcratedetail(nolock) sd on sm.contractNo=sd.contractNo " +
                    "where sd.quotestatus='A' and sd.contractstatus in ('A','D') and sm.custcode=? and sm.contractNo=? and sd.amendmentNo=? and CAST(sd.effectivedate AS DATE) <= CAST(getdate() AS DATE)";
        
        
        List<Map<String, Object>> results = reportJdbcTemplate.queryForList(sql, customerCode, contractNo, amendmentNo);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Get contract details for PDF download including carrierCode, shiptype, userup.
     * Returns the contract row if found and accessible by the customer, null otherwise.
     */
    public Map<String, Object> findContractForDownload(String contractNo, Integer amendmentNo, String customerCode) {
        String sql = "select distinct sm.carrierCode, sm.userup, sm.usrcr, sm.contractNo, sm.shiptype, "
                   + "sm.ContractName, sm.custCode, sm.ValidFrom, sm.ValidTo, "
                   + "sd.amendmentNo, sd.effectivedate, "
                   + "case when (sm.ValidTo < getdate()) then 'E' else sm.status end as status "
                   + "from svcratetmaster(Nolock) sm "
                   + "inner join svcratedetail(nolock) sd on sm.contractNo = sd.contractNo "
                   + "where sd.quotestatus = 'A' and sd.contractstatus  in ('A','D') "
                   + "and sm.custcode = ? and sm.contractNo = ? and sd.amendmentNo = ? "
                   + "and CAST(sd.effectivedate AS DATE) <= CAST(getdate() AS DATE)";

        List<Map<String, Object>> results = reportJdbcTemplate.queryForList(sql, customerCode, contractNo, amendmentNo);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Get Jasper report base URL from globalconfigproduct table.
     */
    public String getJasperBaseUrl() {
        String sql = "select pathvalue from globalconfigproduct(NOLOCK) where pathname = ?";
        try {
            return reportJdbcTemplate.queryForObject(sql, String.class, "blprintwsurl");
        } catch (Exception e) {
            logger.error("Failed to get Jasper base URL from globalconfigproduct: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get Jasper report path from rptserverinfo table.
     */
    public String getReportPath() {
        String sql = "select top 1 reportpath from rptserverinfo(NOLOCK) where locationcode = 'N/A' and agencycode = 'N/A'";
        try {
            return reportJdbcTemplate.queryForObject(sql, String.class);
        } catch (Exception e) {
            logger.error("Failed to get report path from rptserverinfo: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get user date format preference from userdetails table.
     */
    public String getDateFormat(String loginName) {
        String sql = "select dateformat from userdetails(NOLOCK) where loginname = ?";
        try {
            return reportJdbcTemplate.queryForObject(sql, String.class, loginName);
        } catch (Exception e) {
            logger.warn("Failed to get dateformat for loginname '{}': {}", loginName, e.getMessage());
            return null;
        }
    }

    /**
     * Get grptype from svcratedetail joined with srrUsrPortGroup for determining Jasper template ID.
     */
    public String getGrpType(String contractNo, Integer amendmentNo) {
        String sql = "SELECT TOP 1 ISNULL(pg.gtype, '') AS grptype "
                   + "FROM svcratedetail(NOLOCK) sd "
                   + "LEFT JOIN srrUsrPortGroup(NOLOCK) pg ON sd.grpcode = pg.pcgrp "
                   + "WHERE sd.contractNo = ? AND sd.amendmentNo = ?";
        try {
            return reportJdbcTemplate.queryForObject(sql, String.class, contractNo, amendmentNo);
        } catch (Exception e) {
            logger.warn("Failed to get grptype for contract {}, amendment {}: {}", contractNo, amendmentNo, e.getMessage());
            return null;
        }
    }

    /**
     * Get port pairs details for a specific contract with full rate and surcharge information.
     * Joins svcratefreedays and svcraterouting to retrieve transport service, service code,
     * free-day detention/storage surcharges and all extended port-pair fields.
     * sd.itemno is included so the service layer can correlate non-all-in surcharges per row.
     */
    public List<Map<String, Object>> getPortPairsDetailsWithStatus(String contractNo, Integer amendmentNo, LocalDate effectiveDate) {
        String sql = "select distinct " +
                    "sd.itemno, " +
                    "sd.loadport as origin, sd.pol, sd.pod, sd.dischport as delivery, sd.eqptype, " +
                    "sd.tservice as transportService, sr.svccode as serviceCode, " +
                    "sd.dg, sd.dgDetail, sd.oog, sd.oogDetail, sd.soc, sd.nor, " +
                    "sd.allin as ofr, sd.allinamt as oceanFreight, sd.amt as baseRate, " +
                    "sd.quantity, sd.currency, sd.remarks, sd.class as commodityClass, " +
                    "sd.lkvalidfrm, sd.lkvalidto, " +
                    "case when sd.lkvalidto < getdate() then 'E' else 'A' end as status, " +
                    "sf.Expdet as exportContainerRent, sf.Impdet as importContainerRent, " +
                    "sf.expdemu as exportTerminalStorage, sf.Impdemu as importTerminalStorage " +
                    "from svcratedetail sd (Nolock) " +
                    "inner join svcratefreedays sf (nolock) " +
                    "    on sd.contractno = sf.contractno and sd.itemno = sf.itemno and sd.amendmentNo = sf.amendmentNo " +
                    "inner join svcraterouting sr (nolock) " +
                    "    on sf.contractno = sr.contractno and sr.itemno = sf.itemno and sr.amendflag = sf.amendmentno " +
                    "where sd.contractNo = ? and sd.amendmentNo = ? and sd.lkvalidto > ? " +
                    "and sd.quotestatus = 'A' and sd.contractstatus in ('A','D')";

        return reportJdbcTemplate.queryForList(sql, contractNo, amendmentNo, effectiveDate);
    }

    /**
     * Get all non-all-in surcharges (allin='N') for a contract amendment.
     * Returns sd.itemno so the service layer can group them per port pair.
     * Used to compute totalFreight = oceanFreight + sum(surcharges) in EUR.
     */
    public List<Map<String, Object>> getSurchargesForContract(String contractNo, Integer amendmentNo, LocalDate effectiveDate) {
        String sql = "select sd.itemno, ss.amount, ss.currency " +
                    "from svcratedetail sd (Nolock) " +
                    "inner join svcratesurcharge(nolock) ss " +
                    "    on sd.contractno = ss.contractno and sd.itemno = ss.itemno and sd.amendmentNo = ss.amendflag " +
                    "where ss.allin = 'N' " +
                    "and sd.contractNo = ? and sd.amendmentNo = ? " +
                    "and sd.lkvalidto > ? and sd.quotestatus = 'A' and sd.contractstatus  in ('A','D')";

        try {
            return reportJdbcTemplate.queryForList(sql, contractNo, amendmentNo, effectiveDate);
        } catch (Exception e) {
            logger.warn("Failed to fetch surcharges for contract {}, amendment {}: {}", contractNo, amendmentNo, e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Get today's active Rate of Exchange (ROE) entries.
     * The roe column represents: 1 EUR = roe units of that currency.
     * EUR itself is expected to have roe = 1.
     */
    public List<Map<String, Object>> getCurrentRoeRates() {
        String sql = "select currencycode, roe from roe(nolock) " +
                    "where convert(varchar, roedate, 103) = convert(varchar, getdate(), 103) and status = 'A'";
        try {
            return reportJdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            logger.warn("Failed to fetch ROE rates: {}", e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

}