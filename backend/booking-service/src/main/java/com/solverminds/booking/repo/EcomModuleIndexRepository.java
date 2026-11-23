package com.solverminds.booking.repo;

import com.solverminds.booking.entity.EcomModuleIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface EcomModuleIndexRepository extends JpaRepository<EcomModuleIndex, String> {
    
    /**
     * Find module index by mdlId with pessimistic write lock
     * This ensures thread-safe access when generating booking numbers
     * @param mdlId the module ID
     * @return Optional containing the EcomModuleIndex if found, empty otherwise
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EcomModuleIndex e WHERE e.mdlId = :mdlId")
    Optional<EcomModuleIndex> findByMdlIdWithLock(@Param("mdlId") String mdlId);
}

