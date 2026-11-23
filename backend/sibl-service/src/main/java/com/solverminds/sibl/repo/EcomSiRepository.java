package com.solverminds.sibl.repo;

import com.solverminds.sibl.entity.EcomSi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("siblEcomSiRepository")
public interface EcomSiRepository extends JpaRepository<EcomSi, String> {
    /**
     * Find an EcomSi by its SI number (primary key).
     * @param eSINo the SI number
     * @return Optional containing the EcomSi if found, empty otherwise
     */
    Optional<EcomSi> findByeSINo(String eSINo);
    
    /**
     * Find an EcomSi by BL number.
     * @param blno the BL number
     * @return Optional containing the EcomSi if found, empty otherwise
     */
    Optional<EcomSi> findByBlno(String blno);

    /**
     * Check if an SI already exists for the given booking number (used to prevent multiple SI per booking on create).
     * @param bookno the booking number (book_no in ecomSI)
     * @return true if at least one ecomSI row exists with this bookno
     */
    boolean existsByBookno(String bookno);
}
