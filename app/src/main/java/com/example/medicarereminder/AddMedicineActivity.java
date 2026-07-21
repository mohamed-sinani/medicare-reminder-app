package com.example.medicarereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddMedicineActivity extends AppCompatActivity {
    EditText etName, etDosage;
    Button btnPickTime, btnSave;
    TextView txtTime;
    Spinner spinnerFreq;
    DatabaseHelper dbHelper;
    String selectedTime = "";
    int selectedHour, selectedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etMedName);
        etDosage = findViewById(R.id.etDosage);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnSave = findViewById(R.id.btnSaveMed);
        txtTime = findViewById(R.id.txtSelectedTime);
        spinnerFreq = findViewById(R.id.spinnerFreq);

        String medNameFromIntent = getIntent().getStringExtra("medName");
        if (medNameFromIntent != null) {
            etName.setText(medNameFromIntent);
        }

        String[] frequencies = {"Once a day", "Twice a day", "Three times a day", "Every 4 hours"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, frequencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFreq.setAdapter(adapter);

        btnPickTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
                selectedHour = hourOfDay;
                selectedMinute = minuteOfHour;
                selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                txtTime.setText(selectedTime);
            }, hour, minute, true);
            timePickerDialog.show();
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String dosage = etDosage.getText().toString().trim();
            String freq = spinnerFreq.getSelectedItem().toString();

            if (name.isEmpty() || dosage.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = dbHelper.insertData(name, dosage, selectedTime, freq);
            if (isInserted) {
                scheduleNotification(name);
                Toast.makeText(this, "Medicine Saved Successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving medicine", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleNotification(String medName) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("medName", medName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
