package com.example.agrosmart;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Cultivo extends AppCompatActivity {

    private LinearLayout linearLayoutCultivos;
    private MaterialButton btnAgregarCultivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultivo); // 👈 tu XML de cultivos

        // 🔹 Vincular vistas
        linearLayoutCultivos = findViewById(R.id.linearLayoutCultivos);
        btnAgregarCultivo = findViewById(R.id.btnAgregarCultivo);


    }

    /**
     * Método para agregar un cultivo dinámicamente
     */
    private void agregarNuevoCultivo(String nombreCultivo, int imagenResId) {
        // Crear ImageView
        ImageView imgCultivo = new ImageView(this);
        imgCultivo.setLayoutParams(new LinearLayout.LayoutParams(250, 180));
        imgCultivo.setImageResource(imagenResId);
        imgCultivo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams paramsImg = (LinearLayout.LayoutParams) imgCultivo.getLayoutParams();
        paramsImg.bottomMargin = 8;
        imgCultivo.setLayoutParams(paramsImg);

        // Crear TextView
        TextView txtCultivo = new TextView(this);
        txtCultivo.setText("Cultivo de " + nombreCultivo);
        txtCultivo.setTextSize(16);
        txtCultivo.setTextColor(getResources().getColor(R.color.darkGreen));
        LinearLayout.LayoutParams paramsTxt = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsTxt.bottomMargin = 24;
        txtCultivo.setLayoutParams(paramsTxt);

        // Agregar al layout
        linearLayoutCultivos.addView(imgCultivo);
        linearLayoutCultivos.addView(txtCultivo);

        // Mensaje de confirmación
        Toast.makeText(this, nombreCultivo + " agregado", Toast.LENGTH_SHORT).show();
    }
}
