package com.example.sql2025f;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected TextView courseListTextView;
    protected FloatingActionButton addCourseFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        courseListTextView = findViewById(R.id.courseListTextView);
        addCourseFAB = findViewById(R.id.addCourseFAB);
        addCourseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
//                dbHelper.addCourse(new Course(-1, "Engineering Design Course", "COEN/ELEC390"));

                addCourseDialog dialog = new addCourseDialog();
                dialog.show(getSupportFragmentManager(), "AddCourseDialogFragment");
                updateTextView();
            }
        });

        updateTextView();
// comment
        // comment2
        //comment 3
    }

    public void updateTextView(){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        List<Course> courses = dbHelper.getAllCourses();

        String courseListString = "";
        for (Course course: courses){
            courseListString += course.getId() + ": " + course.getTitle() + " - " + course.getCode() + "\n";
        }

        courseListTextView.setText(courseListString);
    }
}