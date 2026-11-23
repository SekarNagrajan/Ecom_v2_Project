package com.solverminds.sibl.repo;

import com.solverminds.sibl.entity.EcomSiDg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEcomSiDgRepository")
public interface EcomSiDgRepository extends JpaRepository<EcomSiDg, Long> {
    List<EcomSiDg> findByeSINo(String eSINo);
    void deleteByeSINo(String eSINo);
}
