package com.example.marsmeteo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.marsmeteo.adapter.WeatherAdapter;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";
    private ListView weatherListView;
    private ProgressBar loadingProgressBar;
    private WeatherAdapter adapter;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        weatherListView = findViewById(R.id.weatherListView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        loadingProgressBar.setVisibility(View.VISIBLE);
        weatherListView.setVisibility(View.GONE);

        try {
            WeatherDataManager weatherManager = WeatherDataManager.getInstance();
            if (!weatherManager.hasData()) {
                throw new Exception("Pas de données disponibles");
            }

            // Récupérer les sols
            JSONArray solKeys = weatherManager.getSolKeys();
            if (solKeys == null) {
                throw new Exception("Pas de sols disponibles");
            }

            // Créer la liste des sols
            List<String> sols = new ArrayList<>();
            for (int i = 0; i < solKeys.length(); i++) {
                sols.add(solKeys.getString(i));
            }

            // Initialiser l'adapter
            adapter = new WeatherAdapter(this, sols);
            weatherListView.setAdapter(adapter);

            loadingProgressBar.setVisibility(View.GONE);
            weatherListView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du chargement des données", e);
            Toast.makeText(this, "Erreur lors du chargement des données", Toast.LENGTH_LONG).show();
            finish();
        }
    }
} 