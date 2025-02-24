package com.eventlinkr.userservice.utils;

public final class LoggingFormat {
    private LoggingFormat() {
    } // Prevent instantiation

    // Error logging formats
    public static final String ERROR_OPERATION = "Error {}: {}";
    public static final String ERROR_WITH_ID = "Error {} with ID {}: {}";

    // Info logging formats
    public static final String INFO_CREATED = "Successfully created {} with ID: {}";
    public static final String INFO_UPDATED = "Successfully updated {} with ID: {}";
    public static final String INFO_DELETED = "Successfully deleted {} with ID: {}";
    public static final String INFO_RETRIEVED = "Successfully retrieved {} with ID: {}";

    // Debug logging formats
    public static final String DEBUG_PROCESSING = "Processing {}: {}";
    public static final String DEBUG_FOUND = "Found {} for {}";
}