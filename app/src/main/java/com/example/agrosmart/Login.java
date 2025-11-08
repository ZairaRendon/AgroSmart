package com.example.agrosmart;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // referencias a los elementos del layout
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        // boton de login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Validacion de credenciales
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Por favor, ingresa usuario y contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    // Aqui va validacion contra backend o base de datos
                    Toast.makeText(Login.this, "Login exitoso", Toast.LENGTH_SHORT).show();

                    // AQUI ir a otra actividad
                }
            }
        });
    }
}