package de.schalter.losungen2.dataAccess

import org.hamcrest.Matchers
import org.junit.Assert.assertThat
import org.junit.Test
import de.schalter.losungen2.dataAccess.weekly.WeeklyVerse
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

        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        assertThat(verse.date, Matchers.equalTo(calendar.time))

        verse.date = dateNotConverted
        assertThat(verse.date, Matchers.equalTo(calendar.time))
    }
}