package com.example.medicarereminder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    EditText etName, etEmail, etPassword;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.editTextName);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        Button btnRegister = findViewById(R.id.buttonRegister);
        TextView tvLoginLink = findViewById(R.id.textViewLoginLink);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = dbHelper.insertUser(name, email, password);
            if (isInserted) {
                getSharedPreferences("user_prefs", MODE_PRIVATE).edit().putString("email", email).apply();
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, dashboard.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
