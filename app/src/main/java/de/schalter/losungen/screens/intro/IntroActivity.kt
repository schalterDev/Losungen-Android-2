package de.schalter.losungen.screens.intro

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import de.schalter.losungen.R
import de.schalter.losungen.backgroundTasks.dailyNotifications.ScheduleNotification
import de.schalter.losungen.utils.PreferenceTags

class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Notifications
        addSlide(IntroCustomFragment.newInstance(
                getString(R.string.notifications),
                R.drawable.intro_notifications,
                backgroundColor = Color.parseColor("#1976D2"),
                foregroundColor = Color.WHITE,
                switchText = getString(R.string.activate_notifications),
                subtitle = getString(R.string.remember_gods_word),
                description = getString(R.string.can_be_changed_in_the_settings),
                switchForegroundColor = Color.parseColor("#009688"),
                switchPreferenceCheckChanged = { showNotification ->
                    if (showNotification) {
                        ScheduleNotification(this).scheduleNotification()
                    } else {
                        ScheduleNotification(this).cancelScheduledNotification(true)
                    }
                }
        ))

        // Widgets
        addSlide(IntroCustomFragment.newInstance(
                getString(R.string.widgets),
                R.drawable.intro_widget,
                backgroundColor = Color.parseColor("#FF5722"),
                foregroundColor = Color.WHITE,
                subtitle = getString(R.string.widgets_personalized)
        ))

        // Ads
        addSlide(IntroCustomFragment.newInstance(
                getString(R.string.ads),
                R.drawable.intro_ads,
                backgroundColor = Color.parseColor("#00796B"),
                foregroundColor = Color.WHITE,
                switchText = getString(R.string.show_ads),
                subtitle = getString(R.string.show_ads_description),
                description = getString(R.string.can_be_changed_in_the_settings),
                switchForegroundColor = Color.parseColor("#8BC34A"),
                switchPreferenceTag = PreferenceTags.SHOW_ADS,
                switchChecked = true
        ))

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(PreferenceTags.SHOW_ADS, true)
                .putBoolean(PreferenceTags.SEND_STATISTICS, true)
                .apply()

        showSkipButton(false)
        setColorTransitionsEnabled(true)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }

}