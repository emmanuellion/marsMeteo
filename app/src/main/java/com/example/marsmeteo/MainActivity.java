package com.example.marsmeteo;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.marsmeteo.adapter.WeatherAdapter;
import com.example.marsmeteo.api.NasaApiClient;
import com.example.marsmeteo.model.MarsWeatherData;
import com.example.marsmeteo.model.MarsWeatherData.SolData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button historyButton;
    private Button robotButton;
    private RequestQueue requestQueue;
    private static final String NASA_API_URL = "https://api.nasa.gov/insight_weather/?api_key=BhayclsiilOrFkHcRxSYpCxIMnrqA5Y3rPfoM4um&feedtype=json&ver=1.0";
    private ListView weatherListView;
    private ProgressBar loadingProgressBar;
    private WeatherAdapter adapter;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        historyButton = findViewById(R.id.historyButton);
        robotButton = findViewById(R.id.robotButton);
        weatherListView = findViewById(R.id.weatherListView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        adapter = new WeatherAdapter(this, new ArrayList<>());
        weatherListView.setAdapter(adapter);

        historyButton.setOnClickListener(v -> {
            if (WeatherDataManager.getInstance().hasData()) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            } else {
                Toast.makeText(this, "Chargement des données en cours...", Toast.LENGTH_SHORT).show();
                loadMarsWeather();
            }
        });

        robotButton.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, RobotControlActivity.class)));

        loadMarsWeather();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadMarsWeather() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        weatherListView.setVisibility(View.GONE);

        NasaApiClient.getInstance().getMarsWeather(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Erreur lors de la requête API", e);
                mainHandler.post(() -> {
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, 
                        "Erreur lors du chargement des données", 
                        Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mainHandler.post(() -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,
                            "Erreur serveur: " + response.code(),
                            Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                try {
                    WeatherDataManager weatherManager = WeatherDataManager.getInstance();
                    if (!weatherManager.hasData()) {
                        throw new IOException("Pas de données disponibles");
                    }

                    // Récupérer les sols
                    JSONArray solKeys = weatherManager.getSolKeys();
                    if (solKeys == null) {
                        throw new IOException("Pas de sols disponibles");
                    }

                    // Créer la liste des sols
                    final List<String> sols = new ArrayList<>();
                    for (int i = 0; i < solKeys.length(); i++) {
                        sols.add(solKeys.getString(i));
                    }

                    // Mettre à jour l'UI
                    mainHandler.post(() -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        weatherListView.setVisibility(View.VISIBLE);
                        adapter.clear();
                        adapter.addAll(sols);
                        adapter.notifyDataSetChanged();
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Erreur lors du traitement des données", e);
                    mainHandler.post(() -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,
                            "Erreur lors du traitement des données",
                            Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}