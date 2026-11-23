package com.solverminds.sibl.repo;

import com.solverminds.edi.entity.EdiTempBookingCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEdiTempBookingCargoRepository")
public interface EdiTempBookingCargoRepository extends JpaRepository<EdiTempBookingCargo, Long> {

    List<EdiTempBookingCargo> findByBookNo(String bookNo);

    List<EdiTempBookingCargo> findByBookNoAndEdireaderid(String bookNo, String edireaderid);

    void deleteByBookNo(String bookNo);
}
