package com.example.studentmanagementapp;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView listViewStudents;
    private SimpleAdapter adapter;
    private ListView listViewMajors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        listViewStudents = findViewById(R.id.listViewStudents);
        listViewMajors = findViewById(R.id.listViewMajors);

        Button btnAddMajor = findViewById(R.id.btnAddMajor);
        Button btnAddStudent = findViewById(R.id.btnAddStudent);

        btnAddMajor.setOnClickListener(v -> showAddMajorDialog());
        btnAddStudent.setOnClickListener(v -> showAddStudentDialog());

        updateStudentList();
    }
    private class MajorAdapter extends ArrayAdapter<Map<String, String>> {
        private List<Map<String, String>> majors;

        public MajorAdapter(Context context, List<Map<String, String>> majors) {
            super(context, 0, majors);
            this.majors = majors;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Map<String, String> major = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_major, parent, false);
            }

            TextView textViewMajorName = convertView.findViewById(R.id.textViewMajorName);
            textViewMajorName.setText(major.get("name")); // Replace with the actual key for the major name

            return convertView;
        }
    }
    private class StudentAdapter extends ArrayAdapter<Map<String, String>> {
        private List<Map<String, String>> students;
        private Context context;

        public StudentAdapter(Context context, List<Map<String, String>> students) {
            super(context, 0, students);
            this.context = context;
            this.students = students;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Map<String, String> student = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_student, parent, false);
            }

            TextView textViewName = convertView.findViewById(R.id.textViewName);
            TextView textViewMajor = convertView.findViewById(R.id.textViewMajor);
            TextView textViewEmail = convertView.findViewById(R.id.textViewEmail);

            textViewName.setText(student.get("name"));
            textViewMajor.setText("Major: " + student.get("major"));
            textViewEmail.setText("Email: " + student.get("email"));

            return convertView;
        }
    }
    private void updateStudentList() {
        new Thread(() -> {
            List<Map<String, String>> students = dbHelper.getAllStudentsWithMajors();
            runOnUiThread(() -> {
                StudentAdapter adapter = new StudentAdapter(this, students);
                listViewStudents.setAdapter(adapter);

                // Set an OnItemClickListener to show the options dialog when an item is clicked
                listViewStudents.setOnItemClickListener((parent, view, position, id) -> {
                    Map<String, String> selectedStudent = students.get(position);
                    showStudentOptionsDialog(selectedStudent); // Show options dialog for the selected student
                });
            });
        }).start();
    }

    private void updateMajorList() {
        new Thread(() -> {
            List<Map<String, String>> majors = dbHelper.getAllMajors(); // Implement this method
            runOnUiThread(() -> {
                MajorAdapter majorAdapter = new MajorAdapter(MainActivity.this, majors);
                listViewMajors.setAdapter(majorAdapter);
                listViewMajors.setOnItemClickListener((parent, view, position, id) -> {
                    // Show options to edit or delete the selected major
                    showMajorOptionsDialog(majors.get(position));
                });
            });
        }).start();
    }
    private void showMajorOptionsDialog(Map<String, String> major) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Major Options")
                .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        // Edit major
                        showUpdateMajorDialog(major);
                    } else if (which == 1) {
                        // Delete major
                        dbHelper.deleteMajor(Long.parseLong(major.get("id"))); // Ensure the ID key exists
                        updateMajorList();
                    }
                });
        builder.show();
    }
    private void showUpdateMajorDialog(Map<String, String> major) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Major");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_major_dialog, null); // Create this layout
        builder.setView(dialogView);

        EditText inputMajorName = dialogView.findViewById(R.id.inputMajorName);
        inputMajorName.setText(major.get("name")); // Get the current major name

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newMajorName = inputMajorName.getText().toString();
            dbHelper.updateMajor(Long.parseLong(major.get("id")), newMajorName); // Implement this in your DB helper
            updateMajorList();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }



    private void showAddMajorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Major");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String majorName = input.getText().toString();
            long id = dbHelper.addMajor(majorName);
            if (id != -1) {
                Toast.makeText(MainActivity.this, "Major added successfully", Toast.LENGTH_SHORT).show();
                // Update the list if needed
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }





    private void showAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Student");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_student_dialog, null);
        builder.setView(dialogView);

        // Get references to the input fields
        EditText inputName = dialogView.findViewById(R.id.inputStudentName);
        EditText inputDate = dialogView.findViewById(R.id.inputStudentDate);
        EditText inputGender = dialogView.findViewById(R.id.inputStudentGender);
        EditText inputEmail = dialogView.findViewById(R.id.inputStudentEmail);
        EditText inputAddress = dialogView.findViewById(R.id.inputStudentAddress);
        Spinner spinnerMajor = dialogView.findViewById(R.id.spinnerMajor);

        // Populate the Spinner with majors from the database
        List<String> majors = dbHelper.getAllMajorNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, majors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMajor.setAdapter(adapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Get values from the inputs
            String name = inputName.getText().toString();
            String date = inputDate.getText().toString();
            String gender = inputGender.getText().toString();
            String email = inputEmail.getText().toString();
            String address = inputAddress.getText().toString();
            String majorName = spinnerMajor.getSelectedItem().toString();

            // Validate inputs
            if (name.isEmpty() || date.isEmpty() || gender.isEmpty() || email.isEmpty() || address.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get major ID by name
            long majorId = dbHelper.getMajorIdByName(majorName);
            if (majorId == -1) {
                Toast.makeText(MainActivity.this, "Invalid Major selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert student into the database
            long studentId = dbHelper.addStudent(name, date, gender, email, address, majorId);
            if (studentId != -1) {
                Toast.makeText(MainActivity.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                updateStudentList();
            } else {
                Toast.makeText(MainActivity.this, "Failed to add student", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showStudentOptionsDialog(final Map<String, String> student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student Options")
                .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        // Edit student
                        showUpdateStudentDialog(student); // Call a method to update the student
                    } else if (which == 1) {
                        // Delete student using ID from the map
                        long studentId = Long.parseLong(student.get("id")); // Ensure the "id" key exists

                        showDeleteStudentDialog(studentId); // Call delete dialog with student ID
                    }
                });
        builder.show();
    }

    private void showUpdateStudentDialog(final Map<String, String> student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Student");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_student_dialog, null);
        builder.setView(dialogView);

        // Get references to the input fields
        EditText inputName = dialogView.findViewById(R.id.inputStudentName);
        EditText inputDate = dialogView.findViewById(R.id.inputStudentDate);
        EditText inputGender = dialogView.findViewById(R.id.inputStudentGender);
        EditText inputEmail = dialogView.findViewById(R.id.inputStudentEmail);
        EditText inputAddress = dialogView.findViewById(R.id.inputStudentAddress);
        Spinner spinnerMajor = dialogView.findViewById(R.id.spinnerMajor);

        // Populate the input fields with current data from the map
        inputName.setText(student.get("name"));
        inputDate.setText(student.get("date"));
        inputGender.setText(student.get("gender"));
        inputEmail.setText(student.get("email"));
        inputAddress.setText(student.get("address"));

        // Populate the spinner with majors
        List<String> majors = dbHelper.getAllMajorNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, majors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMajor.setAdapter(adapter);

        // Set the spinner to the current major
        String studentMajor = student.get("major");
        int majorPosition = majors.indexOf(studentMajor);
        if (majorPosition != -1) {
            spinnerMajor.setSelection(majorPosition);
        }

        builder.setPositiveButton("Update", (dialog, which) -> {
            // Get updated values from the inputs
            String name = inputName.getText().toString();
            String date = inputDate.getText().toString();
            String gender = inputGender.getText().toString();
            String email = inputEmail.getText().toString();
            String address = inputAddress.getText().toString();
            String majorName = spinnerMajor.getSelectedItem().toString();

            // Validate inputs
            if (name.isEmpty() || date.isEmpty() || gender.isEmpty() || email.isEmpty() || address.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get major ID by name
            long majorId = dbHelper.getMajorIdByName(majorName);
            if (majorId == -1) {
                Toast.makeText(MainActivity.this, "Invalid Major selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update student in the database using the map's ID
            long studentId = Long.parseLong(student.get("id"));
            int rowsAffected = dbHelper.updateStudent(studentId, name, date, gender, email, address, majorId);
            if (rowsAffected > 0) {
                Toast.makeText(MainActivity.this, "Student updated successfully", Toast.LENGTH_SHORT).show();
                updateStudentList();
            } else {
                Toast.makeText(MainActivity.this, "Failed to update student", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void showDeleteStudentDialog(final long studentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Student")
                .setMessage("Are you sure you want to delete this student?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Delete student from database
                    dbHelper.deleteStudent(studentId);
                    Toast.makeText(MainActivity.this, "Student deleted", Toast.LENGTH_SHORT).show();
                    updateStudentList();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .show();
    }

}