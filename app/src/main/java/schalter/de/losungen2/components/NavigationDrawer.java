package schalter.de.losungen2.components;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import schalter.de.losungen2.R;
import schalter.de.losungen2.fragments.DailyVersesOverviewFragment;
import schalter.de.losungen2.fragments.FavouriteVersesOverviewFragment;
import schalter.de.losungen2.fragments.InfoFragment;
import schalter.de.losungen2.fragments.MonthlyVersesOverviewFragment;
import schalter.de.losungen2.fragments.WidgetsOverviewFragment;
import schalter.de.losungen2.settings.SettingsActivity;
import schalter.de.losungen2.utils.Open;

public class NavigationDrawer {

    private Activity activity;
    private Drawer navigationDrawer;
    private FragmentChangeListener fragmentChangeListener;

    // Fragments
    private Fragment actualFragment;
    private DailyVersesOverviewFragment dailyVersesOverviewFragment;
    private MonthlyVersesOverviewFragment monthlyVersesOverviewFragment;
    private FavouriteVersesOverviewFragment favouriteVersesOverviewFragment;
    private WidgetsOverviewFragment widgetsOverviewFragment;
    private InfoFragment infoFragment;

    // Items
    private PrimaryDrawerItem itemDailyVerses;
    private PrimaryDrawerItem itemMonthlyVerses;
    private PrimaryDrawerItem itemFavourite;
    private PrimaryDrawerItem itemWidget;
    private PrimaryDrawerItem itemSettings;
    private PrimaryDrawerItem itemRate;
    private PrimaryDrawerItem itemFeedback;
    private PrimaryDrawerItem itemInfo;
    private PrimaryDrawerItem itemPrivacy;

    public NavigationDrawer(Activity activity, @NonNull FragmentChangeListener fragmentChangeListener) {
        this.activity = activity;
        this.fragmentChangeListener = fragmentChangeListener;
    }

    public Drawer initAndShow(Toolbar toolbar) {
        itemDailyVerses = new PrimaryDrawerItem().withName(R.string.losungen)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_event_note);
        itemMonthlyVerses = new PrimaryDrawerItem().withName(R.string.monthly_verses)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_event_note);
        itemFavourite = new PrimaryDrawerItem().withName(R.string.favorite_verses)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_favorite);
        itemWidget = new PrimaryDrawerItem().withName(R.string.configure_widgets)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_color_lens);
        itemSettings = new PrimaryDrawerItem().withName(R.string.settings)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_settings)
                .withSelectable(false);
        itemRate = new PrimaryDrawerItem().withName(R.string.rate)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_star)
                .withSelectable(false);
        itemFeedback = new PrimaryDrawerItem().withName(R.string.send_feedback_bug)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_email)
                .withSelectable(false);
        itemInfo = new PrimaryDrawerItem().withName(R.string.info_help)
                .withIcon(R.drawable.ic_action_info)
                .withIconTintingEnabled(true);
        itemPrivacy = new PrimaryDrawerItem().withName(R.string.privacy_policy)
                .withIcon(R.drawable.ic_action_notes)
                .withIconTintingEnabled(true)
                .withSelectable(false);

        navigationDrawer = new DrawerBuilder()
                .withActivity(activity)
                .withTranslucentStatusBar(true)
                .withToolbar(toolbar)
                .addDrawerItems(
                        itemDailyVerses,
                        itemMonthlyVerses,
                        itemFavourite,
                        itemWidget,
                        itemSettings,
                        new DividerDrawerItem(),
                        itemRate,
                        itemFeedback,
                        itemInfo,
                        itemPrivacy
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return NavigationDrawer.this.onItemClicked(drawerItem);
                    }
                })
                .build();

        return navigationDrawer;
    }

    private boolean onItemClicked(IDrawerItem drawerItem) {
        Fragment fragmentToShowNext = null;

        if (drawerItem.equals(itemDailyVerses)) {
            if (dailyVersesOverviewFragment == null) {
                dailyVersesOverviewFragment = DailyVersesOverviewFragment.newInstance();
            }
            fragmentToShowNext = dailyVersesOverviewFragment;
        } else if (drawerItem.equals(itemMonthlyVerses)) {
            if (monthlyVersesOverviewFragment == null) {
                monthlyVersesOverviewFragment = MonthlyVersesOverviewFragment.newInstance();
            }
            fragmentToShowNext = monthlyVersesOverviewFragment;
        } else if (drawerItem.equals(itemFavourite)) {
            if (favouriteVersesOverviewFragment == null) {
                favouriteVersesOverviewFragment = FavouriteVersesOverviewFragment.newInstance();
            }
            fragmentToShowNext = favouriteVersesOverviewFragment;
        } else if (drawerItem.equals(itemWidget)) {
            if (widgetsOverviewFragment == null) {
                widgetsOverviewFragment = WidgetsOverviewFragment.newInstance();
            }
            fragmentToShowNext = widgetsOverviewFragment;
        } else if (drawerItem.equals(itemSettings)) {
            activity.startActivity(new Intent(activity, SettingsActivity.class));
        } else if (drawerItem.equals(itemRate)) {
            Open.appInPlayStore(activity);
        } else if (drawerItem.equals(itemFeedback)) {
            Open.sendMailToProgrammer(activity);
        } else if (drawerItem.equals(itemInfo)) {
            if (infoFragment == null) {
                infoFragment = InfoFragment.newInstance();
            }
            fragmentToShowNext = infoFragment;
        } else if (drawerItem.equals(itemPrivacy)) {
            Open.privacyWebsite(activity);
        }

        if (fragmentToShowNext != null) {
            fragmentChangeListener.onChangeFragment(actualFragment, fragmentToShowNext);
            actualFragment = fragmentToShowNext;

            navigationDrawer.closeDrawer();
            return true;
        }

        return false;
    }

    public interface FragmentChangeListener {
        void onChangeFragment(Fragment previousFragment, Fragment nextFragment);
    }
}
