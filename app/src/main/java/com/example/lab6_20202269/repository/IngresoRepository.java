package com.example.lab6_20202269.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab6_20202269.model.Ingreso;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class IngresoRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Ingreso>> ingresosLiveData = new MutableLiveData<>();

    public LiveData<List<Ingreso>> getIngresos(String uid) {
        db.collection("ingresos")
                .whereEqualTo("uid", uid)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;
                    List<Ingreso> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Ingreso ingreso = doc.toObject(Ingreso.class);
                        lista.add(ingreso); // el ID se autocompleta con @DocumentId
                    }
                    ingresosLiveData.setValue(lista);
                });
        return ingresosLiveData;
    }

    public void insertarIngreso(Ingreso ingreso) {
        FirebaseFirestore.getInstance()
                .collection("ingresos")
                .add(ingreso);
    }

    public void actualizarIngreso(String id, double nuevoMonto, String nuevaDescripcion) {
        db.collection("ingresos").document(id)
                .update("monto", nuevoMonto, "descripcion", nuevaDescripcion);
    }

    public void eliminarIngreso(String id) {
        db.collection("ingresos").document(id).delete();
    }


}
