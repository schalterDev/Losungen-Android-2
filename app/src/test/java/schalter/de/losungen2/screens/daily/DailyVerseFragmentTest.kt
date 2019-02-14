package schalter.de.losungen2.screens.daily

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
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
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.screens.ARG_DATE
import schalter.de.losungen2.utils.DatabaseUtils.mockDailyVerseDaoFindDailyVerseByDate
import java.util.*

@RunWith(RobolectricTestRunner::class)
class DailyVerseFragmentTest {

    private lateinit var fragmentScenario: FragmentScenario<DailyVerseFragment>
    private lateinit var date: Date
    private lateinit var context: Context

    // Mock data
    private lateinit var dailyVerseLiveData: MutableLiveData<DailyVerse>
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

        dailyVerseLiveData = mockDailyVerseDaoFindDailyVerseByDate()

        date = Calendar.getInstance().time
        val fragmentArgs = Bundle().apply {
            putLong(ARG_DATE, Calendar.getInstance().time.time)
        }

        fragmentScenario = launchFragmentInContainer<DailyVerseFragment>(fragmentArgs)
    }

    @Test
    fun shouldShowData() {
        dailyVerseLiveData.postValue(dailyVerse)

        var verseListView: RecyclerView? = null
        var emptyStateView: EmptyStateView? = null

        fragmentScenario.onFragment {
            verseListView = it.view?.findViewById(R.id.versesList)
            emptyStateView = it.view?.findViewById(R.id.emptyState)
        }

        assertThat(emptyStateView!!.visibility, equalTo(View.GONE))
        assertThat(verseListView!!.visibility, equalTo(View.VISIBLE))

        assertThat(verseListView!!.adapter!!.itemCount, equalTo(2))

        val oldTestamentViewHolder: VerseCardGridAdapter.VerseCardViewHolder = verseListView!!.findViewHolderForAdapterPosition(0) as VerseCardGridAdapter.VerseCardViewHolder
        val newTestamentViewHolder: VerseCardGridAdapter.VerseCardViewHolder = verseListView!!.findViewHolderForAdapterPosition(1) as VerseCardGridAdapter.VerseCardViewHolder

        val expectedDataOldTestament = VerseCardData(
                context.getString(R.string.old_testament_card_title),
                dailyVerse.oldTestamentVerseText,
                dailyVerse.oldTestamentVerseBible,
                "",
                "",
                ""
        )
        val expectedDataNewTestament = VerseCardData(
                context.getString(R.string.new_testament_card_title),
                dailyVerse.newTestamentVerseText,
                dailyVerse.newTestamentVerseBible,
                "",
                "",
                ""
        )

        assertThat(oldTestamentViewHolder.getData(), equalTo(expectedDataOldTestament))
        assertThat(newTestamentViewHolder.getData(), equalTo(expectedDataNewTestament))
    }

    @Test
    fun shouldShowErrorMessage() {
        dailyVerseLiveData.postValue(null)

        var emptyStateView: EmptyStateView? = null

        fragmentScenario.onFragment {
            emptyStateView = it.view?.findViewById(R.id.emptyState)
        }

        assertThat(emptyStateView!!.visibility, equalTo(View.VISIBLE))
    }
}