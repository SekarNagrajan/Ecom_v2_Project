package com.solverminds.booking.common.repository;

import com.solverminds.booking.common.entity.NdgMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Read-only access to {@code ndgmaster} (documentation DB).
 */
@Repository
public interface NdgMasterViewRepository extends JpaRepository<NdgMaster, String> {

    List<NdgMaster> findByPartDgRefNo(String partDgRefNo);
}
