package com.example.agrosmart;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.content.SharedPreferences;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Sensores extends AppCompatActivity {

    private LinearLayout sensoresContainer;
    private TextView tvEstadoGeneral;
    private TextView tvSensoresActivos;
    private TextView tvUltimaActualizacion;
    private MaterialButton btnAgregarSensor;
    private MaterialButton btnEliminarSensor;

    private List<Sensor> listaSensores;
    private Handler handler;
    private Runnable actualizacionRunnable;
    private Random random;
    private SharedPreferences preferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoreo_sensores);

        inicializarVistas();
        inicializarDatos();
        configurarBotones();
        iniciarActualizacionAutomatica();
    }

    private void inicializarVistas() {
        sensoresContainer = findViewById(R.id.sensoresContainer);
        tvEstadoGeneral = findViewById(R.id.tvEstadoGeneral);
        tvSensoresActivos = findViewById(R.id.tvSensoresActivos);
        tvUltimaActualizacion = findViewById(R.id.tvUltimaActualizacion);
        btnAgregarSensor = findViewById(R.id.btnAgregarSensor);
        btnEliminarSensor = findViewById(R.id.btnEliminarSensor);
    }

    private void inicializarDatos() {
        listaSensores = new ArrayList<>();
        handler = new Handler();
        random = new Random();
        preferences = getSharedPreferences("SensoresPrefs", MODE_PRIVATE);
        gson = new Gson();

        cargarSensoresGuardados();
    }

    private void cargarSensoresGuardados() {
        String json = preferences.getString("sensores", null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<Sensor>>(){}.getType();
            listaSensores = gson.fromJson(json, type);

            if (listaSensores == null) {
                listaSensores = new ArrayList<>();
            }
        }

        actualizarUI();
    }

    private void configurarBotones() {
        btnAgregarSensor.setOnClickListener(v -> mostrarDialogoAgregarSensor());
        btnEliminarSensor.setOnClickListener(v -> mostrarDialogoEliminarSensor());
    }

    private void mostrarDialogoAgregarSensor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_sensor, null);

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreSensor);
        TextInputEditText etUnidad = dialogView.findViewById(R.id.etUnidadSensor);
        TextInputEditText etMin = dialogView.findViewById(R.id.etValorMin);
        TextInputEditText etMax = dialogView.findViewById(R.id.etValorMax);

        builder.setView(dialogView)
                .setTitle("Agregar Nuevo Sensor")
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String unidad = etUnidad.getText().toString().trim();
                    String minStr = etMin.getText().toString().trim();
                    String maxStr = etMax.getText().toString().trim();

                    if (validarDatosSensor(nombre, minStr, maxStr)) {
                        double min = Double.parseDouble(minStr);
                        double max = Double.parseDouble(maxStr);

                        Sensor nuevoSensor = new Sensor(nombre, unidad, min, max);
                        listaSensores.add(nuevoSensor);

                        guardarSensores();
                        actualizarUI();

                        Toast.makeText(this, "Sensor agregado correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    private void mostrarDialogoEliminarSensor() {
        if (listaSensores.isEmpty()) {
            Toast.makeText(this, "No hay sensores para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nombresSensores = new String[listaSensores.size()];
        for (int i = 0; i < listaSensores.size(); i++) {
            nombresSensores[i] = listaSensores.get(i).getNombre();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona el sensor a eliminar")
                .setItems(nombresSensores, (dialog, which) -> {
                    String sensorEliminado = listaSensores.get(which).getNombre();
                    listaSensores.remove(which);

                    guardarSensores();
                    actualizarUI();

                    Toast.makeText(this, "Sensor '" + sensorEliminado + "' eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    private boolean validarDatosSensor(String nombre, String minStr, String maxStr) {
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre del sensor", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (minStr.isEmpty() || maxStr.isEmpty()) {
            Toast.makeText(this, "Ingresa los valores mínimo y máximo", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double min = Double.parseDouble(minStr);
            double max = Double.parseDouble(maxStr);

            if (min >= max) {
                Toast.makeText(this, "El valor mínimo debe ser menor al máximo", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingresa valores numéricos válidos", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void actualizarUI() {
        sensoresContainer.removeAllViews();

        for (Sensor sensor : listaSensores) {
            agregarVistaSensor(sensor);
        }

        actualizarEstadoGeneral();
        actualizarHoraActualizacion();

        btnEliminarSensor.setEnabled(!listaSensores.isEmpty());
    }

    private void agregarVistaSensor(Sensor sensor) {
        View sensorView = LayoutInflater.from(this).inflate(R.layout.item_sensor, sensoresContainer, false);

        TextView tvNombreSensor = sensorView.findViewById(R.id.tvNombreSensor);
        TextView tvValorSensor = sensorView.findViewById(R.id.tvValorSensor);
        TextView tvEstadoSensor = sensorView.findViewById(R.id.tvEstadoSensor);

        double valor = sensor.getMin() + (sensor.getMax() - sensor.getMin()) * random.nextDouble();
        sensor.setValorActual(valor);

        tvNombreSensor.setText(sensor.getNombre());
        tvValorSensor.setText(String.format(Locale.getDefault(), "%.1f %s", valor, sensor.getUnidad()));

        String estado = determinarEstado(valor, sensor);
        tvEstadoSensor.setText(estado);

        sensoresContainer.addView(sensorView);
    }

    private String determinarEstado(double valor, Sensor sensor) {
        double rango = sensor.getMax() - sensor.getMin();
        double umbralCritico = sensor.getMax() - (rango * 0.1);
        double umbralAdvertencia = sensor.getMax() - (rango * 0.25);

        if (valor >= umbralCritico) {
            return "⚠️ CRÍTICO";
        } else if (valor >= umbralAdvertencia) {
            return "⚡ ADVERTENCIA";
        } else {
            return "✅ NORMAL";
        }
    }

    private void actualizarEstadoGeneral() {
        int totalSensores = listaSensores.size();
        tvSensoresActivos.setText(totalSensores + (totalSensores == 1 ? " sensor activo" : " sensores activos"));

        if (totalSensores == 0) {
            tvEstadoGeneral.setText("➕ AGREGA TU PRIMER SENSOR");
            tvEstadoGeneral.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            boolean hayCriticos = false;
            boolean hayAdvertencias = false;

            for (Sensor sensor : listaSensores) {
                String estado = determinarEstado(sensor.getValorActual(), sensor);
                if (estado.contains("CRÍTICO")) {
                    hayCriticos = true;
                    break;
                } else if (estado.contains("ADVERTENCIA")) {
                    hayAdvertencias = true;
                }
            }

            if (hayCriticos) {
                tvEstadoGeneral.setText("⚠️ ATENCIÓN REQUERIDA");
                tvEstadoGeneral.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else if (hayAdvertencias) {
                tvEstadoGeneral.setText("⚡ REVISAR SENSORES");
                tvEstadoGeneral.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                tvEstadoGeneral.setText("✅ TODOS LOS SISTEMAS NORMALES");
                tvEstadoGeneral.setTextColor(getResources().getColor(R.color.darkGreen));
            }
        }
    }

    private void actualizarHoraActualizacion() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String horaActual = sdf.format(new Date());
        tvUltimaActualizacion.setText("Última actualización: " + horaActual);
    }

    private void iniciarActualizacionAutomatica() {
        actualizacionRunnable = new Runnable() {
            @Override
            public void run() {
                if (!listaSensores.isEmpty()) {
                    actualizarUI();
                }
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(actualizacionRunnable);
    }

    private void guardarSensores() {
        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(listaSensores);
        editor.putString("sensores", json);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && actualizacionRunnable != null) {
            handler.removeCallbacks(actualizacionRunnable);
        }
    }

    private static class Sensor {
        private String nombre;
        private String unidad;
        private double min;
        private double max;
        private double valorActual;

        public Sensor(String nombre, String unidad, double min, double max) {
            this.nombre = nombre;
            this.unidad = unidad;
            this.min = min;
            this.max = max;
            this.valorActual = 0;
        }

        public String getNombre() {
            return nombre;
        }

        public String getUnidad() {
            return unidad;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getValorActual() {
            return valorActual;
        }

        public void setValorActual(double valorActual) {
            this.valorActual = valorActual;
        }
    }
}
