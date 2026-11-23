package com.svm.ecom.service;

import com.svm.ecom.dto.AgencyLocationDTO;
import com.svm.ecom.dto.AgencyLocationListResponse;
import com.svm.ecom.repo.AgencyLocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AgencyLocationServiceTest {

    @Mock
    private AgencyLocationRepository repository;

    private AgencyLocationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AgencyLocationService(repository);
    }

    @Test
    void list_shouldReturnAllLocations() {
        // Arrange
        List<AgencyLocationDTO> allLocations = Arrays.asList(
            new AgencyLocationDTO("INNSA", "Nhava Sheva", "INDMUM"),
            new AgencyLocationDTO("AEJEA", "Jebel Ali", "AEJEA1"),
            new AgencyLocationDTO("USNYC", "New York", "USNYC1")
        );
        when(repository.fetchAll()).thenReturn(allLocations);

        // Act
        AgencyLocationListResponse response = service.list();

        // Assert
        assertEquals(3, response.getData().size());
        assertEquals("INNSA", response.getData().get(0).getPortCode());
        assertEquals("AEJEA", response.getData().get(1).getPortCode());
        assertEquals("USNYC", response.getData().get(2).getPortCode());
    }

    @Test
    void list_shouldReturnEmptyResponse_whenRepositoryReturnsEmpty() {
        // Arrange
        when(repository.fetchAll()).thenReturn(Collections.emptyList());

        // Act
        AgencyLocationListResponse response = service.list();

        // Assert
        assertEquals(0, response.getData().size());
    }
}
