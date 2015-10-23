package org.oporaua.localelections.accidents;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import org.oporaua.localelections.MySpinnerAdapter;
import org.oporaua.localelections.NewAccidentActivity;
import org.oporaua.localelections.R;
import org.oporaua.localelections.data.OporaContract.AccidentEntry;
import org.oporaua.localelections.data.OporaContract.RegionEntry;
import org.oporaua.localelections.interfaces.SetToolbarListener;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccidentsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final int ACCIDENTS_LOADER_ID = 0;
    private static final int REGIONS_LOADER_ID = 1;
    private AccidentsAdapter mAccidentsAdapter;
    private MySpinnerAdapter mSpinnerAdapter;

    private Spinner mSpinner;
    private long mRegionId;


    private static final String[] ACCIDENTS_COLUMNS = {
            AccidentEntry._ID,
            AccidentEntry.COLUMN_TITLE,
            AccidentEntry.COLUMN_DATE_TEXT,
            AccidentEntry.COLUMN_SOURCE,
            AccidentEntry.COLUMN_EVIDENCE_URL
    };

    public final static int COL_ACCIDENT_ID = 0;
    public final static int COL_ACCIDENT_TITLE = 1;
    public final static int COL_ACCIDENT_DATE = 2;
    public final static int COL_ACCIDENT_SOURCE = 3;
    public final static int COL_ACCIDENT_EVIDENCE_URL = 4;

    public static AccidentsListFragment newInstance() {
        return new AccidentsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accidents_list, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() instanceof SetToolbarListener) {
            Toolbar toolbar = ButterKnife.findById(view, R.id.filter_toolbar);
            ((SetToolbarListener) getActivity()).onSetToolbar(toolbar);
            mSpinner = ButterKnife.findById(toolbar, R.id.spinner_filter);
        }
        mAccidentsAdapter = new AccidentsAdapter(getActivity());
        mSpinnerAdapter = new MySpinnerAdapter(getActivity(), R.layout.spinner_dropdown_item, null,
                new String[]{RegionEntry.COLUMN_TITLE},
                new int[]{android.R.id.text1},
                0);
        setListAdapter(mAccidentsAdapter);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(REGIONS_LOADER_ID, null, this);
        getLoaderManager().initLoader(ACCIDENTS_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder;
        switch (id) {
            case 0:
                sortOrder = "date (" + AccidentEntry.COLUMN_DATE_TEXT + ") DESC";
                String selection = null;
                String[] selectionArgs = null;
                if (mRegionId != -1) {
                    selection = AccidentEntry.COLUMN_REGION_ID + " = ?";
                    selectionArgs = new String[]{Long.toString(mRegionId)};
                }
                return new CursorLoader(
                        getActivity(),
                        AccidentEntry.CONTENT_URI,
                        ACCIDENTS_COLUMNS,
                        selection,
                        selectionArgs,
                        sortOrder
                );
            case 1:
                sortOrder = RegionEntry.COLUMN_TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(
                        getActivity(),
                        RegionEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        sortOrder
                );
            default:
                throw new UnsupportedOperationException("Unknown loader");
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case ACCIDENTS_LOADER_ID:
                mAccidentsAdapter.swapCursor(data);
                break;
            case REGIONS_LOADER_ID:
                mSpinnerAdapter.swapCursor(data);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAccidentsAdapter.swapCursor(null);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            startActivity(new Intent(getActivity(), NewAccidentActivity.class));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getSelectedItem();
        mRegionId = cursor.getLong(0);
        getLoaderManager().restartLoader(ACCIDENTS_LOADER_ID, null, this);
        Log.d("log", Long.toString(cursor.getLong(0)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @OnClick(R.id.fab)
    void addNewAccident() {
        Toast.makeText(getActivity(), "New One", Toast.LENGTH_SHORT).show();
    }

}
