package schalter.de.losungen2.tabs

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import schalter.de.losungen2.fragments.DateFragment
import java.text.SimpleDateFormat
import java.util.*


/**
 * @param dateRange a constant from calendar. For example Calendar.DAY_OF_MONTH
 */
abstract class DatePagerAdapter(fm: FragmentManager, private var dateFormat: String, private val dateRange: Int) : FragmentStatePagerAdapter(fm) {

    protected val dateList: MutableList<Date> = mutableListOf()

    init {
        this.setDate(Calendar.getInstance().time)
    }

    override fun getItemPosition(item: Any) = PagerAdapter.POSITION_NONE

    override fun getPageTitle(position: Int): CharSequence? = this.getTitleByDate(dateList[position])

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
            calendar.add(dateRange, -1)
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
            calendar.add(dateRange, 1)
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