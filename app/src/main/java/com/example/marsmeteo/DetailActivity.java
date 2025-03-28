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
    private LinearLayout mainContainer;  // Conteneur principal pour empiler les vues

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        weatherDataManager = WeatherDataManager.getInstance();
        scrollView = findViewById(R.id.scrollView);
        mainContainer = findViewById(R.id.mainContainer);

        // Récupérer le sol depuis l'intent
        currentSol = getIntent().getStringExtra(EXTRA_SOL);
        if (currentSol == null) {
            Log.e(TAG, "Pas de numéro de sol fourni");
            Toast.makeText(this, "Erreur : numéro de sol manquant", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Charger les données initiales
        addSolView(currentSol);

        // Configurer le ScrollView pour détecter le défilement
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (isLoading) return;

            int totalHeight = scrollView.getChildAt(0).getHeight();
            int scrollViewHeight = scrollView.getHeight();

            // Chargement vers le bas
            if (scrollY + scrollViewHeight > totalHeight - 200) {
                isLoading = true;
                loadNextSol();
            }

            // Chargement vers le haut
            if (scrollY < 200) {
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
                    addSolView(nextSol);
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
                    addSolViewAtTop(previousSol);
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
        }
    }
}