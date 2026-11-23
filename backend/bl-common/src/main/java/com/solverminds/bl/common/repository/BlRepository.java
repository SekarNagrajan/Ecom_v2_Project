package com.solverminds.bl.common.repository;

import com.solverminds.bl.common.entity.Bl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Bl entity (view-only operations).
 */
@Repository
public interface BlRepository extends JpaRepository<Bl, String> {

    Optional<Bl> findByBlNo(String blNo);
}
