package com.solverminds.booking.repo;

import com.solverminds.booking.dto.v2.vgm.row.ContainerProfileTareRow;
import com.solverminds.booking.dto.v2.vgm.row.VgmInsertRow;
import com.solverminds.booking.dto.v2.vgm.row.VgmSolasRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * ECOM persistence for VGM using the ECOM JPA persistence unit.
 * Uses native SQL to preserve existing SQL Server behavior (TOP 1, NOLOCK, GETDATE, etc.).
 */
@Repository
public class EcomVgmJpaRepository {

    private static final Logger logger = LoggerFactory.getLogger(EcomVgmJpaRepository.class);

    private static final String Q3_VGM_HEADER =
            "SELECT TOP 1 "
                    + "    eqpid, "
                    + "    vgm_weight, "
                    + "    vgm_unit, "
                    + "    vgm_obtained_date, "
                    + "    vgm_obtained_method, "
                    + "    vgm_authorized_person, "
                    + "    vgm_company_name, "
                    + "    vgm_ref_number, "
                    + "    VGM_order_Number, "
                    + "    VGM_Address1, "
                    + "    VGM_Address2, "
                    + "    VGM_City, "
                    + "    VGM_Zipcode, "
                    + "    VGM_Country, "
                    + "    Telephone, "
                    + "    Email, "
                    + "    status, "
                    + "    version_no, "
                    + "    vgm_source "
                    + "FROM ecom_vgmsolas (NOLOCK) "
                    + "WHERE book_no = ?1 "
                    + "  AND status = 'A' "
                    + "ORDER BY version_no DESC";

    /** V3 GET: all active rows (latest version per eqpid chosen in Java). */
    private static final String Q_VGM_ACTIVE_ROWS_ECOM =
            "SELECT "
                    + "    eqpid, "
                    + "    eqptype, "
                    + "    vgm_weight, "
                    + "    vgm_unit, "
                    + "    vgm_obtained_date, "
                    + "    vgm_obtained_method, "
                    + "    vgm_authorized_person, "
                    + "    vgm_company_name, "
                    + "    vgm_ref_number, "
                    + "    VGM_order_Number, "
                    + "    VGM_Address1, "
                    + "    VGM_Address2, "
                    + "    VGM_City, "
                    + "    VGM_Zipcode, "
                    + "    VGM_Country, "
                    + "    Telephone, "
                    + "    Email, "
                    + "    status, "
                    + "    version_no, "
                    + "    vgm_source "
                    + "FROM ecom_vgmsolas (NOLOCK) "
                    + "WHERE book_no = ?1 "
                    + "  AND status = 'A'";

    private static final String Q4_VGM_PER_CONTAINER =
            "SELECT TOP 1 "
                    + "    eqpid, "
                    + "    vgm_weight, "
                    + "    vgm_unit "
                    + "FROM ecom_vgmsolas (NOLOCK) "
                    + "WHERE book_no = ?1 "
                    + "  AND eqpid = ?2 "
                    + "  AND status = 'A' "
                    + "ORDER BY version_no DESC";

    private static final String ACCESS_ECOM =
            "SELECT TOP 1 1 FROM ecombooking e WITH (NOLOCK) "
                    + "INNER JOIN ecombooking_cust ec WITH (NOLOCK) ON ec.ebookno = e.ebookno "
                    + "WHERE ec.custId = ?1 AND ec.custTypeId IN ('AP','BP') "
                    + "  AND (e.ebookno = ?2 OR e.extBookNo = ?3)";

    private static final String Q_MAX_VERSION_ECOM =
            "SELECT MAX(CAST(version_no AS INT)) AS max_version "
                    + "FROM ecom_vgmsolas (NOLOCK) "
                    + "WHERE book_no = ?1 "
                    + "  AND eqpid = ?2";

    private static final String UPDATE_INVALIDATE_ECOM_VGM =
            "UPDATE ecom_vgmsolas "
                    + "SET status = 'I' "
                    + "WHERE book_no = ?1 "
                    + "  AND eqpid = ?2 "
                    + "  AND status IN ('A', 'C')";

