package schalter.de.losungen2

import androidx.fragment.app.Fragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import schalter.de.losungen2.components.navigationDrawer.NavigationDrawer
import schalter.de.losungen2.screens.daily.DailyVersesOverviewFragment
import schalter.de.losungen2.screens.favourite.FavouriteVersesOverviewFragment
import schalter.de.losungen2.screens.info.InfoFragment
import schalter.de.losungen2.screens.monthly.MonthlyVersesOverviewFragment
import schalter.de.losungen2.screens.settings.SettingsActivity
import schalter.de.losungen2.screens.widget.WidgetsOverviewFragment

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val rule = ActivityTestRule(MainActivity::class.java, false, false)

    private lateinit var activity: MainActivity

    @Before
    fun loadActivity() {
        rule.launchActivity(null)
        activity = rule.activity
    }

    @Test
    fun shouldShowDailyVerseFragment() {
        val fragment: DailyVersesOverviewFragment = activity.supportFragmentManager.findFragmentByTag(NavigationDrawer.DrawerItem.DAILY_OVERVIEW.toString()) as DailyVersesOverviewFragment

        Assert.assertNotNull(fragment)
        Assert.assertTrue(fragment.isVisible)
    }

    @Test
    fun shouldNavigateWithNavigationDrawer() {
        openNavigationDrawer()

        //Monthly Verse
        onView(withText(R.string.monthly_verses)).perform(click())
        var fragment: Fragment = activity.supportFragmentManager.findFragmentByTag(NavigationDrawer.DrawerItem.MONTHLY_OVERVIEW.toString()) as MonthlyVersesOverviewFragment
        assertTrue(fragment.isVisible)

        //Favourite Verse
        openNavigationDrawer()
        onView(withText(R.string.favorite_verses)).perform(click())
        fragment = activity.supportFragmentManager.findFragmentByTag(NavigationDrawer.DrawerItem.FAVOURITE_OVERVIEW.toString()) as FavouriteVersesOverviewFragment
        assertTrue(fragment.isVisible)

        //Widget
        openNavigationDrawer()
        onView(withText(R.string.configure_widgets)).perform(click())
        fragment = activity.supportFragmentManager.findFragmentByTag(NavigationDrawer.DrawerItem.WIDGET_OVERVIEW.toString()) as WidgetsOverviewFragment
        assertTrue(fragment.isVisible)

        //Info
        openNavigationDrawer()
        onView(withText(R.string.info_help)).perform(click())
        fragment = activity.supportFragmentManager.findFragmentByTag(NavigationDrawer.DrawerItem.INFO.toString()) as InfoFragment
        assertTrue(fragment.isVisible)

        //Settings
        Intents.init()
        openNavigationDrawer()
        onView(withText(R.string.settings)).perform(click())
        intended(hasComponent(SettingsActivity::class.java.name))
    }

    private fun openNavigationDrawer() {
        onView(withId(R.id.material_drawer_layout)).perform(DrawerActions.open())
    }


}