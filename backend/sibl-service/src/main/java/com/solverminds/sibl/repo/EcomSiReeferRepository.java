package com.solverminds.sibl.repo;

import com.solverminds.sibl.entity.EcomSiReefer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEcomSiReeferRepository")
public interface EcomSiReeferRepository extends JpaRepository<EcomSiReefer, Long> {
    List<EcomSiReefer> findByeSINo(String eSINo);
    void deleteByeSINo(String eSINo);
}
