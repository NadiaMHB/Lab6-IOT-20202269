package com.example.lab6_20202269;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.lab6_20202269.model.Egreso;
import com.example.lab6_20202269.model.Ingreso;
import com.example.lab6_20202269.viewmodel.ResumenViewModel;
import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
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

    private PieChart pieChart;
    private BarChart barChart;
    private TextView textMesActual;
    private Button btnCambiarMes;
    private ResumenViewModel viewModel;
    private Calendar calendario;
    private String uid;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_resumen);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_egresos) {
                startActivity(new Intent(this, EgresosActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_ingresos) {
                startActivity(new Intent(this, IngresosActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_logout) {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> {
                            com.facebook.login.LoginManager.getInstance().logOut();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        });
                return true;
            }
            return true;
        });

        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        textMesActual = findViewById(R.id.textMesActual);
        btnCambiarMes = findViewById(R.id.btnCambiarMes);

        calendario = Calendar.getInstance();
        viewModel = new ViewModelProvider(this).get(ResumenViewModel.class);

        actualizarMes();

        viewModel.getIngresos().observe(this, ingresos -> actualizarGraficos());
        viewModel.getEgresos().observe(this, egresos -> actualizarGraficos());

        btnCambiarMes.setOnClickListener(v -> mostrarDatePicker());
    }

    private void actualizarMes() {
        int mes = calendario.get(Calendar.MONTH);
        int anio = calendario.get(Calendar.YEAR);
        String nombreMes = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendario.getTime());
        textMesActual.setText(nombreMes);
        viewModel.cargarDatosPorMes(uid, mes, anio);
    }

    //aquí le pedí a la IA correcciones:
    // Prompt: hay alguna forma de que me lance solo un selector de meses? para no usar datepicker
    // y que me permita seleccionar el mes y el año, pero sin el día? Porque se que Android nativo
    // no tiene un selector de meses, pero quizás hay alguna librería que lo haga o algún método
    private void mostrarDatePicker() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        final android.view.LayoutInflater inflater = getLayoutInflater();
        final android.view.View dialogView = inflater.inflate(R.layout.dialog_month_picker, null);
        builder.setView(dialogView);

        final NumberPicker monthPicker = dialogView.findViewById(R.id.monthPicker);
        final NumberPicker yearPicker = dialogView.findViewById(R.id.yearPicker);

        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setDisplayedValues(new String[]{
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        });
        monthPicker.setValue(calendario.get(Calendar.MONTH));

        int year = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(2000);
        yearPicker.setMaxValue(year + 10);
        yearPicker.setValue(calendario.get(Calendar.YEAR));

        builder.setTitle("Seleccionar Mes y Año");
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            int mesSeleccionado = monthPicker.getValue();
            int anioSeleccionado = yearPicker.getValue();
            calendario.set(Calendar.MONTH, mesSeleccionado);
            calendario.set(Calendar.YEAR, anioSeleccionado);
            actualizarMes();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }



    // Consulta a IA para formato visual, averigué el uso de MPAndroidChart
    // solución más completa para los gráficos ya que tiene piechart (formato anillo)
    // y barchart (formato barras), es más sencillo de usar y personalizar
    private void actualizarGraficos() {
        List<Ingreso> ingresos = viewModel.getIngresos().getValue();
        List<Egreso> egresos = viewModel.getEgresos().getValue();

        if (ingresos == null || egresos == null) return;

        float totalIngresos = 0;
        for (Ingreso i : ingresos) totalIngresos += i.getMonto();

        float totalEgresos = 0;
        for (Egreso e : egresos) totalEgresos += e.getMonto();

        float total = totalIngresos - totalEgresos;

        // PieChart: % egresos vs ingresos
        pieChart.setData(null);
        List<PieEntry> pieEntries = new ArrayList<>();

        if (totalIngresos + totalEgresos > 0) {
            if (totalEgresos > 0)
                pieEntries.add(new PieEntry(totalEgresos, "Egresos"));
            if (totalIngresos > 0)
                pieEntries.add(new PieEntry(totalIngresos, "Ingresos"));
        } else {
            pieEntries.add(new PieEntry(1, "Sin datos"));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(new int[]{Color.RED, Color.GREEN});
        dataSet.setValueTextSize(16f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();

        // BarChart: ingresos, egresos, total
        barChart.setData(null);
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, totalIngresos));
        entries.add(new BarEntry(1, totalEgresos));
        entries.add(new BarEntry(2, total));

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(new int[]{Color.GREEN, Color.RED, Color.CYAN});
        BarData barData = new BarData(barDataSet);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return NumberFormat.getCurrencyInstance().format(value);
            }
        });

        // Aquí le pedí a la IA que me ayudara a configurar el BarChart
        // con correcciones en el formato de los ejes y etiquetas
        barChart.setData(barData);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setLabelCount(3);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                switch ((int) value) {
                    case 0: return "Ingresos";
                    case 1: return "Egresos";
                    case 2: return "Total";
                    default: return "";
                }
            }
        });
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }

}
