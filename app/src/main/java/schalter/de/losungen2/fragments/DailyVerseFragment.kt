package schalter.de.losungen2.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import schalter.de.losungen2.R
import schalter.de.losungen2.components.views.OneVerseCardView
import schalter.de.losungen2.dataAccess.DailyVerse
import schalter.de.losungen2.dataAccess.VersesDatabase
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [DailyVerseFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DailyVerseFragment : DateFragment() {

    private var mContext: Context? = null

    private var oldTestamentCard: OneVerseCardView? = null
    private var newTestamentCard: OneVerseCardView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_daily_verse, container, false)
        mContext = view.context

        oldTestamentCard = view.findViewById(R.id.oldTestamentCard)
        newTestamentCard = view.findViewById(R.id.newTestamentCard)

        oldTestamentCard?.setTitle(R.string.old_testament_card_title)
        newTestamentCard?.setTitle(R.string.new_testament_card_title)

        if (date != null) {
            loadDate(date!!)
        }

        updateData(null)

        return view
    }

    private fun loadDate(date: Date) {
        val dailyVersesDatabase = VersesDatabase.provideVerseDatabase(mContext!!)
        dailyVersesDatabase.dailyVerseDao().findDailyVerseByDate(date).observe(
                this,
                androidx.lifecycle.Observer<DailyVerse> { dailyVerse: DailyVerse? -> updateData(dailyVerse) })
    }

    private fun updateData(dailyVerse: DailyVerse?) {
        val errorVerseNotFound = mContext!!.getString(R.string.no_verse_found)

        oldTestamentCard?.setVerse(dailyVerse?.oldTestamentVerseText ?: errorVerseNotFound)
        oldTestamentCard?.setVerseInBible(dailyVerse?.oldTestamentVerseBible ?: errorVerseNotFound)
        newTestamentCard?.setVerse(dailyVerse?.newTestamentVerseText ?: errorVerseNotFound)
        newTestamentCard?.setVerseInBible(dailyVerse?.newTestamentVerseBible ?: errorVerseNotFound)
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
