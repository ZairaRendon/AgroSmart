package com.example.agrosmart;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class Reportes extends AppCompatActivity {

    Button btnFechaInicio, btnFechaFin, btnGenerarPDF, btnExportarExcel, btnCompartir;
    RadioGroup radioGroupReportes;
    RadioButton radioSeleccionado;

    String fechaInicio = "", fechaFin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        // Vincular vistas
        btnFechaInicio = findViewById(R.id.btnFechaInicio);
        btnFechaFin = findViewById(R.id.btnFechaFin);
        btnGenerarPDF = findViewById(R.id.btnGenerarPDF);
        btnExportarExcel = findViewById(R.id.btnExportarExcel);
        btnCompartir = findViewById(R.id.btnCompartir);


        // Selección de fechas
        btnFechaInicio.setOnClickListener(v -> mostrarCalendario(true));
        btnFechaFin.setOnClickListener(v -> mostrarCalendario(false));

        // Botones de acción
        btnGenerarPDF.setOnClickListener(v -> generarPDF());
        btnExportarExcel.setOnClickListener(v -> exportarExcel());
        btnCompartir.setOnClickListener(v -> compartirReporte());
    }

    private void mostrarCalendario(boolean esInicio) {
        final Calendar calendar = Calendar.getInstance();
        int año = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int día = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                    if (esInicio) {
                        fechaInicio = fecha;
                        btnFechaInicio.setText("Inicio: " + fecha);
                    } else {
                        fechaFin = fecha;
                        btnFechaFin.setText("Fin: " + fecha);
                    }
                },
                año, mes, día
        );
        datePickerDialog.show();
    }

    private void generarPDF() {
        radioSeleccionado = findViewById(radioGroupReportes.getCheckedRadioButtonId());
        if (radioSeleccionado == null || fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(this, "Seleccione fechas y tipo de reporte", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Generando PDF de " + radioSeleccionado.getText(), Toast.LENGTH_SHORT).show();
        // Aquí podrías agregar la lógica para generar el PDF real
    }

    private void exportarExcel() {
        if (radioGroupReportes.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Seleccione un tipo de reporte", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Exportando reporte a Excel...", Toast.LENGTH_SHORT).show();
        // Aquí iría la lógica para crear el archivo Excel
    }

    private void compartirReporte() {
        Toast.makeText(this, "Compartiendo reporte...", Toast.LENGTH_SHORT).show();
        // Aquí se implementaría el Intent para compartir el reporte
    }
}
