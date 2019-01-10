package schalter.de.losungen2.xmlProcessing

import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isEmptyOrNullString
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import schalter.de.losungen2.dataAccess.Language
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import java.io.InputStream
import java.text.ParseException
import java.util.*

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
            val dailyVerses = xmlParser.parseDailyVerseXml(fileInputStream)

            assertThat(dailyVerses.size, equalTo(3))

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, 2019)
            calendar.set(Calendar.DAY_OF_YEAR, 1)
            calendar.time = DailyVerse.getDateForDay(calendar.time)

            for (i in 1..3) {
                val dailyVerse = dailyVerses[i - 1]
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

    @Test(expected = ParseException::class)
    fun invalidDate() {
        val fileInputStream: InputStream? = this.javaClass.classLoader?.getResourceAsStream(invalidDatePath)
        val xmlParser = LosungenXmlParser(Language.DE)

        if (fileInputStream == null) {
            assert(false)
        } else {
            xmlParser.parseDailyVerseXml(fileInputStream)
        }
    }

    @Test(expected = ParseException::class)
    fun invalidDataSet() {
        val fileInputStream: InputStream? = this.javaClass.classLoader?.getResourceAsStream(invalidDatasetPath)
        val xmlParser = LosungenXmlParser(Language.DE)

        if (fileInputStream == null) {
            assert(false)
        } else {
            xmlParser.parseDailyVerseXml(fileInputStream)
        }
    }
}