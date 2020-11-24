package org.macho.beforeandafter.shared.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.*

fun showDatePickerDialog(context: Context, defaultDate: Date, onSelect: (Date) -> Unit) {
    val calendar = Calendar.getInstance()
    calendar.time = defaultDate
    DatePickerDialog(context, { view, year, month, dayOfMonth ->
        TimePickerDialog(context, { view, hourOfDay, minute ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(year, month, dayOfMonth, hourOfDay, minute)
            onSelect(newCalendar.time)

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
}