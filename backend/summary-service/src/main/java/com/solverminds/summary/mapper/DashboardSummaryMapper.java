package com.solverminds.summary.mapper;

import com.svm.ecom.common.enums.BlStatus;
import com.solverminds.summary.dto.DashboardSummaryDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * RowMapper for mapping ResultSet to DashboardSummaryDto
 * Uses common JDBC connections from common-security module
 */
public class DashboardSummaryMapper implements RowMapper<DashboardSummaryDto> {

    @Override
    public DashboardSummaryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        DashboardSummaryDto dto = new DashboardSummaryDto();
        
        // String fields
        dto.setOnlineRefNo(rs.getString("onlinerefno"));
        dto.setOriginPort(rs.getString("originportid"));
        dto.setFinalPortIc(rs.getString("finalportid"));
        dto.setOriginPortSecond(rs.getString("originportdesc"));
        dto.setFinalPortD(rs.getString("finalportdesc"));
        dto.setAgencyId(rs.getString("agencyid"));
        dto.setLoadPortIc(rs.getString("loadportid"));
        dto.setDischargeL(rs.getString("dischargeportid"));
        dto.setBookNo(rs.getString("book_no"));
        dto.setStatus(BlStatus.fromValue(rs.getString("status")));
        dto.setBlNo(rs.getString("bl_no"));
        dto.setContainerL(rs.getString("containerno"));
        dto.setLatestAct(rs.getString("latestact"));
        
        Timestamp polAtTs = rs.getTimestamp("polat");
        dto.setPolAt(polAtTs != null ? polAtTs.toLocalDateTime().atOffset(ZoneOffset.UTC) : null);
        
        // Date field - convert from MSSQL datetime to LocalDate
        Timestamp bookDateTs = rs.getTimestamp("bookdate");
        if (bookDateTs != null) {
            dto.setBookDate(bookDateTs.toLocalDateTime());
        }
        
        // Handle nullable integer field
        Integer tuesCount = rs.getObject("tuescount", Integer.class);
        dto.setTuesCount(tuesCount);
        
        return dto;
    }
}
