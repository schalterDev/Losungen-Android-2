package de.schalter.losungen2.screens.daily

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.schalter.losungen2.R
import de.schalter.losungen2.components.dialogs.dateChooser.DateChooserDialog
import de.schalter.losungen2.components.tabs.DatePagerAdapter
import de.schalter.losungen2.firebase.AnalyticsFragment
import kotlinx.android.synthetic.main.fragment_tabs.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [DailyVersesOverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DailyVersesOverviewFragment : AnalyticsFragment() {

    private var actualDate: Date = Calendar.getInstance().time
    private lateinit var pagerAdapter: DailyVersePagerAdapter

    /**
     * Updates the verses showed to the given date
     * @param date show the verses for this date
     */
    fun setDateToShow(date: Date) {
        pagerAdapter.setDate(date)
        actualDate = date
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tabs, container, false)
        val pager = view.findViewById<ViewPager>(R.id.viewPager)
        val tabs = view.findViewById<TabLayout>(R.id.tabLayout)

        pagerAdapter = DailyVersePagerAdapter(childFragmentManager)
        pager.adapter = pagerAdapter
        tabs.setupWithViewPager(pager)
        pager.setCurrentItem(pagerAdapter.count / 2, false)

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                actualDate = when {
                    position < DatePagerAdapter.thresholdLoadNewFragments -> {
                        pagerAdapter.addDatesAtStart()
                        val itemPosition = position + DatePagerAdapter.numberOfDaysAfterAndBeforeDate
                        pager.setCurrentItem(itemPosition, false)
                        tabLayout.setScrollPosition(itemPosition, 0f, true)

                        pagerAdapter.getDateByPosition(itemPosition)
                    }
                    position > pagerAdapter.count - DatePagerAdapter.thresholdLoadNewFragments -> {
                        pagerAdapter.addDatesAtEnd()
                        pagerAdapter.getDateByPosition(position)
                    }
                    else -> pagerAdapter.getDateByPosition(position)
                }
            }
        })

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_daily_verse_overview, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_date -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = actualDate.time

                DateChooserDialog(context!!, calendar) { date -> setDateToShow(date) }

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment
         * @return A new instance of fragment DailyVersesOverviewFragment.
         */
        fun newInstance(): DailyVersesOverviewFragment {
            return DailyVersesOverviewFragment()
        }
    }
}
