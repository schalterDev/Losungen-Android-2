package de.schalter.losungen.screens

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import de.schalter.losungen.R
import de.schalter.losungen.components.emptyState.EmptyStateView
import de.schalter.losungen.components.verseCard.VerseCardData
import de.schalter.losungen.components.verseCard.VerseCardGridAdapter
import de.schalter.losungen.components.verseCard.datePattern
import de.schalter.losungen.dataAccess.Language
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.monthly.MonthlyVerse
import de.schalter.losungen.dataAccess.monthly.MonthlyVersesDao
import de.schalter.losungen.dataAccess.weekly.WeeklyVerse
import de.schalter.losungen.dataAccess.weekly.WeeklyVersesDao
import de.schalter.losungen.screens.monthly.MonthlyVerseFragment
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkObject
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.text.SimpleDateFormat
import java.util.*

@RunWith(RobolectricTestRunner::class)
class MonthlyVerseFragmentTest {

    private lateinit var fragmentScenario: FragmentScenario<MonthlyVerseFragment>
    private lateinit var date: Date
    private lateinit var context: Context
    private lateinit var database: VersesDatabase

    // Mock data
    private var monthlyVerseLiveData = MutableLiveData<MonthlyVerse>()
    private var weeklyVersesLiveData = MutableLiveData<List<WeeklyVerse>>()
    private val monthlyVerse = MonthlyVerse(
            date = Date(),
            verseText = "monthly text",
            verseBible = "monthly bible",
            language = Language.DE
    )
    private val weeklyVerse = WeeklyVerse(
            date = Date(),
            verseText = "weekly text",
            verseBible = "weekly bible",
            language = Language.DE
    )

    @Before
    fun initFragment() {
        context = getApplicationContext()
        context.setTheme(R.style.Theme_Blue)

        mockDatabase()

        date = Calendar.getInstance().time
        val fragmentArgs = Bundle().apply {
            putLong(ARG_DATE, Calendar.getInstance().time.time)
        }

        fragmentScenario = launchFragmentInContainer(fragmentArgs)
    }

    private fun mockDatabase() {
        database = mockkClass(VersesDatabase::class)
        val monthlyVersesDao = mockkClass(MonthlyVersesDao::class)
        val weeklyVersesDao = mockkClass(WeeklyVersesDao::class)

        every { database.monthlyVerseDao() } returns monthlyVersesDao
        every { monthlyVersesDao.findMonthlyVerseByDate(any()) } returns monthlyVerseLiveData

        every { database.weeklyVerseDao() } returns weeklyVersesDao
        every { weeklyVersesDao.findWeeklyVerseInDateRange(any(), any()) } returns weeklyVersesLiveData

        mockkObject(VersesDatabase)
        every { VersesDatabase.provideVerseDatabase(any()) } returns database
    }

    @Test
    fun shouldShowData() {
        monthlyVerseLiveData.postValue(monthlyVerse)
        weeklyVersesLiveData.postValue(listOf(weeklyVerse, weeklyVerse, weeklyVerse, weeklyVerse))

        var verseListView: RecyclerView? = null
        var emptyStateView: EmptyStateView? = null

        fragmentScenario.onFragment {
            verseListView = it.view?.findViewById(R.id.versesList)
            emptyStateView = it.view?.findViewById(R.id.emptyState)
        }

        assertThat(emptyStateView!!.visibility, equalTo(View.GONE))
        assertThat(verseListView!!.visibility, equalTo(View.VISIBLE))

        assertThat(verseListView!!.adapter!!.itemCount, equalTo(5))

        val monthlyVerseViewHolder: VerseCardGridAdapter.VerseCardViewHolder = verseListView!!.findViewHolderForAdapterPosition(0) as VerseCardGridAdapter.VerseCardViewHolder
        val firstWeeklyVerseViewHolder: VerseCardGridAdapter.VerseCardViewHolder = verseListView!!.findViewHolderForAdapterPosition(1) as VerseCardGridAdapter.VerseCardViewHolder

        val expectedDataMonth = VerseCardData(
                context.getString(R.string.monthly_verse_title),
                monthlyVerse.verseText,
                monthlyVerse.verseBible,
                type = VerseCardData.Type.MONTHLY,
                date = monthlyVerse.date
        )

        val simpleDateFormat = SimpleDateFormat(datePattern)

        val expectedDataWeek = VerseCardData(
                simpleDateFormat.format(weeklyVerse.date),
                weeklyVerse.verseText,
                weeklyVerse.verseBible,
                type = VerseCardData.Type.WEEKLY,
                date = weeklyVerse.date
        )

        assertThat(monthlyVerseViewHolder.getData(), equalTo(expectedDataMonth))
        assertThat(firstWeeklyVerseViewHolder.getData(), equalTo(expectedDataWeek))
    }

    @Test
    fun shouldShowErrorMessage() {
        monthlyVerseLiveData.postValue(null)

        var emptyStateView: EmptyStateView? = null

        fragmentScenario.onFragment {
            emptyStateView = it.view?.findViewById(R.id.emptyState)
        }

        assertThat(emptyStateView!!.visibility, equalTo(View.VISIBLE))
    }
}