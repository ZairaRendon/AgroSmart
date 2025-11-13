package com.example.agrosmart;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.Random;

public class Predicciones extends AppCompatActivity {

    private TextView prediccion1, prediccion2, prediccion3;
    private TextView confianza1, confianza2, confianza3;
    private MaterialButton recalcularBtn;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predicciones);

        random = new Random();

        initializeViews();
        setupListeners();
        loadPredictions();
    }

    private void initializeViews() {
        // Predicciones
        prediccion1 = findViewById(R.id.prediccion1);
        prediccion2 = findViewById(R.id.prediccion2);
        prediccion3 = findViewById(R.id.prediccion3);

        // Confianzas
        confianza1 = findViewById(R.id.confianza1);
        confianza2 = findViewById(R.id.confianza2);
        confianza3 = findViewById(R.id.confianza3);

        // Botón
        recalcularBtn = findViewById(R.id.recalcularBtn);
    }

    private void setupListeners() {
        recalcularBtn.setOnClickListener(v -> {
            recalcularBtn.setEnabled(false);
            recalcularBtn.setText("Recalculando...");

            // Simular proceso de recálculo
            recalcularBtn.postDelayed(() -> {
                loadPredictions();
                recalcularBtn.setEnabled(true);
                recalcularBtn.setText("Recalcular Predicciones");
                Toast.makeText(Predicciones.this, "Predicciones actualizadas", Toast.LENGTH_SHORT).show();
            }, 2000);
        });
    }

    private void loadPredictions() {
        // Aquí conectarías con tu servicio de ML o API
        // Por ahora, simulamos las predicciones

        PredictionData[] predictions = generatePredictions();

        // Actualizar UI con las predicciones
        updatePrediction(prediccion1, confianza1, predictions[0]);
        updatePrediction(prediccion2, confianza2, predictions[1]);
        updatePrediction(prediccion3, confianza3, predictions[2]);
    }

    private void updatePrediction(TextView textoPred, TextView textoConf, PredictionData data) {
        textoPred.setText(data.emoji + " " + data.texto);
        textoConf.setText("Confianza: " + data.confianza + "%");
    }

    private PredictionData[] generatePredictions() {
        // Simulación de datos
        PredictionData[] predictions = new PredictionData[3];

        // Array de posibles predicciones
        String[][] posiblesPredicciones = {
                // Riego
                {"💧", "Riego necesario en ", " horas", "85", "95"},
                {"💦", "Riego óptimo mañana a las ", ":00 AM", "88", "93"},
                {"🌊", "Sistema de riego: revisar presión", "", "90", "92"},

                // Plagas
                {"🐛", "Riesgo de plaga: ", " en 48h", "70", "85"},
                {"🦟", "Monitorear mosca blanca próximos ", " días", "75", "82"},
                {"🕷️", "Araña roja: riesgo ", " - prevención", "68", "79"},

                // Fertilización
                {"🌱", "Aplicar fertilizante NPK en ", " días", "82", "91"},
                {"🍃", "Déficit de nitrógeno detectado", "", "87", "94"},
                {"🌿", "Micronutrientes necesarios en ", " días", "79", "88"},

                // Cosecha
                {"🌾", "Cosecha óptima: ", " días", "89", "96"},
                {"📦", "Rendimiento esperado: ", " kg/m²", "84", "90"},
                {"✅", "Calidad premium alcanzable", "", "91", "97"},

                // Clima
                {"☀️", "Temperatura alta próximas ", " horas", "86", "93"},
                {"🌡️", "Riesgo de estrés térmico: ", "", "77", "84"},
                {"💨", "Viento fuerte esperado día ", "", "81", "88"},

                // General
                {"📊", "Crecimiento ", "% sobre promedio", "83", "89"},
                {"⚡", "Eficiencia de riego: mejorar ", "%", "76", "83"},
                {"🎯", "Objetivo de producción: ", "% alcanzado", "88", "94"}
        };

        // Seleccionar 3 predicciones aleatorias diferentes
        int[] indices = new int[3];
        for (int i = 0; i < 3; i++) {
            int index;
            boolean repetido;
            do {
                repetido = false;
                index = random.nextInt(posiblesPredicciones.length);
                for (int j = 0; j < i; j++) {
                    if (indices[j] == index) {
                        repetido = true;
                        break;
                    }
                }
            } while (repetido);
            indices[i] = index;

            String[] pred = posiblesPredicciones[index];
            int valor = random.nextInt(10) + (i * 3) + 5;

            predictions[i] = new PredictionData(
                    pred[0], // emoji
                    pred[1] + valor + pred[2],
                    Integer.parseInt(pred[3]) + random.nextInt(Integer.parseInt(pred[4]) - Integer.parseInt(pred[3]))
            );
        }

        return predictions;
    }


    // Clase interna para datos de predicción
    private static class PredictionData {
        String emoji;
        String texto;
        int confianza;

        PredictionData(String emoji, String texto, int confianza) {
            this.emoji = emoji;
            this.texto = texto;
            this.confianza = confianza;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}