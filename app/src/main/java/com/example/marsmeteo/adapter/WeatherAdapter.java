package com.example.marsmeteo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.marsmeteo.DetailActivity;
import com.example.marsmeteo.R;
import com.example.marsmeteo.WeatherDataManager;
import java.util.List;

public class WeatherAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final WeatherDataManager weatherManager;

    public WeatherAdapter(Context context, List<String> sols) {
        super(context, R.layout.item_weather, sols);
        this.context = context;
        this.weatherManager = WeatherDataManager.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weather, parent, false);
        }

        String solKey = getItem(position);
        TextView solText = convertView.findViewById(R.id.solText);
        TextView tempText = convertView.findViewById(R.id.tempText);
        TextView pressureText = convertView.findViewById(R.id.pressureText);

        if (solKey != null) {
            // Afficher le numéro du sol
            solText.setText("Sol " + solKey);

            // Afficher la température moyenne
            double avgTemp = weatherManager.getAverageTemp(solKey);
            if (!Double.isNaN(avgTemp)) {
                tempText.setText(String.format("🌡️ %.1f°C", avgTemp));
            } else {
                tempText.setText("🌡️ N/A");
            }

            // Afficher la pression moyenne
            double avgPressure = weatherManager.getAveragePressure(solKey);
            if (!Double.isNaN(avgPressure)) {
                pressureText.setText(String.format("🌪️ %.1f Pa", avgPressure));
            } else {
                pressureText.setText("🌪️ N/A");
            }

            // Gérer le clic sur l'élément
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_SOL, solKey);
                context.startActivity(intent);
            });
        }

        return convertView;
    }
} 