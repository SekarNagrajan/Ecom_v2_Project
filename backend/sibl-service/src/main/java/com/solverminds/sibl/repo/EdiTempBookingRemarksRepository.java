package com.solverminds.sibl.repo;

import com.solverminds.edi.entity.EdiTempBookingRemarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEdiTempBookingRemarksRepository")
public interface EdiTempBookingRemarksRepository extends JpaRepository<EdiTempBookingRemarks, Long> {

    List<EdiTempBookingRemarks> findByBookNo(String bookNo);

    List<EdiTempBookingRemarks> findByBookNoAndEdireaderid(String bookNo, String edireaderid);

    void deleteByBookNo(String bookNo);
}
