package com.example.agrosmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;

public class Dashboard extends AppCompatActivity {

    private TextView txtBienvenida;
    private MaterialButton btnCultivos, btnSensores, btnAlertas, btnMenu;
    private DrawerLayout drawerLayout;

    // Botones del menú lateral
    private Button btnCuenta, btnSensoresLateral, btnPredicciones, btnRecursos, btnCultivosLateral, btnReportes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Vincular vistas principales
        txtBienvenida = findViewById(R.id.txtBienvenida);
        btnCultivos = findViewById(R.id.btn1Cultivos);
        btnSensores = findViewById(R.id.btn1Sensores);
        btnAlertas = findViewById(R.id.btn1Predicciones);
        btnMenu = findViewById(R.id.btnMenu);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Vincular botones de la barra lateral (usa los IDs de barra_lateral.xml)
        btnCuenta = findViewById(R.id.btnCuenta);
        btnSensoresLateral = findViewById(R.id.btnSensores);
        btnPredicciones = findViewById(R.id.btnPredicciones);
        btnRecursos = findViewById(R.id.btnRecursos);
        btnCultivosLateral = findViewById(R.id.btnCultivos);
        btnReportes = findViewById(R.id.btnReportes);

        // Mensaje de bienvenida
        txtBienvenida.setText("Bienvenido a AgroSmart");

        // ---------- Botones principales ----------
        btnCultivos.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Cultivo.class);
            startActivity(intent);
        });

        btnSensores.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Sensores.class);
            startActivity(intent);
        });

        btnAlertas.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Predicciones.class);
            startActivity(intent);
        });

        // ---------- Menú lateral ----------
        btnMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(findViewById(R.id.barra_lateral_root))) {
                drawerLayout.closeDrawer(findViewById(R.id.barra_lateral_root));
            } else {
                drawerLayout.openDrawer(findViewById(R.id.barra_lateral_root));
            }
        });

        // Cultivos → abre la vista cultivo.java
        btnCultivosLateral.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Cultivo.class);
            startActivity(intent);
            drawerLayout.closeDrawer(findViewById(R.id.barra_lateral_root));
        });

        // Reportes → abre la vista Reportes.java
        btnReportes.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Reportes.class);
            startActivity(intent);
            drawerLayout.closeDrawer(findViewById(R.id.barra_lateral_root));
        });

        // Opcional: botones restantes pueden mostrar mensajes o abrir otras vistas
        btnCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Configuracion.class);
            startActivity(intent);
            drawerLayout.closeDrawer(findViewById(R.id.barra_lateral_root));
        });
        btnSensoresLateral.setOnClickListener(v -> {});
        btnPredicciones.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Predicciones.class);
            startActivity(intent);
            drawerLayout.closeDrawer(findViewById(R.id.barra_lateral_root));
        });
        btnRecursos.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, GestionRecursos.class);
            startActivity(intent);
            drawerLayout.closeDrawer(findViewById(R.id.barra_lateral_root));
        });
    }
}
