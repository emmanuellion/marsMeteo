package com.example.marsmeteo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.marsmeteo.R;
import com.example.marsmeteo.model.MarsWeatherData.SolData;
import java.util.List;
import java.util.Map;

public class SolAdapter extends ArrayAdapter<String> {
    private static final String TAG = "SolAdapter";
    private final Map<String, SolData> solsData;

    public SolAdapter(Context context, List<String> solKeys, Map<String, SolData> solsData) {
        super(context, R.layout.item_sol, solKeys);
        this.solsData = solsData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_sol, parent, false);
            }

            String solKey = getItem(position);
            if (solKey == null) {
                Log.e(TAG, "Sol key is null at position " + position);
                return convertView;
            }

            TextView solNumberText = convertView.findViewById(R.id.solNumberText);
            TextView temperatureText = convertView.findViewById(R.id.temperatureText);
            TextView pressureText = convertView.findViewById(R.id.pressureText);

            solNumberText.setText(String.format("Sol %s", solKey));

            SolData solData = solsData.get(solKey);
            if (solData == null) {
                Log.e(TAG, "Sol data is null for key: " + solKey);
                temperatureText.setText("🌡️ Température non disponible");
                pressureText.setText("🌪️ Pression non disponible");
                return convertView;
            }

            if (solData.getAtmosphericTemp() != null) {
                double temp = solData.getAtmosphericTemp().getAverage();
                double tempMin = solData.getAtmosphericTemp().getMin();
                double tempMax = solData.getAtmosphericTemp().getMax();
                temperatureText.setText(String.format("🌡️ %.1f°C (min: %.1f°C, max: %.1f°C)", 
                    temp, tempMin, tempMax));
            } else {
                temperatureText.setText("🌡️ Température non disponible");
            }

            if (solData.getPressure() != null) {
                double pressure = solData.getPressure().getAverage();
                pressureText.setText(String.format("🌪️ %.1f Pa", pressure));
            } else {
                pressureText.setText("🌪️ Pression non disponible");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in getView: " + e.getMessage(), e);
        }

        return convertView;
    }
} 