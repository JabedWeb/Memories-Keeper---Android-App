package com.example.memories.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memories.R;

import java.util.ArrayList;
import java.util.Collections;

public class PreviewActivity extends AppCompatActivity {

    EditText searchBar;
    GridLayout gridLayout;
    Button btnSort;
    ImageView btnBack;
    SQLiteDatabase db;
    ArrayList<String[]> memoryList;
    boolean isAscending = true;
    String userEmail;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        searchBar = findViewById(R.id.searchBar);
        gridLayout = findViewById(R.id.gridLayout);
        btnBack = findViewById(R.id.btnBack);
        btnSort = findViewById(R.id.btnSort);

        db = openOrCreateDatabase("memories_dbss", MODE_PRIVATE, null);
        memoryList = new ArrayList<>();

        // Get user email from session
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        userEmail = preferences.getString("user_email", "");
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        loadData("");

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadData(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSort.setOnClickListener(v -> {
            isAscending = !isAscending;
            loadData(searchBar.getText().toString());
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadData(String filter) {
        memoryList.clear();
        gridLayout.removeAllViews();

        // Load data specific to logged-in user
        Cursor c = db.rawQuery("SELECT * FROM memories WHERE user_email = ? AND (title LIKE ? OR description LIKE ? OR tags LIKE ? OR date LIKE ?)",
                new String[]{userEmail, "%" + filter + "%", "%" + filter + "%", "%" + filter + "%", "%" + filter + "%"});
        while (c.moveToNext()) {
            @SuppressLint("Range") int id = c.getInt(c.getColumnIndex("id"));
            @SuppressLint("Range") String title = c.getString(c.getColumnIndex("title"));
            @SuppressLint("Range") String description = c.getString(c.getColumnIndex("description"));
            @SuppressLint("Range") String tags = c.getString(c.getColumnIndex("tags"));
            @SuppressLint("Range") String date = c.getString(c.getColumnIndex("date"));
            memoryList.add(new String[]{String.valueOf(id), title, description, tags, date});

        }
        c.close();

        if (!isAscending) {
            memoryList.sort((a, b) -> b[4].compareTo(a[4]));
        } else {
            memoryList.sort((a, b) -> a[4].compareTo(b[4]));
        }

        for (String[] memory : memoryList) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.grid_item, gridLayout, false);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(8, 8, 8, 8);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            itemView.setLayoutParams(params);

            TextView tvTitle = itemView.findViewById(R.id.tvTitle);
            TextView tvDate = itemView.findViewById(R.id.tvDate);
            TextView tvTags = itemView.findViewById(R.id.tvTags);
            tvTitle.setText(memory[1]);
            tvDate.setText(memory[4]);
            tvTags.setText(memory[3]);

            itemView.setOnClickListener(v -> showDetailsDialog(memory));
            gridLayout.addView(itemView);
        }

        // Show empty state if no data
        if (memoryList.isEmpty()) {
            Toast.makeText(this, "No memories found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDetailsDialog(String[] memory) {
        int memoryId = Integer.parseInt(memory[0]); // Retrieve memory ID

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_memory_details, null);
        builder.setView(dialogView);

        // Initialize views
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvDescription = dialogView.findViewById(R.id.tvDescription);
        TextView tvTags = dialogView.findViewById(R.id.tvTags);
        TextView tvDate = dialogView.findViewById(R.id.tvDate);
        ImageView imgClose = dialogView.findViewById(R.id.imgClose);
        Button btnEdit = dialogView.findViewById(R.id.btnEdit);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        // Set values
        tvTitle.setText(memory[1]);
        tvDescription.setText(memory[2]);
        tvTags.setText(memory[3]);
        tvDate.setText(memory[4]);

        AlertDialog dialog = builder.create();

        // Close button
        imgClose.setOnClickListener(v -> dialog.dismiss());

        // Edit button - Pass memoryId
        btnEdit.setOnClickListener(v -> {
            editMemory(memory, memoryId); // Pass ID
            dialog.dismiss();
        });

        // Delete button - Pass memoryId
        btnDelete.setOnClickListener(v -> {
            confirmDelete(memoryId, dialog); // Pass ID
        });

        dialog.show();
    }


    private void editMemory(String[] memory, int memoryId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Memory");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Input Fields
        final EditText titleInput = new EditText(this);
        titleInput.setText(memory[1]); // Title
        layout.addView(titleInput);

        final EditText descriptionInput = new EditText(this);
        descriptionInput.setText(memory[2]); // Description
        layout.addView(descriptionInput);

        final EditText tagsInput = new EditText(this);
        tagsInput.setText(memory[3]); // Tags
        layout.addView(tagsInput);

        final EditText dateInput = new EditText(this);
        dateInput.setText(memory[4]); // Date
        layout.addView(dateInput);

        builder.setView(layout);

        // Update Button with ID
        builder.setPositiveButton("Update", (dialog, which) -> {
            db.execSQL("UPDATE memories SET title = ?, description = ?, tags = ?, date = ? WHERE id = ?",
                    new Object[]{titleInput.getText().toString(),
                            descriptionInput.getText().toString(),
                            tagsInput.getText().toString(),
                            dateInput.getText().toString(),
                            memoryId}); // Use ID for the update
            loadData("");
            Toast.makeText(this, "Memory updated successfully!", Toast.LENGTH_SHORT).show();
        });

        // Cancel Button
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }




    private void confirmDelete(int id, AlertDialog dialog) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Memory")
                .setMessage("Are you sure you want to delete this memory?")
                .setPositiveButton("Delete", (d, which) -> {
                    db.execSQL("DELETE FROM memories WHERE id = ?", new Object[]{id});
                    loadData("");
                    Toast.makeText(this, "Memory deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}
