package com.solverminds.booking.dto.validation;

/**
 * Validation groups for conditional validation based on operation type.
 * Used to differentiate validation rules between create and update operations.
 */
public class ValidationGroups {

    /**
     * Validation group for create/new booking operations.
     * containerNo is optional - will be auto-generated if not provided.
     */
    public interface CreateBooking {}

    /**
     * Validation group for update/amendment booking operations.
     * containerNo is required to identify which container to modify.
     */
    public interface UpdateBooking {}

    /**
     * Validation group for VGM PUT upsert operations.
     */
    public interface VgmUpsert {}
}

