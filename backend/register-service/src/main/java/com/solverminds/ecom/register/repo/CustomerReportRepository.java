package com.solverminds.ecom.register.repo;

import com.solverminds.ecom.register.dto.CustomerContactDto;
import com.solverminds.ecom.register.dto.CustomerDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository for customer queries using report database
 * Implements the native SQL query for customer details
 */
@Repository
public class CustomerReportRepository {

    private final JdbcTemplate reportJdbcTemplate;

    @Autowired
    public CustomerReportRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    private void ensureReportDb() {
        if (reportJdbcTemplate == null) {
            throw new IllegalStateException("reportdb is not configured");
        }
    }

    /**
     * First query: Get customer contact information by webid
     * Original query:
     * select customermaster.custcode,customermaster.countryid,customercontacts.contactname,customermaster.custname,
     * customermaster.address1,customermaster.phone,customercontacts.fax,customercontacts.email,customercontacts.mobile,
     * customercontacts.OnlineActive,customermaster.city,customermaster.countryid,customercontacts.webid,customercontacts.admin,
     * customermaster.custtype from customercontacts
     * inner join customermaster on customercontacts.custcode=customermaster.custcode
     * where customercontacts.status='A' and customercontacts.contactType='online' and customercontacts.webid=?
     */
    public List<CustomerContactDto> getCustomerContactByWebId(String webId) {
        ensureReportDb();
        
        final String sql = "SELECT " +
                "cm.custcode, cm.countryid, cc.contactname, cm.custname, " +
                "cm.address1, cm.phone, cc.fax, cc.email, cc.mobile, " +
                "cc.OnlineActive, cm.city, cm.countryid, cc.webid, cc.admin, " +
                "cm.custtype " +
                "FROM customercontacts cc (NOLOCK) " +
                "INNER JOIN customermaster cm (NOLOCK) ON cc.custcode = cm.custcode " +
                "WHERE cc.status = 'A' AND cc.contactType = 'online' AND cc.webid = ?";
                
        return reportJdbcTemplate.query(sql, new CustomerContactRowMapper(), webId);
    }

    /**
     * Second query: Get customer details by customer code
     * Original query:
     * select customermaster.cmpcode,customermaster.custname,customermaster.address1,customermaster.address2,customermaster.city,
     * customermaster.countryId+' - '+country.countryname as countryname,customermaster.fax,customermaster.email,customermaster.phone,
     * customermaster.website,customermaster.otherscity,customermaster.pobox,customermaster.countryid,customermaster.zipcode,customermaster.statecode as state,
     * customermaster.controllocation,customermaster.statecode as state,customermaster.eori,customermaster.persontype from customermaster(nolock)
     * Inner join country(nolock) on customermaster.countryid=country.countrycode where customermaster.status='A' and
     * customermaster.custcode=?
     */
    public List<CustomerDetailsDto> getCustomerDetailsByCustCode(String custCode) {
        ensureReportDb();
        
        final String sql = "SELECT " +
                "cm.cmpcode, cm.custname, cm.address1, cm.address2, cm.city, " +
                "cm.countryId + ' - ' + c.countryname as countryname, cm.fax, cm.email, cm.phone, " +
                "cm.website, cm.otherscity, cm.pobox, cm.countryid, cm.zipcode, cm.statecode as state, " +
                "cm.controllocation, cm.statecode as state, cm.eori, cm.persontype " +
                "FROM customermaster cm (NOLOCK) " +
                "INNER JOIN country c (NOLOCK) ON cm.countryid = c.countrycode " +
                "WHERE cm.status = 'A' AND cm.custcode = ?";
                
        return reportJdbcTemplate.query(sql, new CustomerDetailsRowMapper(), custCode);
    }

    /**
     * Row mapper for CustomerContactDto
     * Maps all columns from the customer contact query
     */
    private static class CustomerContactRowMapper implements RowMapper<CustomerContactDto> {
        @Override
        public CustomerContactDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            CustomerContactDto dto = new CustomerContactDto();
            
            dto.setCustCode(rs.getString("custcode"));
            dto.setCountryId(rs.getString("countryid"));
            dto.setContactName(rs.getString("contactname"));
            dto.setCustName(rs.getString("custname"));
            dto.setAddress1(rs.getString("address1"));
            dto.setPhone(rs.getString("phone"));
            dto.setFax(rs.getString("fax"));
            dto.setEmail(rs.getString("email"));
            dto.setMobile(rs.getString("mobile"));
            dto.setOnlineActive(rs.getString("OnlineActive"));
            dto.setCity(rs.getString("city"));
            dto.setWebId(rs.getString("webid"));
            dto.setAdmin(rs.getString("admin"));
            dto.setCustType(rs.getString("custtype"));
            
            return dto;
        }
    }



    /**
     * Row mapper for CustomerDetailsDto
     * Maps all columns from the customer details query
     */
    private static class CustomerDetailsRowMapper implements RowMapper<CustomerDetailsDto> {
        @Override
        public CustomerDetailsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            CustomerDetailsDto dto = new CustomerDetailsDto();
            
            dto.setCmpCode(rs.getString("cmpcode"));
            dto.setCustName(rs.getString("custname"));
            dto.setAddress1(rs.getString("address1"));
            dto.setAddress2(rs.getString("address2"));
            dto.setCity(rs.getString("city"));
            dto.setCountryName(rs.getString("countryname"));
            dto.setFax(rs.getString("fax"));
            dto.setEmail(rs.getString("email"));
            dto.setPhone(rs.getString("phone"));
            dto.setWebsite(rs.getString("website"));
            dto.setOthersCity(rs.getString("otherscity"));
            dto.setPoBox(rs.getString("pobox"));
            dto.setCountryId(rs.getString("countryid"));
            dto.setZipCode(rs.getString("zipcode"));
            dto.setState(rs.getString("state"));
            dto.setControlLocation(rs.getString("controllocation"));
            dto.setEori(rs.getString("eori"));
            dto.setPersonType(rs.getString("persontype"));
            
            return dto;
        }
    }
}
