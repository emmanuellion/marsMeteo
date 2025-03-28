package com.example.marsmeteo;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.marsmeteo.model.MarsWeatherData;
import com.example.marsmeteo.model.MarsWeatherData.SolData;
import com.example.marsmeteo.model.MarsWeatherData.WindDirectionData;
import java.util.Map;

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
            // Vérifier que le WeatherDataManager a des données
            WeatherDataManager weatherManager = WeatherDataManager.getInstance();
            if (!weatherManager.hasData()) {
                Log.e(TAG, "Pas de données météo disponibles");
                Toast.makeText(this, "Erreur : données météo non disponibles", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Récupérer les données météo
            MarsWeatherData weatherData = weatherManager.getWeatherData();
            if (weatherData == null) {
                Log.e(TAG, "Données météo nulles");
                Toast.makeText(this, "Erreur : données météo invalides", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Récupérer la Map des sols
            Map<String, SolData> sols = weatherData.getSols();
            if (sols == null) {
                Log.e(TAG, "Map des sols nulle");
                Toast.makeText(this, "Erreur : données des sols invalides", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Récupérer les données du sol spécifique
            SolData solData = sols.get(solKey);
            if (solData == null) {
                Log.e(TAG, "Données nulles pour le sol " + solKey);
                Toast.makeText(this, "Erreur : données non trouvées pour le Sol " + solKey, Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Mettre à jour le titre avec la saison
            String seasonInfo = String.format("Sol %s\n%s", solKey, solData.getSeason());
            solTitleText.setText(seasonInfo);

            // Mettre à jour la température (AT)
            if (solData.getAtmosphericTemp() != null) {
                String tempText = String.format(
                    "🌡️ Température atmosphérique :\n" +
                    "Moyenne : %.1f°C\n" +
                    "Min : %.1f°C\n" +
                    "Max : %.1f°C\n" +
                    "Nombre de mesures : %d",
                    solData.getAtmosphericTemp().getAverage(),
                    solData.getAtmosphericTemp().getMin(),
                    solData.getAtmosphericTemp().getMax(),
                    solData.getAtmosphericTemp().getCount()
                );
                temperatureDetailText.setText(tempText);
            } else {
                temperatureDetailText.setText("🌡️ Température non disponible");
            }

            // Mettre à jour la pression (PRE)
            if (solData.getPressure() != null) {
                String pressureText = String.format(
                    "🌪️ Pression atmosphérique :\n" +
                    "Moyenne : %.1f Pa\n" +
                    "Min : %.1f Pa\n" +
                    "Max : %.1f Pa\n" +
                    "Nombre de mesures : %d",
                    solData.getPressure().getAverage(),
                    solData.getPressure().getMin(),
                    solData.getPressure().getMax(),
                    solData.getPressure().getCount()
                );
                pressureDetailText.setText(pressureText);
            } else {
                pressureDetailText.setText("🌪️ Pression non disponible");
            }

            // Mettre à jour le vent (HWS et WD)
            StringBuilder windText = new StringBuilder("💨 Vent :\n");
            
            // Vitesse du vent
            if (solData.getWindSpeed() != null) {
                windText.append(String.format(
                    "Vitesse moyenne : %.1f m/s\n" +
                    "Vitesse min : %.1f m/s\n" +
                    "Vitesse max : %.1f m/s\n" +
                    "Nombre de mesures : %d\n\n",
                    solData.getWindSpeed().getAverage(),
                    solData.getWindSpeed().getMin(),
                    solData.getWindSpeed().getMax(),
                    solData.getWindSpeed().getCount()
                ));
            }

            // Direction du vent
            if (solData.getWindDirections() != null && !solData.getWindDirections().isEmpty()) {
                WindDirectionData mostCommon = solData.getWindDirections().get("most_common");
                if (mostCommon != null) {
                    windText.append(String.format(
                        "Direction dominante : %s (%.1f°)\n" +
                        "Nombre de mesures : %d",
                        mostCommon.getCompassPoint(),
                        mostCommon.getCompassDegrees(),
                        mostCommon.getCount()
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