package com.solverminds.bl.common.repository;

import com.solverminds.bl.common.entity.BlCommodity;
import com.solverminds.bl.common.entity.BlCommodityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for BlCommodity entity (view-only operations).
 */
@Repository
public interface BlCommodityRepository extends JpaRepository<BlCommodity, BlCommodityId> {

    @Query("SELECT bc FROM BlCommodity bc WHERE bc.blNo = :blNo AND bc.eqpId = :eqpId")
    List<BlCommodity> findByBlNoAndEqpId(@Param("blNo") String blNo, @Param("eqpId") String eqpId);

    @Query("SELECT bc FROM BlCommodity bc WHERE bc.blNo = :blNo")
    List<BlCommodity> findByBlNo(@Param("blNo") String blNo);
}
