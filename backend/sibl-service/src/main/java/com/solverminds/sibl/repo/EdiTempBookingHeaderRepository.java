package com.solverminds.sibl.repo;

import com.solverminds.edi.entity.EdiTempBookingHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEdiTempBookingHeaderRepository")
public interface EdiTempBookingHeaderRepository extends JpaRepository<EdiTempBookingHeader, Integer> {

    List<EdiTempBookingHeader> findByBookNo(String bookNo);

    List<EdiTempBookingHeader> findByBookNoAndEdireaderid(String bookNo, String edireaderid);
}
