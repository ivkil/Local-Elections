package org.oporaua.localelections.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.oporaua.localelections.NewAccidentActivity;
import org.oporaua.localelections.R;
import org.oporaua.localelections.accidents.AccidentsAdapter;
import org.oporaua.localelections.data.OporaContract.AccidentEntry;
import org.oporaua.localelections.data.OporaContract.PartyEntry;

public class AccidentsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private AccidentsAdapter mAccidentsAdapter;

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
        mAccidentsAdapter = new AccidentsAdapter(getActivity(), null, 0);
        view.findViewById(R.id.fab).setOnClickListener(this);
        setListAdapter(mAccidentsAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                String sortOrder = "date (" + AccidentEntry.COLUMN_DATE_TEXT + ") DESC";
                return new CursorLoader(
                        getActivity(),
                        AccidentEntry.CONTENT_URI,
                        ACCIDENTS_COLUMNS,
                        null,
                        null,
                        sortOrder
                );
            case 1:
                return new CursorLoader(
                        getActivity(),
                        PartyEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            default:
                throw new UnsupportedOperationException("Unknown loader");
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case 0:
                mAccidentsAdapter.swapCursor(data);
                break;
            case 1:
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
}
