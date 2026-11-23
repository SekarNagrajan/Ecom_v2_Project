package com.solverminds.ecom.register.repo;

import com.solverminds.ecom.register.entity.EcomUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EcomUserDetailsRepository extends JpaRepository<EcomUserDetails, Integer> {

    /**
     * Find user by email ID (case-insensitive)
     */
    Optional<EcomUserDetails> findByEmailIDIgnoreCase(String emailID);

    /**
     * Check if an email ID already exists in ecomuserdetails (case-insensitive)
     */
    boolean existsByEmailIDIgnoreCase(String emailID);

    /**
     * Find user by email ID and status
     */
    Optional<EcomUserDetails> findByEmailIDAndStatus(String emailID, String status);
    /**
     * Increment wrong password attempts count
     */
    @Modifying
    @Query("update EcomUserDetails e set e.wrgPassAtmpt = coalesce(e.wrgPassAtmpt,0) + 1 where e.id = :id")
    int incWrongAttempts(@Param("id") Integer id);

    /**
     * Reset attempts and unlock user on successful login
     */
    @Modifying
    @Query("update EcomUserDetails e set e.wrgPassAtmpt = 0, e.lockedStatus = 'N', e.logindate = :now where e.id = :id")
    int resetOnSuccess(@Param("id") Integer id, @Param("now") LocalDateTime now);

    /**
     * Lock user account
     */
    @Modifying
    @Query("update EcomUserDetails e set e.lockedStatus = 'Y', e.lockedTime = :now where e.id = :id")
    int lock(@Param("id") Integer id, @Param("now") LocalDateTime now);

    /**
     * Set refresh token for user with creation timestamp
     */
    @Modifying
    @Query("update EcomUserDetails e set e.apptoken = :token, e.apptokenCreatedAt = :createdAt where e.id = :id")
    int setRefreshToken(@Param("id") Integer id, @Param("token") String token, @Param("createdAt") LocalDateTime createdAt);

    /**
     * Set logout time for user
     */
    @Modifying
    @Query("update EcomUserDetails e set e.logoutdate = :now where e.id = :id")
    int setLogout(@Param("id") Integer id, @Param("now") LocalDateTime now);

    /**
     * Find user by refresh token
     */
    Optional<EcomUserDetails> findByApptoken(String apptoken);

    /**
     * Auto-unlock user if lock time has expired
     */
    @Modifying
    @Query("update EcomUserDetails e set e.lockedStatus = 'N', e.wrgPassAtmpt = 0 where e.lockedStatus = 'Y' and e.lockedTime < :unlockTime")
    int autoUnlockExpiredLocks(@Param("unlockTime") LocalDateTime unlockTime);

    /**
     * Update password and last password update date for user
     */
    @Modifying
    @Query("update EcomUserDetails e set e.password = :password, e.lastpwdup = :lastpwdup where e.id = :id")
    int updatePassword(@Param("id") Integer id, @Param("password") String password, @Param("lastpwdup") LocalDateTime lastpwdup);
}
