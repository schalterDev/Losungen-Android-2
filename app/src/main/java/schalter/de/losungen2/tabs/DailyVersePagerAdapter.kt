package schalter.de.losungen2.tabs

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import schalter.de.losungen2.fragments.DailyVerseFragment
import schalter.de.losungen2.fragments.DateFragment
import java.text.SimpleDateFormat
import java.util.*

private const val dateFormat = "E, dd.MM"

class DailyVersePagerAdapter(fm: FragmentManager, context: Context) : FragmentStatePagerAdapter(fm) {

    private val dateList: MutableList<Date> = mutableListOf()

    init {
        this.setDate(Calendar.getInstance().time)
    }

    override fun getItemPosition(item: Any): Int {
        return if (item is DateFragment) {
            dateList.indexOf(item.date)
        } else {
            PagerAdapter.POSITION_NONE
        }
    }

    override fun getPageTitle(position: Int): CharSequence? = this.getTitleByDate(dateList[position])

    override fun getItem(position: Int): Fragment = DailyVerseFragment.newInstance(dateList[position])

    override fun getCount(): Int = dateList.size

    fun setDate(date: Date) {
        dateList.clear()
        dateList.add(date)

        addDatesAtStart()
        addDatesAtEnd()
    }

    /**
     * @param itemCount how many items should be added at the beginning
     */
    fun addDatesAtStart(itemCount: Int = numberOfDaysAfterAndBeforeDate) {
        val calendar = Calendar.getInstance()
        calendar.time = dateList.first()

        for (i in 1..itemCount) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            dateList.add(0, calendar.time)
        }

        this.notifyDataSetChanged()
    }

    /**
     * @param itemCount how many items should be added at the end
     */
    fun addDatesAtEnd(itemCount: Int = numberOfDaysAfterAndBeforeDate) {
        val calendar = Calendar.getInstance()
        calendar.time = dateList.last()

        for (i in 1..itemCount) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            dateList.add(calendar.time)
        }

        this.notifyDataSetChanged()
    }

    private fun getTitleByDate(date: Date): String {
        // TODO change for multi language
        val df = SimpleDateFormat(dateFormat, Locale.GERMANY)
        return df.format(date)
    }

    companion object {
        const val numberOfDaysAfterAndBeforeDate = 4
        const val thresholdLoadNewFragments = 3
    }
}