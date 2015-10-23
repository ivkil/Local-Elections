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
    }

}
