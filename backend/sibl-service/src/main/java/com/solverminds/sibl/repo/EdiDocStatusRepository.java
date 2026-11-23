package com.solverminds.sibl.repo;

import com.solverminds.edi.entity.EdiDocStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEdiDocStatusRepository")
public interface EdiDocStatusRepository extends JpaRepository<EdiDocStatus, Integer> {

    List<EdiDocStatus> findByBookNo(String bookNo);

    List<EdiDocStatus> findByEdireaderid(String edireaderid);

    List<EdiDocStatus> findByStatus(String status);

    EdiDocStatus findByBookNoAndEdireaderid(String bookNo, String edireaderid);
}
