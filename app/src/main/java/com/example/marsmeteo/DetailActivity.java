package com.example.marsmeteo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.marsmeteo.view.WindRoseView;
import org.json.JSONObject;
import org.json.JSONArray;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    public static final String EXTRA_SOL = "extra_sol";

    private ScrollView scrollView;
    private LinearLayout mainContainer;
    private String currentSol;
    private WeatherDataManager weatherDataManager;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        weatherDataManager = WeatherDataManager.getInstance();
        scrollView = findViewById(R.id.scrollView);
        mainContainer = findViewById(R.id.mainContainer);
        currentSol = getIntent().getStringExtra(EXTRA_SOL);

        if (currentSol == null) {
            Log.e(TAG, "Pas de numéro de sol fourni");
            Toast.makeText(this, "Erreur : numéro de sol manquant", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        addSolView(currentSol);
        initScrollListener();
    }

    private void initScrollListener() {
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (isLoading) return;

            int totalHeight = scrollView.getChildAt(0).getHeight();
            int scrollViewHeight = scrollView.getHeight();

            if (scrollY + scrollViewHeight > totalHeight - 200) {
                isLoading = true;
                loadNextSol();
            }

            if (scrollY < 200) {
                isLoading = true;
                loadPreviousSol();
            }
        });
    }

    private void loadNextSol() {
        try {
            JSONObject data = weatherDataManager.getData();
            if (data == null) {
                isLoading = false;
                return;
            }

            JSONArray solKeys = data.getJSONArray("sol_keys");
            for (int i = 0; i < solKeys.length(); i++) {
                if (solKeys.getString(i).equals(currentSol) && i < solKeys.length() - 1) {
                    currentSol = solKeys.getString(i + 1);
                    addSolView(currentSol);
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading next sol", e);
            Toast.makeText(this, "Erreur lors du chargement du sol suivant", Toast.LENGTH_SHORT).show();
        } finally {
            isLoading = false;
        }
    }

    private void loadPreviousSol() {
        try {
            JSONObject data = weatherDataManager.getData();
            if (data == null) {
                isLoading = false;
                return;
            }

            JSONArray solKeys = data.getJSONArray("sol_keys");
            for (int i = 0; i < solKeys.length(); i++) {
                if (solKeys.getString(i).equals(currentSol) && i > 0) {
                    currentSol = solKeys.getString(i - 1);
                    addSolViewAtTop(currentSol);
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading previous sol", e);
            Toast.makeText(this, "Erreur lors du chargement du sol précédent", Toast.LENGTH_SHORT).show();
        } finally {
            isLoading = false;
        }
    }

    private void addSolView(String sol) {
        View solView = getLayoutInflater().inflate(R.layout.item_sol_detail, mainContainer, false);
        setupSolView(solView, sol);
        mainContainer.addView(solView);
    }

    private void addSolViewAtTop(String sol) {
        View solView = getLayoutInflater().inflate(R.layout.item_sol_detail, mainContainer, false);
        setupSolView(solView, sol);
        mainContainer.addView(solView, 0);
    }

    private void setupSolView(View view, String sol) {
        try {
            TextView solTitleText = view.findViewById(R.id.solTitleText);
            TextView temperatureDetailText = view.findViewById(R.id.temperatureDetailText);
            TextView pressureDetailText = view.findViewById(R.id.pressureDetailText);
            TextView windDetailText = view.findViewById(R.id.windDetailText);
            WindRoseView windRoseView = view.findViewById(R.id.windRoseView);

            JSONObject solData = weatherDataManager.getSolData(sol);
            if (solData == null) {
                Toast.makeText(this, "Erreur : données non disponibles pour le Sol " + sol, Toast.LENGTH_SHORT).show();
                return;
            }

            String season = weatherDataManager.getSeason(sol);
            solTitleText.setText(String.format("Sol %s\n%s", sol, season != null ? season : ""));

            setupTemperatureView(temperatureDetailText, solData.optJSONObject("AT"));
            setupPressureView(pressureDetailText, solData.optJSONObject("PRE"));
            setupWindView(windDetailText, windRoseView, solData);

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du chargement des détails: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur lors du chargement des détails", Toast.LENGTH_LONG).show();
        }
    }

    private void setupTemperatureView(TextView view, JSONObject tempData) {
        if (tempData != null) {
            view.setText(String.format(
                "🌡️ Température atmosphérique :\n" +
                "Moyenne : %.1f°C\n" +
                "Min : %.1f°C\n" +
                "Max : %.1f°C\n" +
                "Nombre de mesures : %d",
                tempData.optDouble("av", 0.0),
                tempData.optDouble("mn", 0.0),
                tempData.optDouble("mx", 0.0),
                tempData.optInt("ct", 0)
            ));
        } else {
            view.setText("🌡️ Température non disponible");
        }
    }

    private void setupPressureView(TextView view, JSONObject pressureData) {
        if (pressureData != null) {
            view.setText(String.format(
                "🌪️ Pression atmosphérique :\n" +
                "Moyenne : %.1f Pa\n" +
                "Min : %.1f Pa\n" +
                "Max : %.1f Pa\n" +
                "Nombre de mesures : %d",
                pressureData.optDouble("av", 0.0),
                pressureData.optDouble("mn", 0.0),
                pressureData.optDouble("mx", 0.0),
                pressureData.optInt("ct", 0)
            ));
        } else {
            view.setText("🌪️ Pression non disponible");
        }
    }

    private void setupWindView(TextView textView, WindRoseView roseView, JSONObject solData) {
        StringBuilder windText = new StringBuilder("💨 Vent :\n");
        
        JSONObject windSpeedData = solData.optJSONObject("HWS");
        if (windSpeedData != null) {
            windText.append(String.format(
                "Vitesse moyenne : %.1f m/s\n" +
                "Vitesse min : %.1f m/s\n" +
                "Vitesse max : %.1f m/s\n" +
                "Nombre de mesures : %d\n\n",
                windSpeedData.optDouble("av", 0.0),
                windSpeedData.optDouble("mn", 0.0),
                windSpeedData.optDouble("mx", 0.0),
                windSpeedData.optInt("ct", 0)
            ));
        }

        JSONObject windDirData = solData.optJSONObject("WD");
        if (windDirData != null) {
            JSONObject mostCommon = windDirData.optJSONObject("most_common");
            if (mostCommon != null) {
                windText.append(String.format(
                    "Direction dominante : %s (%.1f°)\n" +
                    "Nombre de mesures : %d",
                    mostCommon.optString("compass_point", "N/A"),
                    mostCommon.optDouble("compass_degrees", 0.0),
                    mostCommon.optInt("ct", 0)
                ));
            }
            roseView.setWindData(windDirData);
        }

        textView.setText(windText.toString());
    }
}