package com.example.studentmanagementapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudentManagement.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_STUDENT = "students";
    private static final String TABLE_MAJOR = "majors";

    // Common column names
    private static final String KEY_ID = "id";

    // STUDENT Table - column names
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_MAJOR_ID = "major_id";

    // MAJOR Table - column names
    private static final String KEY_MAJOR_NAME = "major_name";

    // Create table statements
    private static final String CREATE_TABLE_STUDENT = "CREATE TABLE " + TABLE_STUDENT +
            "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," +
            KEY_DATE + " TEXT," + KEY_GENDER + " TEXT," + KEY_EMAIL + " TEXT," +
            KEY_ADDRESS + " TEXT," + KEY_MAJOR_ID + " INTEGER, " +
            "FOREIGN KEY(" + KEY_MAJOR_ID + ") REFERENCES " + TABLE_MAJOR + "(" + KEY_ID + "))";


    private static final String CREATE_TABLE_MAJOR = "CREATE TABLE " + TABLE_MAJOR +
            "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MAJOR_NAME + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MAJOR);
        db.execSQL(CREATE_TABLE_STUDENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAJOR);
        onCreate(db);
    }

    // Add a new major
    public long addMajor(String majorName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MAJOR_NAME, majorName);
        long id = db.insert(TABLE_MAJOR, null, values);
        db.close();
        return id;
    }
    public String getMajorNameById(long majorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MAJOR, new String[]{KEY_MAJOR_NAME}, KEY_ID + " = ?", new String[]{String.valueOf(majorId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String majorName = cursor.getString(0);
            cursor.close();
            return majorName;
        }
        return null;
    }

    // Add a new student
    public long addStudent(String name, String date, String gender, String email, String address, long majorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DATE, date);
        values.put(KEY_GENDER, gender);
        values.put(KEY_EMAIL, email);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_MAJOR_ID, majorId);

        // Check if values are properly inserted
        long id = db.insert(TABLE_STUDENT, null, values);
        db.close();
        return id;
    }


    // Get all students with their majors
    public List<Map<String, String>> getAllStudentsWithMajors() {
        List<Map<String, String>> studentList = new ArrayList<>();
        String selectQuery = "SELECT s.*, m." + KEY_MAJOR_NAME + " FROM " + TABLE_STUDENT + " s " +
                "LEFT JOIN " + TABLE_MAJOR + " m ON s." + KEY_MAJOR_ID + " = m." + KEY_ID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> student = new HashMap<>();
                student.put("id", cursor.getString(0));
                student.put("name", cursor.getString(1));
                student.put("date", cursor.getString(2));
                student.put("gender", cursor.getString(3));
                student.put("email", cursor.getString(4));
                student.put("address", cursor.getString(5));
                student.put("major", cursor.getString(7)); // major name
                studentList.add(student);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return studentList;
    }

    // Update a student
    public int updateStudent(long id, String name, String date, String gender, String email, String address, long majorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DATE, date);
        values.put(KEY_GENDER, gender);
        values.put(KEY_EMAIL, email);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_MAJOR_ID, majorId);
        return db.update(TABLE_STUDENT, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Delete a student
    public void deleteStudent(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENT, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    // Update a major
    public int updateMajor(long id, String majorName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MAJOR_NAME, majorName);
        return db.update(TABLE_MAJOR, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Delete a major
    public void deleteMajor(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MAJOR, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<String> getAllMajorNames() {
        List<String> majorNames = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_MAJOR_NAME + " FROM " + TABLE_MAJOR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                majorNames.add(cursor.getString(0)); // Major name
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return majorNames;
    }
    public List<Map<String, String>> getAllMajors() {
        List<Map<String, String>> majorsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM majors", null);

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> major = new HashMap<>();
                major.put("id", cursor.getString(cursor.getColumnIndex("id")));
                major.put("name", cursor.getString(cursor.getColumnIndex("name")));
                majorsList.add(major);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return majorsList;
    }


    public long getMajorIdByName(String majorName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_MAJOR + " WHERE " + KEY_MAJOR_NAME + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{majorName});
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        return -1;
    }
}
