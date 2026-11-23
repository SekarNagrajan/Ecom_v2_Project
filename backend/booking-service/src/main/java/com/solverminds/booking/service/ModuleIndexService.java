package com.solverminds.booking.service;

import com.solverminds.booking.entity.EcomModuleIndex;
import com.solverminds.booking.repo.EcomModuleIndexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for generating sequential IDs using the ecomMdlIndex table.
 * Uses ECOM transaction manager for proper database access.
 */
@Service
@Transactional(transactionManager = "ecomTransactionManager")
public class ModuleIndexService {

    private static final Logger logger = LoggerFactory.getLogger(ModuleIndexService.class);

    private final EcomModuleIndexRepository moduleIndexRepo;

    public ModuleIndexService(EcomModuleIndexRepository moduleIndexRepo) {
        this.moduleIndexRepo = moduleIndexRepo;
    }

    /**
     * Generate next sequential ID for a given module
     * @param moduleId The module ID (e.g., "BKG", "EDI")
     * @return Generated ID in format: prefix + index
     */
    public String generateNextId(String moduleId) {
        // Find module index with pessimistic lock to prevent concurrent updates
        EcomModuleIndex moduleIndex = moduleIndexRepo.findByMdlIdWithLock(moduleId)
            .orElseThrow(() -> new IllegalStateException(
                "Module index not found for module: " + moduleId + 
                ". Please ensure ecomMdlIndex table has entry for this module."));
        
        // Get current values
        String prefix = moduleIndex.getMdlPrefix() != null ? moduleIndex.getMdlPrefix() : moduleId;
        Integer currentIndex = moduleIndex.getMdlIndex() != null ? moduleIndex.getMdlIndex() : 0;
        
        // Increment the index
        int newIndex = currentIndex + 1;
        moduleIndex.setMdlIndex(newIndex);
        
        // Update the database with new index
        moduleIndexRepo.save(moduleIndex);
        
        // Generate and return ID: prefix + newIndex
        String generatedId = prefix + newIndex;
        
        logger.debug("Generated {} ID: {}", moduleId, generatedId);
        
        return generatedId;
    }

    /**
     * Generate EDI reader ID
     * Uses mdlId = "EDI" from ecomMdlIndex table
     * @return Generated EDI reader ID (e.g., "EBKON1234")
     */
    public String generateEdiReaderId() {
        return generateNextId("EDI");
    }

    /**
     * Generate booking number
     * Uses mdlId = "BKG" from ecomMdlIndex table
     * @return Generated booking number (e.g., "BKON1234")
     */
    public String generateBookingNumber() {
        return generateNextId("BKG");
    }
}

