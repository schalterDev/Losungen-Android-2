package schalter.de.losungen2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import schalter.de.losungen2.R
import schalter.de.losungen2.components.layouts.OneVerseCardView
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [DailyVerseFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DailyVerseFragment : DateFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_daily_verse, container, false)

        val oldTestamentCard: OneVerseCardView = view.findViewById(R.id.oldTestamentCard)
        val newTestamentCard: OneVerseCardView = view.findViewById(R.id.newTestamentCard)

        oldTestamentCard.setTitle(R.string.oldTestamentCardTitle)
        newTestamentCard.setTitle(R.string.newTestamentCardtitle)

        oldTestamentCard.setVerse("Verse")
        oldTestamentCard.setVerseInBible("Verse in bible")

        newTestamentCard.setVerse("Verse")
        newTestamentCard.setVerseInBible("Verse in bible")

        return view
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
