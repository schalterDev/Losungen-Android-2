package de.schalter.losungen.screens.daily

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import de.schalter.losungen.components.tabs.DatePagerAdapter
import java.util.*

private const val dateFormat = "E, dd.MM"

class DailyVersePagerAdapter(fm: FragmentManager) : DatePagerAdapter(fm, dateFormat, Calendar.DAY_OF_MONTH) {

    override fun getItem(position: Int): Fragment = DailyVerseFragment.newInstance(dateList[position])
}