package schalter.de.losungen2.screens.favourite

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import schalter.de.losungen2.R
import schalter.de.losungen2.components.verseCard.VerseCardData
import schalter.de.losungen2.dataAccess.VersesDatabase
import schalter.de.losungen2.screens.VerseListFragment

/**
 * A simple [Fragment] subclass.
 */
class FavouriteVersesOverviewFragment : VerseListFragment() {

    private lateinit var mContext: Context
    private var dailyVerses: List<VerseCardData> = listOf()
    private var weeklyVerses: List<VerseCardData> = listOf()
    private var monthlyVerses: List<VerseCardData> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        mContext = view!!.context

        emptyStateView.hideButton()
        emptyStateView.setIcon(R.drawable.ic_action_favorite_border)
        emptyStateView.setTitle(R.string.markes_verses_will_be_here)

        updateData(listOf())
        loadData()

        return view
    }

    private fun loadData() {
        val versesDatabase = VersesDatabase.provideVerseDatabase(mContext)

        val dailyVerseDao = versesDatabase.dailyVerseDao()
        dailyVerseDao.findDailyVersesByFavourite().observe(
                this,
                Observer { verses ->
                    val verseCardDataList = mutableListOf<VerseCardData>()
                    verses.forEach { verse -> verseCardDataList.add(VerseCardData.fromDailyVerse(mContext, verse)) }
                    updateData(verseCardDataList, daily = true)
                }
        )

        val weeklyVersesDao = versesDatabase.weeklyVerseDao()
        weeklyVersesDao.findWeeklyVersesByFavourite().observe(
                this,
                Observer { verses ->
                    val verseCardDataList = VerseCardData.fromWeeklyVerses(mContext, verses.toList())
                    verseCardDataList.forEach { verse ->
                        verse.title = requireContext().getString(R.string.weekly_verse) + " " + verse.title
                    }
                    updateData(verseCardDataList, weekly = true)
                }
        )

        val monthlyVersesDao = versesDatabase.monthlyVerseDao()
        monthlyVersesDao.findMonthlyVersesByFavourite().observe(
                this,
                Observer { verses ->
                    val verseCardDataList = mutableListOf<VerseCardData>()
                    verses.forEach { verse ->
                        val verseToAdd = VerseCardData.fromMonthlyVerse(mContext, verse)
                        verseToAdd.title += " " + VerseCardData.formateDateOnlyMonthAndYear(verseToAdd.date!!)
                        verseCardDataList.add(verseToAdd)
                    }
                    updateData(verseCardDataList, monthly = true)
                }
        )
    }

    private fun updateData(data: List<VerseCardData>, daily: Boolean = false, weekly: Boolean = false, monthly: Boolean = false) {
        val listToSort = mutableListOf<VerseCardData>()
        when {
            daily -> {
                dailyVerses = data
            }
            weekly -> {
                weeklyVerses = data
            }
            monthly -> {
                monthlyVerses = data
            }
        }
        listToSort.addAll(dailyVerses)
        listToSort.addAll(weeklyVerses)
        listToSort.addAll(monthlyVerses)
        super.updateData(sortData(listToSort))
    }

    private fun sortData(data: List<VerseCardData>): List<VerseCardData> {
        return data.sortedBy { verse -> verse.date }
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment
         * @return A new instance of fragment FavouriteVersesOverviewFragment.
         */
        fun newInstance(): FavouriteVersesOverviewFragment {
            return FavouriteVersesOverviewFragment()
        }
    }

}
