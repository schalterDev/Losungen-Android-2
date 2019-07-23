package de.schalter.losungen2.screens.daily

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import de.schalter.losungen2.components.tabs.DatePagerAdapter
import java.util.*

private const val dateFormat = "E, dd.MM"

class DailyVersePagerAdapter(fm: FragmentManager) : DatePagerAdapter(fm, dateFormat, Calendar.DAY_OF_MONTH) {

    override fun getItem(position: Int): Fragment = DailyVerseFragment.newInstance(dateList[position])
}