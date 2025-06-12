package com.example.lab6_20202269;

//PROMPT: cual sería la forma más optima de reutilizar el mismo datePickerFragment para más de 1 activity?

//RESPUESTA:
// La forma más óptima de reutilizar el mismo DatePickerFragment para múltiples actividades es definir
// una interfaz que las actividades implementen.
// De esta manera, el fragmento puede comunicarse con cualquier actividad que implemente la interfaz,
// permitiendo que cada actividad maneje la selección de fecha de manera específica.
public interface OnDateSelectedListener {
    void onDateSelected(int year, int month, int day);
}