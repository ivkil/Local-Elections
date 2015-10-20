package org.oporaua.localelections.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.oporaua.localelections.R;
import org.oporaua.localelections.data.AccidentsContract.AccidentEntry;
import org.oporaua.localelections.data.AccidentsProvider;
import org.oporaua.localelections.interfaces.AccidentsService;
import org.oporaua.localelections.model.Accident;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class OporaSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = OporaSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private AccidentsService mAccidentsService;


    public OporaSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dts2015.oporaua.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mAccidentsService = retrofit.create(AccidentsService.class);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        long sinceId = -1;

        Cursor cursor = getContext().getContentResolver().query(
                AccidentEntry.CONTENT_URI,
                null,
                null,
                null,
                AccidentsProvider.sOnlyFirstAccident
        );

        if (cursor != null && cursor.moveToFirst()) {
            sinceId = cursor.getLong(0);
        }

        Call<List<Accident>> call = mAccidentsService.getAccidents(sinceId);
        try {
            Response<List<Accident>> response = call.execute();
            List<Accident> accidents = response.body();
            Vector<ContentValues> cVVector = new Vector<>(accidents.size());
            for (Accident accident : accidents) {
                cVVector.add(getAccidentValues(accident));
            }
            if (cVVector.size() > 0) {
                ContentValues[] cVArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cVArray);
                int count = getContext().getContentResolver().bulkInsert(
                        AccidentEntry.CONTENT_URI,
                        cVArray
                );
                Log.d(LOG_TAG, count + " accidents inserted");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


    }

    private void notifyAccident() {
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        //OporaSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private static ContentValues getAccidentValues(Accident accident) {
        ContentValues accidentValues = new ContentValues();

        accidentValues.put(AccidentEntry._ID, accident.getId());
        accidentValues.put(AccidentEntry.COLUMN_DATE_TEXT, accident.getDate());
        accidentValues.put(AccidentEntry.COLUMN_TITLE, accident.getTitle());
        accidentValues.put(AccidentEntry.COLUMN_SOURCE, accident.getSource());
        accidentValues.put(AccidentEntry.COLUMN_EVIDENCE_URL, accident.getEvidence().getUrl());
        accidentValues.put(AccidentEntry.COLUMN_LAT, accident.getLatitude());
        accidentValues.put(AccidentEntry.COLUMN_LNG, accident.getLongitude());
        accidentValues.put(AccidentEntry.COLUMN_REGION_ID, accident.getRegionId());
        accidentValues.put(AccidentEntry.COLUMN_LOCALITY_ID, accident.getLocalityId());
        accidentValues.put(AccidentEntry.COLUMN_ELECTIONS_ID, accident.getElectionsId());
        accidentValues.put(AccidentEntry.COLUMN_OFFENDER_PARTY_ID, accident.getOffenderPartyId());

        return accidentValues;
    }

}