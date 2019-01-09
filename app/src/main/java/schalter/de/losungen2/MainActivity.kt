package schalter.de.losungen2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import schalter.de.losungen2.components.navigationDrawer.NavigationDrawer

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupNavigationDrawer()
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.app_name)
    }

    private fun setupNavigationDrawer() {
        val navigationDrawer = NavigationDrawer(this) { nextFragment -> this@MainActivity.changeFragment(nextFragment) }
        navigationDrawer.initAndShow(toolbar)
        navigationDrawer.setActiveItem(NavigationDrawer.DrawerItem.DAILY_OVERVIEW)
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_activity_fragment, fragment)
                .commit()
    }
}
