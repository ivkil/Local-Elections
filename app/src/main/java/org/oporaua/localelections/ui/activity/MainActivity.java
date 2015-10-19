package org.oporaua.localelections.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.oporaua.localelections.R;
import org.oporaua.localelections.blanks.BlanksFragment;
import org.oporaua.localelections.gcm.RegistrationIntentService;
import org.oporaua.localelections.interfaces.SetToolbarListener;
import org.oporaua.localelections.ui.fragment.ContactsFragment;
import org.oporaua.localelections.tvk.TvkMembersFragment;
import org.oporaua.localelections.ui.fragment.WebViewFragment;
import org.oporaua.localelections.util.Constants;
import org.oporaua.localelections.violations.ViolationsFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, SetToolbarListener {

    private static final String PREV_MENU_ID_TAG = "prev_menu_id";
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    private final static int DRAWER_ID_LAW = 10;
    private final static int DRAWER_ID_MANUAL = 20;
    private final static int DRAWER_ID_VIOLATIONS = 30;
    private final static int DRAWER_ID_BLANKS = 40;
    private final static int DRAWER_ID_TVK_MEMBERS = 50;
    private final static int DRAWER_ID_CONTACTS = 60;

    private int mPreviousMenuItem = -1;

    @IntDef({DRAWER_ID_LAW, DRAWER_ID_MANUAL, DRAWER_ID_VIOLATIONS, DRAWER_ID_BLANKS, DRAWER_ID_TVK_MEMBERS, DRAWER_ID_CONTACTS})
    @Retention(RetentionPolicy.SOURCE)
    private @interface DrawerId {
    }

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;

    @DrawerId
    private int mCurrentDrawerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mNavigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            replaceFragment(getFragmentByDrawerId(DRAWER_ID_LAW));
            mPreviousMenuItem = R.id.law;
        } else {
            mPreviousMenuItem = savedInstanceState.getInt(PREV_MENU_ID_TAG);
        }
        mNavigationView.getMenu().findItem(mPreviousMenuItem).setCheckable(true);
        mNavigationView.getMenu().findItem(mPreviousMenuItem).setChecked(true);

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
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

        menuItem.setCheckable(true);
        menuItem.setChecked(true);
        if (mPreviousMenuItem != -1) {
            mNavigationView.getMenu().findItem(mPreviousMenuItem).setChecked(false);
        }
        mPreviousMenuItem = menuItem.getItemId();

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
                return WebViewFragment.newInstance(Constants.LAW_PATH, true, false);
            case DRAWER_ID_MANUAL:
                return WebViewFragment.newInstance(Constants.MANUAL_PATH, true, false);
            case DRAWER_ID_VIOLATIONS:
                return ViolationsFragment.newInstance();
            case DRAWER_ID_BLANKS:
                return BlanksFragment.newInstance();
            case DRAWER_ID_TVK_MEMBERS:
                return TvkMembersFragment.newInstance();
            case DRAWER_ID_CONTACTS:
                return ContactsFragment.newInstance();
            default:
                return WebViewFragment.newInstance(Constants.LAW_PATH, true, false);
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
            case R.id.violations:
                return DRAWER_ID_VIOLATIONS;
            case R.id.blanks:
                return DRAWER_ID_BLANKS;
            case R.id.tvkmembers:
                return DRAWER_ID_TVK_MEMBERS;
            case R.id.contacts:
                return DRAWER_ID_CONTACTS;
            default:
                return DRAWER_ID_LAW;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSetToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PREV_MENU_ID_TAG, mPreviousMenuItem);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, Constants.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
