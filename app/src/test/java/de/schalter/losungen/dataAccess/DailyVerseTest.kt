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
        calendar.set(2019, Calendar.JANUARY, 1, Calendar.HOUR_OF_DAY, 0)

        val dateAt2 = calendar.time
        val verse = DailyVerse(
                date = dateAt2,
                language = Language.DE,
                newTestamentVerseBible = "",
                newTestamentVerseText = "",
                oldTestamentVerseBible = "",
                oldTestamentVerseText = ""
        )

        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcCalendar.set(2019, Calendar.JANUARY, 1, 12, 0, 0)
        utcCalendar.set(Calendar.MILLISECOND, 0)

        assertThat(verse.date, equalTo(utcCalendar.time))

        verse.date = dateAt2
        assertThat(verse.date, equalTo(utcCalendar.time))
    }
}