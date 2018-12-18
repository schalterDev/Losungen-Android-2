package schalter.de.losungen2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_tabs.*
import schalter.de.losungen2.R
import schalter.de.losungen2.tabs.DailyVersePagerAdapter
import schalter.de.losungen2.tabs.DatePagerAdapter
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [DailyVersesOverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DailyVersesOverviewFragment : Fragment() {

    private lateinit var pagerAdapter: DailyVersePagerAdapter

    /**
     * Updates the verses showed to the given date
     * @param date show the verses for this date
     */
    fun setDateToShow(date: Date) {
        pagerAdapter.setDate(date)
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

        pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
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
         * @return A new instance of fragment DailyVersesOverviewFragment.
         */
        fun newInstance(): DailyVersesOverviewFragment {
            return DailyVersesOverviewFragment()
        }
    }
}
