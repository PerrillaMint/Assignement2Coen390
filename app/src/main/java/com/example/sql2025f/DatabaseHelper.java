package com.example.sql2025f;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DatabaseHelper manages SQLite database operations
 * This follows MVC pattern - handles data persistence
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, Config.DATABASE_NAME, null, Config.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Profile Table
        String CREATE_PROFILE_TABLE = "CREATE TABLE " + Config.TABLE_PROFILE + " (" +
                Config.COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY," +
                Config.COLUMN_PROFILE_NAME + " TEXT NOT NULL," +
                Config.COLUMN_PROFILE_SURNAME + " TEXT NOT NULL," +
                Config.COLUMN_PROFILE_GPA + " REAL NOT NULL," +
                Config.COLUMN_PROFILE_CREATION_DATE + " TEXT NOT NULL);";

        // Create Access Table
        String CREATE_ACCESS_TABLE = "CREATE TABLE " + Config.TABLE_ACCESS + " (" +
                Config.COLUMN_ACCESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Config.COLUMN_ACCESS_PROFILE_ID + " INTEGER NOT NULL," +
                Config.COLUMN_ACCESS_TYPE + " TEXT NOT NULL," +
                Config.COLUMN_ACCESS_TIMESTAMP + " TEXT NOT NULL);";

        db.execSQL(CREATE_PROFILE_TABLE);
        db.execSQL(CREATE_ACCESS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables if they exist and recreate
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_ACCESS);
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_PROFILE);
        onCreate(db);
    }

    /**
     * Get current timestamp formatted as yyyy-MM-dd @ HH:mm:ss
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // ========== PROFILE TABLE OPERATIONS ==========

    /**
     * Add a new profile to the database
     * Also creates an "created" access entry
     * @return true if successful, false otherwise
     */
    public boolean addProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Config.COLUMN_PROFILE_ID, profile.getProfileId());
        values.put(Config.COLUMN_PROFILE_NAME, profile.getName());
        values.put(Config.COLUMN_PROFILE_SURNAME, profile.getSurname());
        values.put(Config.COLUMN_PROFILE_GPA, profile.getGpa());

        String timestamp = getCurrentTimestamp();
        values.put(Config.COLUMN_PROFILE_CREATION_DATE, timestamp);

        try {
            long result = db.insertOrThrow(Config.TABLE_PROFILE, null, values);

            if (result != -1) {
                // Profile added successfully, now add "created" access entry
                addAccessEntry(profile.getProfileId(), Config.ACCESS_TYPE_CREATED, timestamp);
                return true;
            }
        } catch (SQLiteException e) {
            Toast.makeText(context, "Error adding profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    /**
     * Get all profiles from database
     * @param sortByName if true, sort alphabetically by surname; if false, sort by ID
     */
    public List<Profile> getAllProfiles(boolean sortByName) {
        List<Profile> profileList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Determine sort order
        String orderBy = sortByName ?
                Config.COLUMN_PROFILE_SURNAME + " ASC, " + Config.COLUMN_PROFILE_NAME + " ASC" :
                Config.COLUMN_PROFILE_ID + " ASC";

        Cursor cursor = null;
        try {
            cursor = db.query(Config.TABLE_PROFILE, null, null, null, null, null, orderBy);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(Config.COLUMN_PROFILE_ID));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_NAME));
                    @SuppressLint("Range") String surname = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_SURNAME));
                    @SuppressLint("Range") float gpa = cursor.getFloat(cursor.getColumnIndex(Config.COLUMN_PROFILE_GPA));
                    @SuppressLint("Range") String creationDate = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_CREATION_DATE));

                    Profile profile = new Profile(id, name, surname, gpa, creationDate);
                    profileList.add(profile);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Toast.makeText(context, "Error reading profiles: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return profileList;
    }

    /**
     * Get a specific profile by ID
     */
    public Profile getProfileById(long profileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Profile profile = null;

        String selection = Config.COLUMN_PROFILE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(profileId) };

        Cursor cursor = null;
        try {
            cursor = db.query(Config.TABLE_PROFILE, null, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(Config.COLUMN_PROFILE_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_NAME));
                @SuppressLint("Range") String surname = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_SURNAME));
                @SuppressLint("Range") float gpa = cursor.getFloat(cursor.getColumnIndex(Config.COLUMN_PROFILE_GPA));
                @SuppressLint("Range") String creationDate = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_CREATION_DATE));

                profile = new Profile(id, name, surname, gpa, creationDate);
            }
        } catch (SQLiteException e) {
            Toast.makeText(context, "Error reading profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return profile;
    }

    /**
     * Delete a profile from database
     * Adds a "deleted" access entry (does not delete access history)
     */
    public boolean deleteProfile(long profileId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // First add "deleted" access entry
            addAccessEntry(profileId, Config.ACCESS_TYPE_DELETED, getCurrentTimestamp());

            // Then delete the profile
            String whereClause = Config.COLUMN_PROFILE_ID + " = ?";
            String[] whereArgs = { String.valueOf(profileId) };

            int rowsDeleted = db.delete(Config.TABLE_PROFILE, whereClause, whereArgs);
            return rowsDeleted > 0;
        } catch (SQLiteException e) {
            Toast.makeText(context, "Error deleting profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Check if a profile ID already exists
     */
    public boolean profileIdExists(long profileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM " + Config.TABLE_PROFILE +
                " WHERE " + Config.COLUMN_PROFILE_ID + " = ?";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(profileId)});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // ========== ACCESS TABLE OPERATIONS ==========

    /**
     * Add an access entry to the database
     */
    public boolean addAccessEntry(long profileId, String accessType, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Config.COLUMN_ACCESS_PROFILE_ID, profileId);
        values.put(Config.COLUMN_ACCESS_TYPE, accessType);
        values.put(Config.COLUMN_ACCESS_TIMESTAMP, timestamp);

        try {
            long result = db.insertOrThrow(Config.TABLE_ACCESS, null, values);
            return result != -1;
        } catch (SQLiteException e) {
            Toast.makeText(context, "Error adding access entry: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Add an access entry with current timestamp
     */
    public boolean addAccessEntry(long profileId, String accessType) {
        return addAccessEntry(profileId, accessType, getCurrentTimestamp());
    }

    /**
     * Get all access entries for a specific profile
     */
    public List<Access> getAccessHistory(long profileId) {
        List<Access> accessList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = Config.COLUMN_ACCESS_PROFILE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(profileId) };
        String orderBy = Config.COLUMN_ACCESS_TIMESTAMP + " ASC";  // Oldest first

        Cursor cursor = null;
        try {
            cursor = db.query(Config.TABLE_ACCESS, null, selection, selectionArgs, null, null, orderBy);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") long accessId = cursor.getLong(cursor.getColumnIndex(Config.COLUMN_ACCESS_ID));
                    @SuppressLint("Range") long profId = cursor.getLong(cursor.getColumnIndex(Config.COLUMN_ACCESS_PROFILE_ID));
                    @SuppressLint("Range") String accessType = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ACCESS_TYPE));
                    @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ACCESS_TIMESTAMP));

                    Access access = new Access(accessId, profId, accessType, timestamp);
                    accessList.add(access);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Toast.makeText(context, "Error reading access history: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return accessList;
    }

    /**
     * Get total count of profiles in database
     */
    public int getProfileCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + Config.TABLE_PROFILE;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }
}