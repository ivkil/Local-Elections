package org.oporaua.localelections.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.oporaua.localelections.R;
import org.oporaua.localelections.accidents.Accident;
import org.oporaua.localelections.accidents.AccidentDetailsActivity;
import org.oporaua.localelections.accidents.AccidentsService;
import org.oporaua.localelections.data.AccidentSubtype;
import org.oporaua.localelections.data.AccidentType;
import org.oporaua.localelections.data.ElectionsType;
import org.oporaua.localelections.data.Locality;
import org.oporaua.localelections.data.OporaContract;
import org.oporaua.localelections.data.OporaContract.AccidentEntry;
import org.oporaua.localelections.data.OporaContract.AccidentSubtypeEntry;
import org.oporaua.localelections.data.OporaContract.AccidentTypeEntry;
import org.oporaua.localelections.data.OporaContract.ElectionTypeEntry;
import org.oporaua.localelections.data.OporaContract.LocalityEntry;
import org.oporaua.localelections.data.OporaContract.PartyEntry;
import org.oporaua.localelections.data.OporaContract.RegionEntry;
import org.oporaua.localelections.data.Party;
import org.oporaua.localelections.data.Region;
import org.oporaua.localelections.util.PrefUtil;
import org.oporaua.localelections.util.Constants;

import java.io.IOException;
import java.util.Vector;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class OporaSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = OporaSyncAdapter.class.getSimpleName();
    public static final String SYNC_MODE = "sync_mode";

    public final static int SYNC_ACCIDENT_TYPES = 100;
    public final static int SYNC_ACCIDENT_SUBTYPES = 200;
    public final static int SYNC_REGIONS = 300;
    public final static int SYNC_LOCALITIES = 400;
    public final static int SYNC_PARTIES = 500;
    public final static int SYNC_ELECTIONS_TYPES = 600;
    public final static int SYNC_ACCIDENTS = 700;
    public final static int SYNC_ACCIDENT = 800;

    private AccidentsService mAccidentsService;


    public OporaSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ACCIDENTS_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mAccidentsService = retrofit.create(AccidentsService.class);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if (extras == null) return;
        if (extras.containsKey(SYNC_MODE)) {
            int mode = extras.getInt(SYNC_MODE);
            switch (mode) {
                case SYNC_ACCIDENT_TYPES:
                    syncAccidentTypes();
                    break;
                case SYNC_ACCIDENT_SUBTYPES:
                    syncAccidentsSubtypes();
                    break;
                case SYNC_REGIONS:
                    syncRegions();
                    break;
                case SYNC_LOCALITIES:
                    syncLocalities();
                    break;
                case SYNC_PARTIES:
                    syncParties();
                    break;
                case SYNC_ELECTIONS_TYPES:
                    syncElectionsTypes();
                    break;
                case SYNC_ACCIDENTS:
//                    syncAccidents();
                    break;
                case SYNC_ACCIDENT:
                    long id = extras.getLong(AccidentDetailsActivity.ARG_ACCIDENT_ID);
                    syncAccident(id);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown sync mode");
            }
        }
    }

    private void syncAccident(long id) {
        Call<Accident> call = mAccidentsService.getAccident(id);
        try {
            Response<Accident> response = call.execute();
            Accident accident = response.body();
            ContentValues accidentValues = getAccidentValues(accident);
            int count = getContext().getContentResolver().update(
                    AccidentEntry.CONTENT_URI,
                    accidentValues,
                    AccidentEntry._ID + " = ? ",
                    new String[]{String.valueOf(id)});
            if (count == 0) {
                getContext().getContentResolver().insert(AccidentEntry.CONTENT_URI, accidentValues);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void syncElectionsTypes() {
        Call<ElectionsType[]> call = mAccidentsService.getElectionsTypes();
        call.enqueue(new Callback<ElectionsType[]>() {
            @Override
            public void onResponse(Response<ElectionsType[]> response, Retrofit retrofit) {
                ElectionsType[] electionsTypes = response.body();
                Vector<ContentValues> cVVector = new Vector<>(electionsTypes.length);
                for (ElectionsType electionsType : electionsTypes) {
                    ContentValues valuesRegions = new ContentValues();
                    valuesRegions.put(ElectionTypeEntry._ID, electionsType.getId());
                    valuesRegions.put(ElectionTypeEntry.COLUMN_TITLE, electionsType.getTitle());
                    cVVector.add(valuesRegions);
                }
                if (cVVector.size() > 0) {
                    ContentValues[] cVArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cVArray);
                    int count = getContext().getContentResolver().bulkInsert(
                            ElectionTypeEntry.CONTENT_URI,
                            cVArray
                    );
                    if (count > 0) {
                        PrefUtil.getInstance().setElectionsTypes(true);
                    }
                    Log.d(LOG_TAG, count + " elections types inserted");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void syncParties() {
        Call<Party[]> call = mAccidentsService.getParties();
        call.enqueue(new Callback<Party[]>() {
            @Override
            public void onResponse(Response<Party[]> response, Retrofit retrofit) {
                Party[] parties = response.body();
                Vector<ContentValues> cVVector = new Vector<>(parties.length);
                for (Party party : parties) {
                    ContentValues valuesRegions = new ContentValues();
                    valuesRegions.put(PartyEntry._ID, party.getId());
                    valuesRegions.put(PartyEntry.COLUMN_TITLE, party.getTitle());
                    cVVector.add(valuesRegions);
                }
                if (cVVector.size() > 0) {
                    ContentValues[] cVArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cVArray);
                    int count = getContext().getContentResolver().bulkInsert(
                            PartyEntry.CONTENT_URI,
                            cVArray
                    );
                    if (count > 0) {
                        PrefUtil.getInstance().setParties(true);
                    }
                    Log.d(LOG_TAG, count + " parties inserted");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void syncLocalities() {
        Call<Locality[]> call = mAccidentsService.getLocalities();
        call.enqueue(new Callback<Locality[]>() {
            @Override
            public void onResponse(Response<Locality[]> response, Retrofit retrofit) {
                Locality[] localities = response.body();
                Vector<ContentValues> cVVector = new Vector<>(localities.length);
                for (Locality locality : localities) {
                    ContentValues valuesLocalities = new ContentValues();
                    valuesLocalities.put(LocalityEntry._ID, locality.getId());
                    valuesLocalities.put(LocalityEntry.COLUMN_TITLE, locality.getTitle());
                    valuesLocalities.put(LocalityEntry.COLUMN_REGION_ID, locality.getRegionId());
                    valuesLocalities.put(LocalityEntry.COLUMN_DISTRICT, locality.getDistrict());
                    cVVector.add(valuesLocalities);
                }
                if (cVVector.size() > 0) {
                    ContentValues[] cVArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cVArray);
                    int count = getContext().getContentResolver().bulkInsert(
                            LocalityEntry.CONTENT_URI,
                            cVArray
                    );
                    if (count > 0) {
                        PrefUtil.getInstance().setLocalities(true);
                    }
                    Log.d(LOG_TAG, count + " localities inserted");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void syncRegions() {
        Call<Region[]> call = mAccidentsService.getRegions();
        call.enqueue(new Callback<Region[]>() {
            @Override
            public void onResponse(Response<Region[]> response, Retrofit retrofit) {
                Region[] regions = response.body();
                Vector<ContentValues> cVVector = new Vector<>(regions.length);
                ContentValues values = new ContentValues();
                values.put(RegionEntry._ID, -1);
                values.put(RegionEntry.COLUMN_TITLE, " Усі регіони");
                cVVector.add(values);
                for (Region region : regions) {
                    ContentValues valuesRegions = new ContentValues();
                    valuesRegions.put(RegionEntry._ID, region.getId());
                    valuesRegions.put(RegionEntry.COLUMN_TITLE, region.getTitle());
                    cVVector.add(valuesRegions);
                }
                if (cVVector.size() > 0) {
                    ContentValues[] cVArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cVArray);
                    int count = getContext().getContentResolver().bulkInsert(
                            RegionEntry.CONTENT_URI,
                            cVArray
                    );
                    Log.d(LOG_TAG, count + " regions inserted");
                    if (count > 0) {
                        PrefUtil.getInstance().setRegions(true);
                    }
                }

            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void syncAccidentsSubtypes() {
        Call<AccidentSubtype[]> call = mAccidentsService.getAccidentSubtypes();
        call.enqueue(new Callback<AccidentSubtype[]>() {
            @Override
            public void onResponse(Response<AccidentSubtype[]> response, Retrofit retrofit) {
                AccidentSubtype[] accidentSubtypes = response.body();
                Vector<ContentValues> cVVector = new Vector<>(accidentSubtypes.length);
                for (AccidentSubtype accidentSubtype : accidentSubtypes) {
                    ContentValues valuesAccidentsSubtypes = new ContentValues();
                    valuesAccidentsSubtypes.put(AccidentSubtypeEntry._ID, accidentSubtype.getId());
                    valuesAccidentsSubtypes.put(AccidentSubtypeEntry.COLUMN_TITLE, accidentSubtype.getTitle());
                    valuesAccidentsSubtypes.put(AccidentSubtypeEntry.COLUMN_ACCIDENT_TYPE_ID, accidentSubtype.getAccidentTypeId());
                    cVVector.add(valuesAccidentsSubtypes);
                }
                if (cVVector.size() > 0) {
                    ContentValues[] cVArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cVArray);
                    int count = getContext().getContentResolver().bulkInsert(
                            AccidentSubtypeEntry.CONTENT_URI,
                            cVArray
                    );
                    Log.d(LOG_TAG, count + " accidents subtypes inserted");
                    if (count > 0) {
                        PrefUtil.getInstance().setAccidentsSubtypes(true);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void syncAccidentTypes() {
        Call<AccidentType[]> call = mAccidentsService.getAccidentTypes();
        call.enqueue(new Callback<AccidentType[]>() {
            @Override
            public void onResponse(Response<AccidentType[]> response, Retrofit retrofit) {
                AccidentType[] accidentTypes = response.body();
                Vector<ContentValues> cVVector = new Vector<>(accidentTypes.length);
                ContentValues values = new ContentValues();
                values.put(AccidentTypeEntry._ID, -1);
                values.put(AccidentTypeEntry.COLUMN_TITLE, " Усі порушення");
                cVVector.add(values);
                for (AccidentType accidentType : accidentTypes) {
                    ContentValues valuesAccidentsTypes = new ContentValues();
                    valuesAccidentsTypes.put(AccidentTypeEntry._ID, accidentType.getId());
                    valuesAccidentsTypes.put(AccidentTypeEntry.COLUMN_TITLE, accidentType.getTitle());
                    cVVector.add(valuesAccidentsTypes);
                }
                if (cVVector.size() > 0) {
                    ContentValues[] cVArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cVArray);
                    int count = getContext().getContentResolver().bulkInsert(
                            AccidentTypeEntry.CONTENT_URI,
                            cVArray
                    );
                    Log.d(LOG_TAG, count + " accidents types inserted");
                    if (count > 0) {
                        PrefUtil.getInstance().setAccidentsTypes(true);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void syncAccidents() {
        long sinceId = -1;
        final String[] accidentsColumns = new String[]{AccidentEntry._ID};
        final int COL_ACCIDENT_ID = 0;
        String sortOrder = AccidentEntry._ID + " DESC LIMIT 1";
        Cursor cursor = getContext().getContentResolver().query(
                AccidentEntry.CONTENT_URI,
                accidentsColumns,
                null,
                null,
                sortOrder
        );
        if (cursor != null && cursor.moveToFirst()) {
            sinceId = cursor.getLong(COL_ACCIDENT_ID);
            cursor.close();
        }
        Call<Accident[]> call = mAccidentsService.getAccidents(sinceId);
        try {
            Response<Accident[]> response = call.execute();
            Accident[] accidents = response.body();
            Vector<ContentValues> cVVector = new Vector<>(accidents.length);
            for (Accident accident : accidents) {
                if (accident.getId() != 933) { // REMOVE THIS
                    cVVector.add(getAccidentValues(accident));
                }
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
        }

    }

    private void notifyAccident() {
    }


    public static void syncImmediately(Context context, int mode) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(SYNC_MODE, mode);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void syncImmediately(Context context, long id) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(SYNC_MODE, SYNC_ACCIDENT);
        bundle.putLong(AccidentDetailsActivity.ARG_ACCIDENT_ID, id);
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
        }
        return newAccount;
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private static ContentValues getAccidentValues(Accident accident) {
        ContentValues accidentValues = new ContentValues();

        accidentValues.put(AccidentEntry._ID, accident.getId());
        accidentValues.put(AccidentEntry.COLUMN_DATE_TEXT, OporaContract.getDbDateString(accident.getDate()));
        accidentValues.put(AccidentEntry.COLUMN_TITLE, accident.getTitle());
        accidentValues.put(AccidentEntry.COLUMN_SOURCE, accident.getSource());
        accidentValues.put(AccidentEntry.COLUMN_EVIDENCE_URL, accident.getEvidence().getUrl());
        accidentValues.put(AccidentEntry.COLUMN_LAT, accident.getPosition().latitude);
        accidentValues.put(AccidentEntry.COLUMN_LNG, accident.getPosition().longitude);
        accidentValues.put(AccidentEntry.COLUMN_REGION_ID, accident.getRegionId());
        accidentValues.put(AccidentEntry.COLUMN_LOCALITY_ID, accident.getLocalityId());
        accidentValues.put(AccidentEntry.COLUMN_ELECTIONS_ID, accident.getElectionsId());
        accidentValues.put(AccidentEntry.COLUMN_OFFENDER_PARTY_ID, accident.getOffenderPartyId());
        accidentValues.put(AccidentEntry.COLUMN_ACCIDENT_SUBTYPE, accident.getAccidentSubtypeId());

        return accidentValues;
    }

}