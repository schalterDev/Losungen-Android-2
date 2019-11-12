package de.schalter.losungen.dataAccess

import de.schalter.losungen.dataAccess.weekly.WeeklyVerse
import org.hamcrest.Matchers
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class WeeklyVerseTest {
    @Test
    fun timeConvertingFromMonday() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.set(2019, Calendar.JANUARY, 7) // is monday

        val dateNotConverted = calendar.time
        val verse = WeeklyVerse(
                date = dateNotConverted,
                language = Language.DE,
                verseText = "",
                verseBible = ""
        )

        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendar.set(Calendar.DAY_OF_YEAR, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        assertThat(verse.date, Matchers.equalTo(calendar.time))

        verse.date = dateNotConverted
        assertThat(verse.date, Matchers.equalTo(calendar.time))
    }

    @Test
    fun timeConvertingFromSunday() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.set(2019, Calendar.JANUARY, 6) // is sunday

        val dateNotConverted = calendar.time
        val verse = WeeklyVerse(
                date = dateNotConverted,
                language = Language.DE,
                verseText = "",
                verseBible = ""
        )

        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        assertThat(calendar.time, Matchers.equalTo(verse.date))

        verse.date = dateNotConverted
        assertThat(calendar.time, Matchers.equalTo(verse.date))
    }
}