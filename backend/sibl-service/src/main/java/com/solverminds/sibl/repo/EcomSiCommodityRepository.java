package com.solverminds.sibl.repo;

import com.solverminds.sibl.entity.EcomSiCommodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEcomSiCommodityRepository")
public interface EcomSiCommodityRepository extends JpaRepository<EcomSiCommodity, Long> {
    
    /**
     * Find all commodities for a specific SI
     * @param eSINo the SI number
     * @return list of commodities for the SI
     */
    @Query("SELECT c FROM EcomSiCommodity c WHERE c.eSINo = :eSINo")
    List<EcomSiCommodity> findByeSINo(@Param("eSINo") String eSINo);

    void deleteByeSINo(String eSINo);
}
