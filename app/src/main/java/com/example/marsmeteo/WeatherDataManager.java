package com.example.marsmeteo;

import android.util.Log;
import com.example.marsmeteo.model.MarsWeatherData;

public class WeatherDataManager {
    private static final String TAG = "WeatherDataManager";
    private static WeatherDataManager instance;
    private MarsWeatherData weatherData;

    private WeatherDataManager() {}

    public static synchronized WeatherDataManager getInstance() {
        if (instance == null) {
            instance = new WeatherDataManager();
        }
        return instance;
    }

    public void setWeatherData(MarsWeatherData data) {
        if (data == null) {
            Log.e(TAG, "Tentative de définir des données null");
            return;
        }
        if (data.getSolKeys() == null || data.getSolKeys().isEmpty()) {
            Log.e(TAG, "Les données n'ont pas de sols valides");
            return;
        }
        if (data.getSols() == null || data.getSols().isEmpty()) {
            Log.e(TAG, "Les données n'ont pas de données de sols");
            return;
        }
        Log.d(TAG, "Stockage de " + data.getSolKeys().size() + " sols");
        this.weatherData = data;
    }

    public MarsWeatherData getWeatherData() {
        if (weatherData == null) {
            Log.e(TAG, "Tentative de récupérer des données null");
            return null;
        }
        return weatherData;
    }

    public boolean hasData() {
        boolean hasValidData = weatherData != null && 
                             weatherData.getSolKeys() != null && 
                             !weatherData.getSolKeys().isEmpty() &&
                             weatherData.getSols() != null &&
                             !weatherData.getSols().isEmpty();
        
        if (!hasValidData) {
            Log.e(TAG, "Données invalides ou manquantes");
        }
        
        return hasValidData;
    }
} 