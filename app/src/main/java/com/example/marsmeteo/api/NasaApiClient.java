package com.example.marsmeteo.api;

import android.util.Log;
import com.example.marsmeteo.WeatherDataManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class NasaApiClient {
    private static final String TAG = "NasaApiClient";
    private static final String BASE_URL = "https://api.nasa.gov/";
    private static final String API_KEY = "DEMO_KEY"; // Remplacer par votre clé API

    private static NasaApiClient instance;
    private final OkHttpClient client;

    private NasaApiClient() {
        client = new OkHttpClient.Builder().build();
    }

    public static synchronized NasaApiClient getInstance() {
        if (instance == null) {
            instance = new NasaApiClient();
        }
        return instance;
    }

    public void getMarsWeather(final Callback callback) {
        HttpUrl url = HttpUrl.parse(BASE_URL + "insight_weather/")
                .newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("feedtype", "json")
                .addQueryParameter("ver", "1.0")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Erreur lors de la requête API", e);
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Réponse non réussie: " + response.code());
                    callback.onFailure(call, new IOException("Réponse non réussie: " + response.code()));
                    return;
                }

                String jsonData = response.body().string();
                WeatherDataManager.getInstance().setData(jsonData);
                callback.onResponse(call, response);
            }
        });
    }
} 