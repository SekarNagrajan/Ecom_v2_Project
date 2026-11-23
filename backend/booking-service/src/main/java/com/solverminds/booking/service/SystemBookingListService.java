package com.solverminds.booking.service;

import com.solverminds.booking.cursor.SystemBookingListCursorCodec;
import com.solverminds.booking.cursor.SystemBookingListCursorCodec.CursorKey;
import com.solverminds.booking.dto.SystemBookingListResponse;
import com.solverminds.booking.dto.SystemBookingSummaryResponseDto;
import com.solverminds.booking.repo.SystemBookingListRepository;
import com.solverminds.booking.repo.SystemBookingListRepository.Row;
import com.svm.ecom.common.util.PortLocalTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SystemBookingListService {

    private static final Logger logger = LoggerFactory.getLogger(SystemBookingListService.class);

    public static final int DEFAULT_LIMIT = 500;
    public static final int MAX_LIMIT = 1000;

    private final SystemBookingListRepository repository;
    private final SystemBookingListCursorCodec cursorCodec;
    private final MasterDataCacheService masterDataCacheService;
    private final Semaphore bulkhead;
    private final long bulkheadWaitMs;
    private final int rateLimitPerSecond;
    private final AtomicInteger rateWindowCount = new AtomicInteger(0);
    private final AtomicLong rateWindowStartMs = new AtomicLong(System.currentTimeMillis());

    public SystemBookingListService(
            SystemBookingListRepository repository,
            SystemBookingListCursorCodec cursorCodec,
            MasterDataCacheService masterDataCacheService,
            @Value("${booking.system-list.bulkhead.max-concurrent:2}") int maxConcurrent,
            @Value("${booking.system-list.bulkhead.wait-ms:0}") long bulkheadWaitMs,
            @Value("${booking.system-list.rate-limit-per-second:5}") int rateLimitPerSecond) {
        this.repository = repository;
        this.cursorCodec = cursorCodec;
        this.masterDataCacheService = masterDataCacheService;
        this.bulkhead = new Semaphore(Math.max(1, maxConcurrent), true);
        this.bulkheadWaitMs = Math.max(0L, bulkheadWaitMs);
        this.rateLimitPerSecond = Math.max(1, rateLimitPerSecond);
    }

    public SystemBookingListResponse list(int limit, String cursor, Timestamp changeDateAfter) {
        if (!tryAcquireRatePermit()) {
            throw new SystemBookingListRejectedException("System booking list rate limit exceeded");
        }
        boolean acquired = false;
        try {
            acquired = bulkheadWaitMs == 0L
                    ? bulkhead.tryAcquire()
                    : bulkhead.tryAcquire(bulkheadWaitMs, TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new SystemBookingListRejectedException("System booking list is busy; retry later");
            }
            CursorKey key = cursorCodec.decode(cursor);
            int fetchSize = limit + 1;
            List<Row> rows = repository.findPage(
                    key == null ? null : key.getCreatedDate(),
                    key == null ? null : key.getBookingId(),
                    fetchSize,
                    changeDateAfter,
                    key == null ? null : key.getSource());

            boolean hasNext = rows.size() > limit;
            int pageSize = hasNext ? limit : rows.size();
            List<SystemBookingSummaryResponseDto> bookings = new ArrayList<SystemBookingSummaryResponseDto>(pageSize);
            for (int i = 0; i < pageSize; i++) {
                bookings.add(rows.get(i).getDto());
            }
            applyDepartureDateOffsets(bookings);

            String nextCursor = null;
            if (hasNext && pageSize > 0) {
                Row last = rows.get(pageSize - 1);
                try {
                    nextCursor = cursorCodec.encode(last.getCreatedDate(), last.getBookingId(), last.getSource());
                } catch (SystemBookingListCursorCodec.InvalidCursorException e) {
                    // Boundary row is missing data needed to build a cursor (e.g. null id/date).
                    // Don't fail the whole page for it: end the traversal cleanly instead of
                    // surfacing this as a client-facing "invalid cursor" 400.
                    logger.warn("Unable to build nextCursor for system booking list boundary row "
                                    + "(createdDate={}, bookingId={}, source={}); treating as end-of-list: {}",
                            last.getCreatedDate(), last.getBookingId(), last.getSource(), e.getMessage());
                    nextCursor = null;
                    hasNext = false;
                }
            }
            return new SystemBookingListResponse(bookings, nextCursor, hasNext);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SystemBookingListRejectedException("System booking list interrupted");
        } finally {
            if (acquired) {
                bulkhead.release();
            }
        }
    }

    /**
     * Same port-local departureDate labeling used by {@code GET /api/v1/booking/summary}.
     */
    private void applyDepartureDateOffsets(List<SystemBookingSummaryResponseDto> data) {
        if (data == null || masterDataCacheService == null) {
            return;
        }
        for (SystemBookingSummaryResponseDto dto : data) {
            if (dto.getDepartureDate() != null && dto.getOriginPortId() != null) {
                try {
                    String tz = masterDataCacheService.getTimezoneOffset(dto.getOriginPortId());
                    tz = PortLocalTimeUtil.normalizeTimezoneValue(tz);
                    dto.setDepartureDate(PortLocalTimeUtil.labelWithOffset(dto.getDepartureDate(), tz));
                } catch (Exception ignored) {
                }
            }
        }
    }

    private boolean tryAcquireRatePermit() {
        long now = System.currentTimeMillis();
        long start = rateWindowStartMs.get();
        if (now - start >= 1000L) {
            if (rateWindowStartMs.compareAndSet(start, now)) {
                rateWindowCount.set(0);
            }
        }
        while (true) {
            int current = rateWindowCount.get();
            if (current >= rateLimitPerSecond) {
                return false;
            }
            if (rateWindowCount.compareAndSet(current, current + 1)) {
                return true;
            }
        }
    }

    public static final class SystemBookingListRejectedException extends IllegalStateException {
        public SystemBookingListRejectedException(String message) {
            super(message);
        }
    }
}
