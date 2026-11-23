package com.solverminds.sibl.repo;

import com.solverminds.sibl.entity.EcomSiRequestMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("siblEcomSiRequestMasterRepository")
public interface EcomSiRequestMasterRepository extends JpaRepository<EcomSiRequestMaster, String> {
}
