package org.oporaua.localelections.accidents;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.oporaua.localelections.R;
import org.oporaua.localelections.data.OporaContract.AccidentEntry;
import org.oporaua.localelections.interfaces.SetToolbarListener;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccidentsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ACCIDENTS_LOADER_ID = 100;
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
        ButterKnife.bind(this, view);
        mAccidentsAdapter = new AccidentsAdapter(getActivity());
        setListAdapter(mAccidentsAdapter);
        if (getActivity() instanceof SetToolbarListener) {
            Toolbar toolbar = ButterKnife.findById(view, R.id.app_toolbar);
            ((SetToolbarListener) getActivity()).onSetToolbar(toolbar);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ACCIDENTS_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = "date (" + AccidentEntry.COLUMN_DATE_TEXT + ") DESC";
        return new CursorLoader(
                getActivity(),
                AccidentEntry.CONTENT_URI,
                ACCIDENTS_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAccidentsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAccidentsAdapter.swapCursor(null);
    }

    @OnClick(R.id.fab)
    void addNewAccident() {
        Toast.makeText(getActivity(), "New One", Toast.LENGTH_SHORT).show();
    }

}
