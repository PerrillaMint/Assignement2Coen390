package com.example.sql2025f;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

/**
 * DialogFragment for inserting new profiles
 * Validates all inputs before saving to database
 */
public class InsertProfileDialog extends DialogFragment {

    private EditText nameEditText, surnameEditText, idEditText, gpaEditText;
    private Button saveButton, cancelButton;
    private DatabaseHelper dbHelper;

    public InsertProfileDialog() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentinsertprofiledialog, container, false);

        // Initialize views
        nameEditText = view.findViewById(R.id.nameEditText);
        surnameEditText = view.findViewById(R.id.surnameEditText);
        idEditText = view.findViewById(R.id.idEditText);
        gpaEditText = view.findViewById(R.id.gpaEditText);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        dbHelper = new DatabaseHelper(getContext());

        // Cancel button - just close dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Save button - validate and save
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        return view;
    }

    /**
     * Validate inputs and save profile to database
     */
    private void saveProfile() {
        // Get input values
        String name = nameEditText.getText().toString().trim();
        String surname = surnameEditText.getText().toString().trim();
        String idString = idEditText.getText().toString().trim();
        String gpaString = gpaEditText.getText().toString().trim();

        // Validate: Check if any field is empty
        if (name.isEmpty() || surname.isEmpty() || idString.isEmpty() || gpaString.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_LONG).show();
            return;
        }

        // Validate: Name and Surname should not contain special characters (only letters and spaces)
        if (!name.matches("[a-zA-Z ]+") || !surname.matches("[a-zA-Z ]+")) {
            Toast.makeText(getContext(), "Name and Surname can only contain letters and spaces", Toast.LENGTH_LONG).show();
            return;
        }

        // Validate and parse Profile ID
        long profileId;
        try {
            profileId = Long.parseLong(idString);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Profile ID must be a valid number", Toast.LENGTH_LONG).show();
            return;
        }

        // Validate: ID must be 8 digits (10000000 to 99999999)
        if (profileId < 10000000 || profileId > 99999999) {
            Toast.makeText(getContext(), "Profile ID must be an 8-digit number (10000000-99999999)", Toast.LENGTH_LONG).show();
            return;
        }

        // Validate: Check if ID already exists
        if (dbHelper.profileIdExists(profileId)) {
            Toast.makeText(getContext(), "Profile ID already exists! Choose a different ID.", Toast.LENGTH_LONG).show();
            return;
        }

        // Validate and parse GPA
        float gpa;
        try {
            gpa = Float.parseFloat(gpaString);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "GPA must be a valid number", Toast.LENGTH_LONG).show();
            return;
        }

        // Validate: GPA must be between 0.0 and 4.3
        if (gpa < 0.0f || gpa > 4.3f) {
            Toast.makeText(getContext(), "GPA must be between 0.0 and 4.3", Toast.LENGTH_LONG).show();
            return;
        }

        // All validations passed - create and save profile
        Profile newProfile = new Profile(profileId, name, surname, gpa, "");  // Creation date set by database

        boolean success = dbHelper.addProfile(newProfile);

        if (success) {
            Toast.makeText(getContext(), "Profile added successfully!", Toast.LENGTH_SHORT).show();

            // Update MainActivity's ListView
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateListView();
            }

            dismiss();
        } else {
            Toast.makeText(getContext(), "Error adding profile", Toast.LENGTH_LONG).show();
        }
    }
}