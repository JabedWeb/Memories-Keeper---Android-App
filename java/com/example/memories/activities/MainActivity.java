package com.example.memories.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memories.R;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText etTitle, etTags, etDate, etDescription;
    Button btnInsert, btnDisplay, btnExit;
    String title, description, tags, date;
    SQLiteDatabase db;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTitle = findViewById(R.id.etTitle);
        etTags = findViewById(R.id.etTags);
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        btnInsert = findViewById(R.id.btnInsert);
        btnDisplay = findViewById(R.id.btnDisplay);
        btnExit = findViewById(R.id.btnExit);

        // Retrieve user email from session
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        userEmail = preferences.getString("user_email", "");
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Create or open database
        db = openOrCreateDatabase("memories_dbss", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS memories (id INTEGER PRIMARY KEY AUTOINCREMENT, user_email TEXT, title VARCHAR, description VARCHAR, tags VARCHAR, date VARCHAR, image VARCHAR);");


        etDate.setOnClickListener(v -> showDatePicker());

        // Insert data
        btnInsert.setOnClickListener(v -> {
            title = etTitle.getText().toString().trim();
            description = etDescription.getText().toString().trim();
            tags = etTags.getText().toString().trim();
            date = etDate.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                db.execSQL("INSERT INTO memories (user_email, title, description, tags, date, image) VALUES(?, ?, ?, ?, ?, ?)",
                        new String[]{userEmail, title, description, tags, date, ""});
                Toast.makeText(getApplicationContext(), "Memory Added Successfully", Toast.LENGTH_SHORT).show();
                clearFields();
            }
        });

        // Display data
        btnDisplay.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PreviewActivity.class);
            startActivity(intent);
            finish();
        });

        // Exit the app
        btnExit.setOnClickListener(view -> System.exit(0));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> etDate.setText(String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void clearFields() {
        etTitle.setText("");
        etTags.setText("");
        etDate.setText("");
        etDescription.setText("");
    }
}
