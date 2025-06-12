package com.example.lab6_20202269;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    //DATEPICKER REUSABLE PARA MÁS DE 1 ACTIVIDAD, SOLUCIÓN BRINDADA POR IA
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (getActivity() instanceof OnDateSelectedListener) {
            ((OnDateSelectedListener) getActivity()).onDateSelected(year, month, day);
        }
    }
}
