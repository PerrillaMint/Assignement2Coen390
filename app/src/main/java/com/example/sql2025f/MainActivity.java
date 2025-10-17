package com.example.sql2025f;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity - Main screen of the app
 * Displays list of all profiles with two display modes:
 * - By Name (alphabetical by surname)
 * - By ID (increasing order)
 * This is the "V" (View) and "C" (Controller) in MVC
 */
public class MainActivity extends AppCompatActivity {

    private TextView headerTextView;
    private ListView profileListView;
    private FloatingActionButton addProfileFAB;
    private DatabaseHelper dbHelper;

    private boolean displayByName = true;  // true = by name, false = by ID
    private List<Profile> currentProfiles;  // Store current profile list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile Manager");
        }

        // Initialize views
        headerTextView = findViewById(R.id.headerTextView);
        profileListView = findViewById(R.id.profileListView);
        addProfileFAB = findViewById(R.id.addProfileFAB);

        dbHelper = new DatabaseHelper(this);

        // Set up FAB to open dialog
        addProfileFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertProfileDialog dialog = new InsertProfileDialog();
                dialog.show(getSupportFragmentManager(), "InsertProfileDialog");
            }
        });

        // Set up ListView click listener
        profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected profile
                Profile selectedProfile = currentProfiles.get(position);

                // Open ProfileActivity with the selected profile ID
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("PROFILE_ID", selectedProfile.getProfileId());
                startActivity(intent);
            }
        });

        // Load and display profiles
        updateListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning from ProfileActivity
        updateListView();
    }

    /**
     * Update the ListView with current profiles
     */
    public void updateListView() {
        // Get profiles from database (sorted based on display mode)
        currentProfiles = dbHelper.getAllProfiles(displayByName);

        // Create display strings for ListView
        List<String> displayStrings = new ArrayList<>();

        for (int i = 0; i < currentProfiles.size(); i++) {
            Profile profile = currentProfiles.get(i);
            String lineNumber = (i + 1) + ". ";  // Line number starts at 1

            String displayText;
            if (displayByName) {
                // Display format: "1. Surname, Name"
                displayText = lineNumber + profile.getFullName();
            } else {
                // Display format: "1. 12345678"
                displayText = lineNumber + profile.getProfileId();
            }

            displayStrings.add(displayText);
        }

        // Set up adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayStrings
        );
        profileListView.setAdapter(adapter);

        // Update header text
        updateHeaderText();
    }

    /**
     * Update the header TextView with count and display mode
     */
    private void updateHeaderText() {
        int count = dbHelper.getProfileCount();
        String displayMode = displayByName ? "By Name" : "By ID";

        String headerText = "Total Profiles: " + count + "\n" +
                "Display Mode: " + displayMode;

        headerTextView.setText(headerText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu with the toggle action
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Set the correct title for the toggle button
        MenuItem toggleItem = menu.findItem(R.id.action_toggle_display);
        if (toggleItem != null) {
            toggleItem.setTitle(displayByName ? "By ID" : "By Name");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_display) {
            // Toggle display mode
            displayByName = !displayByName;

            // Update menu item text
            item.setTitle(displayByName ? "By ID" : "By Name");

            // Refresh the list
            updateListView();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}