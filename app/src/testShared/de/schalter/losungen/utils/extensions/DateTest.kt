package de.schalter.losungen.utils.extensions

import java.util.*

fun Date.addDays(days: Int = 1): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DAY_OF_MONTH, days)
    return calendar.time
}

fun Date.addMonths(months: Int = 1): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.MONTH, months)
    return calendar.time
}