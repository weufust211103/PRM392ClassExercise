package com.example.studentmanagementapp;

// Major.java
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "majors")
public class Major {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    // Default constructor required by Room
    public Major() {}

    // Constructor with name (id is auto-generated)
    public Major(String name) {
        this.name = name;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
