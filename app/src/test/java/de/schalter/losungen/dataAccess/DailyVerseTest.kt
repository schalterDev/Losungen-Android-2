package de.schalter.losungen.dataAccess

import de.schalter.losungen.dataAccess.daily.DailyVerse
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class DailyVerseTest {
    @Test
    fun timeConverting() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 2)

        val dateAt2 = calendar.time
        val verse = DailyVerse(
                date = dateAt2,
                language = Language.DE,
                newTestamentVerseBible = "",
                newTestamentVerseText = "",
                oldTestamentVerseBible = "",
                oldTestamentVerseText = ""
        )

        val calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendarUTC.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        calendarUTC.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR))
        calendarUTC.set(Calendar.HOUR_OF_DAY, 12)
        calendarUTC.set(Calendar.MINUTE, 0)
        calendarUTC.set(Calendar.SECOND, 0)
        calendarUTC.set(Calendar.MILLISECOND, 0)

        assertThat(verse.date, equalTo(calendarUTC.time))

        verse.date = dateAt2
        assertThat(verse.date, equalTo(calendarUTC.time))
    }
}