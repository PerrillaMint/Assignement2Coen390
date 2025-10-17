package com.example.sql2025f;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class addCourseDialog extends DialogFragment {

    protected EditText courseTitleEditText, courseCodeEditText;
    protected Button cancelButton, saveButton;

    public addCourseDialog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_add_course_dialog, container, false);

        courseTitleEditText = view.findViewById(R.id.courseTitleEditText);
        courseCodeEditText = view.findViewById(R.id.courseCodeEditText);
        cancelButton = view.findViewById(R.id.cancelButton);
        saveButton = view.findViewById(R.id.saveButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = courseTitleEditText.getText().toString();
                String code = courseCodeEditText.getText().toString();

                if (title.isEmpty() | code.isEmpty()){
                    Toast.makeText(getContext().getApplicationContext(), "Fields cannot be empty!", Toast.LENGTH_LONG).show();
                }
                else{
                    DatabaseHelper dbHelper = new DatabaseHelper(getContext().getApplicationContext());
                    dbHelper.addCourse(new Course(-1, title, code));
                    Toast.makeText(getContext().getApplicationContext(), "Added course successfully!", Toast.LENGTH_LONG).show();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainActivity)getActivity()).updateTextView();
                        }
                    });

                    dismiss();
                }
            }
        });

        return view;
    }
}