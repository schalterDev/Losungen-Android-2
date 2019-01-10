package schalter.de.losungen2.screens.monthly

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import schalter.de.losungen2.components.verseCard.VerseCardData
import schalter.de.losungen2.dataAccess.VersesDatabase
import schalter.de.losungen2.dataAccess.monthly.MonthlyVerse
import schalter.de.losungen2.dataAccess.weekly.WeeklyVerse
import schalter.de.losungen2.screens.ARG_VERSE_DATE
import schalter.de.losungen2.screens.VerseListDateFragment
import java.util.*

/**
 * A [Fragment] subclass.
 * Use the [DailyVerseFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MonthlyVerseFragment : VerseListDateFragment() {

    private lateinit var mContext: Context
    private var monthlyVerse: MonthlyVerse? = null
    private var weeklyVerses: List<WeeklyVerse> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        mContext = view!!.context

        this.updateData(listOf())
        date?.let { loadDate(it) }

        return view
    }

    private fun loadDate(date: Date) {
        val dailyVersesDatabase = VersesDatabase.provideVerseDatabase(mContext)
        dailyVersesDatabase.monthlyVerseDao().findMonthlyVerseByDate(date).observe(
                this,
                androidx.lifecycle.Observer<MonthlyVerse> { monthlyVerse: MonthlyVerse? -> updateDataByMonthlyVerse(monthlyVerse) })

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val lastDayOfMonth = calendar.time

        dailyVersesDatabase.weeklyVerseDao().findWeeklyVerseInDateRange(firstDayOfMonth, lastDayOfMonth).observe(
                this,
                androidx.lifecycle.Observer<List<WeeklyVerse>> { weeklyVerses: List<WeeklyVerse> -> updateDataByWeeklyVerses(weeklyVerses) }
        )
    }

    private fun updateDataByMonthlyVerse(monthlyVerse: MonthlyVerse?) {
        this.monthlyVerse = monthlyVerse
        this.updateDataByVerses()
    }

    private fun updateDataByWeeklyVerses(weeklyVerses: List<WeeklyVerse>) {
        this.weeklyVerses = weeklyVerses
        this.updateDataByVerses()
    }

    private fun updateDataByVerses() {
        val verses: MutableList<VerseCardData> = mutableListOf()
        if (monthlyVerse != null) {
            verses.add(VerseCardData.fromMonthlyVerse(mContext, monthlyVerse!!))
        }
        verses.addAll(VerseCardData.fromWeeklyVerses(weeklyVerses))

        this.updateData(verses)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param verseDate date of the vers to show
         * @return A new instance of fragment DailyVerseFragment.
         */
        @JvmStatic
        fun newInstance(verseDate: Date) =
                MonthlyVerseFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_VERSE_DATE, verseDate.time)
                    }
                }
    }
}
