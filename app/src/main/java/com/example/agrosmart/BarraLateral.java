package com.example.agrosmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class BarraLateral extends AppCompatActivity {

    private ImageView imgUsuario;
    private MaterialButton btnCuenta, btnSensores, btnPredicciones, btnRecursos, btnCultivos, btnReportes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barra_lateral);

        // Vincular vistas
        imgUsuario = findViewById(R.id.imgUsuario);
        btnCuenta = findViewById(R.id.btnCuenta);
        btnSensores = findViewById(R.id.btnSensores);
        btnPredicciones = findViewById(R.id.btnPredicciones);
        btnRecursos = findViewById(R.id.btnRecursos);
        btnCultivos = findViewById(R.id.btnCultivos);
        btnReportes = findViewById(R.id.btnReportes);

        // 👉 Abrir Cultivos
        btnCultivos.setOnClickListener(v -> {
            Intent intent = new Intent(BarraLateral.this, CultivoActivity.class);
            startActivity(intent);
        });

        // 👉 Abrir Reportes
        btnReportes.setOnClickListener(v -> {
            Intent intent = new Intent(BarraLateral.this, Reportes.class);
            startActivity(intent);
        });

        // Otras secciones (todavía sin implementación)
        btnCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(BarraLateral.this, Configuracion.class);
            startActivity(intent);
        });

        btnSensores.setOnClickListener(v ->
                mostrarMensaje("Abrir sección de Sensores"));


        btnPredicciones.setOnClickListener(v -> {
            Intent intent = new Intent(BarraLateral.this, Predicciones.class);
            startActivity(intent);
        });


        btnRecursos.setOnClickListener(v ->{
            Intent intent = new Intent(BarraLateral.this, GestionRecursos.class);
            startActivity(intent);
        });
    }

    // Método auxiliar para mostrar mensajes rápidos
    private void mostrarMensaje(String mensaje) {
        android.widget.Toast.makeText(this, mensaje, android.widget.Toast.LENGTH_SHORT).show();
    }
}
