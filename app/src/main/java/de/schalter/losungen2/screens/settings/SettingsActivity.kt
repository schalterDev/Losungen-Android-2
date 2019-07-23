package de.schalter.losungen2.screens.settings

import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.preference.*
import de.schalter.customize.ChooseStylePreference
import de.schalter.customize.ChooseStylePreferenceDialog
import de.schalter.customize.Customize
import de.schalter.customize.CustomizeActivity
import de.schalter.losungen2.R
import de.schalter.losungen2.backgroundTasks.dailyNotifications.ScheduleNotification
import de.schalter.losungen2.components.dialogs.deleteSermons.DeleteSermonsDialog
import de.schalter.losungen2.components.preferences.timePicker.TimeDialog
import de.schalter.losungen2.components.preferences.timePicker.TimePreference
import de.schalter.losungen2.utils.FirebaseUtil
import de.schalter.losungen2.utils.PreferenceTags


class SettingsActivity : CustomizeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(Customize.getTheme(this))
        setContentView(R.layout.activity_settings)

        setupToolbar()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_activity_fragment, SettingsFragment())
                .commit()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.settings)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click in toolbar
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var preferences: SharedPreferences
    private var sharedPreferenceListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onResume() {
        super.onResume()

        context?.let { context ->
            sharedPreferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                val newValue = preferences.all[key]
                FirebaseUtil.trackSettingsChanged(context, key, newValue.toString())
            }
            preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceListener)
        }

    }

    override fun onPause() {
        super.onPause()

        sharedPreferenceListener?.let {
            preferences.unregisterOnSharedPreferenceChangeListener(it)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        allowStatistics()
        openExternalDefault()
        showNotification()
        deleteSermons()
        deleteSermonsAutomatically()

        val colorAttr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.R.attr.colorAccent
        } else {
            android.R.attr.textColorSecondary
        }

        context?.theme?.obtainStyledAttributes(intArrayOf(colorAttr))?.apply {
            val iconColor = this.getColor(0, 0)
            this.recycle()
            tintIcons(preferenceScreen, iconColor)
        }

    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        when (preference) {
            is TimePreference -> {
                TimeDialog.newInstance(preference.key).let { dialogFragment ->
                    dialogFragment.setTargetFragment(this, 0)
                    dialogFragment.show(fragmentManager!!, null)
                }
            }
            is ChooseStylePreference -> {
                ChooseStylePreferenceDialog.newInstance().let { dialogFragment ->
                    dialogFragment.setTargetFragment(this, 0)
                    dialogFragment.show(fragmentManager!!, null)
                }
            }
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

    private fun tintIcons(preference: Preference, color: Int) {
        if (preference is PreferenceGroup) {
            preference.forEach {
                tintIcons(it, color)
            }
        }
        val icon = preference.icon
        icon?.setColorFilter(color, PorterDuff.Mode.SRC_IN)

    }

    // ---------- NOTIFICATION CHANGE LISTENER ---------
    private fun allowStatistics() {
        findPreference<SwitchPreferenceCompat>(PreferenceTags.SEND_STATISTICS)?.apply {
            this.setOnPreferenceChangeListener { _, newValue ->
                if (newValue is Boolean) {
                    FirebaseUtil.allowSending(context, newValue)
                }
                true
            }
        }
    }

    private fun openExternalDefault() {
        findPreference<Preference>(PreferenceTags.OPEN_EXTERNAL_DEFAULT)?.apply {
            this.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val editor = preferences.edit()
                editor.putString(PreferenceTags.OPEN_EXTERNAL_DEFAULT, null)
                editor.apply()

                Toast.makeText(context!!, R.string.successful, Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    private fun showNotification() {
        findPreference<SwitchPreferenceCompat>(PreferenceTags.NOTIFICATION_SHOW)?.apply {
            this.setOnPreferenceChangeListener { _, newValue ->
                val showNotification = newValue as Boolean

                if (showNotification) {
                    ScheduleNotification(context!!).scheduleNotification(instantlyShowNotification = true)
                } else {
                    ScheduleNotification(context!!).cancelScheduledNotification(true)
                }

                true
            }
        }
    }

    private fun deleteSermonsAutomatically() {
        findPreference<EditTextPreference>(PreferenceTags.SERMONS_DELETE_AUTO)?.apply {
            this.setOnPreferenceChangeListener { _, newValue ->
                var days = 0
                try {
                    days = (newValue as String).toInt()
                } catch (error: NumberFormatException) {
                    Toast.makeText(context, R.string.only_numbers_allowed, Toast.LENGTH_LONG).show()
                }

                val editor = preferences.edit()
                editor.putString(PreferenceTags.SERMONS_DELETE_AUTO, days.toString())
                editor.apply()

                this.text = days.toString()

                false
            }
        }
    }

    private fun deleteSermons() {
        findPreference<Preference>(PreferenceTags.SERMONS_DELETE)?.apply {
            this.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                DeleteSermonsDialog(context).show(this@SettingsFragment.fragmentManager!!, null)
                true
            }
        }
    }
}

