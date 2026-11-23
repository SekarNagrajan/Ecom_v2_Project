package com.solverminds.sibl.repo;

import com.solverminds.edi.entity.EdiTempBookingCust;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEdiTempBookingCustRepository")
public interface EdiTempBookingCustRepository extends JpaRepository<EdiTempBookingCust, Long> {

    List<EdiTempBookingCust> findByBookNo(String bookNo);

    List<EdiTempBookingCust> findByBookNoAndEdireaderid(String bookNo, String edireaderid);

    void deleteByBookNo(String bookNo);
}
