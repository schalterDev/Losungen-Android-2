package de.schalter.losungen2.screens.monthly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.schalter.losungen2.R
import de.schalter.losungen2.components.tabs.DatePagerAdapter
import de.schalter.losungen2.firebase.AnalyticsFragment
import kotlinx.android.synthetic.main.fragment_tabs.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MonthlyVersesOverviewFragment : AnalyticsFragment() {

    private lateinit var pagerAdapter: MonthlyVersePagerAdapter

    /**
     * Updates the verses showed to the given date
     * @param date show the verses for this date
     */
    fun setDateToShow(date: Date) {
        pagerAdapter.setDate(date)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tabs, container, false)
        val pager = view.findViewById<ViewPager>(R.id.viewPager)
        val tabs = view.findViewById<TabLayout>(R.id.tabLayout)

        pagerAdapter = MonthlyVersePagerAdapter(childFragmentManager)
        pager.adapter = pagerAdapter
        tabs.setupWithViewPager(pager)
        pager.setCurrentItem(pagerAdapter.count / 2, false)

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position < DatePagerAdapter.thresholdLoadNewFragments) {
                    pagerAdapter.addDatesAtStart()
                    pager.setCurrentItem(position + DatePagerAdapter.numberOfDaysAfterAndBeforeDate, false)
                    tabLayout.setScrollPosition(position + DatePagerAdapter.numberOfDaysAfterAndBeforeDate, 0f, true)
                } else if (position > pagerAdapter.count - DatePagerAdapter.thresholdLoadNewFragments) {
                    pagerAdapter.addDatesAtEnd()
                }
            }

        })

        return view
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment
         * @return A new instance of fragment MonthlyVersesOverviewFragment.
         */
        fun newInstance(): MonthlyVersesOverviewFragment {
            return MonthlyVersesOverviewFragment()
        }
    }

}
