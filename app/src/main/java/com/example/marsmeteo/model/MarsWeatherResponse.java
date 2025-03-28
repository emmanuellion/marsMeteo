package com.example.marsmeteo.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class MarsWeatherResponse {
    @SerializedName("sol_keys")
    private List<String> solKeys;

    private Map<String, SolData> sols;

    public List<String> getSolKeys() {
        return solKeys;
    }

    public Map<String, SolData> getSols() {
        return sols;
    }

    public static class SolData {
        @SerializedName("AT")
        private TemperatureData temperature;
        
        @SerializedName("HWS")
        private WindData windSpeed;
        
        @SerializedName("PRE")
        private PressureData pressure;

        @SerializedName("WD")
        private Map<String, WindDirectionData> windDirections;
        
        @SerializedName("First_UTC")
        private String firstUtc;
        
        @SerializedName("Last_UTC")
        private String lastUtc;

        public TemperatureData getTemperature() {
            return temperature;
        }

        public WindData getWindSpeed() {
            return windSpeed;
        }

        public PressureData getPressure() {
            return pressure;
        }

        public Map<String, WindDirectionData> getWindDirections() {
            return windDirections;
        }

        public String getFirstUtc() {
            return firstUtc;
        }

        public String getLastUtc() {
            return lastUtc;
        }
    }

    public static class WindDirectionData {
        @SerializedName("compass_degrees")
        private float compassDegrees;

        @SerializedName("compass_point")
        private String compassPoint;

        @SerializedName("ct")
        private int count;

        public float getCompassDegrees() {
            return compassDegrees;
        }

        public String getCompassPoint() {
            return compassPoint;
        }

        public int getCount() {
            return count;
        }
    }

    public static class TemperatureData {
        private double av;
        private double mn;
        private double mx;

        public double getAverage() {
            return av;
        }

        public double getMin() {
            return mn;
        }

        public double getMax() {
            return mx;
        }
    }

    public static class WindData {
        private double av;
        private double mn;
        private double mx;

        public double getAverage() {
            return av;
        }

        public double getMin() {
            return mn;
        }

        public double getMax() {
            return mx;
        }
    }

    public static class PressureData {
        private double av;
        private double mn;
        private double mx;

        public double getAverage() {
            return av;
        }

        public double getMin() {
            return mn;
        }

        public double getMax() {
            return mx;
        }
    }
} 