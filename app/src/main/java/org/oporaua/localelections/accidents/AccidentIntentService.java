package org.oporaua.localelections.accidents;

import android.app.IntentService;
import android.content.Intent;

import org.oporaua.localelections.data.OporaContract.AccidentEntry;
import org.oporaua.localelections.gcm.MyGcmListenerService;
import org.oporaua.localelections.ui.activity.AccidentDetailsActivity;

public class AccidentIntentService extends IntentService {

    public AccidentIntentService() {
        super(AccidentIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(MyGcmListenerService.ACTION_DELETE_ACCIDENT)) {
            long id = intent.getLongExtra(AccidentDetailsActivity.ACCIDENT_ID_TAG, -1);
            getContentResolver().delete(
                    AccidentEntry.CONTENT_URI,
                    AccidentEntry._ID + " = ? ",
                    new String[]{String.valueOf(id)}
            );
        }
    }
}