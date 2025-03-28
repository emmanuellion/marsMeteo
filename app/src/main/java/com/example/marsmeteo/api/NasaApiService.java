package com.example.marsmeteo.api;

import com.example.marsmeteo.model.MarsWeatherResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NasaApiService {
    @GET("insight_weather/?feedtype=json&ver=1.0")
    Call<MarsWeatherResponse> getMarsWeather(
        @Query("api_key") String apiKey
    );
} 