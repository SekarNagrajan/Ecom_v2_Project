package com.solverminds.booking.service;

import com.solverminds.booking.dto.v2.vgm.row.VgmInsertRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Best-effort mirror of ECOM-committed VGM data into Documentation/LRP.
 *
 * <p>Important: this class exists to ensure {@link VgmTransactionalWriter#applyLrpMirror(List, List)}
 * is invoked through Spring's transactional proxy (avoiding self-invocation).</p>
 */
@Component
public class VgmDocumentationMirrorService {

    private static final Logger logger = LoggerFactory.getLogger(VgmDocumentationMirrorService.class);

    private final VgmTransactionalWriter transactionalWriter;

    public VgmDocumentationMirrorService(VgmTransactionalWriter transactionalWriter) {
        this.transactionalWriter = transactionalWriter;
    }

    public void mirrorToDocumentationBestEffort(List<VgmInsertRow> rows, List<String> weightStrings, String bookingId) {
        try {
            transactionalWriter.applyLrpMirror(rows, weightStrings);
        } catch (Exception ex) {
            logger.error("VGM saved to ECOM but failed to mirror to documentation DB. bookingId={}", bookingId, ex);
        }
    }
}

