package de.schalter.losungen

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import de.schalter.losungen.components.navigationDrawer.NavigationDrawer
import de.schalter.losungen.screens.daily.DailyVersesOverviewFragment
import de.schalter.losungen.screens.favourite.FavouriteVersesOverviewFragment
import de.schalter.losungen.screens.info.InfoFragment
import de.schalter.losungen.screens.monthly.MonthlyVersesOverviewFragment
import de.schalter.losungen.screens.settings.SettingsActivity
import de.schalter.losungen.utils.Constants


@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val rule = ActivityTestRule(MainActivity::class.java, false, false)

    private lateinit var activity: MainActivity

    @Before
    fun loadActivity() {
        rule.launchActivity(null)
        activity = rule.activity

        Intents.init()
        intending(not(isInternal())).respondWith(ActivityResult(Activity.RESULT_OK, null))
    }

    @After
    fun cleanUp() {
        Intents.release()
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

        //Info
        openNavigationDrawer()
        onView(withText(R.string.info_help)).perform(click())
        fragment = activity.supportFragmentManager.findFragmentByTag(NavigationDrawer.DrawerItem.INFO.toString()) as InfoFragment
        assertTrue(fragment.isVisible)
    }

    @Test
    fun openSettingsActivity() {
        openNavigationDrawer()
        onView(withText(R.string.settings)).perform(click())
        intended(hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun rateClick() {
        openNavigationDrawer()
        onView(withText(R.string.rate)).perform(click())
        intended(hasAction(Intent.ACTION_VIEW))
    }

    @Test
    fun feedbackClick() {
        openNavigationDrawer()
        onView(withText(R.string.send_feedback_bug)).perform(click())
        intending(allOf(hasAction(Intent.ACTION_CHOOSER), hasExtra("act", Intent.ACTION_SENDTO)))
    }

    @Test
    fun privacyClick() {
        openNavigationDrawer()
        onView(withText(R.string.privacy_policy)).perform(click())
        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData(Constants.urlPrivacyWebsite)))
    }

    private fun openNavigationDrawer() {
        onView(withId(R.id.material_drawer_layout)).perform(DrawerActions.open())
    }


}