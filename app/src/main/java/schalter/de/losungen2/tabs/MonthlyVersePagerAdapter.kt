package schalter.de.losungen2.tabs

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import schalter.de.losungen2.fragments.DailyVerseFragment
import schalter.de.losungen2.fragments.DateFragment
import schalter.de.losungen2.fragments.MonthlyVerseFragment
import java.text.SimpleDateFormat
import java.util.*

private const val dateFormat = "MMM"

class MonthlyVersePagerAdapter(fm: FragmentManager, context: Context) : DatePagerAdapter(fm, dateFormat, Calendar.MONTH) {

    override fun getItem(position: Int): Fragment = MonthlyVerseFragment.newInstance(dateList[position])
}