package de.schalter.losungen.components.dialogs.dateChooser

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

class DateChooserDialog(context: Context, calendar: Calendar, listener: ((Date) -> Unit)) {

    private val dateChooseListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val calendarSelected = Calendar.getInstance()
        calendarSelected.set(Calendar.YEAR, year)
        calendarSelected.set(Calendar.MONTH, month)
        calendarSelected.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        listener(calendarSelected.time)
    }

    init {
        DatePickerDialog(context, dateChooseListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}