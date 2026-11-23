package com.solverminds.bl.common.repository;

import com.solverminds.bl.common.entity.BlCargo;
import com.solverminds.bl.common.entity.BlCargoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for BlCargo entity (view-only operations).
 */
@Repository
public interface BlCargoRepository extends JpaRepository<BlCargo, BlCargoId> {

    @Query("SELECT bc FROM BlCargo bc WHERE bc.blNo = :blNo ORDER BY bc.cargoNo")
    List<BlCargo> findByBlNoOrderByCargoNo(@Param("blNo") String blNo);
}
