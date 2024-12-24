package com.example.memories.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memories.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink, tvForgotPassword;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize database
        db = openOrCreateDatabase("memories_dbss", MODE_PRIVATE, null);
        createTable();

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Login action
        btnLogin.setOnClickListener(v -> loginUser());

        // Switch to Register view
        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        // Forgot password
        tvForgotPassword.setOnClickListener(v -> forgotPassword());
    }

    private void createTable() {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (email TEXT PRIMARY KEY, name TEXT, password TEXT)");
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        if (cursor.getCount() > 0) {
            SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("user_email", email);
            editor.putLong("session_start_time", System.currentTimeMillis()); // Start session timer
            editor.apply();

            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PreviewActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void forgotPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");

        final EditText input = new EditText(this);
        input.setHint("Enter your email");
        builder.setView(input);

        builder.setPositiveButton("Reset", (dialog, which) -> {
            String email = input.getText().toString().trim().toLowerCase();
            Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
            if (cursor.getCount() > 0) {
                showNewPasswordDialog(email);
            } else {
                Toast.makeText(this, "Email not found!", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showNewPasswordDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        final EditText input = new EditText(this);
        input.setHint("Enter new password");
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newPassword = input.getText().toString().trim();
            if (newPassword.length() < 8 || !newPassword.matches(".*[A-Z].*") || !newPassword.matches(".*[0-9].*")) {
                Toast.makeText(this, "Password must be at least 8 characters, include a number and an uppercase letter!", Toast.LENGTH_LONG).show();
                return;
            }
            db.execSQL("UPDATE users SET password = ? WHERE email = ?", new String[]{newPassword, email});
            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public static boolean isSessionExpired(SharedPreferences preferences) {
        long sessionStartTime = preferences.getLong("session_start_time", 0);
        long currentTime = System.currentTimeMillis();
        long sessionDuration = 30 * 60 * 1000; // 30 minutes
        return (currentTime - sessionStartTime) > sessionDuration;
    }
}
