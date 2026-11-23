package com.solverminds.sibl.repo;

import com.solverminds.edi.entity.EdiTempBookingRouting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEdiTempBookingRoutingRepository")
public interface EdiTempBookingRoutingRepository extends JpaRepository<EdiTempBookingRouting, Integer> {

    EdiTempBookingRouting findByBookNo(String bookNo);

    List<EdiTempBookingRouting> findByBookNoAndEdireaderid(String bookNo, String edireaderid);

    void deleteByBookNo(String bookNo);
}
