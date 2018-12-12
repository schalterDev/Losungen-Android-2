package schalter.de.losungen2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import schalter.de.losungen2.R
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MonthlyVersesOverviewFragment : Fragment() {

    /**
     * Updates the verses showed to the given date
     * @param date show the verses for this date
     */
    fun setDateToShow(date: Date) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monthly_verses_overview, container, false)
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
