package de.schalter.losungen2.firebase

import androidx.fragment.app.Fragment

abstract class AnalyticsFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        FirebaseUtil.trackFragment(this)
    }

}