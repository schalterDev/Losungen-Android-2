package schalter.de.losungen2.tabs

import android.content.Context
import androidx.fragment.app.FragmentManager
import schalter.de.losungen2.fragments.DailyVerseFragment
import java.text.SimpleDateFormat
import java.util.*

class DailyVersePagerAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {

    private val dateFormat = "E, dd.MM"

    private lateinit var dateFirstFragment: Date
    private lateinit var dateLastFragment: Date

    init {
        this.setDate(Calendar.getInstance().time)
    }

    fun setDate(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, -numberOfDaysAfterAndBeforeDate - 1)

        dateFirstFragment = calendar.time

        for (i in -numberOfDaysAfterAndBeforeDate..numberOfDaysAfterAndBeforeDate) {
            val fragmentWithTitle = FragmentWithTitle(DailyVerseFragment.newInstance(calendar.time), getTitleByDate(calendar.time))
            this.addItemAtEnd(fragmentWithTitle)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        dateLastFragment = calendar.time
    }

    /**
     * @param itemCount how many items should be added at the beginning
     */
    fun addDatesAtStart(itemCount: Int = numberOfDaysAfterAndBeforeDate) {
        val calendar = Calendar.getInstance()
        calendar.time = dateFirstFragment

        for (i in 1..itemCount) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val fragmentWithTitle = FragmentWithTitle(DailyVerseFragment.newInstance(calendar.time), getTitleByDate(calendar.time))
            this.addItemAtStart(fragmentWithTitle)
        }

        dateFirstFragment = calendar.time
    }

    /**
     * @param itemCount how many items should be added at the end
     */
    fun addDatesAtEnd(itemCount: Int = numberOfDaysAfterAndBeforeDate) {
        val calendar = Calendar.getInstance()
        calendar.time = dateLastFragment

        for (i in 1..itemCount) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val fragmentWithTitle = FragmentWithTitle(DailyVerseFragment.newInstance(calendar.time), getTitleByDate(calendar.time))
            this.addItemAtEnd(fragmentWithTitle)
        }

        dateLastFragment = calendar.time
    }

    private fun getTitleByDate(date: Date): String {
        val df = SimpleDateFormat(dateFormat, Locale.GERMANY)
        return df.format(date)
    }

    companion object {
        const val numberOfDaysAfterAndBeforeDate = 4
        const val thresholdLoadNewFragments = 3
    }
}