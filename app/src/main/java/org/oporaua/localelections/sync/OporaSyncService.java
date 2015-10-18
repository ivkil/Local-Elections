package org.oporaua.localelections.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OporaSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static OporaSyncAdapter sOporaSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("OporaSyncService", "onCreate - OporaSyncService");
        synchronized (sSyncAdapterLock) {
            if (sOporaSyncAdapter == null) {
                sOporaSyncAdapter = new OporaSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sOporaSyncAdapter.getSyncAdapterBinder();
    }
}