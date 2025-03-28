package com.example.marsmeteo.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class MarsWeatherData {
    @SerializedName("sol_keys")
    private List<String> solKeys;

    @SerializedName("validity_checks")
    private Map<String, Object> validityChecks;

    private Map<String, SolData> sols;

    public List<String> getSolKeys() {
        return solKeys;
    }

    public Map<String, SolData> getSols() {
        return sols;
    }

    public static class SolData {
        @SerializedName("AT")
        private AtmosphericData atmosphericTemp;

        @SerializedName("PRE")
        private AtmosphericData pressure;

        @SerializedName("HWS")
        private AtmosphericData windSpeed;

        @SerializedName("WD")
        private Map<String, WindDirectionData> windDirections;

        @SerializedName("First_UTC")
        private String firstUTC;

        @SerializedName("Last_UTC")
        private String lastUTC;

        @SerializedName("Season")
        private String season;

        @SerializedName("Northern_season")
        private String northernSeason;

        @SerializedName("Southern_season")
        private String southernSeason;

        @SerializedName("Month_ordinal")
        private int monthOrdinal;

        public AtmosphericData getAtmosphericTemp() {
            return atmosphericTemp;
        }

        public AtmosphericData getPressure() {
            return pressure;
        }

        public AtmosphericData getWindSpeed() {
            return windSpeed;
        }

        public Map<String, WindDirectionData> getWindDirections() {
            return windDirections;
        }

        public String getFirstUTC() {
            return firstUTC;
        }

        public String getLastUTC() {
            return lastUTC;
        }

        public String getSeason() {
            return season;
        }

        public String getNorthernSeason() {
            return northernSeason;
        }

        public String getSouthernSeason() {
            return southernSeason;
        }

        public int getMonthOrdinal() {
            return monthOrdinal;
        }
    }

    public static class AtmosphericData {
        @SerializedName("av")
        private double average;

        @SerializedName("mn")
        private double min;

        @SerializedName("mx")
        private double max;

        @SerializedName("ct")
        private int count;

        public double getAverage() {
            return average;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public int getCount() {
            return count;
        }
    }

    public static class WindDirectionData {
        @SerializedName("compass_degrees")
        private double compassDegrees;

        @SerializedName("compass_point")
        private String compassPoint;

        @SerializedName("compass_right")
        private double compassRight;

        @SerializedName("compass_up")
        private double compassUp;

        @SerializedName("ct")
        private int count;

        public double getCompassDegrees() {
            return compassDegrees;
        }

        public String getCompassPoint() {
            return compassPoint;
        }

        public double getCompassRight() {
            return compassRight;
        }

        public double getCompassUp() {
            return compassUp;
        }

        public int getCount() {
            return count;
        }
    }
} 