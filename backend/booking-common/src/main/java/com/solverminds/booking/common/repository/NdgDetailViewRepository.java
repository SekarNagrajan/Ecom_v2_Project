package com.solverminds.booking.common.repository;

import com.solverminds.booking.common.entity.NdgDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Read-only access to {@code ndgdetail} (documentation DB).
 */
@Repository
public interface NdgDetailViewRepository extends JpaRepository<NdgDetail, Integer> {

    List<NdgDetail> findByDgNoInOrderById(Collection<String> dgNos);
}
