package com.solverminds.summary.repo;

import com.solverminds.summary.dto.DashboardSummaryDto;
import com.solverminds.summary.dto.ArrivalNoticeDto;
import com.solverminds.summary.dto.DeliveryOrderDto;
import com.solverminds.summary.dto.CustomerReleaseOrderDto;
import com.solverminds.summary.mapper.DashboardSummaryMapper;
import com.solverminds.summary.mapper.ArrivalNoticeMapper;
import com.solverminds.summary.mapper.DeliveryOrderMapper;
import com.solverminds.summary.mapper.CroMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public class SummaryRepository {

    private final JdbcTemplate reportJdbcTemplate;
    private final JdbcTemplate documentationJdbcTemplate;

    /**
     * Constructor using common JDBC templates from common-security module.
     * Uses reportJdbcTemplate and documentationJdbcTemplate beans configured in common-security.
     */
    public SummaryRepository(
            @Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate,
            @Qualifier("documentationJdbcTemplate") JdbcTemplate documentationJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
        this.documentationJdbcTemplate = documentationJdbcTemplate;
    }

    private void ensureReportDb() {
        if (reportJdbcTemplate == null) {
            throw new IllegalStateException("reportdb is not configured");
        }
    }

    private void ensureDocumentationDb() {
        if (documentationJdbcTemplate == null) {
            throw new IllegalStateException("documentationdb is not configured");
        }
    }

    // Dashboard
    public List<DashboardSummaryDto> dashboard(String customerCode) {
        ensureReportDb();
        final String sql = "exec usp_getdashboardecomboookingsummary ?, '', '', 'N', ''";
        return reportJdbcTemplate.query(sql, new DashboardSummaryMapper(), customerCode);
    }

    // CRO
    public List<CustomerReleaseOrderDto> cro(String customerCode, LocalDate from, LocalDate to) {
        ensureDocumentationDb();
        final String sql = "exec usp_ecomcrodetails ?, ?, ?, '', ''";
        return documentationJdbcTemplate.query(sql, new CroMapper(), customerCode, Date.valueOf(from), Date.valueOf(to));
    }

    // Arrival Notice
    public List<ArrivalNoticeDto> arrivalNotice(String customerCode, LocalDate from, LocalDate to) {
        ensureReportDb();
        final String sql = "select distinct hcust.custcode, ar.blnumber, ar.arrivalnoticeno, " +
                   "h.orginport as originPortId, h.deliveryport as finalPortId, " +
                   "ar.vesselcode, ar.voyage, ar.bound, ar.hblnumber, " +
                   "al.arrdate, h.oprterminal as podterminal, ar.ecomprintstatus " +
            "from reportcargoarrival ar (nolock) " +
            "inner join allschedules al (nolock) on ar.groupcallid=al.groupcallid " +
                   "and ar.bound=al.bound and ar.servicecode=al.service " +
                   "and ar.vesselcode=al.vessel and ar.voyage=al.voyage " +
                   "and ar.terminalcode=al.terminal " +
            "inner join houseblmaster h (nolock) on h.blnumber=ar.blnumber " +
            "inner join houseblcustomers hcust (nolock) on h.pkid=hcust.fkid " +
            "where (hcust.custtype='NT' or hcust.custtype='CN') " +
                  "and ar.status='A' " +
                  "and hcust.custcode=? " +
                  "and al.arrdate between ? and ?";
        return reportJdbcTemplate.query(sql, new ArrivalNoticeMapper(), 
                                      customerCode, Date.valueOf(from), Date.valueOf(to));
    }

    // Delivery Order
    public List<DeliveryOrderDto> deliveryOrder(String customerCode, LocalDate from, LocalDate to) {
        ensureReportDb();
        final String sql = "select distinct delordmaster.printstatus,delordmaster.ecomprintstatus,delordmaster.delordno,delordmaster.dovaliditydate," +
                          "delordmaster.delorddate, delordcntrdetail.blnumber,delordcntrdetail.vessel,delordcntrdetail.voyage,delordcntrdetail.bound," +
                          "houseblmaster.loadport, houseblmaster.dischargeport,delordcntrdetail.terminal,allschedules.arrdate " +
                          "from delordmaster " +
                          "inner join delordcntrdetail on delordmaster.delordno=delordcntrdetail.delordno " +
                          "inner join houseblmaster on delordcntrdetail.blnumber=houseblmaster.blnumber " +
                          "inner join houseblcustomers on houseblcustomers.hblnumber=houseblmaster.hblnumber " +
                          "inner join houseblcargo on houseblmaster.pkid=houseblcargo.fkid and cargotype='local' and delordcntrdetail.cntrno=houseblcargo.cntrno " +
                          "inner join allschedules on allschedules.service=delordcntrdetail.service and allschedules.vessel=delordcntrdetail.vessel and allschedules.voyage=delordcntrdetail.voyage and " +
                          "allschedules.bound=delordcntrdetail.bound and allschedules.port=houseblmaster.dischargeport " +
                          "where delordmaster.status='A' and (houseblcustomers.custtype='NT' or houseblcustomers.custtype='CN') and delordcntrdetail.custcode=houseblcustomers.custcode and " +
                          "delordcntrdetail.custcode=? and delordmaster.delorddate between ? and ?";
        return reportJdbcTemplate.query(sql, new DeliveryOrderMapper(), customerCode, Date.valueOf(from), Date.valueOf(to));
    }
}
