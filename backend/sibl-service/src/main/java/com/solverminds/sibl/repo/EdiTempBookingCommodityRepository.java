package com.solverminds.sibl.repo;

import com.solverminds.edi.entity.EdiTempBookingCommodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEdiTempBookingCommodityRepository")
public interface EdiTempBookingCommodityRepository extends JpaRepository<EdiTempBookingCommodity, Long> {

    List<EdiTempBookingCommodity> findByBookNo(String bookNo);

    List<EdiTempBookingCommodity> findByBookNoAndEdireaderid(String bookNo, String edireaderid);

    void deleteByBookNo(String bookNo);
}
