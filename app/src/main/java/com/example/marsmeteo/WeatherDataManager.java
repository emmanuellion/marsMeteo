package com.example.marsmeteo;

import android.util.Log;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class WeatherDataManager {
    private static final String TAG = "WeatherDataManager";
    private static WeatherDataManager instance;
    private JSONObject rawData;
    private final Gson gson = new Gson();

    private WeatherDataManager() {
    }

    public static WeatherDataManager getInstance() {
        if (instance == null) {
            instance = new WeatherDataManager();
        }
        return instance;
    }

    public void setData(String jsonData) {
        try {
            this.rawData = new JSONObject(jsonData);
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors du parsing JSON", e);
            this.rawData = null;
        }
    }

    public boolean hasData() {
        return rawData != null;
    }

    public JSONObject getSolData(String solKey) {
        try {
            if (rawData != null && rawData.has(solKey)) {
                return rawData.getJSONObject(solKey);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors de la récupération des données du sol " + solKey, e);
        }
        return null;
    }

    public JSONArray getSolKeys() {
        try {
            if (rawData != null && rawData.has("sol_keys")) {
                return rawData.getJSONArray("sol_keys");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors de la récupération des sol_keys", e);
        }
        return null;
    }

    public double getAverageTemp(String solKey) {
        try {
            JSONObject solData = getSolData(solKey);
            if (solData != null && solData.has("AT")) {
                return solData.getJSONObject("AT").getDouble("av");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors de la récupération de la température moyenne", e);
        }
        return Double.NaN;
    }

    public double getAveragePressure(String solKey) {
        try {
            JSONObject solData = getSolData(solKey);
            if (solData != null && solData.has("PRE")) {
                return solData.getJSONObject("PRE").getDouble("av");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors de la récupération de la pression moyenne", e);
        }
        return Double.NaN;
    }

    public String getSeason(String solKey) {
        try {
            JSONObject solData = getSolData(solKey);
            if (solData != null && solData.has("Season")) {
                return solData.getString("Season");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors de la récupération de la saison", e);
        }
        return null;
    }

    public JSONObject getWindData(String solKey) {
        try {
            JSONObject solData = getSolData(solKey);
            if (solData != null && solData.has("WD")) {
                return solData.getJSONObject("WD");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors de la récupération des données de vent", e);
        }
        return null;
    }

    public JSONObject getWindSpeed(String solKey) {
        try {
            JSONObject solData = getSolData(solKey);
            if (solData != null && solData.has("HWS")) {
                return solData.getJSONObject("HWS");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors de la récupération de la vitesse du vent", e);
        }
        return null;
    }

    public JSONObject getData() {
        return rawData;
    }
} 