package org.oporaua.localelections.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.oporaua.localelections.R;
import org.oporaua.localelections.data.AccidentsContract;

public class AccidentsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAccidentsAdapter;

    public static AccidentsListFragment newInstance() {
        return new AccidentsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAccidentsAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_accident,
                null,
                new String[]{
                        AccidentsContract.AccidentEntry.COLUMN_DATE_TEXT,
                        AccidentsContract.AccidentEntry.COLUMN_SOURCE

                },
                new int[]{
                        R.id.list_item_date_textview,
                        R.id.list_item_source_textview
                }, 0);
        return inflater.inflate(R.layout.fragment_accidents_list, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), AccidentsContract.AccidentEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAccidentsAdapter.swapCursor(data);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAccidentsAdapter.swapCursor(null);
    }
}
