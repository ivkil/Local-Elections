package org.oporaua.localelections.accidents;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.google.android.gms.maps.model.LatLng;

import org.oporaua.localelections.data.OporaContract.AccidentEntry;

import java.util.ArrayList;
import java.util.List;

public class AccidentsLoader extends AsyncTaskLoader<List<Accident>> {

    private List<Accident> mAccidents;
    private AccidentsContentObserver mContentObserver;

    private static final String ACCIDENTS_COLUMNS[] = {
            AccidentEntry._ID,
            AccidentEntry.COLUMN_TITLE,
            AccidentEntry.COLUMN_LAT,
            AccidentEntry.COLUMN_LNG
    };

    private final static int COL_ACCIDENT_ID = 0;
    private final static int COL_ACCIDENT_TITLE = 1;
    private final static int COL_ACCIDENT_LAT = 2;
    private final static int COL_ACCIDENT_LNG = 3;

    public AccidentsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Accident> loadInBackground() {
        Cursor data = getContext().getContentResolver().query(
                AccidentEntry.CONTENT_URI,
                ACCIDENTS_COLUMNS,
                null,
                null,
                null
        );
        List<Accident> accidents = new ArrayList<>(data.getCount());
        try {
            if (data.moveToFirst()) {
                do {
                    Accident accident = new Accident();

                    long id = data.getLong(COL_ACCIDENT_ID);
                    accident.setId(id);

                    String title = data.getString(COL_ACCIDENT_TITLE);
                    accident.setTitle(title);

                    double lat = data.getDouble(COL_ACCIDENT_LAT);
                    double lng = data.getDouble(COL_ACCIDENT_LNG);
                    accident.setPosition(new LatLng(lat, lng));

                    accidents.add(accident);
                } while (data.moveToNext());
            }
        } finally {
            data.close();
        }

        return accidents;
    }

    @Override
    public void deliverResult(List<Accident> accidents) {
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


    public class AccidentsContentObserver extends ContentObserver {

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