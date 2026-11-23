package com.svm.ecom.rates.service;

import com.svm.ecom.rates.cursor.MrgCursorCodec;
import com.svm.ecom.rates.dto.mrg.MrgDetailLine;
import com.svm.ecom.rates.dto.mrg.MrgItem;
import com.svm.ecom.rates.dto.mrg.MrgListResponse;
import com.svm.ecom.rates.dto.mrg.MrgPageMetadata;
import com.svm.ecom.rates.repository.MrgRepository;
import com.svm.ecom.rates.repository.MrgRepository.DetailRow;
import com.svm.ecom.rates.repository.MrgRepository.MasterRow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only MRG list: keyset-paginated MRG headers, each with all of its rate detail lines.
 */
@Service
public class MrgService {

    private static final DateTimeFormatter UTC_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final MrgRepository repository;
    private final MrgCursorCodec cursorCodec;
    private final int defaultLimit;
    private final int maxLimit;

    public MrgService(MrgRepository repository,
                      MrgCursorCodec cursorCodec,
                      @Value("${rates.mrg.default-limit:20}") int defaultLimit,
                      @Value("${rates.mrg.max-limit:100}") int maxLimit) {
        this.repository = repository;
        this.cursorCodec = cursorCodec;
        this.maxLimit = Math.max(1, maxLimit);
        this.defaultLimit = Math.min(Math.max(1, defaultLimit), this.maxLimit);
    }

    public int getDefaultLimit() {
        return defaultLimit;
    }

    public int getMaxLimit() {
        return maxLimit;
    }

    /**
     * @param limit  page size, already validated to 1..maxLimit
     * @param cursor opaque cursor from the previous page (null/blank for the first page)
     * @throws MrgCursorCodec.InvalidCursorException when the cursor is malformed or tampered with
     */
    public MrgListResponse list(int limit, String cursor) {
        String afterMrgCode = cursorCodec.decode(cursor);

        List<MasterRow> masters = repository.findMasterPage(afterMrgCode, limit + 1);
        boolean hasNext = masters.size() > limit;
        int pageSize = hasNext ? limit : masters.size();
        List<MasterRow> page = masters.subList(0, pageSize);

        List<String> codes = new ArrayList<String>(pageSize);
        for (MasterRow m : page) {
            codes.add(m.getMrgCode());
        }
        Map<String, List<DetailRow>> detailsByCode = loadDetailsByCode(codes);

        List<MrgItem> items = new ArrayList<MrgItem>(pageSize);
        for (MasterRow m : page) {
            items.add(toItem(m, detailsByCode.get(m.getMrgCode())));
        }

        String nextCursor = (hasNext && pageSize > 0) ? cursorCodec.encode(page.get(pageSize - 1).getMrgCode()) : null;
        return new MrgListResponse(items, new MrgPageMetadata(nextCursor, hasNext, limit));
    }

    private Map<String, List<DetailRow>> loadDetailsByCode(List<String> codes) {
        Map<String, List<DetailRow>> detailsByCode = new LinkedHashMap<String, List<DetailRow>>();
        for (DetailRow d : repository.findDetails(codes)) {
            List<DetailRow> bucket = detailsByCode.get(d.getMrgCode());
            if (bucket == null) {
                bucket = new ArrayList<DetailRow>();
                detailsByCode.put(d.getMrgCode(), bucket);
            }
            bucket.add(d);
        }
        return detailsByCode;
    }

    /**
     * MRGMaster carries no validity dates / currency, so (as in the MRG module) they are derived from the lines:
     * validFrom = earliest fmDate, validTo = latest toDate, currency = currency of the first line.
     */
    private static MrgItem toItem(MasterRow master, List<DetailRow> rows) {
        MrgItem item = new MrgItem();
        item.setId(master.getMrgCode());
        item.setDescription(master.getReference() == null ? "" : master.getReference().trim());
        if (rows == null || rows.isEmpty()) {
            return item;
        }
        Timestamp from = null;
        Timestamp to = null;
        List<MrgDetailLine> lines = new ArrayList<MrgDetailLine>(rows.size());
        for (DetailRow r : rows) {
            lines.add(r.getLine());
            if (r.getFromDate() != null && (from == null || r.getFromDate().before(from))) {
                from = r.getFromDate();
            }
            if (r.getToDate() != null && (to == null || r.getToDate().after(to))) {
                to = r.getToDate();
            }
            if (item.getCurrency() == null && r.getLine().getCurrency() != null && !r.getLine().getCurrency().isEmpty()) {
                item.setCurrency(r.getLine().getCurrency());
            }
        }
        item.setValidFrom(formatUtc(from));
        item.setValidTo(formatUtc(to));
        item.setDetails(lines);
        return item;
    }

    private static String formatUtc(Timestamp ts) {
        // DB datetimes are stored without a zone and are published as UTC (IAD sample: 2026-07-17T00:00:00Z).
        return ts == null ? null : ts.toLocalDateTime().atOffset(ZoneOffset.UTC).format(UTC_DATE_TIME);
    }
}
