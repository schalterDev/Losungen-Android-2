package de.schalter.losungen.dataAccess

import de.schalter.losungen.dataAccess.monthly.MonthlyVerse
import org.hamcrest.Matchers
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class MonthlyVerseTest {
    @Test
    fun timeConverting() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 5)

        val dateNotConverted = calendar.time
        val verse = MonthlyVerse(
                date = dateNotConverted,
                language = Language.DE,
                verseText = "",
                verseBible = ""
        )

        val calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendarUTC.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        calendarUTC.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR))
        calendarUTC.set(Calendar.DAY_OF_MONTH, 1)
        calendarUTC.set(Calendar.HOUR_OF_DAY, 12)
        calendarUTC.set(Calendar.MINUTE, 0)
        calendarUTC.set(Calendar.SECOND, 0)
        calendarUTC.set(Calendar.MILLISECOND, 0)

        assertThat(verse.date, Matchers.equalTo(calendarUTC.time))

        verse.date = dateNotConverted
        assertThat(verse.date, Matchers.equalTo(calendarUTC.time))
    }
}