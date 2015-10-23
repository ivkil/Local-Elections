package org.oporaua.localelections.app;

import android.app.Application;

import org.oporaua.localelections.sync.OporaSyncAdapter;
import org.oporaua.localelections.util.AppPrefs;

public class OpirApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppPrefs.initialize(this);
        OporaSyncAdapter.initializeSyncAdapter(this);
    }

}