    private static final String Q_CONTAINER_PROFILE_TARE =
            "SELECT TOP 1 "
                    + "    equipmenttype, "
                    + "    tarewt, "
                    + "    containerno, "
                    + "    shipperownedunit, "
                    + "    fleetgroup "
                    + "FROM containerprofile (NOLOCK) "
                    + "WHERE UPPER(LTRIM(RTRIM(containerno))) = ?1 "
                    + "  AND UPPER(LTRIM(RTRIM(equipmenttype))) = ?2";

    private static final String INSERT_ECOM_VGM =
            "INSERT INTO ecom_vgmsolas ("
                    + "book_no, bl_no, eqptype, eqpid, "
                    + "vgm_source, vgm_type, vgm_ref_number, VGM_order_Number, "
                    + "vgm_obtained_date, vgm_weight, vgm_unit, vgm_obtained_method, "
                    + "vgm_company_name, VGM_Address1, VGM_Address2, VGM_Zipcode, "
                    + "VGM_City, VGM_Country, vgm_authorized_person, "
                    + "Telephone, Fax, Email, "
                    + "status, version_no, usercr, userup, oprstatus, "
                    + "datecr, dateup"
                    + ") VALUES ("
                    + "?1, NULL, ?2, ?3, "
                    + "'C', 'bookno', ?4, ?5, "
                    + "?6, ?7, ?8, ?9, "
                    + "?10, ?11, ?12, ?13, "
                    + "?14, ?15, ?16, "
                    + "?17, NULL, ?18, "
                    + "?19, ?20, ?21, ?22, ?23, "
                    + "getdate(), getdate()"
                    + ")";

    @PersistenceContext(unitName = "ecomPersistenceUnit")
    private EntityManager em;

    private void ensureEcomJpa() {
        if (em == null) {
            // Match BookingRepository style: fail fast when a datasource is not configured.
            throw new IllegalStateException("ECOM JPA EntityManager is not configured");
        }
    }

    public Optional<VgmSolasRow> findLatestActiveVgmHeader(String bookingId) {
        ensureEcomJpa();
        Query q = em.createNativeQuery(Q3_VGM_HEADER);
        q.setParameter(1, bookingId);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        if (rows == null || rows.isEmpty()) return Optional.empty();
        return Optional.of(mapVgmHeader(rows.get(0)));
    }

    public Optional<VgmSolasRow> findLatestActiveVgmForContainer(String bookingId, String eqpid) {
        ensureEcomJpa();
        Query q = em.createNativeQuery(Q4_VGM_PER_CONTAINER);
        q.setParameter(1, bookingId);
        q.setParameter(2, eqpid);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        if (rows == null || rows.isEmpty()) return Optional.empty();
        return Optional.of(mapVgmContainerRow(rows.get(0)));
    }

