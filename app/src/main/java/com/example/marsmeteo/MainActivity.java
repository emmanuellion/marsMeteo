package com.example.marsmeteo;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.marsmeteo.model.MarsWeatherData;
import com.example.marsmeteo.model.MarsWeatherData.SolData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button historyButton;
    private Button robotButton;
    private RequestQueue requestQueue;
    private static final String NASA_API_URL = "https://api.nasa.gov/insight_weather/?api_key=BhayclsiilOrFkHcRxSYpCxIMnrqA5Y3rPfoM4um&feedtype=json&ver=1.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        historyButton = findViewById(R.id.historyButton);
        robotButton = findViewById(R.id.robotButton);

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
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Pas de connexion Internet disponible", Toast.LENGTH_LONG).show();
            return;
        }

        // Désactiver le bouton pendant le chargement
        historyButton.setEnabled(false);
        Toast.makeText(this, "Chargement des données...", Toast.LENGTH_SHORT).show();

        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            NASA_API_URL,
            null,
            response -> {
                try {
                    Log.d(TAG, "Réponse reçue: " + response.toString());
                    
                    Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                    
                    MarsWeatherData weatherData = gson.fromJson(response.toString(), MarsWeatherData.class);
                    
                    if (weatherData != null && weatherData.getSolKeys() != null && !weatherData.getSolKeys().isEmpty()) {
                        Log.d(TAG, "Données parsées avec succès. Nombre de sols: " + weatherData.getSolKeys().size());
                        
                        // Vérifier que chaque sol a des données
                        boolean hasValidData = true;
                        for (String solKey : weatherData.getSolKeys()) {
                            SolData solData = weatherData.getSols().get(solKey);
                            if (solData == null || solData.getAtmosphericTemp() == null || solData.getPressure() == null) {
                                Log.e(TAG, "Données manquantes pour le sol " + solKey);
                                hasValidData = false;
                                break;
                            }
                        }
                        
                        if (hasValidData) {
                            WeatherDataManager.getInstance().setWeatherData(weatherData);
                            Toast.makeText(MainActivity.this,
                                "Données météo Mars téléchargées avec succès ! " +
                                weatherData.getSolKeys().size() + " sols disponibles",
                                Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this,
                                "Erreur : certaines données sont manquantes",
                                Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e(TAG, "Les données météo ou les clés de sol sont nulles");
                        Toast.makeText(MainActivity.this,
                            "Erreur : données météo invalides",
                            Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erreur lors du traitement des données: " + e.getMessage(), e);
                    Toast.makeText(MainActivity.this,
                        "Erreur lors du traitement des données : " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                } finally {
                    // Réactiver le bouton
                    historyButton.setEnabled(true);
                }
            },
            error -> {
                Log.e(TAG, "Erreur réseau: " + error.getMessage(), error);
                String errorMessage = error.getMessage();
                if (errorMessage == null) {
                    errorMessage = "Erreur inconnue lors de la requête";
                }
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                // Réactiver le bouton
                historyButton.setEnabled(true);
            }
        );

        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
            30000, // timeout en ms (30 secondes)
            2, // nombre max de retries
            1.0f // multiplicateur de backoff
        ));

        requestQueue.add(request);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}