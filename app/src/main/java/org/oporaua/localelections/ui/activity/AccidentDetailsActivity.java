package org.oporaua.localelections.ui.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.oporaua.localelections.R;
import org.oporaua.localelections.data.OporaContract.AccidentEntry;
import org.oporaua.localelections.data.OporaContract.ElectionTypeEntry;
import org.oporaua.localelections.data.OporaContract.LocalityEntry;
import org.oporaua.localelections.data.OporaContract.PartyEntry;
import org.oporaua.localelections.data.OporaContract.RegionEntry;

public class AccidentDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String ACCIDENT_ID_TAG = "accident_id";
    private long mId;

    private static final String[] ACCIDENTS_COLUMNS = {
            AccidentEntry.TABLE_NAME + "." + AccidentEntry._ID,
            AccidentEntry.COLUMN_DATE_TEXT,
            AccidentEntry.TABLE_NAME + "." + AccidentEntry.COLUMN_TITLE,
            AccidentEntry.COLUMN_SOURCE,
            AccidentEntry.COLUMN_EVIDENCE_URL,
            AccidentEntry.COLUMN_LAT,
            AccidentEntry.COLUMN_LNG,
            RegionEntry.TABLE_NAME + "." + RegionEntry.COLUMN_TITLE,
            LocalityEntry.TABLE_NAME + "." + LocalityEntry.COLUMN_TITLE,
            ElectionTypeEntry.TABLE_NAME + "." + ElectionTypeEntry.COLUMN_TITLE,
            RegionEntry.TABLE_NAME + "." + PartyEntry.COLUMN_TITLE
    };

    private static final int COL_ACCIDENT_ID = 0;
    private static final int COL_ACCIDENT_DATE = 1;
    private static final int COL_ACCIDENT_TITLE = 2;
    private static final int COL_ACCIDENT_SOURCE = 3;
    private static final int COL_ACCIDENT_EVIDENCE_URL = 4;
    private static final int COL_ACCIDENT_LAT = 5;
    private static final int COL_ACCIDENT_LNG = 6;
    private static final int COL_ACCIDENT_REGION = 7;
    private static final int COL_ACCIDENT_LOCALITY = 8;
    private static final int COL_ACCIDENT_ELECTIONS_TYPE = 8;
    private static final int COL_ACCIDENT_OFFENDER_PARTY = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_details);
        mId = getIntent().getLongExtra(ACCIDENT_ID_TAG, -1);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri accidentWithDataUri = AccidentEntry.buildAccidentUri(mId);
        return new CursorLoader(
                this,
                accidentWithDataUri,
                ACCIDENTS_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String title = data.getString(COL_ACCIDENT_TITLE);
            Log.d("log", title);
            String party = data.getString(COL_ACCIDENT_OFFENDER_PARTY);
            Log.d("log", party);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
