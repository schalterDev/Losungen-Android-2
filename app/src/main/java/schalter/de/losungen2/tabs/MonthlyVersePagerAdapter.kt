package schalter.de.losungen2.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import schalter.de.losungen2.fragments.MonthlyVerseFragment
import java.util.*

private const val dateFormat = "MMM"

class MonthlyVersePagerAdapter(fm: FragmentManager) : DatePagerAdapter(fm, dateFormat, Calendar.MONTH) {

    override fun getItem(position: Int): Fragment = MonthlyVerseFragment.newInstance(dateList[position])
}