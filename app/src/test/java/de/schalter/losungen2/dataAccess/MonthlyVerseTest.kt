package de.schalter.losungen2.dataAccess

import org.hamcrest.Matchers
import org.junit.Assert.assertThat
import org.junit.Test
import de.schalter.losungen2.dataAccess.monthly.MonthlyVerse
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

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        assertThat(verse.date, Matchers.equalTo(calendar.time))

        verse.date = dateNotConverted
        assertThat(verse.date, Matchers.equalTo(calendar.time))
    }
}