package com.example.sql2025f;

/**
 * Model class representing a Profile
 * This is the "M" (Model) in MVC structure
 */
public class Profile {
    private long profileId;        // Primary key (8-digit number)
    private String name;           // First name
    private String surname;        // Last name
    private float gpa;             // GPA (0.0 to 4.3)
    private String creationDate;   // Timestamp when profile was created

    // Constructor - used when creating a new profile or reading from database
    public Profile(long profileId, String name, String surname, float gpa, String creationDate) {
        this.profileId = profileId;
        this.name = name;
        this.surname = surname;
        this.gpa = gpa;
        this.creationDate = creationDate;
    }

    // Getters - used to read the values
    public long getProfileId() {
        return profileId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public float getGpa() {
        return gpa;
    }

    public String getCreationDate() {
        return creationDate;
    }

    // Setters - used to modify the values
    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setGpa(float gpa) {
        this.gpa = gpa;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    // Helper method to get full name formatted as "Surname, Name"
    public String getFullName() {
        return surname + ", " + name;
    }
}