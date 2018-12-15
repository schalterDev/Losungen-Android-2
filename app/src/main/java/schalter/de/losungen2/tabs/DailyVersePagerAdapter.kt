package schalter.de.losungen2.tabs

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import schalter.de.losungen2.fragments.DailyVerseFragment
import java.util.*

private const val dateFormat = "E, dd.MM"

class DailyVersePagerAdapter(fm: FragmentManager, context: Context) : DatePagerAdapter(fm, dateFormat, Calendar.DAY_OF_MONTH) {

    override fun getItem(position: Int): Fragment = DailyVerseFragment.newInstance(dateList[position])
}