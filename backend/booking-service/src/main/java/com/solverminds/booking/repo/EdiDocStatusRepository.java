package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiDocStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdiDocStatusRepository extends JpaRepository<EdiDocStatus, Integer> {
    
    /**
     * Find EDI doc status by booking number
     */
    List<EdiDocStatus> findByBookNo(String bookNo);
    
    /**
     * Find EDI doc status by EDI reader ID
     */
    List<EdiDocStatus> findByEdireaderid(String edireaderid);
    
    /**
     * Find EDI doc status by status
     */
    List<EdiDocStatus> findByStatus(String status);
    
    /**
     * Find EDI doc status by booking number and EDI reader ID
     */
    EdiDocStatus findByBookNoAndEdireaderid(String bookNo, String edireaderid);

    /**
     * Update status to 'V' (Void) for all records with status 'R' for a given booking.
     * Used during re-submit from Submitted (S) status to void the previous EDI request.
     * 
     * @param bookNo The booking number
     * @return Number of records updated
     */
    @Modifying
    @Query("UPDATE EdiDocStatus e SET e.status = 'V' WHERE e.bookNo = :bookNo AND e.status = 'R'")
    int updateStatusToVoid(@Param("bookNo") String bookNo);
}

