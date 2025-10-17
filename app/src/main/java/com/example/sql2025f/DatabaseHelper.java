package com.example.sql2025f;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private Context context;
    public DatabaseHelper(@Nullable Context context) {
        super(context, Config.DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + Config.TABLE_COURSE + " (" +
                Config.COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Config.COLUMN_COURSE_TITLE + " TEXT NOT NULL," +
                Config.COLUMN_COURSE_CODE + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_COURSE);
        onCreate(sqLiteDatabase);
    }

    public long addCourse(Course course){
        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        String title = course.getTitle();
        String code = course.getCode();

        contentValues.put(Config.COLUMN_COURSE_TITLE, title);
        contentValues.put(Config.COLUMN_COURSE_CODE, code);
        try{
            id = db.insertOrThrow(Config.TABLE_COURSE, null, contentValues);
        }catch (SQLiteException e){
            Toast.makeText(context, "Insert Course Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return id;
    }

    public List<Course> getAllCourses(){
        List<Course> courseList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        cursor = db.query(Config.TABLE_COURSE, null, null, null, null, null, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(Config.COLUMN_COURSE_ID));
                    @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(Config.COLUMN_COURSE_TITLE));
                    @SuppressLint("Range") String code = cursor.getString(cursor.getColumnIndex(Config.COLUMN_COURSE_CODE));

                    Course course = new Course(id, title, code);
                    courseList.add(course);
                } while (cursor.moveToNext());
            }
        }catch (SQLiteException e){
            Toast.makeText(context, "Get all courses error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return courseList;
    }
}
