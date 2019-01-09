package schalter.de.losungen2.dataAccess

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isEmptyOrNullString
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import schalter.de.losungen2.TestUtils
import schalter.de.losungen2.dataAccess.daos.DailyVersesDao
import schalter.de.losungen2.dataAccess.entities.DailyVerse
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)

    observeForever { t ->
        value = t
        latch.countDown()
    }

    latch.await(2, TimeUnit.SECONDS)
    return value
}

@RunWith(AndroidJUnit4::class)
class DailyVersesUnitTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dailyVersesDao: DailyVersesDao
    private lateinit var db: VersesDatabase

    private var date = Calendar.getInstance().time
    private var dailyVerse = DailyVerse(
            date = date,
            newTestamentVerseBible = "new testament",
            oldTestamentVerseBible = "old testament",
            newTestamentVerseText = "new testament text",
            oldTestamentVerseText = "old testament text",
            language = Language.DE
    )

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, VersesDatabase::class.java).build()
        dailyVersesDao = db.dailyVerseDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeDailyVerseAndRead() {
        dailyVersesDao.insertDailyVerse(dailyVerse)
        val verseFromDatabase: DailyVerse = dailyVersesDao.findDailyVerseByDate(date).blockingObserve()!!
        assertThat(verseFromDatabase.newTestamentVerseBible, equalTo(dailyVerse.newTestamentVerseBible))
        assertThat(verseFromDatabase.newTestamentVerseText, equalTo(dailyVerse.newTestamentVerseText))
        assertThat(verseFromDatabase.oldTestamentVerseBible, equalTo(dailyVerse.oldTestamentVerseBible))
        assertThat(verseFromDatabase.oldTestamentVerseText, equalTo(dailyVerse.oldTestamentVerseText))
        assertThat(verseFromDatabase.date, equalTo(dailyVerse.date))
        assertThat(verseFromDatabase.language, equalTo(dailyVerse.language))
    }

    @Test
    @Throws(Exception::class)
    fun writeMultipleDailyVerses() {
        dailyVersesDao.insertDailyVerse(dailyVerse)

        val dailyVerseNextDay = dailyVerse.copy()
        dailyVerseNextDay.date = TestUtils.addDaysToDate(dailyVerse.date, 1)
        dailyVerseNextDay.newTestamentVerseBible = "2"
        dailyVersesDao.insertDailyVerse(dailyVerseNextDay)

        var verseFromDatabase: DailyVerse = dailyVersesDao.findDailyVerseByDate(date).blockingObserve()!!
        assertThat(verseFromDatabase.newTestamentVerseBible, equalTo(dailyVerse.newTestamentVerseBible))
        assertThat(verseFromDatabase.newTestamentVerseText, equalTo(dailyVerse.newTestamentVerseText))
        assertThat(verseFromDatabase.oldTestamentVerseBible, equalTo(dailyVerse.oldTestamentVerseBible))
        assertThat(verseFromDatabase.oldTestamentVerseText, equalTo(dailyVerse.oldTestamentVerseText))
        assertThat(verseFromDatabase.date, equalTo(dailyVerse.date))
        assertThat(verseFromDatabase.language, equalTo(dailyVerse.language))

        verseFromDatabase = dailyVersesDao.findDailyVerseByDate(dailyVerseNextDay.date).blockingObserve()!!
        assertThat(verseFromDatabase.date, equalTo(dailyVerseNextDay.date))
        assertThat(verseFromDatabase.newTestamentVerseBible, equalTo(dailyVerseNextDay.newTestamentVerseBible))
    }

    @Test
    @Throws(Exception::class)
    fun updateNotes() {
        val notes = "Note test"

        dailyVersesDao.insertDailyVerse(dailyVerse)

        var verseFromDatabase: DailyVerse = dailyVersesDao.findDailyVerseByDate(dailyVerse.date).blockingObserve()!!
        assertThat(verseFromDatabase.notes, isEmptyOrNullString())

        dailyVersesDao.updateNotes(dailyVerse.date, notes)
        verseFromDatabase = dailyVersesDao.findDailyVerseByDate(dailyVerse.date).blockingObserve()!!
        assertThat(verseFromDatabase.notes, equalTo(notes))
    }

    @Test
    @Throws(Exception::class)
    fun updateIsFavourite() {
        dailyVersesDao.insertDailyVerse(dailyVerse)

        var verseFromDatabase: DailyVerse = dailyVersesDao.findDailyVerseByDate(dailyVerse.date).blockingObserve()!!
        assertThat(verseFromDatabase.isFavourite, equalTo(false))

        dailyVersesDao.updateIsFavourite(dailyVerse.date, true)
        verseFromDatabase = dailyVersesDao.findDailyVerseByDate(dailyVerse.date).blockingObserve()!!
        assertThat(verseFromDatabase.isFavourite, equalTo(true))

        dailyVersesDao.updateIsFavourite(dailyVerse.date, false)
        verseFromDatabase = dailyVersesDao.findDailyVerseByDate(dailyVerse.date).blockingObserve()!!
        assertThat(verseFromDatabase.isFavourite, equalTo(false))
    }

    @Test
    @Throws(Exception::class)
    fun updateLanguage() {
        dailyVerse.notes = "notes"
        dailyVersesDao.insertDailyVerse(dailyVerse)

        val dailyVerseEnglish = DailyVerse(
                date = date,
                oldTestamentVerseText = "old english test",
                oldTestamentVerseBible = "old english bible",
                newTestamentVerseText = "new english test",
                newTestamentVerseBible = "new english bible",
                language = Language.EN
        )
        dailyVersesDao.updateLanguage(dailyVerseEnglish)

        val verseFromDatabase: DailyVerse = dailyVersesDao.findDailyVerseByDate(dailyVerseEnglish.date).blockingObserve()!!
        assertThat(verseFromDatabase.newTestamentVerseBible, equalTo(dailyVerseEnglish.newTestamentVerseBible))
        assertThat(verseFromDatabase.newTestamentVerseText, equalTo(dailyVerseEnglish.newTestamentVerseText))
        assertThat(verseFromDatabase.oldTestamentVerseBible, equalTo(dailyVerseEnglish.oldTestamentVerseBible))
        assertThat(verseFromDatabase.oldTestamentVerseText, equalTo(dailyVerseEnglish.oldTestamentVerseText))
        assertThat(verseFromDatabase.date, equalTo(dailyVerse.date))
        assertThat(verseFromDatabase.language, equalTo(dailyVerseEnglish.language))
        assertThat(verseFromDatabase.notes, equalTo(dailyVerse.notes))
    }

    @Test
    fun dateConverting() {
        dailyVersesDao.insertDailyVerse(dailyVerse)

        val calendar = Calendar.getInstance()
        calendar.time = dailyVerse.date
        calendar.set(Calendar.HOUR_OF_DAY, 3)

        // find verse
        var verseFromDatabase: DailyVerse = dailyVersesDao.findDailyVerseByDate(calendar.time).blockingObserve()!!
        assertThat(verseFromDatabase.date, equalTo(dailyVerse.date))

        // update language
        val dailyVerseOtherDate = dailyVerse.copy()
        dailyVerseOtherDate.date = calendar.time
        dailyVerseOtherDate.newTestamentVerseBible = "other date"
        dailyVersesDao.updateLanguage(dailyVerseOtherDate)
        verseFromDatabase = dailyVersesDao.findDailyVerseByDate(dailyVerse.date).blockingObserve()!!
        assertThat(verseFromDatabase.date, equalTo(dailyVerse.date))
        assertThat(verseFromDatabase.newTestamentVerseBible, equalTo(dailyVerseOtherDate.newTestamentVerseBible))

        // update notes
        val otherNotes = "other notes"
        dailyVersesDao.updateNotes(dailyVerseOtherDate.date, otherNotes)
        verseFromDatabase = dailyVersesDao.findDailyVerseByDate(dailyVerse.date).blockingObserve()!!
        assertThat(verseFromDatabase.date, equalTo(dailyVerse.date))
        assertThat(verseFromDatabase.notes, equalTo(otherNotes))
    }
}
