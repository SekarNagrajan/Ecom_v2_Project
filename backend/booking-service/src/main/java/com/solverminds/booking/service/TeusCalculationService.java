package com.solverminds.booking.service;

import com.solverminds.booking.entity.EcomBookingContainer;
import com.solverminds.booking.repo.EcomBookingContainerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Calculates TEUs for a booking using ecombooking_cargo and ecom_equipments.
 * Logic: SUM(eq.teus * ebc.quantity) per onlinebookno.
 */
@Service
public class TeusCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(TeusCalculationService.class);

    /**
     * SQL: sum(eq.teus * ebc.quantity) from ecombooking_cargo ebc
     * inner join ecom_equipments eq on eq.equiptype = ebc.eqpSize
     * where ebc.ebookno = ?
     */
    private static final String SQL_ECOM_EQUIPMENTS =
        "SELECT ISNULL(SUM(eq.teus * ebc.quantity), 0) " +
        "FROM ecombooking_cargo ebc WITH (NOLOCK) " +
        "INNER JOIN ecom_equipments eq WITH (NOLOCK) ON eq.equiptype = ebc.eqpSize " +
        "WHERE ebc.ebookno = ?";

    private final JdbcTemplate ecomJdbcTemplate;
    private final EcomBookingContainerRepository containerRepo;

    public TeusCalculationService(
            @Qualifier("ecomJdbcTemplate") JdbcTemplate ecomJdbcTemplate,
            EcomBookingContainerRepository containerRepo) {
        this.ecomJdbcTemplate = ecomJdbcTemplate;
        this.containerRepo = containerRepo;
    }

    /**
     * Calculate TEUs for the given ECOM booking (ebookno).
     *
     * @param ebookno The ECOM booking number (onlinebookno)
     * @return TEUs count, or null if calculation fails
     */
    public Integer calculateTeus(String ebookno) {
        if (ebookno == null || ebookno.trim().isEmpty()) {
            return null;
        }
        String trimmed = ebookno.trim();
        try {
            Double rawTeus = tryEcomEquipments(trimmed);
            if (rawTeus == null) {
                rawTeus = calculateFromContainers(trimmed);
            }
            if (rawTeus == null) {
                return 0;
            }
            return (int) Math.round(rawTeus);
        } catch (Exception e) {
            logger.warn("TEUs calculation failed for {}: {}", ebookno, e.getMessage());
            return null;
        }
    }

    private Double tryEcomEquipments(String ebookno) {
        try {
            if (ecomJdbcTemplate == null) return null;
            Number n = ecomJdbcTemplate.queryForObject(SQL_ECOM_EQUIPMENTS, Number.class, ebookno);
            return n != null ? n.doubleValue() : 0.0;
        } catch (Exception e) {
            logger.debug("ecom_equipments query failed (table may not exist): {}", e.getMessage());
            return null;
        }
    }

    /**
     * Fallback: compute TEUs from ecombooking_cargo using standard TEU factors.
     * 20' = 1 TEU, 40' = 2 TEU, 45' = 2.25 TEU.
     */
    private Double calculateFromContainers(String ebookno) {
        List<EcomBookingContainer> containers = containerRepo.findByEbooknoOrderByConid(ebookno);
        if (containers == null || containers.isEmpty()) return 0.0;
        double total = 0;
        for (EcomBookingContainer c : containers) {
            int qty = c.getQuantity() != null ? c.getQuantity() : 1;
            double teuPerUnit = teuPerUnit(c.getEqpSize());
            total += teuPerUnit * qty;
        }
        return total;
    }

    private double teuPerUnit(String eqpSize) {
        if (eqpSize == null || eqpSize.trim().isEmpty()) return 1.0;
        String s = eqpSize.trim().toUpperCase();
        if (s.startsWith("45")) return 2.25;
        if (s.startsWith("40")) return 2.0;
        return 1.0; // 20' and default
    }
}
