package com.solverminds.booking.repo;

import com.solverminds.booking.dto.v2.vgm.row.BookingCargoRow;
import com.solverminds.booking.dto.v2.vgm.row.BookingCargoSnapshot;
import com.solverminds.booking.dto.v2.vgm.row.EquipmentPassportRow;
import com.solverminds.booking.dto.v2.vgm.row.VgmInsertRow;
import com.solverminds.booking.dto.v2.vgm.row.VgmSolasRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Documentation/LRP persistence for VGM using the Documentation JPA persistence unit.
 * Uses native SQL to preserve existing SQL Server behavior (NOLOCK, etc.).
 */
@Repository
public class DocumentationVgmJpaRepository {

    private static final Logger logger = LoggerFactory.getLogger(DocumentationVgmJpaRepository.class);

    private static final String Q1_BOOKING_CARGO =
            "SELECT "
                    + "    booking.book_no, "
                    + "    booking_cargo.eqptypeid, "
                    + "    booking_cargo.eqpid, "
                    + "    booking.agencyid, "
                    + "    booking.originportid, "
                    + "    booking.finalportid, "
                    + "    booking.loadportid, "
                    + "    booking.dischargeportid, "
                    + "    booking_cargo.shipperown, "
                    + "    booking_cargo.isdummy "
                    + "FROM booking (NOLOCK) "
                    + "INNER JOIN booking_cargo (NOLOCK) ON booking.book_no = booking_cargo.book_no "
                    + "WHERE booking.status <> 'V' "
                    + "  AND booking.book_no = ?1";

    /** V3: one read for exists (status &lt;&gt; V) plus cargo. v2 INNER JOIN query is unchanged. */
    private static final String Q_BOOKING_CARGO_SNAPSHOT =
            "SELECT "
                    + "    b.book_no, "
                    + "    c.eqptypeid, "
                    + "    c.eqpid, "
                    + "    c.shipperown, "
                    + "    c.isdummy "
                    + "FROM booking b (NOLOCK) "
                    + "LEFT JOIN booking_cargo c (NOLOCK) ON b.book_no = c.book_no "
                    + "WHERE b.book_no = ?1 "
                    + "  AND b.status <> 'V'";

    private static final String Q2_EQUIPMENT_PASSPORT =
            "SELECT containerno, equipmenttype, tarewt "
                    + "FROM Equipmentpassport (NOLOCK) "
                    + "WHERE status = 'A' "
                    + "  AND containerno = ?1";

    private static final String ACCESS_LRP =
            "SELECT TOP 1 1 FROM booking e WITH (NOLOCK) "
                    + "INNER JOIN booking_cust ec WITH (NOLOCK) ON ec.book_no = e.book_no "
                    + "WHERE ec.custId = ?1 AND ec.custTypeId IN ('AP','BP') AND e.book_no = ?2";

    private static final String Q_BOOKING_ACTIVE =
            "SELECT booking.book_no "
                    + "FROM booking (NOLOCK) "
                    + "WHERE booking.book_no = ?1 "
                    + "  AND booking.status <> 'V'";

    private static final String Q_BOOKING_CARGO_EQPID =
            "SELECT booking_cargo.eqpid "
                    + "FROM booking_cargo (NOLOCK) "
                    + "WHERE booking_cargo.book_no = ?1 "
                    + "  AND booking_cargo.eqpid = ?2";

    private static final String Q_BOOKING_CARGO_EQPTYPE_COUNT =
            "SELECT "
                    + "    c.eqptypeid, "
                    + "    COUNT(*) AS cargoCount "
                    + "FROM booking (NOLOCK) b "
                    + "INNER JOIN booking_cargo (NOLOCK) c ON b.book_no = c.book_no "
                    + "WHERE c.book_no = ?1 "
                    + "GROUP BY c.eqptypeid";

    private static final String UPDATE_INVALIDATE_LRP_VGM =
            "UPDATE vgmsolas "
                    + "SET status = 'I' "
                    + "WHERE book_no = ?1 "
                    + "  AND eqpid = ?2 "
                    + "  AND status IN ('A')";

