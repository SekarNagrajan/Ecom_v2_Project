package com.solverminds.sibl.repo;

import com.solverminds.edi.entity.EdiTempBookingReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEdiTempBookingReferenceRepository")
public interface EdiTempBookingReferenceRepository extends JpaRepository<EdiTempBookingReference, Integer> {

    List<EdiTempBookingReference> findByBookNo(String bookNo);

    List<EdiTempBookingReference> findByBookNoAndEdireaderid(String bookNo, String edireaderid);

    void deleteByBookNo(String bookNo);
}
