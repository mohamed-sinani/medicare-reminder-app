package com.example.medicarereminder;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class dashboard extends AppCompatActivity {
    LinearLayout container;
    TextView txtProgress;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        container = findViewById(R.id.medicinesContainer);
        txtProgress = findViewById(R.id.textViewProgress);
        TextView txtUserName = findViewById(R.id.textViewUserName);

        String email = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("email", "user@example.com");
        String name = dbHelper.getUserName(email);
        txtUserName.setText(name + ",");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadMedicines();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(this, ScheduleActivity.class));
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        findViewById(R.id.buttonAdd).setOnClickListener(v -> {
            startActivity(new Intent(this, AddMedicineActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedicines();
    }

    private void loadMedicines() {
        container.removeAllViews();
        Cursor cursor = dbHelper.getAllData();
        int count = cursor.getCount();
        txtProgress.setText("0 / " + count);

        if (count == 0) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No medicines added yet.");
            emptyView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            container.addView(emptyView);
            return;
        }

        while (cursor.moveToNext()) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_medicine, container, false);
            TextView name = view.findViewById(R.id.tvMedName);
            TextView dosage = view.findViewById(R.id.tvDosage);
            TextView time = view.findViewById(R.id.tvTime);
            TextView freq = view.findViewById(R.id.tvFrequency);
            View btnDelete = view.findViewById(R.id.ivDelete);

            String id = cursor.getString(0);
            String medName = cursor.getString(1);
            String medDosage = cursor.getString(2);
            String medTime = cursor.getString(3);
            String medFreq = cursor.getString(4);

            name.setText(medName);
            dosage.setText(medDosage);
            time.setText(medTime);
            freq.setText(medFreq);

            btnDelete.setOnClickListener(v -> {
                dbHelper.deleteData(id);
                loadMedicines();
                Toast.makeText(this, "Medicine deleted", Toast.LENGTH_SHORT).show();
            });

            container.addView(view);
        }
        cursor.close();
    }
}
