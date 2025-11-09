package com.example.agrosmart; // 👈 usa tu paquete real

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class BarraLateral extends AppCompatActivity {

    private ImageView imgUsuario;
    private MaterialButton btnCuenta, btnSensores, btnPredicciones, btnRecursos, btnCultivos, btnReportes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barra_lateral); // 👈 tu layout XML

        imgUsuario = findViewById(R.id.imgUsuario);
        btnCuenta = findViewById(R.id.btnCuenta);
        btnSensores = findViewById(R.id.btnSensores);
        btnPredicciones = findViewById(R.id.btnPredicciones);
        btnRecursos = findViewById(R.id.btnRecursos);
        btnCultivos = findViewById(R.id.btnCultivos);
        btnReportes = findViewById(R.id.btnReportes);

        // 👇 Ejemplos de acciones al presionar botones
        btnCuenta.setOnClickListener(v ->
                Toast.makeText(this, "Abrir sección de Cuenta", Toast.LENGTH_SHORT).show());

        btnSensores.setOnClickListener(v ->
                Toast.makeText(this, "Abrir sección de Sensores", Toast.LENGTH_SHORT).show());

        btnPredicciones.setOnClickListener(v ->
                Toast.makeText(this, "Abrir Predicciones", Toast.LENGTH_SHORT).show());

        btnRecursos.setOnClickListener(v ->
                Toast.makeText(this, "Abrir Recursos", Toast.LENGTH_SHORT).show());

        btnCultivos.setOnClickListener(v ->
                Toast.makeText(this, "Abrir Cultivos", Toast.LENGTH_SHORT).show());

        btnReportes.setOnClickListener(v ->
                Toast.makeText(this, "Abrir Reportes", Toast.LENGTH_SHORT).show());
    }
}
