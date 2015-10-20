package org.oporaua.localelections.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.clustering.ClusterManager;

import org.oporaua.localelections.R;
import org.oporaua.localelections.content.AccidentsLoader;
import org.oporaua.localelections.model.Accident;

import java.util.List;


public class AccidentsMapFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Accident>>, OnMapReadyCallback {

    private ClusterManager<Accident> mClusterManager;


    public static AccidentsMapFragment newInstance() {
        return new AccidentsMapFragment();
    }

    public AccidentsMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accidents_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mClusterManager = new ClusterManager<>(getActivity(), googleMap);
        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<Accident>> onCreateLoader(int id, Bundle args) {
        return new AccidentsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Accident>> loader, List<Accident> data) {
        mClusterManager.clearItems();
        mClusterManager.addItems(data);
        mClusterManager.cluster();
    }

    @Override
    public void onLoaderReset(Loader<List<Accident>> loader) {

    }

}
