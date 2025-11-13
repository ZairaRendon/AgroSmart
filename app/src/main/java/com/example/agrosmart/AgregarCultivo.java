package com.example.agrosmart;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class AgregarCultivo extends AppCompatActivity {

    private EditText edtNombreCultivo, edtFechaInicio, edtFechaTermino;
    private MaterialButton btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_cultivo); // tu XML de formulario

        // Vincular los EditText
        edtNombreCultivo = findViewById(R.id.edtNombreCultivo);
        edtFechaInicio = findViewById(R.id.edtFechaInicio);
        edtFechaTermino = findViewById(R.id.edtFechaTermino);

        // Vincular el botón Guardar
        btnGuardar = findViewById(R.id.btnGuardarCultivo);

        // Acción al presionar Guardar
        btnGuardar.setOnClickListener(v -> {
            String nombre = edtNombreCultivo.getText().toString();
            String inicio = edtFechaInicio.getText().toString();
            String termino = edtFechaTermino.getText().toString();

            if(nombre.isEmpty() || inicio.isEmpty() || termino.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Aquí puedes guardar los datos en memoria o base de datos

            // Termina la actividad y regresa a la pantalla de Cultivos
            finish();
        });
    }
}
