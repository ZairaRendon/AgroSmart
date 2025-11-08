package com.example.agrosmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {

    private TextView txtBienvenida;
    private Button btnCultivos, btnSensores, btnAlertas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Vincular vistas
        txtBienvenida = findViewById(R.id.txtBienvenida);
        btnCultivos = findViewById(R.id.btnCultivos);
        btnSensores = findViewById(R.id.btnSensores);
        btnAlertas = findViewById(R.id.btnAlertas);

        // mensaje de bienvenida
        txtBienvenida.setText("Bienvenido a AgroSmart ");

        //   botones
        btnCultivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí para la pantalla de cultivos
                // startActivity(new Intent(Dashboard.this, CultivosActivity.class));
            }
        });

        btnSensores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí para abrir la pantalla de sensores
                // startActivity(new Intent(Dashboard.this, SensoresActivity.class));
            }
        });

        btnAlertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí para abrir  la pantalla de alertas
                // startActivity(new Intent(Dashboard.this, AlertasActivity.class));
            }
        });
    }
}
