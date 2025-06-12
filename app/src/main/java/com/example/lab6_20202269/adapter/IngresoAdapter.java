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
import com.example.lab6_20202269.model.Ingreso;
import com.example.lab6_20202269.viewmodel.IngresoViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IngresoAdapter extends RecyclerView.Adapter<IngresoAdapter.IngresoViewHolder> {

    private List<Ingreso> listaIngresos = new ArrayList<>();
    private Context context;
    private IngresoViewModel viewModel;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setViewModel(IngresoViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setListaIngresos(List<Ingreso> lista) {
        this.listaIngresos = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngresoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_ingreso, parent, false);
        return new IngresoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull IngresoViewHolder holder, int position) {
        Ingreso ingreso = listaIngresos.get(position);
        holder.textTitulo.setText(ingreso.getTitulo());
        holder.textMonto.setText("S/ " + ingreso.getMonto());

        // Convertir Timestamp a String
        if (ingreso.getFecha() != null) {
            Date fechaDate = ingreso.getFecha().toDate();
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String fechaFormateada = formato.format(fechaDate);
            holder.textFecha.setText(fechaFormateada);
        } else {
            holder.textFecha.setText("Sin fecha");
        }

        if (ingreso.getDescripcion() != null && !ingreso.getDescripcion().isEmpty()) {
            holder.textDescripcion.setText(ingreso.getDescripcion());
            holder.textDescripcion.setVisibility(View.VISIBLE);
        } else {
            holder.textDescripcion.setVisibility(View.GONE);
        }

        // para editar y eliminar
        holder.btnEditar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_editar_ingreso, null);
            builder.setView(view);

            EditText editMonto = view.findViewById(R.id.editTextNuevoMonto);
            EditText editDesc = view.findViewById(R.id.editTextNuevaDescripcion);
            EditText editTitulo = view.findViewById(R.id.editTextTitulo);
            EditText editFecha = view.findViewById(R.id.editTextFecha);

            editMonto.setText(String.valueOf(ingreso.getMonto()));
            editDesc.setText(ingreso.getDescripcion());
            editTitulo.setText(ingreso.getTitulo());

            if (ingreso.getFecha() != null) {
                Date fechaDate = ingreso.getFecha().toDate();
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                editFecha.setText(formato.format(fechaDate));
            }

            builder.setPositiveButton("Actualizar", (dialog, which) -> {
                double nuevoMonto = Double.parseDouble(editMonto.getText().toString());
                String nuevaDesc = editDesc.getText().toString();
                viewModel.actualizarIngreso(ingreso.getId(), nuevoMonto, nuevaDesc);
                Toast.makeText(context, "Ingreso actualizado", Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        holder.btnEliminar.setOnClickListener(v -> {
            viewModel.eliminarIngreso(ingreso.getId());
            Toast.makeText(context, "Ingreso eliminado", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public int getItemCount() {
        return listaIngresos.size();
    }

    public class IngresoViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textMonto, textFecha, textDescripcion;
        ImageButton btnEditar, btnEliminar;

        public IngresoViewHolder(@NonNull View itemView) {
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
