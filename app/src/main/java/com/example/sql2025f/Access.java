package com.example.sql2025f;

/**
 * Model class representing an Access record
 * Tracks when profiles are created, opened, closed, or deleted
 * This is the "M" (Model) in MVC structure
 */
public class Access {
    private long accessId;         // Primary key (auto-increment)
    private long profileId;        // Foreign key - links to Profile table
    private String accessType;     // Type: "created", "opened", "closed", "deleted"
    private String timestamp;      // When this access occurred (yyyy-MM-dd @ HH:mm:ss)

    // Constructor
    public Access(long accessId, long profileId, String accessType, String timestamp) {
        this.accessId = accessId;
        this.profileId = profileId;
        this.accessType = accessType;
        this.timestamp = timestamp;
    }

    // Getters
    public long getAccessId() {
        return accessId;
    }

    public long getProfileId() {
        return profileId;
    }

    public String getAccessType() {
        return accessType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setAccessId(long accessId) {
        this.accessId = accessId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Helper method to format display string for ListView
    public String getFormattedEntry() {
        return timestamp + " - " + accessType;
    }
}