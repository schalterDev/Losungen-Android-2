package schalter.de.losungen2.components.navigationDrawer

import android.app.Activity
import android.content.Intent
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import schalter.de.losungen2.R
import schalter.de.losungen2.screens.daily.DailyVersesOverviewFragment
import schalter.de.losungen2.screens.favourite.FavouriteVersesOverviewFragment
import schalter.de.losungen2.screens.info.InfoFragment
import schalter.de.losungen2.screens.monthly.MonthlyVersesOverviewFragment
import schalter.de.losungen2.screens.settings.SettingsActivity
import schalter.de.losungen2.screens.widget.WidgetsOverviewFragment
import schalter.de.losungen2.utils.Open

class NavigationDrawer(private val activity: Activity, private val fragmentChangeListener: (Fragment, String) -> Unit) {
    private lateinit var navigationDrawer: Drawer

    // Fragments
    private var dailyVersesOverviewFragment: DailyVersesOverviewFragment? = null
    private var monthlyVersesOverviewFragment: MonthlyVersesOverviewFragment? = null
    private var favouriteVersesOverviewFragment: FavouriteVersesOverviewFragment? = null
    private var widgetsOverviewFragment: WidgetsOverviewFragment? = null
    private var infoFragment: InfoFragment? = null

    // Items
    private lateinit var itemDailyVerses: PrimaryDrawerItem
    private lateinit var itemMonthlyVerses: PrimaryDrawerItem
    private lateinit var itemFavourite: PrimaryDrawerItem
    private lateinit var itemWidget: PrimaryDrawerItem
    private lateinit var itemSettings: PrimaryDrawerItem
    private lateinit var itemRate: PrimaryDrawerItem
    private lateinit var itemFeedback: PrimaryDrawerItem
    private lateinit var itemInfo: PrimaryDrawerItem
    private lateinit var itemPrivacy: PrimaryDrawerItem

    fun initAndShow(toolbar: Toolbar): Drawer? {
        itemDailyVerses = PrimaryDrawerItem().withName(R.string.losungen)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_event_note)
        itemMonthlyVerses = PrimaryDrawerItem().withName(R.string.monthly_verses)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_event_note)
        itemFavourite = PrimaryDrawerItem().withName(R.string.favorite_verses)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_favorite)
        itemWidget = PrimaryDrawerItem().withName(R.string.configure_widgets)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_color_lens)
        itemSettings = PrimaryDrawerItem().withName(R.string.settings)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_settings)
                .withSelectable(false)
        itemRate = PrimaryDrawerItem().withName(R.string.rate)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_star)
                .withSelectable(false)
        itemFeedback = PrimaryDrawerItem().withName(R.string.send_feedback_bug)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_email)
                .withSelectable(false)
        itemInfo = PrimaryDrawerItem().withName(R.string.info_help)
                .withIcon(R.drawable.ic_action_info)
                .withIconTintingEnabled(true)
        itemPrivacy = PrimaryDrawerItem().withName(R.string.privacy_policy)
                .withIcon(R.drawable.ic_action_notes)
                .withIconTintingEnabled(true)
                .withSelectable(false)

        navigationDrawer = DrawerBuilder()
                .withActivity(activity)
                .withTranslucentStatusBar(true)
                .withToolbar(toolbar)
                .addDrawerItems(
                        itemDailyVerses,
                        itemMonthlyVerses,
                        itemFavourite,
                        itemWidget,
                        itemSettings,
                        DividerDrawerItem(),
                        itemRate,
                        itemFeedback,
                        itemInfo,
                        itemPrivacy
                )
                .withOnDrawerItemClickListener { _, _, drawerItem -> this@NavigationDrawer.onItemClicked(drawerItem) }
                .build()

        return navigationDrawer
    }

    private fun getIDrawerItemFromDrawerItem(drawerItem: DrawerItem): IDrawerItem<*, *> {
        return when (drawerItem) {
            DrawerItem.DAILY_OVERVIEW -> itemDailyVerses
            DrawerItem.MONTHLY_OVERVIEW -> itemMonthlyVerses
            DrawerItem.FAVOURITE_OVERVIEW -> itemFavourite
            DrawerItem.WIDGET_OVERVIEW -> itemWidget
            DrawerItem.SETTINGS -> itemSettings
            DrawerItem.RATE -> itemRate
            DrawerItem.FEEDBACK -> itemFeedback
            DrawerItem.INFO -> itemInfo
            DrawerItem.PRIVACY -> itemPrivacy
        }
    }

    fun setActiveItem(drawerItem: DrawerItem) {
        val iDrawerItemClicked = getIDrawerItemFromDrawerItem(drawerItem)
        this.onItemClicked(iDrawerItemClicked)
    }

    fun findActiveItem(supportFragmentManager: FragmentManager) {
        DrawerItem.values().forEach { item ->
            if (supportFragmentManager.findFragmentByTag(item.toString()) != null) {
                navigationDrawer.setSelection(getIDrawerItemFromDrawerItem(item))
            }
        }
    }

    private fun onItemClicked(drawerItem: IDrawerItem<*, *>): Boolean {
        var fragmentToShowNext: Fragment? = null

        when (drawerItem) {
            itemDailyVerses -> fragmentToShowNext = dailyVersesOverviewFragment
                    ?: DailyVersesOverviewFragment.newInstance()
            itemMonthlyVerses -> fragmentToShowNext = monthlyVersesOverviewFragment
                    ?: MonthlyVersesOverviewFragment.newInstance()
            itemFavourite -> fragmentToShowNext = favouriteVersesOverviewFragment
                    ?: FavouriteVersesOverviewFragment.newInstance()
            itemWidget -> fragmentToShowNext = widgetsOverviewFragment
                    ?: WidgetsOverviewFragment.newInstance()
            itemSettings -> activity.startActivity(Intent(activity, SettingsActivity::class.java))
            itemRate -> Open.appInPlayStore(activity)
            itemFeedback -> Open.sendMailToProgrammer(activity)
            itemInfo -> fragmentToShowNext = infoFragment ?: InfoFragment.newInstance()
            itemPrivacy -> Open.privacyWebsite(activity)
        }

        if (fragmentToShowNext != null) {
            fragmentChangeListener.invoke(fragmentToShowNext, getTagForFragment(fragmentToShowNext)!!)
            navigationDrawer.closeDrawer()
            return true
        }

        return false
    }

    private fun getTagForFragment(fragment: Fragment): String? {
        when (fragment) {
            is DailyVersesOverviewFragment -> return DrawerItem.DAILY_OVERVIEW.toString()
            is MonthlyVersesOverviewFragment -> return DrawerItem.MONTHLY_OVERVIEW.toString()
            is FavouriteVersesOverviewFragment -> return DrawerItem.FAVOURITE_OVERVIEW.toString()
            is WidgetsOverviewFragment -> return DrawerItem.WIDGET_OVERVIEW.toString()
            is InfoFragment -> return DrawerItem.INFO.toString()
        }

        return null
    }

    enum class DrawerItem {
        DAILY_OVERVIEW,
        MONTHLY_OVERVIEW,
        FAVOURITE_OVERVIEW,
        WIDGET_OVERVIEW,
        SETTINGS,
        RATE,
        FEEDBACK,
        INFO,
        PRIVACY
    }
}
