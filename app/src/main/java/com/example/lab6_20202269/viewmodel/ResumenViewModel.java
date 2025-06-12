package com.example.lab6_20202269.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lab6_20202269.repository.ResumenRepository;

public class ResumenViewModel extends ViewModel {

    private final MutableLiveData<Float> ingresosLiveData = new MutableLiveData<>(0f);
    private final MutableLiveData<Float> egresosLiveData = new MutableLiveData<>(0f);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>(null);

    private final ResumenRepository repository = new ResumenRepository();

    public LiveData<Float> getIngresos() {
        return ingresosLiveData;
    }

    public LiveData<Float> getEgresos() {
        return egresosLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void cargarResumenDelMes(int mes, int anio) {
        repository.obtenerResumenDelMes(mes, anio, new ResumenRepository.Callback() {
            @Override
            public void onResumenObtenido(float ingresos, float egresos) {
                ingresosLiveData.postValue(ingresos);
                egresosLiveData.postValue(egresos);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                ingresosLiveData.postValue(0f);
                egresosLiveData.postValue(0f);
            }
        });
    }
}
