package com.example.lab6_20202269.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lab6_20202269.model.Egreso;
import com.example.lab6_20202269.model.Ingreso;
import com.example.lab6_20202269.repository.EgresoRepository;
import com.example.lab6_20202269.repository.IngresoRepository;
import com.example.lab6_20202269.repository.ResumenRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ResumenViewModel extends AndroidViewModel {

    private final IngresoRepository ingresoRepo;
    private final EgresoRepository egresoRepo;
    private final MutableLiveData<List<Ingreso>> ingresos = new MutableLiveData<>();
    private final MutableLiveData<List<Egreso>> egresos = new MutableLiveData<>();

    public ResumenViewModel(@NonNull Application application) {
        super(application);
        ingresoRepo = new IngresoRepository();
        egresoRepo = new EgresoRepository();
    }

    public LiveData<List<Ingreso>> getIngresos() {
        return ingresos;
    }

    public LiveData<List<Egreso>> getEgresos() {
        return egresos;
    }

    public void cargarDatosPorMes(String uid, int mes, int anio) {
        ingresoRepo.getIngresos(uid).observeForever(lista -> {
            List<Ingreso> filtrados = new ArrayList<>();
            for (Ingreso i : lista) {
                if (i.getFecha() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(i.getFecha().toDate());
                    if (cal.get(Calendar.MONTH) == mes && cal.get(Calendar.YEAR) == anio) {
                        filtrados.add(i);
                    }
                }
            }
            ingresos.setValue(filtrados);
        });

        egresoRepo.getEgresos(uid).observeForever(lista -> {
            List<Egreso> filtrados = new ArrayList<>();
            for (Egreso e : lista) {
                if (e.getFecha() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(e.getFecha().toDate());
                    if (cal.get(Calendar.MONTH) == mes && cal.get(Calendar.YEAR) == anio) {
                        filtrados.add(e);
                    }
                }
            }
            egresos.setValue(filtrados);
        });
    }
}

