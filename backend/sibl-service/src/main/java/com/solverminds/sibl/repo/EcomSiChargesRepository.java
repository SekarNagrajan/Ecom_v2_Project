package com.solverminds.sibl.repo;

import com.solverminds.sibl.entity.EcomSiCharges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEcomSiChargesRepository")
public interface EcomSiChargesRepository extends JpaRepository<EcomSiCharges, Long> {
    List<EcomSiCharges> findByEsino(String esino);
    void deleteByEsino(String esino);
}
