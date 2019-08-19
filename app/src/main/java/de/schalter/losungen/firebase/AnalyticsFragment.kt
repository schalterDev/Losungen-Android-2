package de.schalter.losungen.firebase

import androidx.fragment.app.Fragment

abstract class AnalyticsFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        FirebaseUtil.trackFragment(this)
    }

}