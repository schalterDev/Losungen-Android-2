package schalter.de.losungen2.xmlProcessing

import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isEmptyOrNullString
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import schalter.de.losungen2.dataAccess.Language
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.monthly.MonthlyVerse
import schalter.de.losungen2.dataAccess.weekly.WeeklyVerse
import java.io.InputStream
import java.text.ParseException
import java.util.*

const val validWeeklyPath = "valid_weekly.xml"
const val validMonthlyPath = "valid_monthly.xml"
const val validXmlPath = "valid_losungen.xml"
const val invalidDatePath = "invalid_date_losungen.xml"
const val invalidDatasetPath = "invalid_dataset_losungen.xml"

@RunWith(RobolectricTestRunner::class)
class LosungenXmlParserTest {

    @Test
    fun parseDailyVerseXml() {
        val fileInputStream: InputStream? = this.javaClass.classLoader?.getResourceAsStream(validXmlPath)
        val xmlParser = LosungenXmlParser(Language.DE)

        if (fileInputStream == null) {
            assert(false)
        } else {
            val dailyVerses = xmlParser.parseVerseXml(fileInputStream)

            assertThat(dailyVerses.size, equalTo(3))

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, 2019)
            calendar.set(Calendar.DAY_OF_YEAR, 1)
            calendar.time = DailyVerse.getDateForDay(calendar.time)

            for (i in 1..3) {
                val dailyVerse = dailyVerses[i - 1].toDailyVerse()
                assertThat(dailyVerse.date, equalTo(calendar.time))
                assertThat(dailyVerse.notes, isEmptyOrNullString())
                assertThat(dailyVerse.language, equalTo(Language.DE))
                assertThat(dailyVerse.newTestamentVerseBible, equalTo("newBible$i"))
                assertThat(dailyVerse.newTestamentVerseText, equalTo("newText$i"))
                assertThat(dailyVerse.oldTestamentVerseBible, equalTo("oldBible$i"))
                assertThat(dailyVerse.oldTestamentVerseText, equalTo("oldText$i"))
                assertThat(dailyVerse.isFavourite, equalTo(false))

                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
    }

    @Test
    fun parseWeeklyVerseXml() {
        val fileInputStream: InputStream? = this.javaClass.classLoader?.getResourceAsStream(validWeeklyPath)
        val xmlParser = LosungenXmlParser(Language.DE)

        if (fileInputStream == null) {
            assert(false)
        } else {
            val weeklyVerses = xmlParser.parseVerseXml(fileInputStream)

            assertThat(weeklyVerses.size, equalTo(3))

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, 2019)
            calendar.set(Calendar.DAY_OF_YEAR, 1)
            calendar.time = WeeklyVerse.getDateForWeek(calendar.time)

            for (i in 1..3) {
                val dailyVerse = weeklyVerses[i - 1].toWeeklyVerse()
                assertThat(dailyVerse.date, equalTo(calendar.time))
                assertThat(dailyVerse.notes, isEmptyOrNullString())
                assertThat(dailyVerse.language, equalTo(Language.DE))
                assertThat(dailyVerse.verseBible, equalTo("bible$i"))
                assertThat(dailyVerse.verseText, equalTo("text$i"))
                assertThat(dailyVerse.isFavourite, equalTo(false))

                calendar.add(Calendar.WEEK_OF_YEAR, 1)
            }
        }
    }

    @Test
    fun parseMonthlyVerseXml() {
        val fileInputStream: InputStream? = this.javaClass.classLoader?.getResourceAsStream(validMonthlyPath)
        val xmlParser = LosungenXmlParser(Language.DE)

        if (fileInputStream == null) {
            assert(false)
        } else {
            val monthlyVerses = xmlParser.parseVerseXml(fileInputStream)

            assertThat(monthlyVerses.size, equalTo(3))

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, 2019)
            calendar.set(Calendar.DAY_OF_YEAR, 1)
            calendar.time = MonthlyVerse.getDateForMonth(calendar.time)

            for (i in 1..3) {
                val monthlyVerse = monthlyVerses[i - 1].toMonthlyVerse()
                assertThat(monthlyVerse.date, equalTo(calendar.time))
                assertThat(monthlyVerse.notes, isEmptyOrNullString())
                assertThat(monthlyVerse.language, equalTo(Language.DE))
                assertThat(monthlyVerse.verseBible, equalTo("bible$i"))
                assertThat(monthlyVerse.verseText, equalTo("text$i"))
                assertThat(monthlyVerse.isFavourite, equalTo(false))

                calendar.add(Calendar.MONTH, 1)
            }
        }
    }

    @Test(expected = ParseException::class)
    fun invalidDate() {
        val fileInputStream: InputStream? = this.javaClass.classLoader?.getResourceAsStream(invalidDatePath)
        val xmlParser = LosungenXmlParser(Language.DE)

        if (fileInputStream == null) {
            assert(false)
        } else {
            xmlParser.parseVerseXml(fileInputStream)
        }
    }

    @Test(expected = ParseException::class)
    fun invalidDataSet() {
        val fileInputStream: InputStream? = this.javaClass.classLoader?.getResourceAsStream(invalidDatasetPath)
        val xmlParser = LosungenXmlParser(Language.DE)

        if (fileInputStream == null) {
            assert(false)
        } else {
            xmlParser.parseVerseXml(fileInputStream)
        }
    }
}