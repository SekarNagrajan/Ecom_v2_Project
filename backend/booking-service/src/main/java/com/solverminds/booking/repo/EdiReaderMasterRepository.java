package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiReaderMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EdiReaderMasterRepository extends JpaRepository<EdiReaderMaster, String> {
    
    /**
     * Find EDI reader master by agency code
     */
    List<EdiReaderMaster> findByAgencycode(String agencycode);
    
    /**
     * Find EDI reader master by upload status
     */
    List<EdiReaderMaster> findByUploadstatus(String uploadstatus);
    
    /**
     * Insert EdiReaderMaster record with explicit edireaderid value.
     * Note: IDENTITY_INSERT not needed since edireaderid is VARCHAR, not a numeric IDENTITY column.
     */
    @Modifying
    @Query(value = "INSERT INTO dbo.edireadermaster (edireaderid, ediid, agencycode, locationcode, " +
            "requestdate, uploadstatus, datecr, partnerid) " +
            "VALUES (:edireaderid, :ediid, :agencycode, :locationcode, " +
            ":requestdate, :uploadstatus, :datecr, :partnerid)", 
            nativeQuery = true)
    void insertWithIdentity(
            @Param("edireaderid") String edireaderid,
            @Param("ediid") String ediid,
            @Param("agencycode") String agencycode,
            @Param("locationcode") String locationcode,
            @Param("requestdate") LocalDateTime requestdate,
            @Param("uploadstatus") String uploadstatus,
            @Param("datecr") LocalDateTime datecr,
            @Param("partnerid") String partnerid
    );
}

