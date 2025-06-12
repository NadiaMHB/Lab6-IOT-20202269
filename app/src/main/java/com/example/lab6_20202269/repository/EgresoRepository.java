package com.example.lab6_20202269.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab6_20202269.model.Egreso;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EgresoRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Egreso>> egresosLiveData = new MutableLiveData<>();

    public LiveData<List<Egreso>> getEgresos(String uid) {
        db.collection("egresos")
                .whereEqualTo("uid", uid)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;
                    List<Egreso> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Egreso egreso = doc.toObject(Egreso.class);
                        lista.add(egreso);
                    }
                    egresosLiveData.setValue(lista);
                });
        return egresosLiveData;
    }

    public void insertarEgreso(Egreso egreso) {
        db.collection("egresos").add(egreso);
    }

    public void actualizarEgreso(String id, double nuevoMonto, String nuevaDescripcion) {
        db.collection("egresos").document(id)
                .update("monto", nuevoMonto, "descripcion", nuevaDescripcion);
    }

    public void eliminarEgreso(String id) {
        db.collection("egresos").document(id).delete();
    }


}
