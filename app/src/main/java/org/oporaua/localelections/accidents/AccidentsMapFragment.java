package org.oporaua.localelections.accidents;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.oporaua.localelections.R;
import org.oporaua.localelections.util.GeneralUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.android.gms.common.api.GoogleApiClient.Builder;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


public class AccidentsMapFragment extends Fragment implements LoaderCallbacks<List<Accident>>,
        OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, ClusterManager.OnClusterItemInfoWindowClickListener<Accident> {

    private static final String ALREADY_CONNECTED_TAG = "already_connected";

    private static final int ACCIDENTS_LOADER_ID = 35;

    private static final float DEFAULT_ZOOM = 13;

    private ClusterManager<Accident> mClusterManager;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;

    private boolean mAlreadyConnected;

    public static AccidentsMapFragment newInstance() {
        return new AccidentsMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accidents_map, container, false);
        ButterKnife.bind(this, view);
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

    @Override
    public void onClusterItemInfoWindowClick(Accident accident) {
        Intent intent = new Intent(getActivity(), AccidentDetailsActivity.class);
        intent.putExtra(AccidentDetailsActivity.ARG_ACCIDENT_ID, accident.getId());
        startActivity(intent);
    }

    @OnClick(R.id.fab)
    void addNewAccident() {
        Toast.makeText(getActivity(), "New One", Toast.LENGTH_SHORT).show();
    }

    private static class AccidentsClusterRenderer extends DefaultClusterRenderer<Accident> {

        private final Context mContext;
        private SparseArray<BitmapDescriptor> mIcons = new SparseArray<>();
        private ShapeDrawable mColoredCircleBackground;
        private final IconGenerator mIconGenerator;
        private final float mDensity;

        public AccidentsClusterRenderer(Context context, GoogleMap map, ClusterManager<Accident> manager) {
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
        protected void onBeforeClusterItemRendered(Accident item, MarkerOptions markerOptions) {


//            final String dateValue = GeneralUtil.getInstance().getFriendlyDayString(item.getDate());
//            final String snippet = String.format("%s, %s", dateValue, item.getPlaceName());
//
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(GeneralUtil.getInstance().getViolationResource(item)))
//                    .title(item.getType())
//                    .snippet(snippet);

            markerOptions.title(item.getTitle());
        }


        @Override
        protected void onBeforeClusterRendered(Cluster<Accident> cluster, MarkerOptions markerOptions) {
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

}
