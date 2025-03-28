package com.example.marsmeteo.api;

import com.example.marsmeteo.model.MarsWeatherData;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;
import java.io.IOException;

public class NasaApiClient {
    private static final String BASE_URL = "https://api.nasa.gov/insight_weather/";
    private static final String API_KEY = "BhayclsiilOrFkHcRxSYpCxIMnrqA5Y3rPfoM4um";
    private static NasaApiClient instance;
    private final OkHttpClient client;
    private final Gson gson;

    private NasaApiClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .build();
            
        gson = new Gson();
    }

    public static NasaApiClient getInstance() {
        if (instance == null) {
            instance = new NasaApiClient();
        }
        return instance;
    }

    public void getMarsWeather(final Callback<MarsWeatherData> callback) {
        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("feedtype", "json")
            .addQueryParameter("ver", "1.0")
            .build();

        Request request = new Request.Builder()
            .url(url)
            .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onFailure(null, e);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    MarsWeatherData data = gson.fromJson(json, MarsWeatherData.class);
                    callback.onResponse(null, Response.success(data));
                } else {
                    callback.onFailure(null, new IOException("Unexpected response " + response));
                }
            }
        });
    }
} 