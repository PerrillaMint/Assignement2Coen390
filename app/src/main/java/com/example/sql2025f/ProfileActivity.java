package com.example.sql2025f;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * ProfileActivity displays detailed information about a profile
 * Shows: Name, Surname, ID, GPA, and Access History
 * This is the "V" (View) and "C" (Controller) in MVC
 */
public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, surnameTextView, idTextView, gpaTextView;
    private ListView accessHistoryListView;
    private Button deleteButton;
    private DatabaseHelper dbHelper;
    private long profileId;
    private Profile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityprofile);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable back button
            getSupportActionBar().setTitle("Profile Details");
        }

        // Initialize views
        nameTextView = findViewById(R.id.nameTextView);
        surnameTextView = findViewById(R.id.surnameTextView);
        idTextView = findViewById(R.id.idTextView);
        gpaTextView = findViewById(R.id.gpaTextView);
        accessHistoryListView = findViewById(R.id.accessHistoryListView);
        deleteButton = findViewById(R.id.deleteButton);

        dbHelper = new DatabaseHelper(this);

        // Get the profile ID passed from MainActivity
        Intent intent = getIntent();
        profileId = intent.getLongExtra("PROFILE_ID", -1);

        if (profileId == -1) {
            Toast.makeText(this, "Error: Profile not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Add "opened" access entry
        dbHelper.addAccessEntry(profileId, Config.ACCESS_TYPE_OPENED);

        // Load and display profile data
        loadProfileData();

        // Set up delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfile();
            }
        });
    }

    /**
     * Load profile data from database and display it
     */
    private void loadProfileData() {
        // Get profile from database
        currentProfile = dbHelper.getProfileById(profileId);

        if (currentProfile == null) {
            Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display profile information
        nameTextView.setText("Name: " + currentProfile.getName());
        surnameTextView.setText("Surname: " + currentProfile.getSurname());
        idTextView.setText("ID: " + currentProfile.getProfileId());
        gpaTextView.setText("GPA: " + String.format("%.2f", currentProfile.getGpa()));

        // Load and display access history
        loadAccessHistory();
    }

    /**
     * Load access history for this profile
     */
    private void loadAccessHistory() {
        List<Access> accessList = dbHelper.getAccessHistory(profileId);

        // Convert Access objects to display strings
        List<String> accessStrings = new ArrayList<>();
        for (Access access : accessList) {
            // Format: "timestamp - accessType"
            accessStrings.add(access.getTimestamp() + " - " + access.getAccessType());
        }

        // Set up ListView with ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                accessStrings
        );
        accessHistoryListView.setAdapter(adapter);
    }

    /**
     * Delete the current profile
     */
    private void deleteProfile() {
        boolean success = dbHelper.deleteProfile(profileId);

        if (success) {
            Toast.makeText(this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
            // Return to MainActivity
            finish();
        } else {
            Toast.makeText(this, "Error deleting profile", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Add "closed" access entry when user leaves this activity
        if (currentProfile != null) {
            dbHelper.addAccessEntry(profileId, Config.ACCESS_TYPE_CLOSED);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar back button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
