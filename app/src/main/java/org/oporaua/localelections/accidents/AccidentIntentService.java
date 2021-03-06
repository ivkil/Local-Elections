package org.oporaua.localelections.accidents;

import android.app.IntentService;
import android.content.Intent;

import org.oporaua.localelections.data.OporaContract.AccidentEntry;

public class AccidentIntentService extends IntentService {

    public static final String ACTION_DELETE_ACCIDENT = "ACTION_DELETE_ACCIDENT";

    public AccidentIntentService() {
        super(AccidentIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_DELETE_ACCIDENT)) {
            long id = intent.getLongExtra(AccidentDetailsActivity.ARG_ACCIDENT_ID, -1);
            getContentResolver().delete(AccidentEntry.CONTENT_URI, AccidentEntry._ID + " = ? ",
                    new String[]{String.valueOf(id)}
            );
        }
    }
}