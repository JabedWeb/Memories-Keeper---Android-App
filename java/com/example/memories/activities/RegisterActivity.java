package com.example.memories.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memories.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private SQLiteDatabase db;
    private ImageView togglePasswordVisibility;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize database
        db = openOrCreateDatabase("memories_dbss", MODE_PRIVATE, null);
        createTable();

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvloginLink);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);

        // Toggle Password Visibility
        togglePasswordVisibility.setOnClickListener(v -> {
            int selection = etPassword.getSelectionEnd(); // Save cursor position
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility);
            }
            isPasswordVisible = !isPasswordVisible;
            etPassword.setSelection(selection); // Restore cursor position
        });

        // Register action
        btnRegister.setOnClickListener(v -> registerUser());

        // Switch to Login view
        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void createTable() {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (email TEXT PRIMARY KEY, name TEXT, password TEXT)");
    }

    private void registerUser() {
        String name = etName.getText().toString().trim().toLowerCase();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation for empty fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email format validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password strength validation
        if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[0-9].*")) {
            Toast.makeText(this, "Password must be at least 8 characters long, contain an uppercase letter, and a number!", Toast.LENGTH_LONG).show();
            return;
        }

        // Check if email already exists
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        if (cursor.getCount() > 0) {
            Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show();
        } else {
            // Insert new user
            db.execSQL("INSERT INTO users VALUES (?, ?, ?)", new String[]{email, name, password});
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

            // Create session for auto-login
            SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("user_email", email);
            editor.putLong("session_start_time", System.currentTimeMillis()); // Save session start time
            editor.apply();

            // Redirect to PreviewActivity
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra("user_email", email);
            startActivity(intent);
            finish();
        }
        cursor.close();
    }

    // Session Expiration Logic
    public static boolean isSessionExpired(SharedPreferences preferences) {
        long sessionStartTime = preferences.getLong("session_start_time", 0);
        long currentTime = System.currentTimeMillis();
        long sessionDuration = 30 * 60 * 1000; // 30 minutes
        return (currentTime - sessionStartTime) > sessionDuration;
    }
}
