package org.oporaua.localelections.blanks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.oporaua.localelections.R;
import org.oporaua.localelections.interfaces.SetToolbarListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BlanksFragment extends Fragment {

    @Bind(R.id.rv_blanks)
    RecyclerView mBlanksRecyclerView;

    public static BlanksFragment newInstance() {
        return new BlanksFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blanks, container, false);
        ButterKnife.bind(this, view);

        mBlanksRecyclerView.setHasFixedSize(true);
        mBlanksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        BlankAdapter blankAdapter = new BlankAdapter(getActivity());
        mBlanksRecyclerView.setAdapter(blankAdapter);

        if(getActivity() instanceof SetToolbarListener){
            Toolbar toolbar = ButterKnife.findById(view, R.id.app_toolbar);
            ((SetToolbarListener)getActivity()).onSetToolbar(toolbar);
        }

        return view;
    }


}
