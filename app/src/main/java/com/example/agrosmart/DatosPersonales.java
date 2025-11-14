package com.example.agrosmart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatosPersonales extends AppCompatActivity {

    private TextView txtNombreCompleto, txtCorreo, txtTelefono, txtTipoProductor, txtFechaRegistro;
    private ImageView imgPerfilDatos;

    private SharedPreferences sharedPreferences;
    private String userId, userEmail, accessToken;

    private static final String SUPABASE_URL = "https://ixnzoybpwjsvjcjucfdp.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml4bnpveWJwd2pzdmpjanVjZmRwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA5Nzk2MTQsImV4cCI6MjA3NjU1NTYxNH0.WYYpch5svJtaAdC6m2D8Vhj8qPnYJUSfhIvdcasGPz4";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_personales);

        // Inicializar vistas
        txtNombreCompleto = findViewById(R.id.txtNombreCompleto);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtTipoProductor = findViewById(R.id.txtTipoProductor);
        txtFechaRegistro = findViewById(R.id.txtFechaRegistro);
        imgPerfilDatos = findViewById(R.id.imgPerfilDatos);

        // Obtener datos de SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("id_usuario", "");
        userEmail = sharedPreferences.getString("email_usuario", "");
        accessToken = sharedPreferences.getString("access_token", "");

        // Mostrar datos básicos inmediatamente
        cargarDatosBasicos();

        // Cargar datos completos desde la base de datos
        cargarDatosCompletos();
    }

    private void cargarDatosBasicos() {
        // Cargar datos que ya tenemos en SharedPreferences
        String nombre = sharedPreferences.getString("nombre_usuario", "Usuario");
        String telefono = sharedPreferences.getString("telefono_usuario", "No registrado");
        String tipoProductor = sharedPreferences.getString("tipo_productor", "No especificado");

        txtNombreCompleto.setText(nombre);
        txtCorreo.setText(userEmail);
        txtTelefono.setText(telefono);
        txtTipoProductor.setText(tipoProductor);
    }

    private void cargarDatosCompletos() {
        if (userId.isEmpty() || accessToken.isEmpty()) {
            Toast.makeText(this, "Error: No se encontraron datos de sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener datos del usuario desde Supabase Auth
        String url = SUPABASE_URL + "/auth/v1/user";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(DatosPersonales.this,
                                "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(DatosPersonales.this,
                                    "No se pudieron cargar los datos", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                String resStr = response.body().string();
                try {
                    JSONObject user = new JSONObject(resStr);

                    String email = user.optString("email", userEmail);
                    String createdAt = user.optString("created_at", "");

                    // Formatear fecha
                    String fechaFormateada = "No disponible";
                    if (!createdAt.isEmpty()) {
                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            Date date = inputFormat.parse(createdAt);
                            fechaFormateada = outputFormat.format(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    String finalFecha = fechaFormateada;
                    String finalEmail = email;

                    runOnUiThread(() -> {
                        txtCorreo.setText(finalEmail);
                        txtFechaRegistro.setText(finalFecha);
                    });

                    // Cargar datos adicionales desde tabla de usuarios
                    cargarDatosDesdeTabla();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(DatosPersonales.this,
                                    "Error procesando datos", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void cargarDatosDesdeTabla() {
        // IMPORTANTE: El nombre de la tabla es "Usuario" con U mayúscula
        // y el campo para buscar es "user_uuid" no "id"
        String url = SUPABASE_URL + "/rest/v1/Usuario?user_uuid=eq." + userId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(DatosPersonales.this,
                                "No se pudieron cargar datos adicionales", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resStr = response.body().string();
                    try {
                        JSONArray array = new JSONArray(resStr);
                        if (array.length() > 0) {
                            JSONObject usuario = array.getJSONObject(0);

                            // Obtener los campos exactos de tu tabla Usuario
                            String nombre = usuario.optString("nombre", "No registrado");
                            String correo = usuario.optString("correo", userEmail);
                            String telefono = usuario.optString("telefono", "No registrado");
                            String productor = usuario.optString("productor", "No especificado");

                            // Capitalizar el tipo de productor para mejor presentación
                            String tipoProductor = productor.substring(0, 1).toUpperCase() +
                                    productor.substring(1).toLowerCase();

                            runOnUiThread(() -> {
                                txtNombreCompleto.setText(nombre);
                                txtCorreo.setText(correo);
                                txtTelefono.setText(telefono);
                                txtTipoProductor.setText(tipoProductor);

                                // Guardar en SharedPreferences para uso futuro
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("nombre_usuario", nombre);
                                editor.putString("telefono_usuario", telefono);
                                editor.putString("tipo_productor", tipoProductor);
                                editor.apply();
                            });
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(DatosPersonales.this,
                                            "No se encontraron datos adicionales del usuario", Toast.LENGTH_SHORT).show()
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(DatosPersonales.this,
                                        "Error procesando datos adicionales", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(DatosPersonales.this,
                                    "No se pudieron obtener datos adicionales", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}