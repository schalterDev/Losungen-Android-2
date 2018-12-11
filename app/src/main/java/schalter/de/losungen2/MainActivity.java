package schalter.de.losungen2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import schalter.de.losungen2.components.NavigationDrawer;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupNavigationDrawer();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.losungen);
    }

    private void setupNavigationDrawer() {
        NavigationDrawer navigationDrawer = new NavigationDrawer(this, new NavigationDrawer.FragmentChangeListener() {
            @Override
            public void onChangeFragment(Fragment previousFragment, Fragment nextFragment) {
                MainActivity.this.changeFragment(nextFragment);
            }
        });
        navigationDrawer.initAndShow(toolbar);
    }

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_fragment, fragment)
                .commit();
    }
}
