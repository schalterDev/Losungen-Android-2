package schalter.de.losungen2.screens.daily

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import schalter.de.losungen2.R
import schalter.de.losungen2.components.verseCard.VerseCardData
import schalter.de.losungen2.dataAccess.VersesDatabase
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.screens.ARG_DATE
import schalter.de.losungen2.screens.VerseListDateFragment
import schalter.de.losungen2.utils.Share
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [DailyVerseFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DailyVerseFragment : VerseListDateFragment() {

    private lateinit var mContext: Context
    private var dailyVerse: DailyVerse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_daily_verse, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                if (dailyVerse != null) {
                    Share.dailyVerse(mContext, dailyVerse!!)
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        mContext = view!!.context

        date?.let { loadDate(it) }

        return view
    }

    private fun loadDate(date: Date) {
        val dailyVersesDatabase = VersesDatabase.provideVerseDatabase(mContext)
        dailyVersesDatabase.dailyVerseDao().findDailyVerseByDate(date).observe(
                this,
                androidx.lifecycle.Observer<DailyVerse> { dailyVerse: DailyVerse? -> updateDataByDailyVerse(dailyVerse) })
    }

    private fun updateDataByDailyVerse(dailyVerse: DailyVerse?) {
        if (dailyVerse != null) {
            this.dailyVerse = dailyVerse
            this.updateData(VerseCardData.fromDailyVerse(mContext, dailyVerse))
        } else {
            this.updateData(listOf())
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param verseDate date of the verse to show
         * @return A new instance of fragment DailyVerseFragment.
         */
        @JvmStatic
        fun newInstance(verseDate: Date) =
                DailyVerseFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_DATE, verseDate.time)
                    }
                }
    }
}
