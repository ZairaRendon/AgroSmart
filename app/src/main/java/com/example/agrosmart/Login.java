package com.example.agrosmart;

import android.content.Intent;
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
import org.json.JSONObject;

import java.io.IOException;

public class Login extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView errorText, signupText;

    private static final String SUPABASE_URL = "https://ixnzoybpwjsvjcjucfdp.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml4bnpveWJwd2pzdmpjanVjZmRwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA5Nzk2MTQsImV4cCI6MjA3NjU1NTYxNH0.WYYpch5svJtaAdC6m2D8Vhj8qPnYJUSfhIvdcasGPz4";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                    runOnUiThread(() -> {
                        errorText.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                        // Aquí puedes iniciar otra actividad o guardar token
                    });

                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(Login.this, "Error procesando respuesta", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
