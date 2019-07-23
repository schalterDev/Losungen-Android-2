package de.schalter.losungen2.firebase

import androidx.fragment.app.Fragment
import de.schalter.losungen2.utils.FirebaseUtil

abstract class AnalyticsFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        FirebaseUtil.trackFragment(this)
    }

}