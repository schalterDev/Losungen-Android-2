package de.schalter.losungen

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import de.schalter.customize.CustomizeActivity
import de.schalter.customize.CustomizeToolbar
import de.schalter.losungen.components.navigationDrawer.NavigationDrawer
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.sermon.Sermon
import de.schalter.losungen.firebase.FirebaseUtil
import de.schalter.losungen.migration.MigrateProgressDialog
import de.schalter.losungen.migration.Migration
import de.schalter.losungen.screens.intro.IntroActivity
import de.schalter.losungen.utils.PreferenceTags
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class MainActivity : CustomizeActivity() {

    private lateinit var toolbar: CustomizeToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        // when rotating savedInstanceState is not null then do not select a fragment
        setupNavigationDrawer(savedInstanceState == null)

        checkForOldSermonsToDelete()
        setupFirebase()

        val legacyMigrationRun = migrateOldData()
        if (!legacyMigrationRun) {
            checkFirstStart()
        }
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.app_name)

        setSupportActionBar(toolbar)
    }

    private fun setupNavigationDrawer(setDefaultFragment: Boolean = true) {
        val navigationDrawer = NavigationDrawer(this) { nextFragment, tag -> this@MainActivity.changeFragment(nextFragment, tag) }
        navigationDrawer.initAndShow(toolbar)
        if (setDefaultFragment)
            navigationDrawer.setActiveItem(NavigationDrawer.DrawerItem.DAILY_OVERVIEW)
        else
            navigationDrawer.findActiveItem(supportFragmentManager)
    }

    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_activity_fragment, fragment, tag)
                .commit()
    }

    private fun setupFirebase() {
        FirebaseUtil.init(this)

        val adView = findViewById<AdView>(R.id.adView)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getBoolean(PreferenceTags.SHOW_ADS, false)) {
            MobileAds.initialize(this)

            adView.apply {
                val request = AdRequest.Builder()
                        .addTestDevice("B8112A79125951F1A5CFFC6EB2FFDE24")
                        .build()
                this.loadAd(request)
            }
        } else {
            adView.visibility = View.GONE
        }
    }

    private fun checkForOldSermonsToDelete() {
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        val daysString = settings.getString(PreferenceTags.SERMONS_DELETE_AUTO, "0")
        val days = Integer.parseInt(daysString!!)
        if (days > 0) {
            val timeLimit = System.currentTimeMillis() - days.toLong() * 24L * 60L * 60L * 1000L
            val sermonDao = VersesDatabase.provideVerseDatabase(this).sermonDao()
            val liveDataSermon = sermonDao.getSermonsBeforeDate(Date(timeLimit))
            liveDataSermon.observe(this, androidx.lifecycle.Observer<List<Sermon>> { sermonsToDelete ->
                liveDataSermon.removeObservers(this)
                for (sermon in sermonsToDelete) {
                    File(sermon.pathSaved).delete()
                    GlobalScope.launch {
                        sermonDao.deleteSermon(sermon)
                    }
                }
            })
        }
    }

    /**
     * @return returns true when the legacy migration has to run
     */
    private fun migrateOldData(): Boolean {
        val migrate = Migration(this)

        if (migrate.needMigrationFromLegacyApp()) {
            val dialog = MigrateProgressDialog(R.string.legacy_migrating_info, R.string.legacy_migrating_info_features)
            dialog.show(this.supportFragmentManager, null)

            migrate.progressChangeListener = dialog
            migrate.migrateFromLegacyIfNecessary()
            return true
        } else {
            if (migrate.needMigration()) {
                val dialog = MigrateProgressDialog(R.string.migrating_time_zone, null, showFinishButton = false)
                dialog.show(this.supportFragmentManager, null)
                migrate.progressChangeListener = dialog
            }
            // to show changelog even when no migration is needed
            migrate.migrateIfNecessaryAndShowChangelog()
        }

        return false
    }

    private fun checkFirstStart() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getBoolean(PreferenceTags.FIRST_START, true)) {
            preferences.edit()
                    .putBoolean(PreferenceTags.FIRST_START, false)
                    .apply()

            startActivity(Intent(this, IntroActivity::class.java))
        }
    }
}
