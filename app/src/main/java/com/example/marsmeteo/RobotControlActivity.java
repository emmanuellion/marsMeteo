package com.example.marsmeteo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RobotControlActivity extends AppCompatActivity {
    private static final String SERVER_IP = ""; // À remplir avec l'IP du serveur
    private static final int SERVER_PORT = 0; // À remplir avec le port du serveur
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private boolean isConnected = false;
    private boolean areMotorsStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_control);

        Button connectButton = findViewById(R.id.connectButton);
        Button startStopButton = findViewById(R.id.startStopButton);
        Button leftButton = findViewById(R.id.leftButton);
        Button frontButton = findViewById(R.id.frontButton);
        Button rightButton = findViewById(R.id.rightButton);

        connectButton.setOnClickListener(v -> connectToServer());
        startStopButton.setOnClickListener(v -> toggleMotors());
        leftButton.setOnClickListener(v -> sendCommand("DIRECT_LEFT"));
        frontButton.setOnClickListener(v -> sendCommand("DIRECT_FRONT"));
        rightButton.setOnClickListener(v -> sendCommand("DIRECT_RIGHT"));
    }

    private void connectToServer() {
        executor.execute(() -> {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isConnected = true;
                handler.post(() -> showToast("Connecté au serveur"));
            } catch (Exception e) {
                handler.post(() -> showToast("Erreur de connexion: " + e.getMessage()));
            }
        });
    }

    private void toggleMotors() {
        if (!isConnected) {
            showToast("Non connecté au serveur");
            return;
        }

        String command = areMotorsStarted ? "STOP" : "START";
        sendCommand(command);
        areMotorsStarted = !areMotorsStarted;
    }

    private void sendCommand(String command) {
        if (!isConnected) {
            showToast("Non connecté au serveur");
            return;
        }

        if (!areMotorsStarted && !command.equals("START")) {
            showToast("Les moteurs doivent être démarrés");
            return;
        }

        executor.execute(() -> {
            try {
                writer.write(command + "\n");
                writer.flush();
                String response = reader.readLine();
                handler.post(() -> showToast("Réponse: " + response));
            } catch (Exception e) {
                handler.post(() -> showToast("Erreur: " + e.getMessage()));
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(() -> {
            try {
                if (socket != null && !socket.isClosed()) {
                    if (areMotorsStarted) {
                        writer.write("STOP\n");
                        writer.flush();
                    }
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
    }
} 