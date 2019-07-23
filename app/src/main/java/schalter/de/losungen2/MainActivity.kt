package schalter.de.losungen2

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import schalter.de.customize.CustomizeActivity
import schalter.de.customize.CustomizeToolbar
import schalter.de.losungen2.components.navigationDrawer.NavigationDrawer
import schalter.de.losungen2.dataAccess.VersesDatabase
import schalter.de.losungen2.dataAccess.sermon.Sermon
import schalter.de.losungen2.utils.PreferenceTags
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

        setupAds()
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

    private fun setupAds() {
        MobileAds.initialize(this)

        findViewById<AdView>(R.id.adView).apply {
            val request = AdRequest.Builder()
                    .addTestDevice("B8112A79125951F1A5CFFC6EB2FFDE24")
                    .build()
            this.loadAd(request)
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
}
