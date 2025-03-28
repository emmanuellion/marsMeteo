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
import org.json.JSONException;
import org.json.JSONArray;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    public static final String EXTRA_SOL = "extra_sol";

    private TextView solTitleText;
    private TextView temperatureDetailText;
    private TextView pressureDetailText;
    private TextView windDetailText;
    private WindRoseView windRoseView;
    private ScrollView scrollView;
    private LinearLayout contentLayout;
    private String currentSol;
    private WeatherDataManager weatherDataManager;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        weatherDataManager = WeatherDataManager.getInstance();
        scrollView = findViewById(R.id.scrollView);
        contentLayout = findViewById(R.id.contentLayout);

        // Récupérer le sol depuis l'intent
        currentSol = getIntent().getStringExtra("sol");
        
        // Initialiser les vues
        solTitleText = findViewById(R.id.solTitleText);
        temperatureDetailText = findViewById(R.id.temperatureDetailText);
        pressureDetailText = findViewById(R.id.pressureDetailText);
        windDetailText = findViewById(R.id.windDetailText);
        windRoseView = findViewById(R.id.windRoseView);

        // Charger les données initiales
        loadSolData(currentSol);

        // Configurer le ScrollView pour détecter le défilement
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (isLoading) return;

            View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
            int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

            // Si on atteint le bas
            if (diff == 0) {
                isLoading = true;
                loadNextSol();
            }

            // Si on atteint le haut
            if (scrollView.getScrollY() == 0) {
                isLoading = true;
                loadPreviousSol();
            }
        });
    }

    private void loadNextSol() {
        try {
            JSONObject data = weatherDataManager.getData();
            JSONArray solKeys = data.getJSONArray("sol_keys");
            
            for (int i = 0; i < solKeys.length(); i++) {
                if (solKeys.getString(i).equals(currentSol) && i < solKeys.length() - 1) {
                    String nextSol = solKeys.getString(i + 1);
                    currentSol = nextSol;
                    loadSolData(nextSol);
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("DetailActivity", "Error loading next sol", e);
            Toast.makeText(this, "Erreur lors du chargement du sol suivant", Toast.LENGTH_SHORT).show();
        } finally {
            isLoading = false;
        }
    }

    private void loadPreviousSol() {
        try {
            JSONObject data = weatherDataManager.getData();
            JSONArray solKeys = data.getJSONArray("sol_keys");
            
            for (int i = 0; i < solKeys.length(); i++) {
                if (solKeys.getString(i).equals(currentSol) && i > 0) {
                    String previousSol = solKeys.getString(i - 1);
                    currentSol = previousSol;
                    loadSolData(previousSol);
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("DetailActivity", "Error loading previous sol", e);
            Toast.makeText(this, "Erreur lors du chargement du sol précédent", Toast.LENGTH_SHORT).show();
        } finally {
            isLoading = false;
        }
    }

    private void loadSolData(String sol) {
        try {
            JSONObject data = weatherDataManager.getData();
            JSONObject solData = data.getJSONObject(sol);

            // Mettre à jour le titre avec la saison
            String season = weatherDataManager.getSeason(sol);
            String seasonInfo = String.format("Sol %s\n%s", sol, season != null ? season : "");
            solTitleText.setText(seasonInfo);

            // Température
            JSONObject tempData = solData.optJSONObject("AT");
            if (tempData != null) {
                String tempText = String.format(
                    "🌡️ Température atmosphérique :\n" +
                    "Moyenne : %.1f°C\n" +
                    "Min : %.1f°C\n" +
                    "Max : %.1f°C\n" +
                    "Nombre de mesures : %d",
                    tempData.optDouble("av", 0.0),
                    tempData.optDouble("mn", 0.0),
                    tempData.optDouble("mx", 0.0),
                    tempData.optInt("ct", 0)
                );
                temperatureDetailText.setText(tempText);
            } else {
                temperatureDetailText.setText("🌡️ Température non disponible");
            }

            // Pression
            JSONObject pressureData = solData.optJSONObject("PRE");
            if (pressureData != null) {
                String pressureText = String.format(
                    "🌪️ Pression atmosphérique :\n" +
                    "Moyenne : %.1f Pa\n" +
                    "Min : %.1f Pa\n" +
                    "Max : %.1f Pa\n" +
                    "Nombre de mesures : %d",
                    pressureData.optDouble("av", 0.0),
                    pressureData.optDouble("mn", 0.0),
                    pressureData.optDouble("mx", 0.0),
                    pressureData.optInt("ct", 0)
                );
                pressureDetailText.setText(pressureText);
            } else {
                pressureDetailText.setText("🌪️ Pression non disponible");
            }

            // Vent
            StringBuilder windText = new StringBuilder("💨 Vent :\n");
            
            // Vitesse du vent
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

            // Direction du vent
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
                
                // Mettre à jour la rose des vents
                windRoseView.setWindData(windDirData);
            }

            windDetailText.setText(windText.toString());

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du chargement des détails: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur lors du chargement des détails", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}