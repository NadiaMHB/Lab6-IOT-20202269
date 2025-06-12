package com.example.lab6_20202269.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.lab6_20202269.model.Ingreso;
import com.example.lab6_20202269.repository.IngresoRepository;

import java.util.List;

public class IngresoViewModel extends AndroidViewModel {
    private final IngresoRepository repository;
    private LiveData<List<Ingreso>> ingresos;

    public IngresoViewModel(@NonNull Application application) {
        super(application);
        repository = new IngresoRepository();
    }

    public LiveData<List<Ingreso>> getIngresos(String uid) {
        if (ingresos == null) ingresos = repository.getIngresos(uid);
        return ingresos;
    }

    public void insertarIngreso(Ingreso ingreso) {
        repository.insertarIngreso(ingreso);
    }

    public void actualizarIngreso(String id, double nuevoMonto, String nuevaDescripcion) {
        repository.actualizarIngreso(id, nuevoMonto, nuevaDescripcion);
    }

    public void eliminarIngreso(String id) {
        repository.eliminarIngreso(id);
    }
}
