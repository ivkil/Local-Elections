package org.oporaua.localelections;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    private final static int DRAWER_ID_LAW = 10;
    private final static int DRAWER_ID_MANUAL = 20;

    @IntDef({DRAWER_ID_LAW, DRAWER_ID_MANUAL})
    @Retention(RetentionPolicy.SOURCE)
    private @interface DrawerId {
    }

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;

    @DrawerId
    private int mCurrentDrawerId = DRAWER_ID_LAW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();

        mNavigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            replaceFragment(getFragmentByDrawerId(mCurrentDrawerId));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                    mDrawerLayout.openDrawer(GravityCompat.START);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        if (mCurrentDrawerId == getDrawerIdByMenuItem(menuItem)) {
            closeDrawer();
            return false;
        }

        mCurrentDrawerId = getDrawerIdByMenuItem(menuItem);
        replaceFragment(getFragmentByDrawerId(mCurrentDrawerId));
        closeDrawer();
        return true;
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private Fragment getFragmentByDrawerId(@DrawerId int drawerId) {
        switch (drawerId) {
            case DRAWER_ID_LAW:
                return WebViewFragment.newInstance("file:///android_asset/law.htm");
            case DRAWER_ID_MANUAL:
                return WebViewFragment.newInstance("file:///android_asset/law.htm");
            default:
                return WebViewFragment.newInstance("file:///android_asset/law.htm");
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @DrawerId
    private int getDrawerIdByMenuItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.law:
                return DRAWER_ID_LAW;
            case R.id.manual:
                return DRAWER_ID_MANUAL;
            default:
                return DRAWER_ID_LAW;
        }
    }

}
