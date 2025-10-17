package com.example.sql2025f;

/**
 * Configuration class containing all database constants
 * This helps maintain consistent naming throughout the app
 */
public class Config {
    // Database name
    public static final String DATABASE_NAME = "ProfileDatabase.db";
    public static final int DATABASE_VERSION = 1;

    // Profile Table
    public static final String TABLE_PROFILE = "Profile";
    public static final String COLUMN_PROFILE_ID = "ProfileID";           // Primary key
    public static final String COLUMN_PROFILE_NAME = "Name";              // First name
    public static final String COLUMN_PROFILE_SURNAME = "Surname";        // Last name
    public static final String COLUMN_PROFILE_GPA = "GPA";                // GPA value
    public static final String COLUMN_PROFILE_CREATION_DATE = "CreationDate"; // Timestamp

    // Access Table
    public static final String TABLE_ACCESS = "Access";
    public static final String COLUMN_ACCESS_ID = "AccessID";             // Primary key
    public static final String COLUMN_ACCESS_PROFILE_ID = "ProfileID";    // Foreign key
    public static final String COLUMN_ACCESS_TYPE = "AccessType";         // created/opened/closed/deleted
    public static final String COLUMN_ACCESS_TIMESTAMP = "Timestamp";     // When it happened

    // Access Types (constants for consistency)
    public static final String ACCESS_TYPE_CREATED = "created";
    public static final String ACCESS_TYPE_OPENED = "opened";
    public static final String ACCESS_TYPE_CLOSED = "closed";
    public static final String ACCESS_TYPE_DELETED = "deleted";
}