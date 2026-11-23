package com.solverminds.sibl.repo;

import com.solverminds.edi.entity.EdiTempBookingConf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEdiTempBookingConfRepository")
public interface EdiTempBookingConfRepository extends JpaRepository<EdiTempBookingConf, Long> {

    List<EdiTempBookingConf> findByBookNo(String bookNo);

    List<EdiTempBookingConf> findByBookNoAndEdireaderid(String bookNo, String edireaderid);

    void deleteByBookNo(String bookNo);
}
