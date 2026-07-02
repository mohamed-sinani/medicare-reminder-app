package com.example.medicarereminder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddMedicineActivity extends AppCompatActivity {
    EditText etName, etDosage;
    Button btnPickTime, btnSave;
    TextView txtTime;
    Spinner spinnerFreq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        etName = findViewById(R.id.etMedName);
        etDosage = findViewById(R.id.etDosage);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnSave = findViewById(R.id.btnSaveMed);
        txtTime = findViewById(R.id.txtSelectedTime);
        spinnerFreq = findViewById(R.id.spinnerFreq);

        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Save functionality disabled (Navigation only)", Toast.LENGTH_SHORT).show();
            finish();
        });

    }
}
