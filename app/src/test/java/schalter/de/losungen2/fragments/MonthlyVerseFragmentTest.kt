package schalter.de.losungen2.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkObject
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import schalter.de.losungen2.R
import schalter.de.losungen2.components.emptyState.EmptyStateView
import schalter.de.losungen2.components.verseCard.VerseCardData
import schalter.de.losungen2.components.verseCard.VerseCardGridAdapter
import schalter.de.losungen2.dataAccess.*
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

        mockDatabase()

        date = Calendar.getInstance().time
        val fragmentArgs = Bundle().apply {
            putLong(ARG_DATE, Calendar.getInstance().time.time)
        }

        fragmentScenario = launchFragmentInContainer<MonthlyVerseFragment>(fragmentArgs)
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
                "",
                "",
                ""
        )
        val expectedDataWeek = VerseCardData(
                "TODO generate title from date",
                weeklyVerse.verseText,
                weeklyVerse.verseBible,
                "",
                "",
                ""
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