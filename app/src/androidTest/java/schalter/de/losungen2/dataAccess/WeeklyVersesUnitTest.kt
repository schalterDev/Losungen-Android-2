package schalter.de.losungen2.dataAccess

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import schalter.de.losungen2.dataAccess.weekly.WeeklyVerse
import schalter.de.losungen2.dataAccess.weekly.WeeklyVersesDao
import schalter.de.losungen2.utils.DatabaseUtils
import schalter.de.losungen2.utils.DateUtils
import schalter.de.losungen2.utils.blockingObserve
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class WeeklyVersesUnitTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var weeklyVerseDao: WeeklyVersesDao
    private lateinit var db: VersesDatabase

    private var date = Date(1550245926000) // 15.02.2019, 16:52:06, Friday
    private var weeklyVerse = WeeklyVerse(
            date = date,
            verseBible = "verse bible",
            verseText = "verse text",
            language = Language.DE
    )

    @Before
    fun createDb() {
        db = DatabaseUtils.getInMemoryDatabase()
        weeklyVerseDao = db.weeklyVerseDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeVerseAndRead() {
        weeklyVerseDao.insertWeeklyVerse(weeklyVerse)
        val verseFromDatabase: WeeklyVerse = weeklyVerseDao.findWeeklyVerseByDate(weeklyVerse.date).blockingObserve()!!
        assertThat(verseFromDatabase.verseBible, equalTo(weeklyVerse.verseBible))
        assertThat(verseFromDatabase.verseText, equalTo(weeklyVerse.verseText))
        assertThat(verseFromDatabase.date, equalTo(weeklyVerse.date))
    }

    @Test
    @Throws(Exception::class)
    fun writeMultipleVersesAndRead() {
        weeklyVerseDao.insertWeeklyVerse(weeklyVerse)

        val weeklyVerseNextWeek = weeklyVerse.copy()
        weeklyVerseNextWeek.verseText = "2"
        weeklyVerseNextWeek.date = DateUtils.addDaysToDate(weeklyVerse.date, 7)
        weeklyVerseDao.insertWeeklyVerse(weeklyVerseNextWeek)

        var verseFromDatabase: WeeklyVerse = weeklyVerseDao.findWeeklyVerseByDate(weeklyVerse.date).blockingObserve()!!
        assertThat(verseFromDatabase.verseText, equalTo(weeklyVerse.verseText))
        assertThat(verseFromDatabase.verseBible, equalTo(weeklyVerse.verseBible))
        assertThat(verseFromDatabase.date, equalTo(weeklyVerse.date))
        assertThat(verseFromDatabase.isFavourite, equalTo(weeklyVerse.isFavourite))
        assertThat(verseFromDatabase.language, equalTo(weeklyVerse.language))
        assertThat(verseFromDatabase.notes, equalTo(weeklyVerse.notes))

        verseFromDatabase = weeklyVerseDao.findWeeklyVerseByDate(weeklyVerseNextWeek.date).blockingObserve()!!
        assertThat(verseFromDatabase.verseText, equalTo(weeklyVerseNextWeek.verseText))
        assertThat(verseFromDatabase.date, equalTo(weeklyVerseNextWeek.date))
    }

    @Test
    fun readRange() {
        weeklyVerseDao.insertWeeklyVerse(weeklyVerse)

        val weeklyVerseNextWeek = weeklyVerse.copy()
        weeklyVerseNextWeek.verseText = "2"
        weeklyVerseNextWeek.date = DateUtils.addDaysToDate(weeklyVerse.date, 7)
        weeklyVerseDao.insertWeeklyVerse(weeklyVerseNextWeek)
        // should not show
        val weeklyVerseInTwoWeeks = weeklyVerse.copy()
        weeklyVerseInTwoWeeks.date = DateUtils.addDaysToDate(weeklyVerse.date, 14)
        weeklyVerseDao.insertWeeklyVerse(weeklyVerseInTwoWeeks)

        // date should be automatic convert to right date
        val calendar = Calendar.getInstance()
        calendar.time = weeklyVerse.date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        val startDate = calendar.time
        calendar.time = weeklyVerseNextWeek.date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val endDate = calendar.time

        val versesFromDatabase: List<WeeklyVerse> = weeklyVerseDao.findWeeklyVerseInDateRange(startDate, endDate).blockingObserve()!!
        assertThat(versesFromDatabase.size, equalTo(2))
        assertThat(versesFromDatabase[0].verseText, equalTo(weeklyVerse.verseText))
        assertThat(versesFromDatabase[0].verseBible, equalTo(weeklyVerse.verseBible))
        assertThat(versesFromDatabase[0].date, equalTo(weeklyVerse.date))
        assertThat(versesFromDatabase[0].isFavourite, equalTo(weeklyVerse.isFavourite))
        assertThat(versesFromDatabase[0].language, equalTo(weeklyVerse.language))
        assertThat(versesFromDatabase[0].notes, equalTo(weeklyVerse.notes))

        assertThat(versesFromDatabase[1].verseText, equalTo(weeklyVerseNextWeek.verseText))
        assertThat(versesFromDatabase[1].date, equalTo(weeklyVerseNextWeek.date))
    }

    @Test
    @Throws(Exception::class)
    fun updateLanguage() {
        weeklyVerse.notes = "notes"
        weeklyVerseDao.insertWeeklyVerse(weeklyVerse)

        val weeklyVerseEnglish = weeklyVerse.copy()
        weeklyVerseEnglish.verseText = "english verse"
        weeklyVerseEnglish.verseBible = "english bible"
        weeklyVerseEnglish.language = Language.EN
        weeklyVerseEnglish.notes = "notes english" // should not update

        weeklyVerseDao.updateLanguage(weeklyVerseEnglish)

        val verseFromDatabase: WeeklyVerse = weeklyVerseDao.findWeeklyVerseByDate(weeklyVerseEnglish.date).blockingObserve()!!
        assertThat(verseFromDatabase.verseBible, equalTo(weeklyVerseEnglish.verseBible))
        assertThat(verseFromDatabase.verseText, equalTo(weeklyVerseEnglish.verseText))
        assertThat(verseFromDatabase.language, equalTo(weeklyVerseEnglish.language))
        assertThat(verseFromDatabase.notes, equalTo(weeklyVerse.notes))
    }

    @Test
    fun dateConverting() {
        weeklyVerseDao.insertWeeklyVerse(weeklyVerse)

        val calendar = Calendar.getInstance()
        calendar.time = weeklyVerse.date
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
        } else {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }

        // find verse
        var verseFromDatabase: WeeklyVerse = weeklyVerseDao.findWeeklyVerseByDate(calendar.time).blockingObserve()!!
        assertThat(verseFromDatabase.date, equalTo(weeklyVerse.date))

        // update language
        val weeklyVerseOtherDate = weeklyVerse.copy()
        weeklyVerseOtherDate.date = calendar.time
        weeklyVerseOtherDate.verseBible = "other date"
        weeklyVerseDao.updateLanguage(weeklyVerseOtherDate)
        verseFromDatabase = weeklyVerseDao.findWeeklyVerseByDate(weeklyVerse.date).blockingObserve()!!
        assertThat(verseFromDatabase.date, equalTo(weeklyVerse.date))
        assertThat(verseFromDatabase.verseBible, equalTo(weeklyVerseOtherDate.verseBible))
    }
}