package com.example.agrosmart;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Random;

public class GestionRecursos extends AppCompatActivity {

    private TextView tvAguaTotal, tvAguaPromedio, tvAguaEstado;
    private TextView tvFertilizanteTotal, tvFertilizantePromedio, tvFertilizanteEstado;
    private TextView tvEnergiaTotal, tvEnergiaPromedio, tvEnergiaEstado;
    private MaterialButton btnActualizar, btnExportar;
    private TabLayout tabLayout;

    private LineChart lineChart;
    private BarChart barChart;
    private PieChart pieChart;

    private String recursoActual = "agua";
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_recursos);

        random = new Random();
        initializeViews();
        setupTabs();
        setupListeners();

        // Cargar datos iniciales
        updateAllChartsAndData();
    }

    private void initializeViews() {
        // Agua
        tvAguaTotal = findViewById(R.id.tvAguaTotal);
        tvAguaPromedio = findViewById(R.id.tvAguaPromedio);
        tvAguaEstado = findViewById(R.id.tvAguaEstado);

        // Fertilizante
        tvFertilizanteTotal = findViewById(R.id.tvFertilizanteTotal);
        tvFertilizantePromedio = findViewById(R.id.tvFertilizantePromedio);
        tvFertilizanteEstado = findViewById(R.id.tvFertilizanteEstado);

        // Energía
        tvEnergiaTotal = findViewById(R.id.tvEnergiaTotal);
        tvEnergiaPromedio = findViewById(R.id.tvEnergiaPromedio);
        tvEnergiaEstado = findViewById(R.id.tvEnergiaEstado);

        // Botones
        btnActualizar = findViewById(R.id.btnActualizar);
        btnExportar = findViewById(R.id.btnExportar);

        // Tabs
        tabLayout = findViewById(R.id.tabLayoutRecursos);

        // Gráficas
        lineChart = findViewById(R.id.lineChartConsumo);
        barChart = findViewById(R.id.barChartComparativo);
        pieChart = findViewById(R.id.pieChartDistribucion);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("💧 Agua"));
        tabLayout.addTab(tabLayout.newTab().setText("🌱 Fertilizante"));
        tabLayout.addTab(tabLayout.newTab().setText("⚡ Energía"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        recursoActual = "agua";
                        break;
                    case 1:
                        recursoActual = "fertilizante";
                        break;
                    case 2:
                        recursoActual = "energia";
                        break;
                }
                updateAllChartsAndData();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupListeners() {
        btnActualizar.setOnClickListener(v -> {
            updateAllChartsAndData();
            Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
        });

        btnExportar.setOnClickListener(v ->
                Toast.makeText(this, "Función de exportar próximamente", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateAllChartsAndData() {
        generateSimulatedData(recursoActual);
        updateLineChart(recursoActual);
        updateBarChart();
        updatePieChart();
    }

    private void generateSimulatedData(String recurso) {
        int total = 0;
        int promedio = 0;
        String unidad = "";
        String estado;

        switch (recurso) {
            case "agua":
                total = 100 + random.nextInt(150);
                promedio = total / 7;
                unidad = "L";
                break;
            case "fertilizante":
                total = 10 + random.nextInt(25);
                promedio = total / 7;
                unidad = "kg";
                break;
            case "energia":
                total = 50 + random.nextInt(70);
                promedio = total / 7;
                unidad = "kWh";
                break;
        }

        int porcentaje = 60 + random.nextInt(40);
        if (porcentaje < 70) estado = "ÓPTIMO";
        else if (porcentaje < 85) estado = "NORMAL";
        else estado = "ALTO";

        updateSummary(recurso, total, promedio, estado, unidad);
    }

    private void updateSummary(String recurso, int total, int promedio, String estado, String unidad) {
        int color;
        switch (estado) {
            case "ÓPTIMO":
                color = Color.parseColor("#4CAF50");
                break;
            case "NORMAL":
                color = Color.parseColor("#FFC107");
                break;
            default:
                color = Color.parseColor("#F44336");
        }

        switch (recurso) {
            case "agua":
                tvAguaTotal.setText(total + " " + unidad);
                tvAguaPromedio.setText(promedio + " " + unidad + "/día");
                tvAguaEstado.setText(estado);
                tvAguaEstado.setTextColor(color);
                break;
            case "fertilizante":
                tvFertilizanteTotal.setText(total + " " + unidad);
                tvFertilizantePromedio.setText(promedio + " " + unidad + "/día");
                tvFertilizanteEstado.setText(estado);
                tvFertilizanteEstado.setTextColor(color);
                break;
            case "energia":
                tvEnergiaTotal.setText(total + " " + unidad);
                tvEnergiaPromedio.setText(promedio + " " + unidad + "/día");
                tvEnergiaEstado.setText(estado);
                tvEnergiaEstado.setTextColor(color);
                break;
        }
    }

    private void updateLineChart(String recurso) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            entries.add(new Entry(i, random.nextInt(100)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Consumo Semanal (" + recurso + ")");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setCircleColor(Color.parseColor("#388E3C"));
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        Description desc = new Description();
        desc.setText("");
        lineChart.setDescription(desc);
        lineChart.invalidate();
    }

    private void updateBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(new BarEntry(i, random.nextInt(100)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Comparativo Diario");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        Description desc = new Description();
        desc.setText("");
        barChart.setDescription(desc);
        barChart.invalidate();
    }

    private void updatePieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(random.nextInt(40) + 10, "Riego"));
        entries.add(new PieEntry(random.nextInt(30) + 10, "Fertilización"));
        entries.add(new PieEntry(random.nextInt(30) + 10, "Energía"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{Color.parseColor("#4CAF50"), Color.parseColor("#FF9800"), Color.parseColor("#03A9F4")});
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);
        pieChart.invalidate();
    }
}
