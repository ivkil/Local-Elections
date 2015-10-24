package org.oporaua.localelections.accidents;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.google.android.gms.maps.model.LatLng;

import org.oporaua.localelections.data.OporaContract.AccidentEntry;
import org.oporaua.localelections.data.OporaContract.AccidentSubtypeEntry;

import java.util.ArrayList;
import java.util.List;

public class AccidentsLoader extends AsyncTaskLoader<List<AccidentMap>> {

    private static final long ALL_ACCIDENT_TYPES_ID = -1;

    private List<AccidentMap> mAccidents;
    private AccidentsContentObserver mContentObserver;

    private long mAccidentTypeId;

    private static final String ACCIDENTS_COLUMNS[] = {
            AccidentEntry.TABLE_NAME + "." + AccidentEntry._ID,
            AccidentEntry.TABLE_NAME + "." + AccidentEntry.COLUMN_TITLE,
            AccidentEntry.COLUMN_LAT,
            AccidentEntry.COLUMN_LNG,
            AccidentSubtypeEntry.COLUMN_ACCIDENT_TYPE_ID
    };

    private final static int COL_ACCIDENT_ID = 0;
    private final static int COL_ACCIDENT_TITLE = 1;
    private final static int COL_ACCIDENT_LAT = 2;
    private final static int COL_ACCIDENT_LNG = 3;
    private final static int COL_ACCIDENT_TYPE = 4;

    public AccidentsLoader(Context context, long accidentTypeId) {
        super(context);
        mAccidentTypeId = accidentTypeId;
    }

    @Override
    public List<AccidentMap> loadInBackground() {
        Uri uri = AccidentEntry.buildAccidentWithTypeUri();
        String selection = null;
        String[] selectionArgs = null;
        if (mAccidentTypeId != ALL_ACCIDENT_TYPES_ID) {
            selection = AccidentSubtypeEntry.COLUMN_ACCIDENT_TYPE_ID + " = ?";
            selectionArgs = new String[]{Long.toString(mAccidentTypeId)};
        }
        Cursor data = getContext().getContentResolver().query(
                uri,
                ACCIDENTS_COLUMNS,
                selection,
                selectionArgs,
                null
        );
        if (data == null) return new ArrayList<>();
        List<AccidentMap> accidents = new ArrayList<>(data.getCount());
        try {
            if (data.moveToFirst()) {
                do {
                    AccidentMap accident = new AccidentMap();

                    long id = data.getLong(COL_ACCIDENT_ID);
                    accident.setId(id);

                    String title = data.getString(COL_ACCIDENT_TITLE);
                    accident.setTitle(title);

                    double lat = data.getDouble(COL_ACCIDENT_LAT);
                    double lng = data.getDouble(COL_ACCIDENT_LNG);
                    accident.setPosition(new LatLng(lat, lng));

                    long type = data.getLong(COL_ACCIDENT_TYPE);
                    accident.setAccidentType(type);

                    accidents.add(accident);
                } while (data.moveToNext());
            }
        } finally {
            data.close();
        }

        return accidents;
    }

    @Override
    public void deliverResult(List<AccidentMap> accidents) {
        mAccidents = accidents;
        if (isStarted()) {
            super.deliverResult(accidents);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mAccidents != null) {
            deliverResult(mAccidents);
        }

        if (mContentObserver == null) {
            mContentObserver = new AccidentsContentObserver(this);
        }

        if (takeContentChanged() || mAccidents == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if (mAccidents != null) {
            mAccidents = null;
        }

        if (mContentObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(mContentObserver);
            mContentObserver = null;
        }
    }


    private class AccidentsContentObserver extends ContentObserver {

        private AccidentsLoader mLoader;

        public AccidentsContentObserver(AccidentsLoader loader) {
            super(null);
            mLoader = loader;
            mLoader.getContext().getContentResolver().registerContentObserver(
                    AccidentEntry.CONTENT_URI,
                    false,
                    this
            );
        }

        @Override
        public void onChange(boolean selfChange) {
            mLoader.onContentChanged();
        }
    }

}