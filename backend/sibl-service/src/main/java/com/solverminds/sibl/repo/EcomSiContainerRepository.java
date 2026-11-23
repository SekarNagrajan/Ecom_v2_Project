package com.solverminds.sibl.repo;

import com.solverminds.sibl.entity.EcomSiContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEcomSiContainerRepository")
public interface EcomSiContainerRepository extends JpaRepository<EcomSiContainer, Long> {
    
    /**
     * Find all containers for a specific SI
     * @param eSINo the SI number
     * @return list of containers for the SI
     */
    @Query("SELECT c FROM EcomSiContainer c WHERE c.eSINo = :eSINo")
    List<EcomSiContainer> findByeSINo(@Param("eSINo") String eSINo);

    void deleteByeSINo(String eSINo);
}
