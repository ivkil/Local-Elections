package org.oporaua.localelections.accidents.new_accident;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.oporaua.localelections.R;
import org.oporaua.localelections.accidents.Accident;
import org.oporaua.localelections.accidents.AccidentsRestService;
import org.oporaua.localelections.accidents.Evidence;
import org.oporaua.localelections.util.Constants;
import org.oporaua.localelections.util.GeneralUtil;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.view.View.VISIBLE;

public class NewAccidentActivity extends AppCompatActivity implements DatePickerFragment.DataSetListener {

    private static final LatLngBounds BOUNDS_UKRAINE = new LatLngBounds(
            new LatLng(45.7597, 21.2300), new LatLng(52.7000, 39.1500));
    private static final String TAG = "NEW_ACCIDENT_TAG";

    private AccidentsRestService mAccidentsRestService;

    @Bind(R.id.et_title)
    EditText mTitleEditText;

    @Bind(R.id.et_district)
    EditText mDistrictEditText;

    @Bind(R.id.et_offender)
    EditText mOffenderEditText;

    @Bind(R.id.et_violation_against)
    EditText mViolationAgainstEditText;

    @Bind(R.id.et_beneficiary)
    EditText mBeneficiaryEditText;

    @Bind(R.id.sp_beneficiary_party)
    Spinner mBeneficiarySpinner;

    @Bind(R.id.sp_elections_type)
    Spinner mElectionTypeSpinner;

    @Bind(R.id.sp_region)
    Spinner mRegionSpinner;

    @Bind(R.id.sp_city)
    Spinner mCitySpinner;

    @Bind(R.id.sp_violation_type)
    Spinner mViolationTypeSpinner;

    @Bind(R.id.sp_violation_sub_type)
    Spinner mViolationSubTypeSpinner;

    @Bind(R.id.sp_offender_party)
    Spinner mOffenderPartySpinner;

    @Bind(R.id.sp_violation_against_party)
    Spinner mAgainstPartySpinner;

    @Bind(R.id.new_violation_date_text_view)
    TextView mDateTextView;

    @Bind(R.id.new_violation_place_complete)
    AutoCompleteTextView mAutoCompleteTextView;

    @Bind(R.id.new_violation_map_view)
    View mMapView;

    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_accident);
        ButterKnife.bind(this);
        initToolbar();

        mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.MINUTES);
        client.setReadTimeout(5, TimeUnit.MINUTES);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(interceptor);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ACCIDENTS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        mAccidentsRestService = retrofit.create(AccidentsRestService.class);

        loadAccident();

        if (GeneralUtil.isPlayServicesAvailable(this)) {
            buildGoogleApiClient();
        }
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_UKRAINE, null);

        mAutoCompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
//        mAutoCompleteTextView.addTextChangedListener(new GeneralTextWatcher(mAutocompleteView));
        mAutoCompleteTextView.setAdapter(mAdapter);

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }

            final Place place = places.get(0);

            final CharSequence placeAddress = place.getAddress();
//            if (placeAddress == null) {
//                mPlaceDetails.setVisibility(View.GONE);
//            } else {
//                mPlaceDetails.setVisibility(VISIBLE);
//                mPlaceDetails.setText(placeAddress);
//            }

            final LatLng placeLocation = place.getLatLng();
//            mNewViolation.setLocation(new ParseGeoPoint(placeLocation.latitude, placeLocation.longitude));
//            mNewViolation.setPlaceName(place.getName().toString());
            showPlaceOnMap(placeLocation);
            Log.i(TAG, "Place details received: " + place.getName());
            places.release();
        }
    };

    private void showPlaceOnMap(LatLng placeLocation) {
        if (placeLocation == null) {
            mMapView.setVisibility(View.GONE);
        } else {
            mMapView.setVisibility(VISIBLE);
            if (mGoogleMap != null) {
                mGoogleMap.addMarker(new MarkerOptions().position(placeLocation));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @OnClick(R.id.pick_date_view)
    void pickDate() {
        showDatePickerDialog();
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void loadAccident() {
        final Accident accident = new Accident();
        accident.setTitle("Android Test Final");
        accident.setAccidentSubtypeId(6);
        accident.setElectionsId(1);
        accident.setRegionId(27);
        accident.setLocalityId(12658);
        accident.setOffender("offender");
        accident.setOffenderPartyId(19);
        accident.setBeneficiary("benef");
        accident.setBeneficiaryPartyId(19);
        accident.setVictim("victim");
        accident.setVictimPartyId(19);
        accident.setSource("Юрій Іванович");
        //accident.setPosition(new LatLng(25, 34));
        accident.setDate(new Date());
        accident.setLastIp("192.18.0.1");
        accident.setEvidence(new Evidence("/url"));
        Call<Accident> call = mAccidentsRestService.loadAccident(accident);
        call.enqueue(new Callback<Accident>() {
            @Override
            public void onResponse(Response<Accident> response, Retrofit retrofit) {
                Log.d("log", "new id :" + Long.toString(response.body().getId()));
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDataSet(Date date) {
        mDateTextView.setText(GeneralUtil.getFriendlyDayString(date));
    }

}
