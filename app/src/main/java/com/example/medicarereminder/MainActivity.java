package com.example.medicarereminder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSharedPreferences("user_prefs", MODE_PRIVATE).contains("email")) {
            startActivity(new Intent(MainActivity.this, dashboard.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        
        etEmail = findViewById(R.id.editTextTextEmailAddress);
        etPassword = findViewById(R.id.editTextTextPassword);
        Button btnLogin = findViewById(R.id.button3);
        
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.checkUser(email, password)) {
                getSharedPreferences("user_prefs", MODE_PRIVATE).edit().putString("email", email).apply();
                Intent intent = new Intent(MainActivity.this, dashboard.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView registerLink = findViewById(R.id.textViewRegisterLink);
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
