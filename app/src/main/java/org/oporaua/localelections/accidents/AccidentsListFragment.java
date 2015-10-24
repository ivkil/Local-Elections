package org.oporaua.localelections.accidents;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import org.oporaua.localelections.FilterSpinnerAdapter;
import org.oporaua.localelections.R;
import org.oporaua.localelections.data.OporaContract.AccidentEntry;
import org.oporaua.localelections.data.OporaContract.RegionEntry;
import org.oporaua.localelections.interfaces.SetToolbarListener;
import org.oporaua.localelections.ui.activity.NewAccidentActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccidentsListFragment extends ListFragment implements LoaderCallbacks<Cursor>,
        OnItemSelectedListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final int ACCIDENTS_LOADER_ID = 21;
    private static final int REGIONS_LOADER_ID = 22;

    private static final String QUERY_TAG = "query";
    private static final long ALL_REGIONS_ID = -1;

    private AccidentsAdapter mAccidentsAdapter;
    private FilterSpinnerAdapter mSpinnerAdapter;

    private long mRegionId;
    private String mQuery = "";

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

    private SearchView mSearchView;

    public static AccidentsListFragment newInstance() {
        return new AccidentsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null && savedInstanceState.containsKey(QUERY_TAG)) {
            mQuery = savedInstanceState.getString(QUERY_TAG);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accidents_list, container, false);
        ButterKnife.bind(this, view);

        Toolbar toolbar = ButterKnife.findById(view, R.id.filter_toolbar);
        if (getActivity() instanceof SetToolbarListener) {
            ((SetToolbarListener) getActivity()).onSetToolbar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Spinner mSpinner = ButterKnife.findById(toolbar, R.id.spinner_filter);

        mAccidentsAdapter = new AccidentsAdapter(getActivity());
        mSpinnerAdapter = new FilterSpinnerAdapter(getActivity(), R.layout.spinner_dropdown_item, null,
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
            case ACCIDENTS_LOADER_ID:
                sortOrder = "date (" + AccidentEntry.COLUMN_DATE_TEXT + ") DESC";
                String selection = AccidentEntry.COLUMN_SOURCE + " LIKE '%" + mQuery + "%'";
                if (mRegionId != ALL_REGIONS_ID) {
                    selection += " AND " + AccidentEntry.COLUMN_REGION_ID + " = '" + Long.toString(mRegionId) + "'";
                }
                return new CursorLoader(
                        getActivity(),
                        AccidentEntry.CONTENT_URI,
                        ACCIDENTS_COLUMNS,
                        selection,
                        null,
                        sortOrder
                );
            case REGIONS_LOADER_ID:
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getSelectedItem();
        mRegionId = cursor.getLong(0);
        getLoaderManager().restartLoader(ACCIDENTS_LOADER_ID, null, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @OnClick(R.id.fab)
    void addNewAccident() {
        startActivity(new Intent(getActivity(), NewAccidentActivity.class));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQuery(mQuery, false);
        mSearchView.clearFocus();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mQuery = query;
        getLoaderManager().restartLoader(ACCIDENTS_LOADER_ID, null, this);
        return false;
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(QUERY_TAG, mQuery);
        super.onSaveInstanceState(outState);
    }
}
