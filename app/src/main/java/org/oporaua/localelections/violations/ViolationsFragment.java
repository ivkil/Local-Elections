package org.oporaua.localelections.violations;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.oporaua.localelections.R;
import org.oporaua.localelections.util.GeneralUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViolationsFragment extends Fragment {

    @Bind(R.id.rv_violations)
    RecyclerView mViolationsRecyclerView;
    private ViolationExpandableAdapter mExpandableAdapter;

    public static ViolationsFragment newInstance() {
        return new ViolationsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_violations, container, false);
        ButterKnife.bind(this, view);

        mViolationsRecyclerView.setHasFixedSize(true);
        mViolationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mExpandableAdapter = new ViolationExpandableAdapter(getActivity(), GeneralUtil.getItemList(getActivity()));

        if (savedInstanceState != null) {
            mExpandableAdapter.onRestoreInstanceState(savedInstanceState);
        }

        mViolationsRecyclerView.setAdapter(mExpandableAdapter);

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mExpandableAdapter.onSaveInstanceState(outState);
    }


}
