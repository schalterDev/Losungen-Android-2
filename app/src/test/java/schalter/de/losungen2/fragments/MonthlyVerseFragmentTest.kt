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
import schalter.de.losungen2.dataAccess.Language
import schalter.de.losungen2.dataAccess.MonthlyVerse
import schalter.de.losungen2.dataAccess.MonthlyVersesDao
import schalter.de.losungen2.dataAccess.VersesDatabase
import java.util.*

@RunWith(RobolectricTestRunner::class)
class MonthlyVerseFragmentTest {

    private lateinit var fragmentScenario: FragmentScenario<MonthlyVerseFragment>
    private lateinit var date: Date
    private lateinit var context: Context
    private lateinit var database: VersesDatabase

    // Mock data
    private var monthlyVerseLiveData = MutableLiveData<MonthlyVerse>()
    private val monthlyVerse = MonthlyVerse(
            date = Date(),
            verseText = "oh yeah",
            verseBible = "oh yeah2",
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
        every { database.monthlyVerseDao() } returns monthlyVersesDao
        every { monthlyVersesDao.findMonthlyVerseByDate(any()) } returns monthlyVerseLiveData

        mockkObject(VersesDatabase)
        every { VersesDatabase.provideVerseDatabase(any()) } returns database
    }

    @Test
    fun shouldShowData() {
        monthlyVerseLiveData.postValue(monthlyVerse)

        var verseListView: RecyclerView? = null
        var emptyStateView: EmptyStateView? = null

        fragmentScenario.onFragment {
            verseListView = it.view?.findViewById(R.id.versesList)
            emptyStateView = it.view?.findViewById(R.id.emptyState)
        }

        assertThat(emptyStateView!!.visibility, equalTo(View.GONE))
        assertThat(verseListView!!.visibility, equalTo(View.VISIBLE))

        assertThat(verseListView!!.adapter!!.itemCount, equalTo(1))

        val monthlyVerseViewHolder: VerseCardGridAdapter.VerseCardViewHolder = verseListView!!.findViewHolderForAdapterPosition(0) as VerseCardGridAdapter.VerseCardViewHolder

        val expectedDataMonth = VerseCardData(
                context.getString(R.string.monthly_verse_title),
                monthlyVerse.verseText,
                monthlyVerse.verseBible,
                "",
                "",
                ""
        )

        assertThat(monthlyVerseViewHolder.getData(), equalTo(expectedDataMonth))
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