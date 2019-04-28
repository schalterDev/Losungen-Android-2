package schalter.de.losungen2.screens.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import schalter.de.losungen2.R
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

        val defaultOpenExternalProgram = findPreference<Preference>(PreferenceTags.OPEN_EXTERNAL_DEFAULT)
        defaultOpenExternalProgram?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val editor = preferences.edit()
            editor.putString(PreferenceTags.OPEN_EXTERNAL_DEFAULT, null)
            editor.apply()

            Toast.makeText(context!!, R.string.successful, Toast.LENGTH_SHORT).show()
            true
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
}