    private static final String INSERT_LRP_VGM =
            "INSERT INTO vgmsolas ("
                    + "book_no, eqptype, eqpid, "
                    + "vgm_source, vgm_type, vgm_ref_number, VGM_order_Number, "
                    + "vgm_obtained_date, vgm_weight, vgm_unit, vgm_obtained_method, "
                    + "vgm_company_name, VGM_Address1, VGM_Address2, VGM_Zipcode, "
                    + "VGM_City, VGM_Country, vgm_authorized_person, "
                    + "Telephone, Fax, Email, "
                    + "status, version_no, usercr, userup, oprstatus, "
                    + "datecr, dateup"
                    + ") VALUES ("
                    + "?1, ?2, ?3, "
                    + "'C', '', ?4, ?5, "
                    + "?6, ?7, ?8, ?9, "
                    + "?10, ?11, ?12, ?13, "
                    + "?14, ?15, ?16, "
                    + "?17, NULL, ?18, "
                    + "?19, ?20, ?21, ?22, ?23, "
                    + "?24, ?25"
                    + ")";

    private static final String Q_MAX_VERSION_LRP =
            "SELECT MAX(CAST(version_no AS INT)) AS max_version "
                    + "FROM vgmsolas (NOLOCK) "
                    + "WHERE book_no = ?1 "
                    + "  AND status IN ('A', 'C')";

    private static final String Q_CONTAINER_LIST_FROM_VGMSOLAS =
            "SELECT "
                    + "    eqpid, "
                    + "    eqptype, "
                    + "    vgm_weight, "
                    + "    vgm_unit "
                    + "FROM vgmsolas (NOLOCK) "
                    + "WHERE book_no = ?1 "
                    + "  AND status IN ('A', 'C') "
                    + "  AND CAST(version_no AS INT) = ?2 "
                    + "ORDER BY datecr";

    @PersistenceContext(unitName = "documentationPersistenceUnit")
    private EntityManager em;

    private void ensureDocumentationJpa() {
        if (em == null) {
            // Match BookingRepository style: fail fast when a datasource is not configured.
            throw new IllegalStateException("Documentation JPA EntityManager is not configured");
        }
    }

