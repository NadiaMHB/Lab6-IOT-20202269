package com.example.lab6_20202269.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.lab6_20202269.model.Egreso;
import com.example.lab6_20202269.repository.EgresoRepository;

import java.util.List;

public class EgresoViewModel extends AndroidViewModel {

    private final EgresoRepository repository;
    private LiveData<List<Egreso>> egresos;

    public EgresoViewModel(@NonNull Application application) {
        super(application);
        repository = new EgresoRepository();
    }

    public LiveData<List<Egreso>> getEgresos(String uid) {
        if (egresos == null) egresos = repository.getEgresos(uid);
        return egresos;
    }

    public void insertarEgreso(Egreso egreso) {
        repository.insertarEgreso(egreso);
    }

    public void actualizarEgreso(String id, double nuevoMonto, String nuevaDescripcion) {
        repository.actualizarEgreso(id, nuevoMonto, nuevaDescripcion);
    }

    public void eliminarEgreso(String id) {
        repository.eliminarEgreso(id);
    }
}
