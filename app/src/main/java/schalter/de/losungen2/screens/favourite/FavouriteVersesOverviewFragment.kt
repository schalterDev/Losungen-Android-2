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

    // TODO test if button of empty state is hidden
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

    // TODO test
    private fun loadData() {
        val dailyVerseDao = VersesDatabase.provideVerseDatabase(mContext).dailyVerseDao()
        dailyVerseDao.findDailyVersesByFavourite().observe(
                this,
                Observer { verses ->
                    val verseCardDataList = mutableListOf<VerseCardData>()
                    verses.forEach { verse -> verseCardDataList.add(VerseCardData.fromDailyVerse(mContext, verse)) }
                    updateData(verseCardDataList)
                }
        )
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
