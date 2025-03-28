package com.example.marsmeteo;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import org.json.JSONException;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    public static final String EXTRA_SOL = "extra_sol";

    private TextView solTitleText;
    private TextView temperatureDetailText;
    private TextView pressureDetailText;
    private TextView windDetailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialiser les vues
        solTitleText = findViewById(R.id.solTitleText);
        temperatureDetailText = findViewById(R.id.temperatureDetailText);
        pressureDetailText = findViewById(R.id.pressureDetailText);
        windDetailText = findViewById(R.id.windDetailText);

        // Récupérer le numéro de sol
        String solKey = getIntent().getStringExtra(EXTRA_SOL);
        if (solKey == null) {
            Log.e(TAG, "Pas de numéro de sol fourni");
            Toast.makeText(this, "Erreur : numéro de sol manquant", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Charger et afficher les données
        loadSolDetails(solKey);
    }

    private void loadSolDetails(String solKey) {
        try {
            WeatherDataManager weatherManager = WeatherDataManager.getInstance();
            if (!weatherManager.hasData()) {
                Toast.makeText(this, "Erreur : données météo non disponibles", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Récupérer les données du sol
            JSONObject solData = weatherManager.getSolData(solKey);
            if (solData == null) {
                Toast.makeText(this, "Erreur : données non trouvées pour le Sol " + solKey, Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Mettre à jour le titre avec la saison
            String season = weatherManager.getSeason(solKey);
            String seasonInfo = String.format("Sol %s\n%s", solKey, season != null ? season : "");
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
            }

            windDetailText.setText(windText.toString());

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du chargement des détails: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur lors du chargement des détails", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}