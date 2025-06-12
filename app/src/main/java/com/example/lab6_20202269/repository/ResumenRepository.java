package com.example.lab6_20202269.repository;

import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class ResumenRepository {
    private static final String TAG = "ResumenRepository";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void obtenerResumenDelMes(int mes, int anio, Callback callback) {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) {
            Log.e(TAG, "Usuario no autenticado");
            callback.onError("Usuario no autenticado");
            return;
        }

        Calendar inicio = Calendar.getInstance();
        inicio.set(anio, mes, 1, 0, 0, 0);
        inicio.set(Calendar.MILLISECOND, 0);

        Calendar fin = (Calendar) inicio.clone();
        fin.add(Calendar.MONTH, 1);
        fin.add(Calendar.MILLISECOND, -1);

        Timestamp fechaInicio = new Timestamp(inicio.getTime());
        Timestamp fechaFin = new Timestamp(fin.getTime());

        float[] ingresosTotales = {0f};
        float[] egresosTotales = {0f};
        AtomicInteger pendientes = new AtomicInteger(2);

        db.collection("ingresos")
                .whereEqualTo("uid", uid)
                .whereGreaterThanOrEqualTo("fecha", fechaInicio)
                .whereLessThanOrEqualTo("fecha", fechaFin)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Number monto = doc.getDouble("monto");
                        if (monto != null) ingresosTotales[0] += monto.floatValue();
                    }
                    if (pendientes.decrementAndGet() == 0) {
                        callback.onResumenObtenido(ingresosTotales[0], egresosTotales[0]);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener ingresos", e);
                    callback.onError("Error al obtener ingresos: " + e.getMessage());
                });

        db.collection("egresos")
                .whereEqualTo("uid", uid)
                .whereGreaterThanOrEqualTo("fecha", fechaInicio)
                .whereLessThanOrEqualTo("fecha", fechaFin)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Number monto = doc.getDouble("monto");
                        if (monto != null) egresosTotales[0] += monto.floatValue();
                    }
                    if (pendientes.decrementAndGet() == 0) {
                        callback.onResumenObtenido(ingresosTotales[0], egresosTotales[0]);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener egresos", e);
                    callback.onError("Error al obtener egresos: " + e.getMessage());
                });
    }

    public interface Callback {
        void onResumenObtenido(float ingresos, float egresos);
        void onError(String error);
    }
}
