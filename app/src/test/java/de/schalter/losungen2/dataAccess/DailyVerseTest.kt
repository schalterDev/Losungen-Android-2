package de.schalter.losungen2.dataAccess

import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import de.schalter.losungen2.dataAccess.daily.DailyVerse
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

        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        assertThat(verse.date, equalTo(calendar.time))

        verse.date = dateAt2
        assertThat(verse.date, equalTo(calendar.time))
    }
}