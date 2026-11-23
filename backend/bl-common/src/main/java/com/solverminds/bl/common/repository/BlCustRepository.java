package com.solverminds.bl.common.repository;

import com.solverminds.bl.common.entity.BlCust;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for BlCust entity (view-only operations).
 */
@Repository
public interface BlCustRepository extends JpaRepository<BlCust, Long> {

    @Query("SELECT bc FROM BlCust bc WHERE bc.bl.blNo = :blNo ORDER BY bc.cid")
    List<BlCust> findByBlNoOrderByCid(@Param("blNo") String blNo);

    @Query(value = "SELECT COUNT(1) FROM bl(NOLOCK) b "
            + "INNER JOIN bl_cust(NOLOCK) bc ON bc.bl_no = b.bl_no "
            + "WHERE bc.custtypeid IN ('AP','BP') AND b.bl_no = :blNo AND bc.custid = :custId",
            nativeQuery = true)
    long countByBlNoAndCustId(@Param("blNo") String blNo, @Param("custId") String custId);
}
