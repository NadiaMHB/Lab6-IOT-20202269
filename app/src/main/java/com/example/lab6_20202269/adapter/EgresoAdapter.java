package com.example.lab6_20202269.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6_20202269.R;
import com.example.lab6_20202269.model.Egreso;
import com.example.lab6_20202269.viewmodel.EgresoViewModel;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EgresoAdapter extends RecyclerView.Adapter<EgresoAdapter.EgresoViewHolder> {

    private List<Egreso> listaEgresos = new ArrayList<>();
    private Context context;
    private EgresoViewModel viewModel;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setViewModel(EgresoViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setListaEgresos(List<Egreso> lista) {
        this.listaEgresos = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EgresoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_egreso, parent, false);
        return new EgresoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull EgresoViewHolder holder, int position) {
        Egreso egreso = listaEgresos.get(position);
        holder.textTitulo.setText(egreso.getTitulo());
        holder.textMonto.setText("S/ " + egreso.getMonto());

        // Convertir Timestamp a String legible
        Timestamp fechaTimestamp = egreso.getFecha();
        if (fechaTimestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String fechaFormateada = sdf.format(fechaTimestamp.toDate());
            holder.textFecha.setText(fechaFormateada);
        } else {
            holder.textFecha.setText("Sin fecha");
        }

        if (egreso.getDescripcion() != null && !egreso.getDescripcion().isEmpty()) {
            holder.textDescripcion.setText(egreso.getDescripcion());
            holder.textDescripcion.setVisibility(View.VISIBLE);
        } else {
            holder.textDescripcion.setVisibility(View.GONE);
        }

        holder.btnEditar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_editar_egreso, null);
            builder.setView(view);

            EditText editTitulo = view.findViewById(R.id.editTextTitulo);
            EditText editFecha = view.findViewById(R.id.editTextFecha);
            EditText editMonto = view.findViewById(R.id.editTextNuevoMonto);
            EditText editDesc = view.findViewById(R.id.editTextNuevaDescripcion);

            editTitulo.setText(egreso.getTitulo());

            if (fechaTimestamp != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                editFecha.setText(sdf.format(fechaTimestamp.toDate()));
            }

            editMonto.setText(String.valueOf(egreso.getMonto()));
            editDesc.setText(egreso.getDescripcion());

            builder.setPositiveButton("Actualizar", (dialog, which) -> {
                double nuevoMonto = Double.parseDouble(editMonto.getText().toString());
                String nuevaDesc = editDesc.getText().toString();
                viewModel.actualizarEgreso(egreso.getId(), nuevoMonto, nuevaDesc);
                Toast.makeText(context, "Egreso actualizado", Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        holder.btnEliminar.setOnClickListener(v -> {
            viewModel.eliminarEgreso(egreso.getId());
            Toast.makeText(context, "Egreso eliminado", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listaEgresos.size();
    }

    public static class EgresoViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textMonto, textFecha, textDescripcion;
        ImageButton btnEditar, btnEliminar;

        public EgresoViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textTitulo);
            textMonto = itemView.findViewById(R.id.textMonto);
            textFecha = itemView.findViewById(R.id.textFecha);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
