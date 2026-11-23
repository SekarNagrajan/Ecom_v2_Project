package com.solverminds.booking.service;

import com.solverminds.booking.dto.v2.vgm.row.VgmInsertRow;
import com.solverminds.booking.repo.VgmDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Persists VGM rows to ECOM and LRP in separate Spring transactions (distinct datasources).
 * ECOM phase commits before LRP; if LRP fails after ECOM success, operators may need to reconcile.
 */
@Component
public class VgmTransactionalWriter {

    private static final Logger logger = LoggerFactory.getLogger(VgmTransactionalWriter.class);

    private final VgmDataRepository data;

    public VgmTransactionalWriter(VgmDataRepository data) {
        this.data = data;
    }

    @Transactional(transactionManager = "ecomTransactionManager", rollbackFor = Exception.class)
    public void applyEcomUpsert(List<VgmInsertRow> rows, List<String> weightStrings) {
        for (int i = 0; i < rows.size(); i++) {
            VgmInsertRow row = rows.get(i);
            data.invalidateEcomVgm(row.getBookNo(), row.getEqpid());
        }
        for (int i = 0; i < rows.size(); i++) {
            data.insertEcomVgm(rows.get(i), weightStrings.get(i));
        }
    }

    @Transactional(transactionManager = "documentationTransactionManager", rollbackFor = Exception.class)
    public void applyLrpMirror(List<VgmInsertRow> rows, List<String> weightStrings) {
        Timestamp ts = Timestamp.valueOf(LocalDateTime.now());
        for (int i = 0; i < rows.size(); i++) {
            VgmInsertRow row = rows.get(i);
            data.invalidateLrpVgm(row.getBookNo(), row.getEqpid());
        }
        for (int i = 0; i < rows.size(); i++) {
            data.insertLrpVgm(rows.get(i), weightStrings.get(i), ts, ts);
        }
    }

    /**
     * Best-effort mirror of ECOM-committed VGM data into Documentation/LRP.
     * Keeps the existing behavior: do not fail the API after ECOM commit.
     */
    public void applyLrpMirrorBestEffort(List<VgmInsertRow> rows, List<String> weightStrings, String bookingId) {
        try {
            applyLrpMirror(rows, weightStrings);
        } catch (Exception ex) {
            logger.error("VGM saved to ECOM but failed to mirror to documentation DB. bookingId={}", bookingId, ex);
        }
    }
}
