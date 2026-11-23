package com.solverminds.summary.mapper;

import com.svm.ecom.common.enums.Bound;
import com.solverminds.summary.dto.CustomerReleaseOrderDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper for mapping ResultSet to CustomerReleaseOrderDto
 */
public class CroMapper implements RowMapper<CustomerReleaseOrderDto> {

    @Override
    public CustomerReleaseOrderDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        CustomerReleaseOrderDto dto = new CustomerReleaseOrderDto();
        
        dto.setFlsBookingNo(rs.getString("flsbookingno"));
        dto.setServiceCode(rs.getString("servicecode"));
        dto.setVslCode(rs.getString("vslcode"));
        dto.setVoyCode(rs.getString("voycode"));
        dto.setBound(Bound.fromValue(rs.getString("bound")));
        dto.setTerminal(rs.getString("terminal"));
        dto.setOrigin(rs.getString("origin"));
        dto.setDelivery(rs.getString("delivery"));
        dto.setPol(rs.getString("pol"));
        dto.setPod(rs.getString("pod"));
        dto.setEquipmentType(rs.getString("equipmenttype"));
        dto.setAgencyCode(rs.getString("agencycode"));
        
        // Handle nullable integer fields
        Integer bkgQty = rs.getObject("bkgqty", Integer.class);
        dto.setBkgQty(bkgQty);
        
        Integer relQty = rs.getObject("relqty", Integer.class);
        dto.setRelQty(relQty);
        
        return dto;
    }
}
