package org.oporaua.localelections.app;

import android.app.Application;

import org.oporaua.localelections.sync.OporaSyncAdapter;
import org.oporaua.localelections.util.PrefUtil;

public class OpirApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PrefUtil.initialize(this);
        OporaSyncAdapter.initializeSyncAdapter(this);
        loadData();
    }

    private void loadData() {
        OporaSyncAdapter.syncImmediately(this, OporaSyncAdapter.SYNC_ACCIDENTS);
        if (!PrefUtil.isAccidentsTypes()) {
            OporaSyncAdapter.syncImmediately(this, OporaSyncAdapter.SYNC_ACCIDENT_TYPES);
        }
        if (!PrefUtil.isAccidentsSubtypes()) {
            OporaSyncAdapter.syncImmediately(this, OporaSyncAdapter.SYNC_ACCIDENT_SUBTYPES);
        }
        if (!PrefUtil.isRegions()) {
            OporaSyncAdapter.syncImmediately(this, OporaSyncAdapter.SYNC_REGIONS);
        }
        if (!PrefUtil.isLocalities()) {
            OporaSyncAdapter.syncImmediately(this, OporaSyncAdapter.SYNC_LOCALITIES);
        }
        if (!PrefUtil.isParties()) {
            OporaSyncAdapter.syncImmediately(this, OporaSyncAdapter.SYNC_PARTIES);
        }
        if (!PrefUtil.isElectionsTypes()) {
            OporaSyncAdapter.syncImmediately(this, OporaSyncAdapter.SYNC_ELECTIONS_TYPES);
        }
    }

}
