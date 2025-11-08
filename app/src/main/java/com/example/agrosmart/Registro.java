package com.example.agrosmart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class Registro extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private MaterialButton btnPequeno, btnMediano;
    private Button registerButton;
    private ProgressDialog progressDialog;

    // IMPORTANTE: Mueve estas credenciales a BuildConfig en producción
    private static final String SUPABASE_URL = "https://ixnzoybpwjsvjcjucfdp.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml4bnpveWJwd2pzdmpjanVjZmRwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA5Nzk2MTQsImV4cCI6MjA3NjU1NTYxNH0.WYYpch5svJtaAdC6m2D8Vhj8qPnYJUSfhIvdcasGPz4";

    private final OkHttpClient client = new OkHttpClient();
    private static final String TAG = "SupabaseRegistro";
    private String productorSeleccionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        initViews();
        setupListeners();
    }

    private void initViews() {
        nameEditText = findViewById(R.id.name);
        phoneEditText = findViewById(R.id.phone);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        btnPequeno = findViewById(R.id.btnPequeno);
        btnMediano = findViewById(R.id.btnMediano);
        registerButton = findViewById(R.id.registerButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando usuario...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        btnPequeno.setOnClickListener(v -> {
            productorSeleccionado = "pequeno";
            btnPequeno.setBackgroundTintList(getResources().getColorStateList(R.color.darkGreen));
            btnMediano.setBackgroundTintList(getResources().getColorStateList(R.color.lightGreen));
        });

        btnMediano.setOnClickListener(v -> {
            productorSeleccionado = "mediano";
            btnMediano.setBackgroundTintList(getResources().getColorStateList(R.color.darkGreen));
            btnPequeno.setBackgroundTintList(getResources().getColorStateList(R.color.lightGreen));
        });

        registerButton.setOnClickListener(v -> validarYRegistrar());
    }

    private void validarYRegistrar() {
        String nombre = nameEditText.getText().toString().trim();
        String telefono = phoneEditText.getText().toString().trim();
        String correo = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Validaciones
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es requerido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (telefono.isEmpty()) {
            Toast.makeText(this, "El teléfono es requerido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (telefono.length() < 10) {
            Toast.makeText(this, "Teléfono debe tener al menos 10 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (correo.isEmpty()) {
            Toast.makeText(this, "El correo es requerido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "La contraseña es requerida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productorSeleccionado.isEmpty()) {
            Toast.makeText(this, "Selecciona el tipo de productor", Toast.LENGTH_SHORT).show();
            return;
        }

        // Todo válido, proceder con el registro
        registrarUsuarioSupabase(nombre, telefono, correo, password, productorSeleccionado);
    }

    private void registrarUsuarioSupabase(String nombre, String telefono, String correo, String password, String productor) {
        progressDialog.show();

        String url = SUPABASE_URL + "/auth/v1/signup";

        JSONObject json = new JSONObject();
        try {
            json.put("email", correo);
            json.put("password", password);

            // Metadata adicional que se puede usar
            JSONObject metadata = new JSONObject();
            metadata.put("nombre", nombre);
            metadata.put("tipo_productor", productor);
            json.put("data", metadata);

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error al preparar datos", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error creando JSON", e);
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(Registro.this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
                Log.e(TAG, "Error de red al registrar usuario", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resStr = response.body().string();
                Log.d(TAG, "Respuesta registro Auth: " + resStr);

                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        String mensaje = parseErrorMessage(resStr, response.code());
                        Toast.makeText(Registro.this, mensaje, Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                try {
                    JSONObject responseJson = new JSONObject(resStr);

                    // El usuario está dentro del objeto "user"
                    if (responseJson.has("user")) {
                        JSONObject userJson = responseJson.getJSONObject("user");
                        String userId = userJson.getString("id");

                        // Guardar perfil adicional en la tabla Usuario
                        guardarPerfilUsuario(nombre, telefono, correo, productor, userId);
                    } else {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(Registro.this, "Error: No se recibió información del usuario", Toast.LENGTH_SHORT).show();
                        });
                    }

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(Registro.this, "Error al procesar respuesta del servidor", Toast.LENGTH_SHORT).show();
                    });
                    Log.e(TAG, "Error parseando JSON registro", e);
                }
            }
        });
    }

    private void guardarPerfilUsuario(String nombre, String telefono, String correo, String productor, String userId) {
        String url = SUPABASE_URL + "/rest/v1/Usuario";

        JSONObject jsonPerfil = new JSONObject();
        try {
            jsonPerfil.put("nombre", nombre);
            jsonPerfil.put("correo", correo);
            jsonPerfil.put("productor", productor);
            jsonPerfil.put("telefono", telefono);
            jsonPerfil.put("user_uuid", userId);
            // NOTA: NO guardamos la contraseña aquí
        } catch (Exception e) {
            runOnUiThread(() -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Error al preparar datos del perfil", Toast.LENGTH_SHORT).show();
            });
            Log.e(TAG, "Error creando JSON perfil", e);
            return;
        }

        RequestBody body = RequestBody.create(jsonPerfil.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(Registro.this, "Error al guardar perfil: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
                Log.e(TAG, "Error red guardando perfil", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resStr = response.body().string();
                Log.d(TAG, "Respuesta guardando perfil: " + resStr);

                runOnUiThread(() -> {
                    progressDialog.dismiss();

                    if (response.isSuccessful() || response.code() == 201) {
                        Toast.makeText(Registro.this, "¡Registro exitoso!", Toast.LENGTH_LONG).show();

                        // Redirigir al login o pantalla principal
                        Intent intent = new Intent(Registro.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Registro.this, "Error al guardar perfil: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private String parseErrorMessage(String errorBody, int code) {
        try {
            JSONObject errorJson = new JSONObject(errorBody);
            if (errorJson.has("msg")) {
                String msg = errorJson.getString("msg");

                // Traducir mensajes comunes
                if (msg.contains("already registered") || msg.contains("already exists")) {
                    return "Este correo ya está registrado";
                }
                if (msg.contains("invalid email")) {
                    return "Correo electrónico inválido";
                }
                if (msg.contains("password")) {
                    return "La contraseña no cumple los requisitos";
                }

                return msg;
            }

            if (errorJson.has("error_description")) {
                return errorJson.getString("error_description");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parseando mensaje de error", e);
        }

        // Mensaje por defecto basado en código
        switch (code) {
            case 400: return "Datos inválidos";
            case 422: return "Este correo ya está registrado";
            case 500: return "Error del servidor";
            default: return "Error de registro (código: " + code + ")";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}