    public List<BookingCargoRow> findBookingCargoRows(String bookingId) {
        ensureDocumentationJpa();
        Query q = em.createNativeQuery(Q1_BOOKING_CARGO);
        q.setParameter(1, bookingId);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        if (rows == null || rows.isEmpty()) return new ArrayList<>();
        List<BookingCargoRow> out = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            out.add(mapBookingCargo(row));
        }
        return out;
    }

    /**
     * V3-only: booking exists (not void) and cargo lines in one query.
     * Empty result means booking missing or void. Rows with null cargo columns mean booking exists with no cargo.
     */
    public BookingCargoSnapshot findBookingCargoSnapshot(String bookingId) {
        ensureDocumentationJpa();
        Query q = em.createNativeQuery(Q_BOOKING_CARGO_SNAPSHOT);
        q.setParameter(1, bookingId);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        if (rows == null || rows.isEmpty()) {
            return BookingCargoSnapshot.notFound();
        }
        List<BookingCargoRow> cargo = new ArrayList<>();
        for (Object[] row : rows) {
            BookingCargoRow mapped = mapBookingCargoSnapshot(row);
            if ((mapped.getEqpid() != null && !mapped.getEqpid().trim().isEmpty())
                    || (mapped.getEqptypeid() != null && !mapped.getEqptypeid().trim().isEmpty())) {
                cargo.add(mapped);
            }
        }
        return new BookingCargoSnapshot(true, cargo);
    }

    public Optional<EquipmentPassportRow> findEquipmentPassport(String eqpid) {
        ensureDocumentationJpa();
        Query q = em.createNativeQuery(Q2_EQUIPMENT_PASSPORT);
        q.setParameter(1, eqpid);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        if (rows == null || rows.isEmpty()) return Optional.empty();
        return Optional.of(mapPassport(rows.get(0)));
    }

    /**
     * Active equipment passport rows for the given container numbers ({@code status = 'A'}).
     * Empty input returns an empty list.
     */
    public List<EquipmentPassportRow> findEquipmentPassportActiveIn(List<String> containerNos) {
        ensureDocumentationJpa();
        if (containerNos == null || containerNos.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> distinct = new LinkedHashSet<>();
        for (String id : containerNos) {
            if (id == null) {
                continue;
            }
            String t = id.trim();
            if (!t.isEmpty()) {
                distinct.add(t);
            }
        }
        if (distinct.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> ids = new ArrayList<>(distinct);
        StringBuilder placeholders = new StringBuilder();
        for (int i = 1; i <= ids.size(); i++) {
            if (i > 1) {
                placeholders.append(", ");
            }
            placeholders.append("?").append(i);
        }
        String sql = "SELECT containerno, equipmenttype, tarewt "
                + "FROM Equipmentpassport (NOLOCK) "
                + "WHERE status = 'A' "
                + "  AND containerno IN (" + placeholders + ")";
        Query q = em.createNativeQuery(sql);
        for (int i = 0; i < ids.size(); i++) {
            q.setParameter(i + 1, ids.get(i));
        }
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        if (rows == null || rows.isEmpty()) {
            return new ArrayList<>();
        }
        List<EquipmentPassportRow> out = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            out.add(mapPassport(row));
        }
        return out;
    }

    public boolean customerHasLrpAccess(String bookingId, String customerCode) {
        ensureDocumentationJpa();
        Query q = em.createNativeQuery(ACCESS_LRP);
        q.setParameter(1, customerCode);
        q.setParameter(2, bookingId);
        @SuppressWarnings("unchecked")
        List<Object> rows = q.getResultList();
        return rows != null && !rows.isEmpty();
    }

    /**
     * Booking exists and is not void (status &lt;&gt; 'V').
     */
    public boolean bookingExistsActive(String bookingId) {
        ensureDocumentationJpa();
        Query q = em.createNativeQuery(Q_BOOKING_ACTIVE);
        q.setParameter(1, bookingId);
        @SuppressWarnings("unchecked")
        List<Object> rows = q.getResultList();
        return rows != null && !rows.isEmpty();
    }

    public boolean bookingCargoHasEqpid(String bookingId, String eqpid) {
        ensureDocumentationJpa();
        Query q = em.createNativeQuery(Q_BOOKING_CARGO_EQPID);
        q.setParameter(1, bookingId);
        q.setParameter(2, eqpid);
        @SuppressWarnings("unchecked")
        List<Object> rows = q.getResultList();
        return rows != null && !rows.isEmpty();
    }

    /**
     * Returns expected container counts grouped by {@code booking_cargo.eqptypeid} for a booking.
     * Keys are returned as-is from DB; callers should normalize (trim/case) consistently with request.
     */
    public List<Object[]> findEquipmentTypeCounts(String bookingId) {
        ensureDocumentationJpa();
        Query q = em.createNativeQuery(Q_BOOKING_CARGO_EQPTYPE_COUNT);
        q.setParameter(1, bookingId);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        return rows != null ? rows : new ArrayList<>();
    }

    public void invalidateLrpVgm(String bookingId, String eqpid) {
        ensureDocumentationJpa();
        Query q = em.createNativeQuery(UPDATE_INVALIDATE_LRP_VGM);
        q.setParameter(1, bookingId);
        q.setParameter(2, eqpid);
        int updated = q.executeUpdate();
        logger.debug("Invalidated LRP VGM rows. bookingId={}, eqpid={}, updated={}", bookingId, eqpid, updated);
    }

    public void insertLrpVgm(VgmInsertRow row, String vgmWeightStr, Timestamp datecr, Timestamp dateup) {
        ensureDocumentationJpa();
        Query q = em.createNativeQuery(INSERT_LRP_VGM);
        q.setParameter(1, row.getBookNo());
        q.setParameter(2, row.getEqptype());
        q.setParameter(3, row.getEqpid());
        q.setParameter(4, row.getVgmRefNumber());
        q.setParameter(5, row.getVgmOrderNumber());
        q.setParameter(6, row.getVgmObtainedDate());
        q.setParameter(7, vgmWeightStr);
        q.setParameter(8, row.getVgmUnit());
        q.setParameter(9, row.getVgmObtainedMethod());
        q.setParameter(10, row.getVgmCompanyName());
        q.setParameter(11, row.getVgmAddress1());
        q.setParameter(12, row.getVgmAddress2());
        q.setParameter(13, row.getVgmZipcode());
        q.setParameter(14, row.getVgmCity());
        q.setParameter(15, row.getVgmCountry());
        q.setParameter(16, row.getVgmAuthorizedPerson());
        q.setParameter(17, row.getTelephone());
        q.setParameter(18, row.getEmail());
        q.setParameter(19, row.getStatus());
        q.setParameter(20, row.getVersionNo());
        q.setParameter(21, "ECOM");
        q.setParameter(22, "ECOM");
        q.setParameter(23, row.getOprStatus());
        q.setParameter(24, datecr);
        q.setParameter(25, dateup);
        int inserted = q.executeUpdate();
        logger.debug("Inserted LRP VGM row. bookingId={}, eqpid={}, versionNo={}, inserted={}", row.getBookNo(), row.getEqpid(), row.getVersionNo(), inserted);
    }

    /**
     * Latest active VGM rows for all containers for a booking (LRP vgmsolas).
     * Returns empty list if no VGM submitted yet for this booking.
     */
    public List<VgmSolasRow> findContainerListFromVgmSolas(String bookingId) {
        ensureDocumentationJpa();
        Query maxQ = em.createNativeQuery(Q_MAX_VERSION_LRP);
        maxQ.setParameter(1, bookingId);
        Object maxResult = maxQ.getSingleResult();
        if (maxResult == null) {
            return new ArrayList<>();
        }
        int maxVersion;
        if (maxResult instanceof Number) {
            maxVersion = ((Number) maxResult).intValue();
        } else {
            try {
                maxVersion = Integer.parseInt(String.valueOf(maxResult).trim());
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }

        Query q = em.createNativeQuery(Q_CONTAINER_LIST_FROM_VGMSOLAS);
        q.setParameter(1, bookingId);
        q.setParameter(2, maxVersion);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        if (rows == null || rows.isEmpty()) {
            return new ArrayList<>();
        }
        List<VgmSolasRow> out = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            out.add(mapVgmContainerRow(row));
        }
        return out;
    }

    private static String s(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static Double d(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try {
            return Double.parseDouble(String.valueOf(o).trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static BookingCargoRow mapBookingCargo(Object[] row) {
        // Q1 order matches select list
        BookingCargoRow r = new BookingCargoRow();
        r.setBookNo(s(row[0]));
        r.setEqptypeid(s(row[1]));
        r.setEqpid(s(row[2]));
        r.setAgencyid(s(row[3]));
        r.setOriginportid(s(row[4]));
        r.setFinalportid(s(row[5]));
        r.setLoadportid(s(row[6]));
        r.setDischargeportid(s(row[7]));
        r.setShipperown(s(row[8]));
        r.setIsdummy(s(row[9]));
        return r;
    }

    private static BookingCargoRow mapBookingCargoSnapshot(Object[] row) {
        BookingCargoRow r = new BookingCargoRow();
        r.setBookNo(s(row[0]));
        r.setEqptypeid(s(row[1]));
        r.setEqpid(s(row[2]));
        r.setShipperown(s(row[3]));
        r.setIsdummy(s(row[4]));
        return r;
    }

    private static EquipmentPassportRow mapPassport(Object[] row) {
        // Q2 order: containerno, equipmenttype, tarewt
        EquipmentPassportRow r = new EquipmentPassportRow();
        r.setContainerno(s(row[0]));
        r.setEquipmenttype(s(row[1]));
        r.setTarewt(d(row[2]));
        return r;
    }

    private static VgmSolasRow mapVgmContainerRow(Object[] row) {
        // Q_CONTAINER_LIST_FROM_VGMSOLAS order: eqpid, eqptype, vgm_weight, vgm_unit
        VgmSolasRow r = new VgmSolasRow();
        r.setEqpid(s(row[0]));
        r.setEqptype(s(row[1]));
        r.setVgmWeight(s(row[2]));
        r.setVgmUnit(s(row[3]));
        return r;
    }
}

