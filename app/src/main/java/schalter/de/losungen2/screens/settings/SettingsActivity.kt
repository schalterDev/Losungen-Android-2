package schalter.de.losungen2.screens.settings

import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.*
import schalter.de.losungen2.R
import schalter.de.losungen2.backgroundTasks.dailyNotifications.ScheduleNotification
import schalter.de.losungen2.components.dialogs.deleteSermons.DeleteSermonsDialog
import schalter.de.losungen2.components.preferences.timePicker.TimeDialog
import schalter.de.losungen2.components.preferences.timePicker.TimePreference
import schalter.de.losungen2.utils.PreferenceTags


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(context!!)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

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
        if (preference is TimePreference) {
            val dialogFragment = TimeDialog.newInstance(preference.key)
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(fragmentManager!!, null)
        } else
            super.onDisplayPreferenceDialog(preference)
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

