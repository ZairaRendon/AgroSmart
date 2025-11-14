package com.example.agrosmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class AgregarCultivo extends AppCompatActivity {

    private EditText edtNombreCultivo, edtFechaInicio;
    private MaterialButton btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_cultivo);

        // Vincular los EditText
        edtNombreCultivo = findViewById(R.id.edtNombreCultivo);
        edtFechaInicio = findViewById(R.id.edtFechaInicio);

        // Vincular el botón Guardar
        btnGuardar = findViewById(R.id.btnGuardarCultivo);

        // Acción al presionar Guardar
        btnGuardar.setOnClickListener(v -> {
            String nombre = edtNombreCultivo.getText().toString().trim();
            String fechaInicio = edtFechaInicio.getText().toString().trim();

            if(nombre.isEmpty() || fechaInicio.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Enviar datos de vuelta a la actividad Cultivo
            Intent intent = new Intent();
            intent.putExtra("nombreCultivo", nombre);
            intent.putExtra("fechaInicio", fechaInicio);
            setResult(RESULT_OK, intent);

            Toast.makeText(this, "Cultivo agregado correctamente", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}