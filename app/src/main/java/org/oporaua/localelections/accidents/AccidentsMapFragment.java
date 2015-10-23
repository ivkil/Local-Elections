package org.oporaua.localelections.accidents;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

import org.oporaua.localelections.MySpinnerAdapter;
import org.oporaua.localelections.R;
import org.oporaua.localelections.data.OporaContract.AccidentEntry;
import org.oporaua.localelections.data.OporaContract.AccidentTypeEntry;
import org.oporaua.localelections.interfaces.SetToolbarListener;
import org.oporaua.localelections.util.GeneralUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.android.gms.common.api.GoogleApiClient.Builder;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


public class AccidentsMapFragment extends Fragment implements LoaderCallbacks<List<AccidentMap>>,
        OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener,
        ClusterManager.OnClusterItemInfoWindowClickListener<AccidentMap>, AdapterView.OnItemSelectedListener {

    private static final String ALREADY_CONNECTED_TAG = "already_connected";

    private static final int ACCIDENTS_LOADER_ID = 31;
    private static final int ACCIDENTS_TYPES_LOADER_ID = 32;

    private static final float DEFAULT_ZOOM = 13;

    private ClusterManager<AccidentMap> mClusterManager;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;

    private boolean mAlreadyConnected;

    private Spinner mSpinner;
    private MySpinnerAdapter mSpinnerAdapter;

    private long mAccidentTypeId;

    public static AccidentsMapFragment newInstance() {
        return new AccidentsMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accidents_map, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() instanceof SetToolbarListener) {
            Toolbar toolbar = ButterKnife.findById(view, R.id.filter_toolbar);
            ((SetToolbarListener) getActivity()).onSetToolbar(toolbar);
            mSpinner = ButterKnife.findById(toolbar, R.id.spinner_filter);
            mSpinnerAdapter = new MySpinnerAdapter(getActivity(), R.layout.spinner_dropdown_item, null,
                    new String[]{AccidentEntry.COLUMN_TITLE},
                    new int[]{android.R.id.text1},
                    0);
            mSpinner.setAdapter(mSpinnerAdapter);
            mSpinner.setOnItemSelectedListener(this);
            mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(ALREADY_CONNECTED_TAG)) {
            mAlreadyConnected = savedInstanceState.getBoolean(ALREADY_CONNECTED_TAG);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (GeneralUtil.isPlayServicesAvailable(getActivity())) {
            buildGoogleApiClient();
        }

        return view;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (!mAlreadyConnected) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            }
            mAlreadyConnected = true;
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.setMyLocationEnabled(true);

        mClusterManager = new ClusterManager<>(getActivity(), googleMap);
        mClusterManager.setRenderer(new AccidentsClusterRenderer(getActivity(),
                mGoogleMap, mClusterManager));
        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        getLoaderManager().initLoader(ACCIDENTS_LOADER_ID, null, this);
        getLoaderManager().initLoader(ACCIDENTS_TYPES_LOADER_ID, null, new AccidentsTypesLoader());
    }

    @Override
    public Loader<List<AccidentMap>> onCreateLoader(int id, Bundle args) {
        return new AccidentsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<AccidentMap>> loader, List<AccidentMap> data) {
        mClusterManager.clearItems();
        mClusterManager.addItems(data);
        mClusterManager.cluster();
    }

    @Override
    public void onLoaderReset(Loader<List<AccidentMap>> loader) {
    }

    @Override
    public void onClusterItemInfoWindowClick(AccidentMap accident) {
        Intent intent = new Intent(getActivity(), AccidentDetailsActivity.class);
        intent.putExtra(AccidentDetailsActivity.ARG_ACCIDENT_ID, accident.getId());
        startActivity(intent);
    }

    @OnClick(R.id.fab)
    void addNewAccident() {
        Toast.makeText(getActivity(), "New One", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getSelectedItem();
        mAccidentTypeId = cursor.getLong(0);
        getLoaderManager().restartLoader(ACCIDENTS_LOADER_ID, null, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private static class AccidentsClusterRenderer extends DefaultClusterRenderer<AccidentMap> {

        private final Context mContext;
        private SparseArray<BitmapDescriptor> mIcons = new SparseArray<>();
        private ShapeDrawable mColoredCircleBackground;
        private final IconGenerator mIconGenerator;
        private final float mDensity;

        public AccidentsClusterRenderer(Context context, GoogleMap map, ClusterManager<AccidentMap> manager) {
            super(context.getApplicationContext(), map, manager);
            mContext = context.getApplicationContext();
            mDensity = mContext.getResources().getDisplayMetrics().density;
            mColoredCircleBackground = new ShapeDrawable(new OvalShape());
            mIconGenerator = new IconGenerator(mContext);
            mIconGenerator.setContentView(makeSquareTextView(mContext));
            mIconGenerator.setTextAppearance(R.style.ClusterIcon_TextAppearance);
            mIconGenerator.setBackground(makeClusterBackground());
        }

        @Override
        protected void onBeforeClusterItemRendered(AccidentMap item, MarkerOptions markerOptions) {


//            final String dateValue = GeneralUtil.getInstance().getFriendlyDayString(item.getDate());
//            final String snippet = String.format("%s, %s", dateValue, item.getPlaceName());
//
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(GeneralUtil.getInstance().getViolationResource(item)))
//                    .title(item.getType())
//                    .snippet(snippet);

            markerOptions.title(item.getTitle());
        }


        @Override
        protected void onBeforeClusterRendered(Cluster<AccidentMap> cluster, MarkerOptions markerOptions) {
            int bucket = getBucket(cluster);
            BitmapDescriptor descriptor = mIcons.get(bucket);
            if (descriptor == null) {
                mColoredCircleBackground.getPaint().setColor(ContextCompat.getColor(mContext, R.color.primary));
                descriptor = BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon(getClusterText(bucket)));
                mIcons.put(bucket, descriptor);
            }
            markerOptions.icon(descriptor);
        }

        private SquareTextView makeSquareTextView(Context context) {
            SquareTextView squareTextView = new SquareTextView(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            squareTextView.setLayoutParams(layoutParams);
            squareTextView.setId(R.id.text);
            int twelveDpi = (int) (12 * mDensity);
            squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
            return squareTextView;
        }

        private LayerDrawable makeClusterBackground() {
            mColoredCircleBackground = new ShapeDrawable(new OvalShape());
            ShapeDrawable outline = new ShapeDrawable(new OvalShape());
            outline.getPaint().setColor(0x80ffffff); // Transparent white.
            LayerDrawable background = new LayerDrawable(new Drawable[]{outline, mColoredCircleBackground});
            int strokeWidth = (int) (mDensity * 3);
            background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
            return background;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ALREADY_CONNECTED_TAG, mAlreadyConnected);
    }

    private class AccidentsTypesLoader implements LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    AccidentTypeEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mSpinnerAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

}
