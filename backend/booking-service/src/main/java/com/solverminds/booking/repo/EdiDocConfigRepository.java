package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiDocConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdiDocConfigRepository extends JpaRepository<EdiDocConfig, Integer> {
    
    /**
     * Find EDI doc config by EDI ID
     */
    EdiDocConfig findByEdiId(String ediId);
    
    /**
     * Find all active EDI doc configs
     */
    List<EdiDocConfig> findByStatus(String status);
}

