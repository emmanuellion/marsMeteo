package com.example.marsmeteo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.marsmeteo.adapter.SolAdapter;
import com.example.marsmeteo.model.MarsWeatherData;

public class HistoryActivity extends AppCompatActivity {
    private ListView solsListView;
    private SolAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        solsListView = findViewById(R.id.solsListView);
        loadMarsWeather();

        solsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String solKey = adapter.getItem(position);
                Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_SOL, solKey);
                startActivity(intent);
            }
        });
    }

    private void loadMarsWeather() {
        WeatherDataManager weatherManager = WeatherDataManager.getInstance();
        if (!weatherManager.hasData()) {
            Toast.makeText(this, "Aucune donnée météo disponible. Veuillez retourner à l'écran principal.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        MarsWeatherData weatherData = weatherManager.getWeatherData();
        adapter = new SolAdapter(this, weatherData.getSolKeys(), weatherData.getSols());
        solsListView.setAdapter(adapter);
    }
} 