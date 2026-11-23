package com.svm.ecom.schedule.service;

import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.schedule.enums.TransportMode;
import com.svm.ecom.schedule.model.VesselScheduleResponse;
import com.svm.ecom.schedule.model.VesselScheduleItem;
import com.svm.ecom.schedule.repository.CutoffConfigRepository;
import com.svm.ecom.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VesselServiceTest {

    @Mock
    private EntityManagerFactory reportEntityManagerFactory;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ScheduleTimezoneCacheService timezoneCache;

    @Mock
    private CutoffConfigRepository cutoffConfigRepository;

    @Mock
    private CutoffCalculator cutoffCalculator;

    @InjectMocks
    private VesselService vesselService;

    private static final String TEST_VESSEL = "BUX";
    private static final String TEST_FROM_DATE = "2025-08-01";
    private static final String TEST_TO_DATE = "2025-08-10";
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        // Setup EntityManager mock chain
        when(reportEntityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.isOpen()).thenReturn(true);
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        
        // Setup JWT validation
        when(jwtUtil.validateTokenWithSession(anyString())).thenReturn(TEST_USERNAME);

        // Timezone cache: return null so conversion falls back to UTC with Z
        when(timezoneCache.getTimezoneForPort(anyString())).thenReturn(null);

        when(cutoffConfigRepository.getCutoffConfigDetails(anyString())).thenReturn(Collections.emptyMap());
        when(cutoffConfigRepository.getCutoffConfigDetails(anySet())).thenReturn(Collections.emptyMap());
        when(cutoffCalculator.calculateCutoffDates(any(), any(), any(), any())).thenReturn(Collections.emptyMap());
    }

    @Test
    void testFetchVesselScheduleFromDatabase_Success() {
        // Arrange
        List<Object[]> mockResults = createMockQueryResults();
        when(query.getResultList()).thenReturn(mockResults);

        // Act
        VesselScheduleResponse response = vesselService.fetchVesselScheduleFromDatabase(
            TEST_VESSEL, TEST_FROM_DATE, TEST_TO_DATE, TEST_USERNAME);

        // Assert
        assertNotNull(response);
        assertEquals("200", response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals(TEST_USERNAME, response.getProcessedBy());
        assertNotNull(response.getByVessel());
        assertEquals(2, response.getByVessel().size());

        // Verify query parameters were set correctly
        verify(query).setParameter(1, TEST_VESSEL);
        verify(query).setParameter(2, TEST_FROM_DATE);
        verify(query).setParameter(3, TEST_TO_DATE);
        
        // Verify EntityManager is closed
        verify(entityManager).close();
    }

    @Test
    void testFetchVesselScheduleFromDatabase_NoResults() {
        // Arrange
        when(query.getResultList()).thenReturn(new ArrayList<>());

        // Act
        VesselScheduleResponse response = vesselService.fetchVesselScheduleFromDatabase(
            TEST_VESSEL, TEST_FROM_DATE, TEST_TO_DATE, TEST_USERNAME);

        // Assert
        assertNotNull(response);
        assertEquals("204", response.getCode());
        assertEquals("No schedules found for the given criteria", response.getMessage());
        assertEquals(TEST_USERNAME, response.getProcessedBy());
        assertNull(response.getByVessel());
        
        // Verify EntityManager is closed
        verify(entityManager).close();
    }

    @Test
    void testFetchVesselScheduleFromDatabase_NullResults() {
        // Arrange
        when(query.getResultList()).thenReturn(null);

        // Act
        VesselScheduleResponse response = vesselService.fetchVesselScheduleFromDatabase(
            TEST_VESSEL, TEST_FROM_DATE, TEST_TO_DATE, TEST_USERNAME);

        // Assert
        assertNotNull(response);
        assertEquals("204", response.getCode());
        assertEquals("No schedules found for the given criteria", response.getMessage());
        assertEquals(TEST_USERNAME, response.getProcessedBy());
        assertNull(response.getByVessel());
        
        // Verify EntityManager is closed
        verify(entityManager).close();
    }

    @Test
    void testFetchVesselScheduleFromDatabase_DatabaseError() {
        // Arrange
        when(query.getResultList()).thenThrow(new RuntimeException("Database connection failed"));

        // Act
        VesselScheduleResponse response = vesselService.fetchVesselScheduleFromDatabase(
            TEST_VESSEL, TEST_FROM_DATE, TEST_TO_DATE, TEST_USERNAME);

        // Assert
        assertNotNull(response);
        assertEquals("500", response.getCode());
        assertTrue(response.getMessage().contains("Database error"));
        assertEquals(TEST_USERNAME, response.getProcessedBy());
        assertNull(response.getByVessel());
        
        // Verify EntityManager is closed even on error
        verify(entityManager).close();
    }

    @Test
    void testFetchVesselScheduleFromDatabase_EntityManagerNotOpen() {
        // Arrange
        when(entityManager.isOpen()).thenReturn(false);
        List<Object[]> mockResults = createMockQueryResults();
        when(query.getResultList()).thenReturn(mockResults);

        // Act
        VesselScheduleResponse response = vesselService.fetchVesselScheduleFromDatabase(
            TEST_VESSEL, TEST_FROM_DATE, TEST_TO_DATE, TEST_USERNAME);

        // Assert
        assertNotNull(response);
        assertEquals("200", response.getCode());
        
        // Verify EntityManager.close() is not called when not open
        verify(entityManager, never()).close();
    }

    @Test
    void testSqlQueryStructure() {
        // Act
        vesselService.fetchVesselScheduleFromDatabase(TEST_VESSEL, TEST_FROM_DATE, TEST_TO_DATE, TEST_USERNAME);

        // Assert - Verify the SQL query contains the required elements
        verify(entityManager).createNativeQuery(argThat(sql -> {
            String sqlStr = (String) sql;
            return sqlStr.contains("uvs_scheduleview.vessel=?") &&
                   sqlStr.contains("uvs_scheduleview.arrdate between ? and ?") &&
                   sqlStr.contains("uvs_scheduleview.status<>'D'") &&
                   sqlStr.contains("inner join vesselmaster") &&
                   sqlStr.contains("inner join Ports") &&
                   sqlStr.contains("inner join terminals");
        }));
    }

    @Test
    void testJwtTokenValidation() {
        // Arrange
        String token = "valid.jwt.token";
        when(jwtUtil.validateTokenWithSession(token)).thenReturn(TEST_USERNAME);

        // Act
        String username = vesselService.validateTokenAndExtractUsername(token);

        // Assert
        assertEquals(TEST_USERNAME, username);
        verify(jwtUtil).validateTokenWithSession(token);
    }

    @Test
    void testJwtTokenValidation_RuntimeException() {
        // Arrange
        String token = "invalid.jwt.token";
        when(jwtUtil.validateTokenWithSession(token)).thenThrow(new RuntimeException("Token expired"));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            vesselService.validateTokenAndExtractUsername(token);
        });
        
        assertEquals("Token expired", exception.getMessage());
    }

    @Test
    void testJwtTokenValidation_GeneralException() {
        // Arrange
        String token = "invalid.jwt.token";
        when(jwtUtil.validateTokenWithSession(token)).thenThrow(new IllegalArgumentException("Invalid format"));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            vesselService.validateTokenAndExtractUsername(token);
        });
        
        assertTrue(exception.getMessage().contains("JWT validation failed"));
    }

    @Test
    void testVesselScheduleItemMapping() {
        // Arrange
        List<Object[]> mockResults = createMockQueryResults();
        when(query.getResultList()).thenReturn(mockResults);

        // Act
        VesselScheduleResponse response = vesselService.fetchVesselScheduleFromDatabase(
            TEST_VESSEL, TEST_FROM_DATE, TEST_TO_DATE, TEST_USERNAME);

        // Assert
        List<VesselScheduleItem> items = response.getByVessel();
        assertNotNull(items);
        assertEquals(2, items.size());

        // Verify first item mapping
        VesselScheduleItem firstItem = items.get(0);
        assertEquals("BUX", firstItem.getVessel());
        assertEquals("SINGAPORE", firstItem.getPortName());
        assertEquals("MSC BUSAN EXPRESS", firstItem.getVesselName());
        assertEquals("511E", firstItem.getVoyage());
        assertEquals("PAX", firstItem.getService());
        assertEquals(Bound.E, firstItem.getBound());
        assertEquals("SGSIN", firstItem.getPort());
        assertEquals("SGSINPSA001", firstItem.getTerminal());
        assertEquals("PSA Terminal 1", firstItem.getTerminalName());
        // arrdate/depdate are now OffsetDateTime in port local
        assertNotNull(firstItem.getArrivalDate());
        assertNotNull(firstItem.getDepartureDate());
        assertTrue(firstItem.getArrivalDate().toString().contains("2025-08-05"));
        assertTrue(firstItem.getDepartureDate().toString().contains("2025-08-05"));
        assertEquals(TransportMode.M, firstItem.getType());

        // Verify second item mapping
        VesselScheduleItem secondItem = items.get(1);
        assertEquals("BUX", secondItem.getVessel());
        assertEquals("COLOMBO, SRI LANKA", secondItem.getPortName());
        assertEquals("MSC BUSAN EXPRESS", secondItem.getVesselName());
        assertEquals("511E", secondItem.getVoyage());
    }

    private List<Object[]> createMockQueryResults() {
        // Create mock SQL result rows
        // SQL columns: vessel, portname, vesselname, voyage, service, Bound, port, Terminal, terminalname, arrdate, depdate, type, status, addhoc, isOverlap
        Object[] row1 = {
            "BUX",                         // vessel
            "SINGAPORE",                   // portname
            "MSC BUSAN EXPRESS",          // vesselname
            "511E",                       // voyage
            "PAX",                        // service
            "E",                          // Bound (N=North, E=East, W=West, S=South)
            "SGSIN",                      // port
            "SGSINPSA001",               // Terminal
            "PSA Terminal 1",            // terminalname
            "2025-08-05 06:00:00.0",     // arrdate
            "2025-08-05 18:00:00.0",     // depdate
            "M",                         // type (F=Feeder, M=Mainline)
            "A",                         // status
            "N",                         // addhoc
            "N"                          // isOverlap
        };

        Object[] row2 = {
            "BUX",                         // vessel
            "COLOMBO, SRI LANKA",         // portname
            "MSC BUSAN EXPRESS",          // vesselname
            "511E",                       // voyage
            "PAX",                        // service
            "E",                          // Bound (N=North, E=East, W=West, S=South)
            "LKCMB",                     // port
            "LKCMBJCT00",               // Terminal
            "Jaya Container Terminal",   // terminalname
            "2025-08-07 12:00:00.0",     // arrdate
            "2025-08-07 20:00:00.0",     // depdate
            "M",                         // type (F=Feeder, M=Mainline)
            "A",                         // status
            "N",                         // addhoc
            "N"                          // isOverlap
        };

        return Arrays.asList(row1, row2);
    }
}
