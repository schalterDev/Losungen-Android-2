package schalter.de.losungen2.components;

import android.app.Activity;
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

public class NavigationDrawer {

    private Activity activity;
    private Drawer navigationDrawer;

    public NavigationDrawer(Activity activity) {
        this.activity = activity;
    }

    public Drawer initAndShow(Toolbar toolbar, @NonNull FragmentChangeListener fragmentChangeListener) {
        final PrimaryDrawerItem itemDailyVerses = new PrimaryDrawerItem().withName(R.string.losungen)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_event_note);
        final PrimaryDrawerItem itemMonthlyVerses = new PrimaryDrawerItem().withName(R.string.monthly_verses)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_event_note);
        final PrimaryDrawerItem itemFavourite = new PrimaryDrawerItem().withName(R.string.favorite_verses)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_favorite);
        final PrimaryDrawerItem itemWidget = new PrimaryDrawerItem().withName(R.string.configure_widgets)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_color_lens);
        final PrimaryDrawerItem itemSettings = new PrimaryDrawerItem().withName(R.string.settings)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_settings)
                .withSelectable(false);
        final PrimaryDrawerItem itemRate = new PrimaryDrawerItem().withName(R.string.rate)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_star)
                .withSelectable(false);
        final PrimaryDrawerItem itemFeedback = new PrimaryDrawerItem().withName(R.string.send_feedback_bug)
                .withIconTintingEnabled(true)
                .withIcon(R.drawable.ic_action_email)
                .withSelectable(false);
        final PrimaryDrawerItem itemInfo = new PrimaryDrawerItem().withName(R.string.info_help)
                .withIcon(R.drawable.ic_action_info)
                .withIconTintingEnabled(true);
        final PrimaryDrawerItem itemPrivacy = new PrimaryDrawerItem().withName(R.string.privacy_policy)
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
                        if (drawerItem.equals(itemDailyVerses)) {

                        } else if(drawerItem.equals(itemMonthlyVerses)) {

                        } else if(drawerItem.equals(itemFavourite)) {

                        } else if(drawerItem.equals(itemWidget)) {

                        } else if(drawerItem.equals(itemSettings)) {

                        } else if(drawerItem.equals(itemRate)) {

                        } else if(drawerItem.equals(itemFeedback)) {

                        } else if(drawerItem.equals(itemInfo)) {

                        } else if(drawerItem.equals(itemPrivacy)) {

                        }

                        navigationDrawer.closeDrawer();
                        return true;
                    }
                })
                .build();

        return navigationDrawer;
    }

    public interface FragmentChangeListener {
        void onChangeFragment(Fragment previousFragment, Fragment nextFragment);
    }
}
