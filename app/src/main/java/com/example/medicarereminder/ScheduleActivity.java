package com.example.medicarereminder;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    LinearLayout container;
    DatabaseHelper dbHelper;
    TextView txtScheduleDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);

        dbHelper = new DatabaseHelper(this);
        container = findViewById(R.id.scheduleContainer);
        txtScheduleDate = findViewById(R.id.txtScheduleDate);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupDayHighlight();
        loadSchedule();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, dashboard.class));
                return true;
            } else if (itemId == R.id.nav_schedule) {
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
    }

    private void setupDayHighlight() {
        Calendar calendar = Calendar.getInstance();
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
        txtScheduleDate.setText(sdf.format(calendar.getTime()));

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        
        int layoutId = -1;
        int textId = -1;

        switch (dayOfWeek) {
            case Calendar.MONDAY: layoutId = R.id.dayMon; textId = R.id.tvMon; break;
            case Calendar.TUESDAY: layoutId = R.id.dayTue; textId = R.id.tvTue; break;
            case Calendar.WEDNESDAY: layoutId = R.id.dayWed; textId = R.id.tvWed; break;
            case Calendar.THURSDAY: layoutId = R.id.dayThu; textId = R.id.tvThu; break;
            case Calendar.FRIDAY: layoutId = R.id.dayFri; textId = R.id.tvFri; break;
            case Calendar.SATURDAY: layoutId = R.id.daySat; textId = R.id.tvSat; break;
            case Calendar.SUNDAY: layoutId = R.id.daySun; textId = R.id.tvSun; break;
        }

        if (layoutId != -1) {
            findViewById(layoutId).setBackgroundResource(R.drawable.day_bg_selected);
            ((TextView)findViewById(textId)).setTextColor(Color.WHITE);
        }
    }

    private void loadSchedule() {
        container.removeAllViews();
        Cursor cursor = dbHelper.getAllData();
        if (cursor.getCount() == 0) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Your schedule is empty.");
            emptyView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            container.addView(emptyView);
            return;
        }

        while (cursor.moveToNext()) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_medicine, container, false);
            TextView name = view.findViewById(R.id.tvMedName);
            TextView dosageFreq = view.findViewById(R.id.tvDosageFreq);
            TextView time = view.findViewById(R.id.tvTime);

            String medName = cursor.getString(1);
            String dosage = cursor.getString(2);
            String medTime = cursor.getString(3);
            String freq = cursor.getString(4);

            name.setText(medName);
            dosageFreq.setText(dosage + " • " + freq);
            time.setText(medTime);

            container.addView(view);
        }
        cursor.close();
    }
}
