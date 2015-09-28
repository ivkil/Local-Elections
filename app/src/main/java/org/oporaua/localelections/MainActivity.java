package org.oporaua.localelections;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MainActivity extends AppCompatActivity {

    @IntDef({DRAWER_ID_LAW, DRAWER_ID_MANUAL})
    @Retention(RetentionPolicy.SOURCE)

    public @interface DrawerId {
    }

    public final static int DRAWER_ID_LAW = 10;
    public final static int DRAWER_ID_MANUAL = 20;

    private Drawer mDrawerResult;

    private int mCurrentDrawerId = DRAWER_ID_LAW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer(savedInstanceState);
        if (savedInstanceState == null) {
            replaceFragment(getFragmentByDrawerId(mCurrentDrawerId));
        }
    }

    private void initDrawer(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        mDrawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withSavedInstance(savedInstanceState)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(getDrawerItems())
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @SuppressWarnings("ResourceType")
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        int drawerId = iDrawerItem.getIdentifier();
                        if (mCurrentDrawerId != drawerId) {
                            mCurrentDrawerId = drawerId;
                            replaceFragment(getFragmentByDrawerId(drawerId));
                        }
                        return false;
                    }
                })
                .build();
        mDrawerResult.setSelection(mCurrentDrawerId);
    }

    private IDrawerItem[] getDrawerItems() {
        return new IDrawerItem[]{
                new PrimaryDrawerItem().withName(R.string.drawer_law).withIcon(R.mipmap.ic_launcher).withIdentifier(DRAWER_ID_LAW),
                new PrimaryDrawerItem().withName(R.string.drawer_manual).withIcon(R.mipmap.ic_launcher).withIdentifier(DRAWER_ID_MANUAL),
        };
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
        if (mDrawerResult != null && mDrawerResult.isDrawerOpen()) {
            mDrawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
