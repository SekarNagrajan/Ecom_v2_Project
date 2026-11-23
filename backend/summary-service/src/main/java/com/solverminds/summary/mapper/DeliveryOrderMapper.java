package com.solverminds.summary.mapper;

import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.common.enums.EcomPrintStatus;
import com.solverminds.summary.dto.DeliveryOrderDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * RowMapper for mapping ResultSet to DeliveryOrderDto
 */
public class DeliveryOrderMapper implements RowMapper<DeliveryOrderDto> {

    @Override
    public DeliveryOrderDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        DeliveryOrderDto dto = new DeliveryOrderDto();
        
        dto.setPrintStatus(rs.getString("printstatus"));
        dto.setEcomPrintStatus(EcomPrintStatus.fromValue(rs.getString("ecomprintstatus")));
        dto.setDelOrdNo(rs.getString("delordno"));
        
        // Handle date fields - convert java.sql.Date to LocalDate
        java.sql.Date doValidityDate = rs.getDate("dovaliditydate");
        if (doValidityDate != null) {
            dto.setDoValidityDate(doValidityDate.toLocalDate());
        }
        
        java.sql.Date delOrdDate = rs.getDate("delorddate");
        if (delOrdDate != null) {
            dto.setDelOrdDate(delOrdDate.toLocalDate());
        }
        
        dto.setBlNumber(rs.getString("blnumber"));
        dto.setVessel(rs.getString("vessel"));
        dto.setVoyage(rs.getString("voyage"));
        dto.setBound(Bound.fromValue(rs.getString("bound")));
        dto.setLoadPort(rs.getString("loadport"));
        dto.setDischargePort(rs.getString("dischargeport"));
        dto.setTerminal(rs.getString("terminal"));
        
        Timestamp arrDate = rs.getTimestamp("arrdate");
        if (arrDate != null) {
            dto.setArrDate(arrDate.toLocalDateTime().atOffset(ZoneOffset.UTC));
        }
        
        return dto;
    }
}
