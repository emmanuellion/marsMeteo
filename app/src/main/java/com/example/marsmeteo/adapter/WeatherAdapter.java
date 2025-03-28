package com.example.marsmeteo.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.marsmeteo.DetailActivity;
import com.example.marsmeteo.R;
import com.example.marsmeteo.model.MarsWeatherResponse;
import java.util.ArrayList;
import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private List<String> solKeys = new ArrayList<>();
    private MarsWeatherResponse weatherData;

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        String solKey = solKeys.get(position);
        MarsWeatherResponse.SolData solData = weatherData.getSols().get(solKey);
        holder.bind(solKey, solData);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_SOL, solKey);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return solKeys.size();
    }

    public void setWeatherData(MarsWeatherResponse data) {
        this.weatherData = data;
        this.solKeys = data.getSolKeys();
        notifyDataSetChanged();
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateText;
        private final TextView solText;
        private final TextView tempText;
        private final TextView pressureText;
        private final TextView windText;

        WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            solText = itemView.findViewById(R.id.solText);
            tempText = itemView.findViewById(R.id.tempText);
            pressureText = itemView.findViewById(R.id.pressureText);
            windText = itemView.findViewById(R.id.opacityText);
        }

        void bind(String sol, MarsWeatherResponse.SolData data) {
            solText.setText("Sol: " + sol);
            dateText.setText("Date: " + data.getFirstUtc().split("T")[0]);
            
            if (data.getTemperature() != null) {
                tempText.setText(String.format("Température: %.1f°C à %.1f°C (moy: %.1f°C)", 
                    data.getTemperature().getMin(),
                    data.getTemperature().getMax(),
                    data.getTemperature().getAverage()));
            }
            
            if (data.getPressure() != null) {
                pressureText.setText(String.format("Pression: %.1f Pa (moy)", 
                    data.getPressure().getAverage()));
            }
            
            if (data.getWindSpeed() != null) {
                windText.setText(String.format("Vent: %.1f m/s à %.1f m/s (moy: %.1f m/s)", 
                    data.getWindSpeed().getMin(),
                    data.getWindSpeed().getMax(),
                    data.getWindSpeed().getAverage()));
            }
        }
    }
} 