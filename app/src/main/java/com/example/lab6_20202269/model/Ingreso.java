package com.example.lab6_20202269.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.sql.Time;

public class Ingreso {

    @DocumentId
    private String id;

    private String titulo;
    private double monto;
    private String descripcion;
    private Timestamp fecha;
    private String uid;

    public Ingreso() {
    }

    public Ingreso(String titulo, double monto, String descripcion, Timestamp fecha, String uid) {
        this.titulo = titulo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.uid = uid;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public double getMonto() {
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getFecha() {
        return fecha;
    }
    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
}
