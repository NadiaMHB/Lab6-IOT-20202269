package com.example.lab6_20202269;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6_20202269.adapter.EgresoAdapter;
import com.example.lab6_20202269.model.Egreso;
import com.example.lab6_20202269.viewmodel.EgresoViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EgresosActivity extends AppCompatActivity implements OnDateSelectedListener {

    private BottomNavigationView bottomNavigation;
    private EgresoViewModel egresoViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAgregarEgreso;
    private EgresoAdapter adapter;
    private EditText selectedFechaField;
    private TextView textVacio;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        uid = auth.getCurrentUser().getUid();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egresos);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        recyclerView = findViewById(R.id.recyclerEgresos);
        fabAgregarEgreso = findViewById(R.id.fabAgregarEgreso);
        textVacio = findViewById(R.id.textVacioEgreso);

        bottomNavigation.setSelectedItemId(R.id.nav_egresos);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_ingresos) {
                startActivity(new Intent(this, IngresosActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_resumen) {
                startActivity(new Intent(this, ResumenActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_logout) {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> {
                            com.facebook.login.LoginManager.getInstance().logOut();
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        });
                return true;
            }
            return true;
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        egresoViewModel = new ViewModelProvider(this).get(EgresoViewModel.class);

        adapter = new EgresoAdapter();
        adapter.setContext(this);
        adapter.setViewModel(egresoViewModel);
        recyclerView.setAdapter(adapter);

        egresoViewModel.getEgresos(uid).observe(this, egresos -> {
            adapter.setListaEgresos(egresos);
            textVacio.setVisibility(egresos.isEmpty() ? View.VISIBLE : View.GONE);
        });

        fabAgregarEgreso.setOnClickListener(v -> mostrarDialogoEgreso());
    }

    private void mostrarDialogoEgreso() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_registro_egreso, null);
        builder.setView(view);

        EditText titulo = view.findViewById(R.id.editTextTitulo);
        EditText monto = view.findViewById(R.id.editTextMonto);
        EditText descripcion = view.findViewById(R.id.editTextDescripcion);
        EditText fecha = view.findViewById(R.id.editTextFecha);

        fecha.setOnClickListener(v -> {
            DatePickerFragment picker = new DatePickerFragment();
            picker.show(getSupportFragmentManager(), "datepicker");
            selectedFechaField = fecha;
        });

        builder.setPositiveButton("Guardar", (dialogInterface, i) -> {
            String tituloStr = titulo.getText().toString().trim();
            String montoStr = monto.getText().toString().trim();
            String descripcionStr = descripcion.getText().toString().trim();
            String fechaStr = fecha.getText().toString().trim();

            if (!tituloStr.isEmpty() && !montoStr.isEmpty() && !fechaStr.isEmpty()) {
                double montoVal = Double.parseDouble(montoStr);
                if (descripcionStr == null) descripcionStr = "";

                // Convertir fecha string a Timestamp
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date date = sdf.parse(fechaStr);
                    Timestamp timestamp = new Timestamp(date);

                    Egreso egreso = new Egreso(tituloStr, montoVal, descripcionStr, timestamp, uid);
                    egresoViewModel.insertarEgreso(egreso);
                    Toast.makeText(this, "Egreso registrado", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Error al convertir la fecha", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    public void onDateSelected(int year, int month, int day) {
        String fecha = String.format("%04d-%02d-%02d", year, month + 1, day);
        if (selectedFechaField != null) {
            selectedFechaField.setText(fecha);
        }
    }
}
