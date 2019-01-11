package schalter.de.losungen2.utils

import java.util.*

class DateUtils {
    companion object {
        fun addDaysToDate(date: Date, days: Int = 1): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DAY_OF_MONTH, days)
            return calendar.time
        }

        fun addMonthsToDate(date: Date, months: Int = 1): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.MONTH, months)
            return calendar.time
        }
    }
}