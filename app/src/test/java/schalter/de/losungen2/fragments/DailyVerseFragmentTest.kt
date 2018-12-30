package schalter.de.losungen2.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkObject
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import schalter.de.losungen2.R
import schalter.de.losungen2.UnitTestUtils
import schalter.de.losungen2.components.views.OneVerseCardView
import schalter.de.losungen2.dataAccess.DailyVerse
import schalter.de.losungen2.dataAccess.DailyVersesDao
import schalter.de.losungen2.dataAccess.Language
import schalter.de.losungen2.dataAccess.VersesDatabase
import java.util.*

@RunWith(RobolectricTestRunner::class)
class DailyVerseFragmentTest {

    private lateinit var fragmentScenario: FragmentScenario<DailyVerseFragment>
    private lateinit var date: Date
    private lateinit var context: Context
    private lateinit var database: VersesDatabase

    // Mock data
    private var dailyVerseLiveData = MutableLiveData<DailyVerse>()
    private val dailyVerse = DailyVerse(
            date = Date(),
            oldTestamentVerseText = "oh yeah",
            oldTestamentVerseBible = "oh yeah2",
            newTestamentVerseText = "oh yeah3",
            newTestamentVerseBible = "oh yeah4",
            language = Language.DE
    )

    @Before
    fun initFragment() {
        context = getApplicationContext()

        mockDatabase()

        date = Calendar.getInstance().time
        val fragmentArgs = Bundle().apply {
            putLong(ARG_DATE, Calendar.getInstance().time.time)
        }

        fragmentScenario = launchFragmentInContainer<DailyVerseFragment>(fragmentArgs)
    }

    private fun mockDatabase() {
        database = mockkClass(VersesDatabase::class)
        val dailyVersesDao = mockkClass(DailyVersesDao::class)
        every { database.dailyVerseDao() } returns dailyVersesDao
        every { dailyVersesDao.findDailyVerseByDate(any()) } returns dailyVerseLiveData

        mockkObject(VersesDatabase)
        every { VersesDatabase.provideVerseDatabase(any()) } returns database
    }

    @Test
    fun shouldCreateFragment() {
        onView(withId(R.id.oldTestamentCard)).check(matches(isDisplayed()))
        onView(withId(R.id.newTestamentCard)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldShowData() {
        dailyVerseLiveData.postValue(dailyVerse)

        var oldTestamentData: UnitTestUtils.OneVerseCardData? = null
        var newTestamentData: UnitTestUtils.OneVerseCardData? = null

        fragmentScenario.onFragment {
            val oldTestamentCard = it.view?.findViewById<OneVerseCardView>(R.id.oldTestamentCard)
            val newTestamentCard = it.view?.findViewById<OneVerseCardView>(R.id.newTestamentCard)
            oldTestamentData = UnitTestUtils.getDataFromOneVerseCard(oldTestamentCard!!)
            newTestamentData = UnitTestUtils.getDataFromOneVerseCard(newTestamentCard!!)
        }

        assertThat(oldTestamentData!!.title, Matchers.equalTo(context.getString(R.string.old_testament_card_title)))
        assertThat(oldTestamentData!!.text, Matchers.equalTo(dailyVerse.oldTestamentVerseText))
        assertThat(oldTestamentData!!.verse, Matchers.equalTo(dailyVerse.oldTestamentVerseBible))

        assertThat(newTestamentData!!.title, Matchers.equalTo(context.getString(R.string.new_testament_card_title)))
        assertThat(newTestamentData!!.text, Matchers.equalTo(dailyVerse.newTestamentVerseText))
        assertThat(newTestamentData!!.verse, Matchers.equalTo(dailyVerse.newTestamentVerseBible))
    }

    @Test
    fun shouldShowErrorMessage() {
        dailyVerseLiveData.postValue(null)

        var oldTestamentData: UnitTestUtils.OneVerseCardData? = null
        var newTestamentData: UnitTestUtils.OneVerseCardData? = null

        fragmentScenario.onFragment {
            val oldTestamentCard = it.view?.findViewById<OneVerseCardView>(R.id.oldTestamentCard)
            val newTestamentCard = it.view?.findViewById<OneVerseCardView>(R.id.newTestamentCard)
            oldTestamentData = UnitTestUtils.getDataFromOneVerseCard(oldTestamentCard!!)
            newTestamentData = UnitTestUtils.getDataFromOneVerseCard(newTestamentCard!!)
        }

        assertThat(oldTestamentData!!.title, Matchers.equalTo(context.getString(R.string.old_testament_card_title)))
        assertThat(oldTestamentData!!.text, Matchers.equalTo(context.getString(R.string.no_verse_found)))
        assertThat(oldTestamentData!!.verse, Matchers.equalTo(context.getString(R.string.no_verse_found)))

        assertThat(newTestamentData!!.title, Matchers.equalTo(context.getString(R.string.new_testament_card_title)))
        assertThat(newTestamentData!!.text, Matchers.equalTo(context.getString(R.string.no_verse_found)))
        assertThat(newTestamentData!!.verse, Matchers.equalTo(context.getString(R.string.no_verse_found)))
    }
}