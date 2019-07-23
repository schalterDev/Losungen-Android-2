package de.schalter.losungen2.firebase

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import de.schalter.losungen2.utils.PreferenceTags

object FirebaseUtil {

    fun init(context: Context) {
        allowSending(
                context,
                PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceTags.SEND_STATISTICS, false),
                false)
    }

    fun allowSending(context: Context, allow: Boolean, sendLastCommand: Boolean = true) {
        if (!allow && sendLastCommand) {
            trackSettingsChanged(context, PreferenceTags.SEND_STATISTICS, allow.toString())
        }

        getInstance(context).setAnalyticsCollectionEnabled(allow)
    }

    fun trackFragment(fragment: Fragment) {
        fragment.activity?.let {
            getInstance(it).setCurrentScreen(it, fragment::class.java.toString(), null)
        }
    }

    fun trackSettingsChanged(context: Context, tag: String, newValue: String, oldValue: String? = null) {
        val params = Bundle()
        params.putString("tag", tag)
        params.putString("newValue", newValue)
        oldValue?.let { params.putString("oldValue", oldValue) }

        getInstance(context).logEvent("preference_change", params)
    }

    private fun getInstance(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
}