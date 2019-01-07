package schalter.de.losungen2.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import schalter.de.losungen2.components.verseCard.VerseCardData
import schalter.de.losungen2.dataAccess.DailyVerse
import schalter.de.losungen2.dataAccess.VersesDatabase
import java.util.*

const val ARG_DATE = "arg_date"

/**
 * A simple [Fragment] subclass.
 * Use the [DailyVerseFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DailyVerseFragment : VerseListDateFragment() {

    private lateinit var mContext: Context

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
