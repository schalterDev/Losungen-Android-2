package de.schalter.losungen.screens.monthly

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import de.schalter.losungen.components.tabs.DatePagerAdapter
import java.util.*

private const val dateFormat = "MMM"

class MonthlyVersePagerAdapter(fm: FragmentManager) : DatePagerAdapter(fm, dateFormat, Calendar.MONTH) {

    override fun getItem(position: Int): Fragment = MonthlyVerseFragment.newInstance(dateList[position])
}