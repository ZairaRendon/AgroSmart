package com.tu_paquete;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Simulación de datos
    private int humedad = 68;
    private int temperatura = 25;
    private int fertilizacion = 45;
    private int sensoresActivos = 3;
    private int sensoresTotales = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a los elementos del layout
        TextView txtHumedad = findViewById(R.id.txtHumedad);
        TextView txtTemperatura = findViewById(R.id.txtTemperatura);
        TextView txtFertilizacion = findViewById(R.id.txtFertilizacion);
        TextView txtAlertas = findViewById(R.id.txtAlertas);
        TextView txtSensores = findViewById(R.id.txtSensores);
        TextView txtRecomendacion = findViewById(R.id.txtRecomendacion);

        // Mostrar valores simulados
        txtHumedad.setText("🌧️ Humedad: " + humedad + "%");
        txtTemperatura.setText("🌡️ Temperatura: " + temperatura + "°C");
        txtFertilizacion.setText("🌿 Fertilización: " + fertilizacion + "%");

        txtAlertas.setText(
                "⚠️ Baja humedad en zona 2\n🔥 Temperatura alta detectada\n🔋 Sensor pH sin conexión"
        );

        txtSensores.setText("🛰️ Sensores activos: " + sensoresActivos + " / " + sensoresTotales);

        txtRecomendacion.setText(
                "💡 Recomendación:\nAplicar riego moderado en la zona 2 dentro de 6 horas."
        );
    }
}
