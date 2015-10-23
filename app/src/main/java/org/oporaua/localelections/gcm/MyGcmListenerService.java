package org.oporaua.localelections.gcm;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

import org.oporaua.localelections.accidents.AccidentDetailsActivity;
import org.oporaua.localelections.accidents.AccidentIntentService;
import org.oporaua.localelections.sync.OporaSyncAdapter;

public class MyGcmListenerService extends GcmListenerService {

    private static final String ACTION_UPDATE_ACCIDENT = "org.oporaua.localelections.ACTION_UPDATE_ACCIDENT";
    private static final String ACTION_DELETE_ACCIDENT = "org.oporaua.localelections.ACTION_DELETE_ACCIDENT";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (from.startsWith("/topics/accidents")) {
            String action = data.getString("action");
            if (action == null) return;
            long id = Long.valueOf(data.getString("id"));
            switch (action) {
                case ACTION_UPDATE_ACCIDENT:
                    OporaSyncAdapter.syncImmediately(this, id);
                    break;
                case ACTION_DELETE_ACCIDENT:
                    Intent deleteIntent = new Intent(this, AccidentIntentService.class);
                    deleteIntent.setAction(AccidentIntentService.ACTION_DELETE_ACCIDENT);
                    deleteIntent.putExtra(AccidentDetailsActivity.ARG_ACCIDENT_ID, id);
                    startService(deleteIntent);
                    break;
            }
        }
    }

}