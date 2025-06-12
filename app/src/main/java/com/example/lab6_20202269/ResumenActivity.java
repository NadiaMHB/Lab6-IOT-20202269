package com.example.lab6_20202269;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.lab6_20202269.viewmodel.ResumenViewModel;
import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ResumenActivity extends AppCompatActivity {

    private TextView textMesActual;
    private Button btnCambiarMes;
    private PieChart pieChart;
    private Calendar calendario = Calendar.getInstance();
    private BottomNavigationView bottomNavigation;
    private ResumenViewModel resumenViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_resumen);

        textMesActual = findViewById(R.id.textMesActual);
        btnCambiarMes = findViewById(R.id.btnCambiarMes);
        pieChart = findViewById(R.id.pieChart);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setSelectedItemId(R.id.nav_resumen);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_ingresos) {
                startActivity(new Intent(this, IngresosActivity.class));
            } else if (id == R.id.nav_egresos) {
                startActivity(new Intent(this, EgresosActivity.class));
            } else if (id == R.id.nav_logout) {
                AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
            }
            return true;
        });

        resumenViewModel = new ViewModelProvider(this).get(ResumenViewModel.class);

        resumenViewModel.getIngresos().observe(this, ingresos -> {
            Float egresos = resumenViewModel.getEgresos().getValue();
            if (egresos != null) actualizarGraficoPie(ingresos, egresos);
        });

        resumenViewModel.getEgresos().observe(this, egresos -> {
            Float ingresos = resumenViewModel.getIngresos().getValue();
            if (ingresos != null) actualizarGraficoPie(ingresos, egresos);
        });

        actualizarMesActual();
        cargarDatosDelMes();

        btnCambiarMes.setOnClickListener(v -> mostrarDatePicker());
    }

    private void actualizarMesActual() {
        SimpleDateFormat formato = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        String mesTexto = formato.format(calendario.getTime());
        textMesActual.setText("Mes: " + mesTexto.substring(0, 1).toUpperCase() + mesTexto.substring(1));
    }

    private void mostrarDatePicker() {
        int year = calendario.get(Calendar.YEAR);
        int month = calendario.get(Calendar.MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year1, month1, day) -> {
            calendario.set(Calendar.YEAR, year1);
            calendario.set(Calendar.MONTH, month1);
            actualizarMesActual();
            cargarDatosDelMes();
        }, year, month, 1);

        try {
            int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
            if (daySpinnerId != 0) {
                View daySpinner = dialog.getDatePicker().findViewById(daySpinnerId);
                if (daySpinner != null) daySpinner.setVisibility(View.GONE);
            }
        } catch (Exception ignored) {}

        dialog.show();
    }

    private void cargarDatosDelMes() {
        int mes = calendario.get(Calendar.MONTH);
        int anio = calendario.get(Calendar.YEAR);
        resumenViewModel.cargarResumenDelMes(mes, anio);
    }

    private void actualizarGraficoPie(float ingresos, float egresos) {
        float consolidado = ingresos - egresos;
        float porcentaje = (ingresos > 0) ? (egresos / ingresos) * 100 : 0;

        List<PieEntry> entries = new ArrayList<>();

        if (ingresos == 0 && egresos == 0) {
            entries.add(new PieEntry(100f, "Sin datos"));
        } else if (ingresos == 0) {
            entries.add(new PieEntry(100f, "Solo Egresos"));
        } else {
            if (porcentaje > 0) {
                entries.add(new PieEntry(porcentaje, "Egresos"));
                entries.add(new PieEntry(100 - porcentaje, "Ahorro"));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(244, 67, 54)); // rojo para egresos
        colors.add(Color.rgb(76, 175, 80)); // verde para ahorro
        colors.add(Color.rgb(158, 158, 158)); // gris para sin datos
        dataSet.setColors(colors);
        dataSet.setValueTextSize(16f);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%d%%", (int) value);
            }
        });

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.getLegend().setTextSize(14f);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        pieChart.setDescription(null);
        pieChart.invalidate();
    }
}
