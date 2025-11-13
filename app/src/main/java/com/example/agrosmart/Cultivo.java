package com.example.agrosmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Cultivo extends AppCompatActivity {

    private LinearLayout linearLayoutCultivos;
    private MaterialButton btnAgregarCultivo;
    private ActivityResultLauncher<Intent> agregarCultivoLauncher;
    private List<CultivoModelo> listaCultivos;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultivo);

        // 🔹 Inicializar SharedPreferences y Gson
        sharedPreferences = getSharedPreferences("CultivosPrefs", Context.MODE_PRIVATE);
        gson = new Gson();
        listaCultivos = new ArrayList<>();

        // 🔹 Vincular vistas
        linearLayoutCultivos = findViewById(R.id.linearLayoutCultivos);
        btnAgregarCultivo = findViewById(R.id.btnAgregarCultivo);

        // 🔹 Cargar cultivos guardados
        cargarCultivos();

        // 🔹 Configurar el launcher para recibir resultados
        agregarCultivoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String nombre = data.getStringExtra("nombreCultivo");
                        String fecha = data.getStringExtra("fechaInicio");

                        // Crear nuevo cultivo y guardarlo
                        CultivoModelo nuevoCultivo = new CultivoModelo(nombre, fecha);
                        listaCultivos.add(nuevoCultivo);
                        guardarCultivos();

                        agregarNuevoCultivoVista(nombre, fecha);
                    }
                });

        // 🔹 Acción del botón "Agregar Cultivo"
        btnAgregarCultivo.setOnClickListener(v -> {
            Intent intent = new Intent(Cultivo.this, AgregarCultivo.class);
            agregarCultivoLauncher.launch(intent);
        });
    }

    /**
     * Guarda la lista de cultivos en SharedPreferences
     */
    private void guardarCultivos() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(listaCultivos);
        editor.putString("listaCultivos", json);
        editor.apply();
    }

    /**
     * Carga la lista de cultivos desde SharedPreferences
     */
    private void cargarCultivos() {
        String json = sharedPreferences.getString("listaCultivos", null);
        Type type = new TypeToken<ArrayList<CultivoModelo>>() {}.getType();

        if (json != null) {
            listaCultivos = gson.fromJson(json, type);

            // Mostrar todos los cultivos guardados
            for (CultivoModelo cultivo : listaCultivos) {
                agregarNuevoCultivoVista(cultivo.getNombre(), cultivo.getFechaInicio());
            }
        }
    }

    /**
     * Busca una imagen drawable basándose en el nombre del cultivo
     */
    private int buscarImagenPorNombre(String nombreCultivo) {
        // Convertir el nombre a minúsculas y quitar espacios
        String nombreLimpio = nombreCultivo.toLowerCase().trim().replace(" ", "_");

        // Intentar buscar el drawable
        int resId = getResources().getIdentifier(nombreLimpio, "drawable", getPackageName());

        // Si no encuentra la imagen, usar una imagen por defecto
        if (resId == 0) {
            // Puedes crear un drawable llamado "cultivo_default" como imagen por defecto
            resId = getResources().getIdentifier("cultivo_default", "drawable", getPackageName());

            // Si tampoco existe la imagen por defecto, usar el ícono de Android
            if (resId == 0) {
                resId = android.R.drawable.ic_menu_gallery;
            }
        }

        return resId;
    }

    /**
     * Método para agregar un cultivo dinámicamente a la vista
     */
    private void agregarNuevoCultivoVista(String nombreCultivo, String fechaInicio) {
        // Contenedor individual del cultivo
        LinearLayout layoutCultivo = new LinearLayout(this);
        layoutCultivo.setOrientation(LinearLayout.VERTICAL);
        layoutCultivo.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsLayout.bottomMargin = 24;
        layoutCultivo.setLayoutParams(paramsLayout);
        layoutCultivo.setPadding(16, 16, 16, 16);

        // 🖼 Imagen del cultivo
        ImageView imgCultivo = new ImageView(this);
        LinearLayout.LayoutParams paramsImg = new LinearLayout.LayoutParams(550, 450);
        paramsImg.bottomMargin = 12;
        imgCultivo.setLayoutParams(paramsImg);
        imgCultivo.setImageResource(buscarImagenPorNombre(nombreCultivo));
        imgCultivo.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // 📝 Nombre del cultivo (en minúsculas)
        TextView txtCultivo = new TextView(this);
        txtCultivo.setText("Cultivo de " + nombreCultivo.toLowerCase());
        txtCultivo.setTextSize(18);
        txtCultivo.setTextColor(getResources().getColor(R.color.darkGreen));
        txtCultivo.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams paramsTxt = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsTxt.bottomMargin = 8;
        txtCultivo.setLayoutParams(paramsTxt);

        // 📅 Fecha de inicio
        TextView txtFecha = new TextView(this);
        txtFecha.setText("Fecha de inicio: " + fechaInicio);
        txtFecha.setTextSize(16);
        txtFecha.setTextColor(0xFF4C4C4C);
        LinearLayout.LayoutParams paramsFecha = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsFecha.bottomMargin = 12;
        txtFecha.setLayoutParams(paramsFecha);

        // ❌ Botón para eliminar cultivo
        MaterialButton btnEliminar = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnEliminar.setText("Eliminar Cultivo");
        btnEliminar.setTextColor(getResources().getColor(android.R.color.white));
        btnEliminar.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_red_dark));
        LinearLayout.LayoutParams paramsBtn = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnEliminar.setLayoutParams(paramsBtn);

        // Acción del botón eliminar
        btnEliminar.setOnClickListener(v -> {
            // Eliminar de la lista y guardar
            listaCultivos.removeIf(c ->
                    c.getNombre().equalsIgnoreCase(nombreCultivo) &&
                            c.getFechaInicio().equals(fechaInicio)
            );
            guardarCultivos();

            // Eliminar de la vista
            linearLayoutCultivos.removeView(layoutCultivo);
            Toast.makeText(Cultivo.this, nombreCultivo.toLowerCase() + " eliminado", Toast.LENGTH_SHORT).show();
        });

        // Agregar todo al contenedor del cultivo
        layoutCultivo.addView(imgCultivo);
        layoutCultivo.addView(txtCultivo);
        layoutCultivo.addView(txtFecha);
        layoutCultivo.addView(btnEliminar);

        // Agregar el cultivo antes del botón "Agregar Cultivo"
        int posicionBoton = linearLayoutCultivos.indexOfChild(btnAgregarCultivo);
        linearLayoutCultivos.addView(layoutCultivo, posicionBoton);
    }
}