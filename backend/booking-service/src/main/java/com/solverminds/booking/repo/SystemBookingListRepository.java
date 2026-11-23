package com.solverminds.booking.repo;

import com.solverminds.booking.dto.SystemBookingSummaryResponseDto;
import com.solverminds.booking.mapper.SystemBookingSummaryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SystemBookingListRepository {

    private static final Logger logger = LoggerFactory.getLogger(SystemBookingListRepository.class);
    private static final String CALL = "{call usp_getbookingsummary_system(?,?,?,?,?)}";

    private final JdbcTemplate jdbcTemplate;

    public SystemBookingListRepository(
            @Qualifier("systemBookingListJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Keyset page from {@code usp_getbookingsummary_system}. Parameter bind order is fixed:
     * cursorDate, cursorId, pageSize, changeDateAfter, cursorSource.
     * Caller must pass {@code pageSize = limit + 1} to detect {@code hasNext}.
     */
    public List<Row> findPage(Timestamp cursorDate,
                              String cursorId,
                              int pageSize,
                              Timestamp changeDateAfter,
                              String cursorSource) {
        if (jdbcTemplate == null) {
            throw new IllegalStateException("reportdb is not configured");
        }
        List<Row> page = jdbcTemplate.execute(CALL, (java.sql.CallableStatement cs) -> {
            if (cursorDate != null) {
                cs.setTimestamp(1, cursorDate);
            } else {
                cs.setNull(1, Types.TIMESTAMP);
            }
            if (cursorId != null && !cursorId.isEmpty()) {
                cs.setString(2, cursorId);
            } else {
                cs.setNull(2, Types.VARCHAR);
            }
            cs.setInt(3, pageSize);
            if (changeDateAfter != null) {
                cs.setTimestamp(4, changeDateAfter);
            } else {
                cs.setNull(4, Types.TIMESTAMP);
            }
            if (cursorSource != null && !cursorSource.isEmpty()) {
                cs.setString(5, cursorSource);
            } else {
                cs.setNull(5, Types.VARCHAR);
            }

            cs.execute();
            List<Row> rows = new ArrayList<Row>();
            ResultSet rs = cs.getResultSet();
            SystemBookingSummaryMapper mapper = new SystemBookingSummaryMapper();
            while (rs != null) {
                if (resultSetHasBookingColumns(rs)) {
                    int rowNum = 0;
                    while (rs.next()) {
                        SystemBookingSummaryResponseDto dto = mapper.mapRow(rs, rowNum++);
                        rows.add(new Row(
                                dto,
                                readCreatedDate(rs),
                                readBookingId(rs, dto),
                                readSource(rs)));
                    }
                    rs.close();
                    while (cs.getMoreResults()) {
                        ResultSet next = cs.getResultSet();
                        if (next != null) {
                            next.close();
                        }
                    }
                    break;
                }
                rs.close();
                if (!cs.getMoreResults()) {
                    break;
                }
                rs = cs.getResultSet();
            }
            logger.debug("usp_getbookingsummary_system returned {} rows (pageSize={})", rows.size(), pageSize);
            return rows;
        });
        return page;
    }

    private static boolean resultSetHasBookingColumns(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            if ("trust_book_no".equalsIgnoreCase(meta.getColumnName(i))) {
                return true;
            }
        }
        return false;
    }

    private static Timestamp readCreatedDate(ResultSet rs) throws SQLException {
        try {
            return rs.getTimestamp("datecr");
        } catch (SQLException e) {
            try {
                return rs.getTimestamp("createdDate");
            } catch (SQLException ignored) {
                return null;
            }
        }
    }

    private static String readBookingId(ResultSet rs, SystemBookingSummaryResponseDto dto) {
        // Prefer the SP's own sort/cursor key (sortBookId = COALESCE(onlinebookno, trust_book_no)) —
        // it's the value the SP actually orders/filters by, and is non-null even on rows where
        // trust_book_no is null (id lives in onlinebookno instead). Legacy columns are kept as a
        // fallback chain for result sets that don't expose sortBookId.
        String id = getStringQuiet(rs, "sortBookId");
        if (id == null || id.trim().isEmpty()) {
            id = getStringQuiet(rs, "bookingId");
        }
        if (id == null || id.trim().isEmpty()) {
            id = getStringQuiet(rs, "trust_book_no");
        }
        if ((id == null || id.trim().isEmpty()) && dto != null) {
            id = dto.getBookingNumber();
        }
        return id;
    }

    private static String readSource(ResultSet rs) {
        String src = getStringQuiet(rs, "cursorSource");
        if (src == null || src.trim().isEmpty()) {
            src = getStringQuiet(rs, "source");
        }
        if (src == null) {
            return null;
        }
        src = src.trim();
        return src.isEmpty() ? null : src.substring(0, 1);
    }

    private static String getStringQuiet(ResultSet rs, String column) {
        try {
            return rs.getString(column);
        } catch (SQLException e) {
            return null;
        }
    }

    public static final class Row {
        private final SystemBookingSummaryResponseDto dto;
        private final Timestamp createdDate;
        private final String bookingId;
        private final String source;

        public Row(SystemBookingSummaryResponseDto dto, Timestamp createdDate, String bookingId, String source) {
            this.dto = dto;
            this.createdDate = createdDate;
            this.bookingId = bookingId;
            this.source = source;
        }

        public SystemBookingSummaryResponseDto getDto() {
            return dto;
        }

        public Timestamp getCreatedDate() {
            return createdDate;
        }

        public String getBookingId() {
            return bookingId;
        }

        public String getSource() {
            return source;
        }
    }
}
