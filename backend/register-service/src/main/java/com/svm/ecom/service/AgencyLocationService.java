package com.svm.ecom.service;

import com.svm.ecom.dto.AgencyLocationDTO;
import com.svm.ecom.dto.AgencyLocationListResponse;
import com.svm.ecom.repo.AgencyLocationRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class AgencyLocationService {

    private static final Logger logger = LoggerFactory.getLogger(AgencyLocationService.class);


    private final AgencyLocationRepository repository;

    public AgencyLocationService(AgencyLocationRepository repository) {
        this.repository = repository;
    }

    /**
     * List all agency locations
     *
     * @return AgencyLocationListResponse with all agency locations
     */
    public AgencyLocationListResponse list() {
        logger.debug("Listing all agency locations");

        // Fetch all data from repository
        List<AgencyLocationDTO> allLocations = repository.fetchAll();
        logger.debug("Retrieved {} total locations from repository", allLocations.size());

        logger.debug("Returning {} total locations", allLocations.size());

        // Build response with all results
        return new AgencyLocationListResponse(allLocations);
    }

}
