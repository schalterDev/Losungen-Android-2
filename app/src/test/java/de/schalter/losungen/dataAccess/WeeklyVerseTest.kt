package de.schalter.losungen.dataAccess

import de.schalter.losungen.dataAccess.weekly.WeeklyVerse
import org.hamcrest.Matchers
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class WeeklyVerseTest {
    @Test
    fun timeConverting() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)

        val dateNotConverted = calendar.time
        val verse = WeeklyVerse(
                date = dateNotConverted,
                language = Language.DE,
                verseText = "",
                verseBible = ""
        )

        val calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendarUTC.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        calendarUTC.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR))
        calendarUTC.firstDayOfWeek = Calendar.SUNDAY
        calendarUTC.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendarUTC.set(Calendar.HOUR_OF_DAY, 12)
        calendarUTC.set(Calendar.MINUTE, 0)
        calendarUTC.set(Calendar.SECOND, 0)
        calendarUTC.set(Calendar.MILLISECOND, 0)

        assertThat(verse.date, Matchers.equalTo(calendarUTC.time))

        verse.date = dateNotConverted
        assertThat(verse.date, Matchers.equalTo(calendarUTC.time))
    }
}