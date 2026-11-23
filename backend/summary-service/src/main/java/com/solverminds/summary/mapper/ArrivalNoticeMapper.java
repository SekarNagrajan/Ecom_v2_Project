package com.solverminds.summary.mapper;

import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.common.enums.EcomPrintStatus;
import com.solverminds.summary.dto.ArrivalNoticeDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * RowMapper for mapping ResultSet to ArrivalNoticeDto
 */
public class ArrivalNoticeMapper implements RowMapper<ArrivalNoticeDto> {

    @Override
    public ArrivalNoticeDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ArrivalNoticeDto dto = new ArrivalNoticeDto();

        dto.setCustCode(rs.getString("custcode"));
        dto.setBlNumber(rs.getString("blnumber"));
        dto.setArrivalNoticeNo(rs.getString("arrivalnoticeno"));
        dto.setOriginPortId(rs.getString("originPortId"));
        dto.setFinalPortId(rs.getString("finalPortId"));
        dto.setVesselCode(rs.getString("vesselcode"));
        dto.setVoyage(rs.getString("voyage"));
        dto.setBound(Bound.fromValue(rs.getString("bound")));
        dto.setHblNumber(rs.getString("hblnumber"));

        Timestamp arrDate = rs.getTimestamp("arrdate");
        if (arrDate != null) {
            dto.setArrDate(arrDate.toLocalDateTime().atOffset(ZoneOffset.UTC));
        }

        dto.setPodTerminal(rs.getString("podterminal"));
        dto.setEcomPrintStatus(EcomPrintStatus.fromValue(rs.getString("ecomprintstatus")));

        return dto;
    }
}
