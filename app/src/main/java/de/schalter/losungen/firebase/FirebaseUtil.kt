package de.schalter.losungen.firebase

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import de.schalter.losungen.utils.PreferenceTags

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
        params.putString("newValue", newValue)
        oldValue?.let { params.putString("oldValue", oldValue) }

        getInstance(context).logEvent("preference_change_$tag", params)
    }

    fun trackSermonPlayed(context: Context) {
        getInstance(context).logEvent("sermon_played", Bundle())
    }

    fun trackFavouriteVerse(context: Context, weekly: Boolean = false, monthly: Boolean = false, value: Boolean) {
        val params = Bundle()
        val whichType =
                when {
                    weekly -> "week"
                    monthly -> "month"
                    else -> "day"
                }
        params.putString("type", whichType)
        params.putBoolean("value", value)

        getInstance(context).logEvent("marked_favourite", params)
    }

    fun trackOpenExternal(context: Context, openedWith: String, defaultValue: Boolean) {
        val params = Bundle()
        params.putString("open_with", openedWith)
        params.putBoolean("default", defaultValue)

        getInstance(context).logEvent("open_external", params)
    }

    fun trackVerseShared(context: Context) {
        getInstance(context).logEvent("verse_shared", Bundle())
    }

    fun trackSermonShared(context: Context, mp3File: Boolean) {
        val params = Bundle()
        params.putBoolean("mp3_file", mp3File)

        getInstance(context).logEvent("sermon_shared", params)
    }

    fun trackShowedNotification(context: Context) {
        getInstance(context).logEvent("showed_notification", Bundle())
    }

    fun trackNotificationClick(context: Context, markFavourite: Boolean = false) {
        val params = Bundle()
        params.putString("action", if (markFavourite) "favourite" else "share")

        getInstance(context).logEvent("notification_action", params)
    }

    fun trackWidgetCreated(context: Context) {
        getInstance(context).logEvent("widget_created", Bundle())
    }

    fun trackWidgetDeleted(context: Context) {
        getInstance(context).logEvent("widget_deleted", Bundle())
    }

    private fun getInstance(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
}