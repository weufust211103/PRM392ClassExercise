package com.example.studentmanagementapp;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(tableName = "students",
        foreignKeys = @ForeignKey(entity = Major.class,
                parentColumns = "id",
                childColumns = "majorId"))
public class Student {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String date;
    private String gender;
    private String email;
    private String address;
    private int majorId;

    // Default constructor required by Room
    public Student() {}

    // Constructor with all fields except id (which is auto-generated)
    public Student(String name, String date, String gender, String email, String address, int majorId) {
        this.name = name;
        this.date = date;
        this.gender = gender;
        this.email = email;
        this.address = address;
        this.majorId = majorId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public int getMajorId() {
        return majorId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }
}
