package com.example.medicarereminder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {
    EditText edtSearch;
    Button btnSearchAction;
    LinearLayout layoutResults;
    ProgressBar progressBar;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_medicine);

        edtSearch = findViewById(R.id.edtSearch);
        btnSearchAction = findViewById(R.id.btnSearchAction);
        layoutResults = findViewById(R.id.layoutResults);
        progressBar = findViewById(R.id.progressBar);

        btnSearchAction.setOnClickListener(v -> {
            String query = edtSearch.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(this, "Please enter a medicine name", Toast.LENGTH_SHORT).show();
                return;
            }
            searchMedicine(query);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, dashboard.class));
                return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(this, ScheduleActivity.class));
                return true;
            } else if (itemId == R.id.nav_search) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void searchMedicine(String query) {
        progressBar.setVisibility(View.VISIBLE);
        layoutResults.removeAllViews();
        
        executorService.execute(() -> {
            String result = "";
            try {
                URL url = new URL("https://rxnav.nlm.nih.gov/REST/drugs.json?name=" + query);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String finalResult = result;
            handler.post(() -> {
                progressBar.setVisibility(View.GONE);
                if (finalResult == null || finalResult.isEmpty()) {
                    Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
                } else {
                    parseAndDisplayResults(finalResult);
                }
            });
        });
    }

    private void parseAndDisplayResults(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (!jsonObject.has("drugGroup")) return;
            JSONObject drugGroup = jsonObject.getJSONObject("drugGroup");
            if (!drugGroup.has("conceptGroup")) {
                Toast.makeText(this, "No medicines found", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray conceptGroups = drugGroup.getJSONArray("conceptGroup");

            for (int i = 0; i < conceptGroups.length(); i++) {
                JSONObject group = conceptGroups.getJSONObject(i);
                if (group.has("conceptProperties")) {
                    JSONArray props = group.getJSONArray("conceptProperties");
                    for (int j = 0; j < props.length(); j++) {
                        JSONObject drug = props.getJSONObject(j);
                        String name = drug.getString("name");
                        addResultView(name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addResultView(String name) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_search_result, layoutResults, false);
        TextView txtName = view.findViewById(R.id.txtName);
        TextView txtPurpose = view.findViewById(R.id.txtPurpose);
        Button btnAdd = view.findViewById(R.id.btnAddMed);

        txtName.setText(name);
        txtPurpose.setText("Commonly used medication");

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMedicineActivity.class);
            intent.putExtra("medName", name);
            startActivity(intent);
        });

        layoutResults.addView(view);
    }
}
