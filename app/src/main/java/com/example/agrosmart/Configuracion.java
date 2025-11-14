package com.example.agrosmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Configuracion extends AppCompatActivity {

    private TextView txtNombreUsuario;
    private TextView btnDatosPersonales;
    private TextView btnNotificaciones;
    private TextView btnIdioma;
    private TextView btnSincronizacion;
    private TextView btnCerrarSesion;
    private ImageView imgPerfil;

    private SharedPreferences sharedPreferences;
    private String nombreUsuario;
    private String idUsuario; // Para enviar a DatosPersonalesActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion); // Cambia por el nombre de tu layout

        // Inicializar vistas
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        btnDatosPersonales = findViewById(R.id.btnDatosPersonales);
        btnNotificaciones = findViewById(R.id.btnNotificaciones);
        btnIdioma = findViewById(R.id.btnIdioma);
        btnSincronizacion = findViewById(R.id.btnSincronizacion);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        imgPerfil = findViewById(R.id.imgPerfil);

        // Obtener datos del usuario desde SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        nombreUsuario = sharedPreferences.getString("nombre_usuario", "Usuario");
        idUsuario = sharedPreferences.getString("id_usuario", "");

        // Mostrar nombre del usuario
        txtNombreUsuario.setText(nombreUsuario);

        // Configurar clicks
        btnDatosPersonales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a la actividad de datos personales
                Intent intent = new Intent(Configuracion.this, DatosPersonales.class);
                startActivity(intent);
            }
        });

        btnNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Configuracion.this,
                        "Configuración de notificaciones próximamente",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnIdioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Configuracion.this,
                        "Selección de idioma próximamente",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnSincronizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Configuracion.this,
                        "Sincronización de sensores exitosa",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
    }

    private void cerrarSesion() {
        // Limpiar ambos SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // También limpiar AgroSmartPrefs
        SharedPreferences agroPrefs = getSharedPreferences("AgroSmartPrefs", MODE_PRIVATE);
        SharedPreferences.Editor agroEditor = agroPrefs.edit();
        agroEditor.clear();
        agroEditor.apply();

        // Ir a la pantalla de login
        Intent intent = new Intent(Configuracion.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
    }
}