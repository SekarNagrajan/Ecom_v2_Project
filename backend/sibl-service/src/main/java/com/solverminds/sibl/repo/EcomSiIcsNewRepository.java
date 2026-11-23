package com.solverminds.sibl.repo;

import com.solverminds.sibl.entity.EcomSiIcsNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEcomSiIcsNewRepository")
public interface EcomSiIcsNewRepository extends JpaRepository<EcomSiIcsNew, Long> {
    List<EcomSiIcsNew> findByeSINo(String eSINo);
    void deleteByeSINo(String eSINo);
}
