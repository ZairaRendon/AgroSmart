package com.example.agrosmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class Login extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView errorText, signupText;

    private static final String SUPABASE_URL = "https://ixnzoybpwjsvjcjucfdp.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml4bnpveWJwd2pzdmpjanVjZmRwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA5Nzk2MTQsImV4cCI6MjA3NjU1NTYxNH0.WYYpch5svJtaAdC6m2D8Vhj8qPnYJUSfhIvdcasGPz4";

    private final OkHttpClient client = new OkHttpClient();

    // SharedPreferences para manejar la sesión
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AgroSmartPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_USER_EMAIL = "userEmail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 🔸 Esta parte se comenta para que NO recuerde la sesión automáticamente
        /*
        if (isUserLoggedIn()) {
            navigateToDashboard();
            return;
        }
        */

        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        errorText = findViewById(R.id.errorText);
        signupText = findViewById(R.id.signupText);

        // Ocultar mensaje error al escribir
        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorText.setVisibility(View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        usernameEditText.addTextChangedListener(clearErrorWatcher);
        passwordEditText.addTextChangedListener(clearErrorWatcher);

        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Por favor, ingresa usuario y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            loginWithSupabase(email, password);
        });

        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
        });
    }

    private void loginWithSupabase(String email, String password) {
        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(Login.this, "Error creando JSON", Toast.LENGTH_SHORT).show());
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
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(Login.this, "Error de red: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        errorText.setText("Usuario o Contraseña no válidos");
                        errorText.setVisibility(View.VISIBLE);
                    });
                    return;
                }

                String resStr = response.body().string();
                try {
                    JSONObject resJson = new JSONObject(resStr);
                    String accessToken = resJson.getString("access_token");

                    // Obtener información del usuario
                    JSONObject user = resJson.getJSONObject("user");
                    String userId = user.getString("id");
                    String userEmail = user.getString("email");

                    // Intentar obtener el nombre desde user_metadata
                    String userName = userEmail; // Por defecto usamos el email

                    if (user.has("user_metadata")) {
                        JSONObject metadata = user.getJSONObject("user_metadata");
                        if (metadata.has("nombre")) {
                            userName = metadata.getString("nombre");
                        } else if (metadata.has("full_name")) {
                            userName = metadata.getString("full_name");
                        }
                    }

                    // Guardar sesión con todos los datos
                    String finalUserName = userName;
                    String finalUserId = userId;

                    runOnUiThread(() -> {
                        saveUserSession(userEmail, accessToken, finalUserName, finalUserId);
                        errorText.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "Login exitoso", Toast.LENGTH_SHORT).show();

                        // Intentar obtener más datos del usuario desde la base de datos
                        getUserDataFromDatabase(finalUserId, accessToken);

                        navigateToDashboard();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(Login.this, "Error procesando respuesta", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    // Método para obtener datos adicionales del usuario desde tu tabla de usuarios
    private void getUserDataFromDatabase(String userId, String accessToken) {
        // Ajusta el nombre de tu tabla según tu base de datos
        String url = SUPABASE_URL + "/rest/v1/usuarios?id=eq." + userId;

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
                // No es crítico si falla, ya tenemos datos básicos
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resStr = response.body().string();
                    try {
                        JSONArray array = new JSONArray(resStr);
                        if (array.length() > 0) {
                            JSONObject usuario = array.getJSONObject(0);

                            // Obtener datos adicionales si existen
                            String nombre = usuario.optString("nombre", "");
                            String apellido = usuario.optString("apellido", "");
                            String telefono = usuario.optString("telefono", "");

                            String nombreCompleto = nombre;
                            if (!apellido.isEmpty()) {
                                nombreCompleto = nombre + " " + apellido;
                            }

                            String finalNombreCompleto = nombreCompleto;

                            runOnUiThread(() -> {
                                // Actualizar SharedPreferences con el nombre completo
                                SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = userPrefs.edit();
                                if (!finalNombreCompleto.isEmpty()) {
                                    editor.putString("nombre_usuario", finalNombreCompleto);
                                }
                                if (!telefono.isEmpty()) {
                                    editor.putString("telefono_usuario", telefono);
                                }
                                editor.apply();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Métodos para manejar la sesión
    private void saveUserSession(String email, String accessToken, String nombre, String userId) {
        // Guardar en AgroSmartPrefs
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString("nombre_usuario", nombre);
        editor.putString("id_usuario", userId);
        editor.apply();

        // También guardar en UserPrefs para que lo use Configuracion
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPrefs.edit();
        userEditor.putString("nombre_usuario", nombre);
        userEditor.putString("id_usuario", userId);
        userEditor.putString("email_usuario", email);
        userEditor.putString("access_token", accessToken);
        userEditor.apply();
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(Login.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Método público para cerrar sesión (llamar desde Dashboard u otra actividad)
    public static void logout(AppCompatActivity activity) {
        // Limpiar ambos SharedPreferences
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        SharedPreferences userPrefs = activity.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPrefs.edit();
        userEditor.clear();
        userEditor.apply();

        Intent intent = new Intent(activity, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}