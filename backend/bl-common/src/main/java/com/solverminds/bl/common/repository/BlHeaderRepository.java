package com.solverminds.bl.common.repository;

import com.solverminds.bl.common.entity.BlHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for BlHeader entity (view-only operations).
 */
@Repository
public interface BlHeaderRepository extends JpaRepository<BlHeader, String> {

    Optional<BlHeader> findByBlNo(String blNo);
}
