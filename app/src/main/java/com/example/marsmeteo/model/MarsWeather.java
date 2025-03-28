package com.example.marsmeteo.model;

import com.google.gson.annotations.SerializedName;

public class MarsWeather {
    @SerializedName("terrestrial_date")
    private String date;
    
    @SerializedName("sol")
    private int sol;
    
    @SerializedName("min_temp")
    private double minTemp;
    
    @SerializedName("max_temp")
    private double maxTemp;
    
    @SerializedName("pressure")
    private double pressure;
    
    @SerializedName("atmo_opacity")
    private String atmosphereOpacity;

    public String getDate() { return date; }
    public int getSol() { return sol; }
    public double getMinTemp() { return minTemp; }
    public double getMaxTemp() { return maxTemp; }
    public double getPressure() { return pressure; }
    public String getAtmosphereOpacity() { return atmosphereOpacity; }
} 