    /**
     * Legacy {@code getecomtareweightforsi} / {@code EcomVGMBean#togetTareWtForCntr} profile step:
     * {@code containerprofile} by container and equipment type.
     */
    public Optional<ContainerProfileTareRow> findContainerProfileTare(String containerno, String equipmenttype) {
        ensureEcomJpa();
        if (containerno == null || containerno.trim().isEmpty()
                || equipmenttype == null || equipmenttype.trim().isEmpty()) {
            return Optional.empty();
        }
        String cn = containerno.trim().toUpperCase(Locale.ROOT);
        String et = equipmenttype.trim().toUpperCase(Locale.ROOT);
        try {
            Query q = em.createNativeQuery(Q_CONTAINER_PROFILE_TARE);
            q.setParameter(1, cn);
            q.setParameter(2, et);
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            if (rows == null || rows.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(mapContainerProfileTare(rows.get(0)));
        } catch (RuntimeException ex) {
            logger.warn(
                    "containerprofile lookup skipped (ECOM). containerno={}, equipmenttype={}",
                    cn,
                    et,
                    ex);
            return Optional.empty();
        }
    }

    public boolean customerHasEcomAccess(String bookingId, String customerCode) {
        ensureEcomJpa();
        Query q = em.createNativeQuery(ACCESS_ECOM);
        q.setParameter(1, customerCode);
        q.setParameter(2, bookingId);
        q.setParameter(3, bookingId);
        @SuppressWarnings("unchecked")
        List<Object> rows = q.getResultList();
        return rows != null && !rows.isEmpty();
    }

    /**
     * @return null when no prior version (including no rows for this container)
     */
    public Integer findMaxVersionEcom(String bookingId, String eqpid) {
        ensureEcomJpa();
        Query q = em.createNativeQuery(Q_MAX_VERSION_ECOM);
        q.setParameter(1, bookingId);
        q.setParameter(2, eqpid);
        Object v = q.getSingleResult();
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        try {
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * V3: latest version per container in one query. Missing eqpid is omitted (caller treats as version 1).
     */
    public Map<String, Integer> findMaxVersionEcomIn(String bookingId, List<String> eqpids) {
        ensureEcomJpa();
        List<String> ids = distinctNonBlank(eqpids);
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        String sql = "SELECT eqpid, MAX(CAST(version_no AS INT)) AS max_version "
                + "FROM ecom_vgmsolas (NOLOCK) "
                + "WHERE book_no = ?1 "
                + "  AND eqpid IN (" + inPlaceholders(2, ids.size()) + ") "
                + "GROUP BY eqpid";
        Query q = em.createNativeQuery(sql);
        q.setParameter(1, bookingId);
        for (int i = 0; i < ids.size(); i++) {
            q.setParameter(i + 2, ids.get(i));
        }
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        Map<String, Integer> out = new HashMap<>();
        if (rows == null) {
            return out;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 2) {
                continue;
            }
            String eqp = s(row[0]);
            if (eqp == null || eqp.trim().isEmpty()) {
                continue;
            }
            Integer ver = i(row[1]);
            if (ver != null) {
                out.put(eqp.trim().toUpperCase(Locale.ROOT), ver);
            }
        }
        return out;
    }

    /**
     * V3 GET: active ECOM rows for the booking. Caller keeps the highest version per eqpid.
     */
    public List<VgmSolasRow> findActiveVgmRowsEcom(String bookingId) {
        ensureEcomJpa();
        Query q = em.createNativeQuery(Q_VGM_ACTIVE_ROWS_ECOM);
        q.setParameter(1, bookingId);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        if (rows == null || rows.isEmpty()) {
            return new ArrayList<>();
        }
        List<VgmSolasRow> out = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            out.add(mapVgmFullRow(row));
        }
        return out;
    }

    /**
     * V3: all containerprofile rows for the given container numbers (type filtered in memory).
     * Table errors are swallowed like {@link #findContainerProfileTare}.
     */
    public List<ContainerProfileTareRow> findContainerProfileTareIn(List<String> containerNos) {
        ensureEcomJpa();
        List<String> ids = distinctNonBlankUpper(containerNos);
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        String sql = "SELECT "
                + "    equipmenttype, "
                + "    tarewt, "
                + "    containerno, "
                + "    shipperownedunit, "
                + "    fleetgroup "
                + "FROM containerprofile (NOLOCK) "
                + "WHERE UPPER(LTRIM(RTRIM(containerno))) IN (" + inPlaceholders(1, ids.size()) + ")";
        try {
            Query q = em.createNativeQuery(sql);
            for (int i = 0; i < ids.size(); i++) {
                q.setParameter(i + 1, ids.get(i));
            }
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            if (rows == null || rows.isEmpty()) {
                return new ArrayList<>();
            }
            List<ContainerProfileTareRow> out = new ArrayList<>(rows.size());
            for (Object[] row : rows) {
                out.add(mapContainerProfileTare(row));
            }
            return out;
        } catch (RuntimeException ex) {
            logger.warn("containerprofile batch lookup skipped (ECOM). count={}", ids.size(), ex);
            return new ArrayList<>();
        }
    }

    public void invalidateEcomVgm(String bookingId, String eqpid) {
        ensureEcomJpa();
        Query q = em.createNativeQuery(UPDATE_INVALIDATE_ECOM_VGM);
        q.setParameter(1, bookingId);
        q.setParameter(2, eqpid);
        int updated = q.executeUpdate();
        logger.debug("Invalidated ECOM VGM rows. bookingId={}, eqpid={}, updated={}", bookingId, eqpid, updated);
    }

    public void insertEcomVgm(VgmInsertRow row, String vgmWeightStr) {
        ensureEcomJpa();
        Query q = em.createNativeQuery(INSERT_ECOM_VGM);
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
        q.setParameter(21, row.getUserCr());
        q.setParameter(22, row.getUserUp());
        q.setParameter(23, row.getOprStatus());
        int inserted = q.executeUpdate();
        logger.debug("Inserted ECOM VGM row. bookingId={}, eqpid={}, versionNo={}, inserted={}", row.getBookNo(), row.getEqpid(), row.getVersionNo(), inserted);
    }

    private static String s(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static Integer i(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(String.valueOf(o).trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static ContainerProfileTareRow mapContainerProfileTare(Object[] row) {
        return ContainerProfileTareRow.builder()
                .equipmenttype(s(row[0]))
                .tarewt(s(row[1]))
                .containerno(s(row[2]))
                .shipperownedunit(s(row[3]))
                .fleetgroup(s(row[4]))
                .build();
    }

    private static VgmSolasRow mapVgmContainerRow(Object[] row) {
        // Q4 order: eqpid, vgm_weight, vgm_unit
        VgmSolasRow r = new VgmSolasRow();
        r.setEqpid(s(row[0]));
        r.setVgmWeight(s(row[1]));
        r.setVgmUnit(s(row[2]));
        return r;
    }

    private static List<String> distinctNonBlank(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> distinct = new LinkedHashSet<>();
        for (String v : values) {
            if (v == null) {
                continue;
            }
            String t = v.trim();
            if (!t.isEmpty()) {
                distinct.add(t);
            }
        }
        return new ArrayList<>(distinct);
    }

    private static List<String> distinctNonBlankUpper(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> distinct = new LinkedHashSet<>();
        for (String v : values) {
            if (v == null) {
                continue;
            }
            String t = v.trim();
            if (!t.isEmpty()) {
                distinct.add(t.toUpperCase(Locale.ROOT));
            }
        }
        return new ArrayList<>(distinct);
    }

    private static String inPlaceholders(int startIndex, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("?").append(startIndex + i);
        }
        return sb.toString();
    }

    private static VgmSolasRow mapVgmFullRow(Object[] row) {
        VgmSolasRow r = new VgmSolasRow();
        r.setEqpid(s(row[0]));
        r.setEqptype(s(row[1]));
        r.setVgmWeight(s(row[2]));
        r.setVgmUnit(s(row[3]));
        r.setVgmObtainedDate(s(row[4]));
        r.setVgmObtainedMethod(s(row[5]));
        r.setVgmAuthorizedPerson(s(row[6]));
        r.setVgmCompanyName(s(row[7]));
        r.setVgmRefNumber(s(row[8]));
        r.setVgmOrderNumber(s(row[9]));
        r.setVgmAddress1(s(row[10]));
        r.setVgmAddress2(s(row[11]));
        r.setVgmCity(s(row[12]));
        r.setVgmZipcode(s(row[13]));
        r.setVgmCountry(s(row[14]));
        r.setTelephone(s(row[15]));
        r.setEmail(s(row[16]));
        r.setStatus(s(row[17]));
        r.setVersionNo(i(row[18]));
        r.setSource(s(row[19]));
        return r;
    }

    private static VgmSolasRow mapVgmHeader(Object[] row) {
        // Q3 order matches select list
        VgmSolasRow r = new VgmSolasRow();
        r.setEqpid(s(row[0]));
        r.setVgmWeight(s(row[1]));
        r.setVgmUnit(s(row[2]));
        r.setVgmObtainedDate(s(row[3]));
        r.setVgmObtainedMethod(s(row[4]));
        r.setVgmAuthorizedPerson(s(row[5]));
        r.setVgmCompanyName(s(row[6]));
        r.setVgmRefNumber(s(row[7]));
        r.setVgmOrderNumber(s(row[8]));
        r.setVgmAddress1(s(row[9]));
        r.setVgmAddress2(s(row[10]));
        r.setVgmCity(s(row[11]));
        r.setVgmZipcode(s(row[12]));
        r.setVgmCountry(s(row[13]));
        r.setTelephone(s(row[14]));
        r.setEmail(s(row[15]));
        r.setStatus(s(row[16]));
        r.setVersionNo(i(row[17]));
        r.setSource(s(row[18]));
        return r;
    }
}

