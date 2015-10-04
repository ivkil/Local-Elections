package org.oporaua.localelections.violations;


import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import org.oporaua.localelections.R;
import org.oporaua.localelections.violations.model.ViolationChild;
import org.oporaua.localelections.violations.model.ViolationParent;

import java.util.ArrayList;
import java.util.List;

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
        mExpandableAdapter = new ViolationExpandableAdapter(getActivity(), getItemList());

        if (savedInstanceState != null) {
            mExpandableAdapter.onRestoreInstanceState(savedInstanceState);
        }

        mViolationsRecyclerView.setAdapter(mExpandableAdapter);

        return view;
    }

    @NonNull
    private List<ParentListItem> getItemList() {
        Resources res = getResources();

        TypedArray childViolationsNames = res
                .obtainTypedArray(R.array.violation_child_names_arrays);
        TypedArray childViolationsSources = res
                .obtainTypedArray(R.array.violation_child_source_arrays);

        String[] parentViolationNames = res.getStringArray(R.array.violation_parent_names);

        List<ParentListItem> violationParents = new ArrayList<>(parentViolationNames.length);

        for (int i = 0; i < parentViolationNames.length; i++) {
            int childViolationsNameId = childViolationsNames.getResourceId(i, -1);
            int childViolationsSourceId = childViolationsSources.getResourceId(i, -1);

            List<ViolationChild> childList = new ArrayList<>();

            String[] names = res.getStringArray(childViolationsNameId);
            String[] sources = res.getStringArray(childViolationsSourceId);

            for (int j = 0; j < names.length; j++) {
                childList.add(new ViolationChild(names[j], sources[j]));
            }

            violationParents.add(new ViolationParent(parentViolationNames[i], childList));
        }

        childViolationsNames.recycle();
        childViolationsSources.recycle();

        return violationParents;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mExpandableAdapter.onSaveInstanceState(outState);
    }


}
