package com.solverminds.booking.repo;

import com.solverminds.booking.dto.v2.vgm.row.BookingCargoRow;
import com.solverminds.booking.dto.v2.vgm.row.BookingCargoSnapshot;
import com.solverminds.booking.dto.v2.vgm.row.ContainerProfileTareRow;
import com.solverminds.booking.dto.v2.vgm.row.EquipmentPassportRow;
import com.solverminds.booking.dto.v2.vgm.row.VgmInsertRow;
import com.solverminds.booking.dto.v2.vgm.row.VgmSolasRow;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Coordinates VGM data access across ECOM and Documentation using JPA
 * ({@link EcomVgmJpaRepository} and {@link DocumentationVgmJpaRepository}, each with its own
 * persistence unit and {@code EntityManager}).
 *
 * <p>This is a pure data-access facade. Business rules/flows live in the service-style
 * {@link VgmRepository} (similar to {@link BookingRepository}).</p>
 */
@Repository
public class VgmDataRepository {

    private final DocumentationVgmJpaRepository documentationRepo;
    private final EcomVgmJpaRepository ecomRepo;

    public VgmDataRepository(DocumentationVgmJpaRepository documentationRepo, EcomVgmJpaRepository ecomRepo) {
        this.documentationRepo = documentationRepo;
        this.ecomRepo = ecomRepo;
    }

    private void ensureReposConfigured() {
        if (documentationRepo == null || ecomRepo == null) {
            throw new IllegalStateException("VGM repositories are not configured");
        }
    }

    public List<BookingCargoRow> findBookingCargoRows(String bookingId) {
        ensureReposConfigured();
        return documentationRepo.findBookingCargoRows(bookingId);
    }

    public BookingCargoSnapshot findBookingCargoSnapshot(String bookingId) {
        ensureReposConfigured();
        return documentationRepo.findBookingCargoSnapshot(bookingId);
    }

    public Optional<EquipmentPassportRow> findEquipmentPassport(String eqpid) {
        ensureReposConfigured();
        return documentationRepo.findEquipmentPassport(eqpid);
    }

    /** @see EcomVgmJpaRepository#findContainerProfileTare */
    public Optional<ContainerProfileTareRow> findContainerProfileTare(String containerno, String equipmenttype) {
        ensureReposConfigured();
        return ecomRepo.findContainerProfileTare(containerno, equipmenttype);
    }

    public List<ContainerProfileTareRow> findContainerProfileTareIn(List<String> containerNos) {
        ensureReposConfigured();
        if (containerNos == null || containerNos.isEmpty()) {
            return Collections.emptyList();
        }
        return ecomRepo.findContainerProfileTareIn(containerNos);
    }

    public Map<String, Integer> findMaxVersionEcomIn(String bookingId, List<String> eqpids) {
        ensureReposConfigured();
        if (eqpids == null || eqpids.isEmpty()) {
            return Collections.emptyMap();
        }
        return ecomRepo.findMaxVersionEcomIn(bookingId, eqpids);
    }

    public List<VgmSolasRow> findActiveVgmRowsEcom(String bookingId) {
        ensureReposConfigured();
        return ecomRepo.findActiveVgmRowsEcom(bookingId);
    }

    /** @see DocumentationVgmJpaRepository#findEquipmentPassportActiveIn */
    public List<EquipmentPassportRow> findEquipmentPassportActiveIn(List<String> containerNos) {
        ensureReposConfigured();
        if (containerNos == null || containerNos.isEmpty()) {
            return Collections.emptyList();
        }
        return documentationRepo.findEquipmentPassportActiveIn(containerNos);
    }

    public List<VgmSolasRow> findContainerListFromVgmSolas(String bookingId) {
        ensureReposConfigured();
        return documentationRepo.findContainerListFromVgmSolas(bookingId);
    }

    public Optional<VgmSolasRow> findLatestActiveVgmHeader(String bookingId) {
        ensureReposConfigured();
        return ecomRepo.findLatestActiveVgmHeader(bookingId);
    }

    public Optional<VgmSolasRow> findLatestActiveVgmForContainer(String bookingId, String eqpid) {
        ensureReposConfigured();
        return ecomRepo.findLatestActiveVgmForContainer(bookingId, eqpid);
    }

    public boolean customerHasEcomAccess(String bookingId, String customerCode) {
        ensureReposConfigured();
        return ecomRepo.customerHasEcomAccess(bookingId, customerCode);
    }

    public boolean customerHasLrpAccess(String bookingId, String customerCode) {
        ensureReposConfigured();
        return documentationRepo.customerHasLrpAccess(bookingId, customerCode);
    }

    /** Booking exists and is not void (status &lt;&gt; 'V'). */
    public boolean bookingExistsActive(String bookingId) {
        ensureReposConfigured();
        return documentationRepo.bookingExistsActive(bookingId);
    }

    public boolean bookingCargoHasEqpid(String bookingId, String eqpid) {
        ensureReposConfigured();
        return documentationRepo.bookingCargoHasEqpid(bookingId, eqpid);
    }

    /**
     * Expected container counts grouped by equipment type (booking_cargo.eqptypeid) for a booking.
     */
    public Map<String, Long> findEquipmentTypeCounts(String bookingId) {
        ensureReposConfigured();
        List<Object[]> rows = documentationRepo.findEquipmentTypeCounts(bookingId);
        Map<String, Long> out = new HashMap<>();
        if (rows == null) {
            return out;
        }
        for (Object[] r : rows) {
            if (r == null || r.length < 2) continue;
            String eqpType = r[0] == null ? null : String.valueOf(r[0]);
            if (eqpType == null) continue;
            Object c = r[1];
            long count;
            if (c == null) {
                count = 0L;
            } else if (c instanceof Number) {
                count = ((Number) c).longValue();
            } else {
                try {
                    count = Long.parseLong(String.valueOf(c).trim());
                } catch (Exception ex) {
                    count = 0L;
                }
            }
            out.put(eqpType, count);
        }
        return out;
    }

    /** @return null when no prior version (including no rows for this container) */
    public Integer findMaxVersionEcom(String bookingId, String eqpid) {
        ensureReposConfigured();
        return ecomRepo.findMaxVersionEcom(bookingId, eqpid);
    }

    public void invalidateEcomVgm(String bookingId, String eqpid) {
        ensureReposConfigured();
        ecomRepo.invalidateEcomVgm(bookingId, eqpid);
    }

    public void insertEcomVgm(VgmInsertRow row, String vgmWeightStr) {
        ensureReposConfigured();
        ecomRepo.insertEcomVgm(row, vgmWeightStr);
    }

    public void invalidateLrpVgm(String bookingId, String eqpid) {
        ensureReposConfigured();
        documentationRepo.invalidateLrpVgm(bookingId, eqpid);
    }

    public void insertLrpVgm(VgmInsertRow row, String vgmWeightStr, Timestamp datecr, Timestamp dateup) {
        ensureReposConfigured();
        documentationRepo.insertLrpVgm(row, vgmWeightStr, datecr, dateup);
    }
}

