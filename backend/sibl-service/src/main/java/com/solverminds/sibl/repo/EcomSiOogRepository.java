package com.solverminds.sibl.repo;

import com.solverminds.sibl.entity.EcomSiOog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEcomSiOogRepository")
public interface EcomSiOogRepository extends JpaRepository<EcomSiOog, Long> {
    List<EcomSiOog> findByeSINo(String eSINo);
    void deleteByeSINo(String eSINo);
}
