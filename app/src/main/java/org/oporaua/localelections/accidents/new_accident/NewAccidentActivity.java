package org.oporaua.localelections.accidents.new_accident;

import android.content.Intent;
import android.database.Cursor;
<<<<<<< HEAD
import android.net.wifi.WifiManager;
=======
import android.graphics.Bitmap;
import android.net.Uri;
>>>>>>> d5ad05db48eb1d1722ea185ecb144bee3b068980
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import org.oporaua.localelections.FilterSpinnerAdapter;
import org.oporaua.localelections.R;
import org.oporaua.localelections.accidents.AccidentPost;
import org.oporaua.localelections.accidents.AccidentsRestService;
import org.oporaua.localelections.data.OporaContract.AccidentSubtypeEntry;
import org.oporaua.localelections.data.OporaContract.AccidentTypeEntry;
import org.oporaua.localelections.data.OporaContract.ElectionTypeEntry;
import org.oporaua.localelections.data.OporaContract.LocalityEntry;
import org.oporaua.localelections.data.OporaContract.PartyEntry;
import org.oporaua.localelections.data.OporaContract.RegionEntry;
import org.oporaua.localelections.util.GeneralUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.View.VISIBLE;


public class NewAccidentActivity extends AppCompatActivity implements DatePickerFragment.DataSetListener,
        LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {

    private static final LatLngBounds BOUNDS_UKRAINE = new LatLngBounds(
            new LatLng(45.7597, 21.2300), new LatLng(52.7000, 39.1500));

    private static final String TAG = "NEW_ACCIDENT_TAG";

    private static final int PICK_IMAGE_ID = 50;

    private AccidentsRestService mAccidentsRestService;

    private static final int LOADER_ACCIDENTS_TYPES = 41;
    private static final int LOADER_ACCIDENTS_SUBTYPES = 42;
    private static final int LOADER_REGIONS = 43;
    private static final int LOADER_LOCALITIES = 44;
    private static final int LOADER_PARTIES = 45;
    private static final int LOADER_ELECTIONS_TYPES = 46;

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

    @Bind(R.id.et_violation_info)
    EditText mSourceEditText;

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

    @Bind(R.id.iv_image_preview)
    ImageView mPreviewImageView;

    private FilterSpinnerAdapter mSpinnerAccidentTypesAdapter;
    private FilterSpinnerAdapter mSpinnerAccidentSubtypesAdapter;
    private FilterSpinnerAdapter mSpinnerRegionsAdapter;
    private FilterSpinnerAdapter mSpinnerLocalitiesAdapter;
    private FilterSpinnerAdapter mSpinnerPartiesAdapter;
    private FilterSpinnerAdapter mSpinnerElectionsTypesAdapter;

    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private GoogleMap mGoogleMap;
    private Bitmap mImageBitmap;

    private long mCurrentAccidentId = -1;
    private long mCurrentRegionId = -1;

    private Accident mAccident;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_accident);
        ButterKnife.bind(this);
        initToolbar();
        mAccident = new Accident();

        mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        OkHttpClient client = new OkHttpClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(interceptor);
        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.ACCIDENTS_BASE_URL)
                .baseUrl("https://dts2015.oporaua.org/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        mAccidentsRestService = retrofit.create(AccidentsRestService.class);

        getSupportLoaderManager().initLoader(LOADER_ACCIDENTS_TYPES, null, this);
        getSupportLoaderManager().initLoader(LOADER_ACCIDENTS_SUBTYPES, null, this);
        getSupportLoaderManager().initLoader(LOADER_REGIONS, null, this);
        getSupportLoaderManager().initLoader(LOADER_LOCALITIES, null, this);
        getSupportLoaderManager().initLoader(LOADER_PARTIES, null, this);
        getSupportLoaderManager().initLoader(LOADER_ELECTIONS_TYPES, null, this);
        initSpinners();

        mBeneficiaryEditText.addTextChangedListener(new GeneralTextWatcher(mBeneficiaryEditText));
        mDistrictEditText.addTextChangedListener(new GeneralTextWatcher(mDistrictEditText));
        mOffenderEditText.addTextChangedListener(new GeneralTextWatcher(mOffenderEditText));
        mTitleEditText.addTextChangedListener(new GeneralTextWatcher(mTitleEditText));
        mViolationAgainstEditText.addTextChangedListener(new GeneralTextWatcher(mViolationAgainstEditText));
        mSourceEditText.addTextChangedListener(new GeneralTextWatcher(mSourceEditText));

        mDateTextView.setText(GeneralUtil.getFriendlyDayString(new Date()));

        //loadAccident();

        if (GeneralUtil.isPlayServicesAvailable(this)) {
            buildGoogleApiClient();
        }
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_UKRAINE, null);

        mAutoCompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mAutoCompleteTextView.addTextChangedListener(new GeneralTextWatcher(mAutoCompleteTextView));
        mAutoCompleteTextView.setAdapter(mAdapter);
    }

    private void initSpinners() {
        mSpinnerAccidentTypesAdapter = new FilterSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, null,
                new String[]{AccidentTypeEntry.COLUMN_TITLE},
                new int[]{android.R.id.text1},
                0);
        mSpinnerAccidentSubtypesAdapter = new FilterSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, null,
                new String[]{AccidentSubtypeEntry.COLUMN_TITLE},
                new int[]{android.R.id.text1},
                0);
        mSpinnerRegionsAdapter = new FilterSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, null,
                new String[]{RegionEntry.COLUMN_TITLE},
                new int[]{android.R.id.text1},
                0);
        mSpinnerLocalitiesAdapter = new FilterSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, null,
                new String[]{LocalityEntry.COLUMN_TITLE},
                new int[]{android.R.id.text1},
                0);
        mSpinnerPartiesAdapter = new FilterSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, null,
                new String[]{PartyEntry.COLUMN_TITLE},
                new int[]{android.R.id.text1},
                0);
        mSpinnerElectionsTypesAdapter = new FilterSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, null,
                new String[]{ElectionTypeEntry.COLUMN_TITLE},
                new int[]{android.R.id.text1},
                0);
        mViolationTypeSpinner.setAdapter(mSpinnerAccidentTypesAdapter);
        mViolationTypeSpinner.setOnItemSelectedListener(this);
        mViolationTypeSpinner.setOnItemSelectedListener(this);
        mViolationSubTypeSpinner.setAdapter(mSpinnerAccidentSubtypesAdapter);
        mRegionSpinner.setAdapter(mSpinnerRegionsAdapter);
        mRegionSpinner.setOnItemSelectedListener(this);
        mCitySpinner.setAdapter(mSpinnerLocalitiesAdapter);
        mOffenderPartySpinner.setAdapter(mSpinnerPartiesAdapter);
        mBeneficiarySpinner.setAdapter(mSpinnerPartiesAdapter);
        mAgainstPartySpinner.setAdapter(mSpinnerPartiesAdapter);
        mElectionTypeSpinner.setAdapter(mSpinnerElectionsTypesAdapter);
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
            mAccident.setLatitude(place.getLatLng().latitude);
            mAccident.setLongitude(place.getLatLng().longitude);
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



        if (TextUtils.isEmpty(mAccident.getTitle())) {
            return;// "Додайте заголовок інциденту";
        }
        if (mAccident.getLatitude() == 0 && mAccident.getLongitude() == 0) {
            return;// "Виберіть місце інцинедту";
        }
        if (TextUtils.isEmpty(mAccident.getSource())) {
            return;// "Додайте опис інциденту";
        }

        Cursor cursorAccidentType = (Cursor) mViolationSubTypeSpinner.getSelectedItem();
        mAccident.setAccidentSubtypeId(cursorAccidentType.getLong(0));

        Cursor cursorRegion = (Cursor) mRegionSpinner.getSelectedItem();
        long regionId = cursorRegion.getLong(0);
        if (regionId == -1) {
            return; // Оберіть регіон
        }
        mAccident.setRegionId(regionId);

        Cursor cursorLocality = (Cursor) mCitySpinner.getSelectedItem();
        mAccident.setLocalityId(cursorLocality.getLong(0));

        Cursor cursorElectionsType = (Cursor) mElectionTypeSpinner.getSelectedItem();
        mAccident.setElectionsId(cursorElectionsType.getLong(0));

        Cursor cursorOffender = (Cursor) mOffenderPartySpinner.getSelectedItem();
        mAccident.setOffenderPartyId(cursorOffender.getLong(0));

        Cursor cursorBeneficiary = (Cursor) mBeneficiarySpinner.getSelectedItem();
        mAccident.setBeneficiaryPartyId(cursorBeneficiary.getLong(0));

        Cursor cursorVictim = (Cursor) mAgainstPartySpinner.getSelectedItem();
        mAccident.setVictimPartyId(cursorVictim.getLong(0));

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        mAccident.setLastIp(ip);
        Log.d("loading", new Gson().toJson(mAccident));

        /*final AccidentPost accident = new AccidentPost();
        accident.setTitle("Android Test Final");
        accident.setAccidentSubtypeId(1);
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
//        accident.setEvidence(new Evidence("/url"));
//        Call<Accident> call = mAccidentsRestService.loadAccident(accident);
//        call.enqueue(new Callback<Accident>() {
//            @Override
//            public void onResponse(Response<Accident> response, Retrofit retrofit) {
//                Log.d("log", "new id :" + Long.toString(response.body().getId()));
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                t.printStackTrace();
//            }
//        });

//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 50, bos);
//        InputStream in = new ByteArrayInputStream(bos.toByteArray());
//        ContentBody photo = new InputStreamBody(in, "compressedFile");
//        entity.addPart("file", photo);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
//        RequestBody image = RequestBody.create(MediaType.parse("image/jpeg"), bos.toByteArray());

//        accident.setEvidence(bos.toByteArray());

//        mAccidentsRestService.loadAccidentRx(accident)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(new Subscriber<Accident>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Toast.makeText(NewAccidentActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onNext(Accident accident) {
//                        Toast.makeText(NewAccidentActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
//                    }
//                });

        GeneralUtil.submitAccident(accident, mImageBitmap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(NewAccidentActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(NewAccidentActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
>>>>>>> d5ad05db48eb1d1722ea185ecb144bee3b068980*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_submit_accident:
                Toast.makeText(NewAccidentActivity.this, "ADD STH", Toast.LENGTH_SHORT).show();
                loadAccident();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24dp);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = null;
        switch (id) {
            case LOADER_ACCIDENTS_TYPES:
                return new CursorLoader(this, AccidentTypeEntry.CONTENT_URI, null, null, null, null);
            case LOADER_ACCIDENTS_SUBTYPES:
                if (mCurrentAccidentId != -1) {
                    return new CursorLoader(this, AccidentSubtypeEntry.CONTENT_URI, null,
                            AccidentSubtypeEntry.COLUMN_ACCIDENT_TYPE_ID + " = ?",
                            new String[]{Long.toString(mCurrentAccidentId)}, null);
                } else {
                    return new CursorLoader(this, AccidentSubtypeEntry.CONTENT_URI, null, null, null, null);
                }
            case LOADER_REGIONS:
                sortOrder = RegionEntry.COLUMN_TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(this, RegionEntry.CONTENT_URI, null, null, null, sortOrder);
            case LOADER_LOCALITIES:
                sortOrder = LocalityEntry.COLUMN_TITLE + " COLLATE LOCALIZED ASC";
                if (mCurrentRegionId != -1) {
                    return new CursorLoader(this, LocalityEntry.CONTENT_URI, null,
                            LocalityEntry.COLUMN_REGION_ID + " <> ? AND " + LocalityEntry.COLUMN_REGION_ID + " = ?",
                            new String[]{Long.toString(0), Long.toString(mCurrentRegionId)}, sortOrder);
                } else {
                    return new CursorLoader(this, LocalityEntry.CONTENT_URI, null, null, null, sortOrder);
                }
            case LOADER_PARTIES:
                return new CursorLoader(this, PartyEntry.CONTENT_URI, null, null, null, null);
            case LOADER_ELECTIONS_TYPES:
                return new CursorLoader(this, ElectionTypeEntry.CONTENT_URI, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onDataSet(Date date) {
        mDateTextView.setText(GeneralUtil.getFriendlyDayString(date));
        mAccident.setDate(date);
        String error = checkAccident();
        if (error == null) {
            loadAccident();
        } else {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            //Snackbar.make(NewAccidentActivity.this, error, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_accident, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @OnClick(R.id.btn_add_photo)
    void pickNewProfileImage() {
        mImageBitmap = null;
//        mPreviewImageView.setImageResource(0);
        mPreviewImageView.setImageResource(android.R.color.transparent);
        mPreviewImageView.setVisibility(View.GONE);

        Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(imageIntent, getString(R.string.image_chooser_title));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_ID && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                mPreviewImageView.setVisibility(VISIBLE);
                Glide.with(this).load(uri).placeholder(android.R.color.white).into(mPreviewImageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ACCIDENTS_TYPES:
                mSpinnerAccidentTypesAdapter.swapCursor(data);
                break;
            case LOADER_ACCIDENTS_SUBTYPES:
                mSpinnerAccidentSubtypesAdapter.swapCursor(data);
                break;
            case LOADER_REGIONS:
                mSpinnerRegionsAdapter.swapCursor(data);
                break;
            case LOADER_LOCALITIES:
                mSpinnerLocalitiesAdapter.swapCursor(data);
                break;
            case LOADER_PARTIES:
                mSpinnerPartiesAdapter.swapCursor(data);
                break;
            case LOADER_ELECTIONS_TYPES:
                mSpinnerElectionsTypesAdapter.swapCursor(data);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_ACCIDENTS_TYPES:
                mSpinnerAccidentTypesAdapter.swapCursor(null);
                break;
            case LOADER_ACCIDENTS_SUBTYPES:
                mSpinnerAccidentSubtypesAdapter.swapCursor(null);
                break;
            case LOADER_REGIONS:
                mSpinnerRegionsAdapter.swapCursor(null);
                break;
            case LOADER_LOCALITIES:
                mSpinnerLocalitiesAdapter.swapCursor(null);
                break;
            case LOADER_PARTIES:
                mSpinnerPartiesAdapter.swapCursor(null);
                break;
            case LOADER_ELECTIONS_TYPES:
                mSpinnerElectionsTypesAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getSelectedItem();
        long currentId = cursor.getLong(0);
        switch (parent.getId()) {
            case R.id.sp_violation_type:
                mCurrentAccidentId = currentId;
                getSupportLoaderManager().restartLoader(LOADER_ACCIDENTS_SUBTYPES, null, this);
                break;
            case R.id.sp_region:
                mCurrentRegionId = currentId;
                getSupportLoaderManager().restartLoader(LOADER_LOCALITIES, null, this);
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private class GeneralTextWatcher implements TextWatcher {

        private View view;

        private GeneralTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (view.getId() == R.id.new_violation_place_complete) {
                if (TextUtils.isEmpty(charSequence)) {
                    mMapView.setVisibility(View.GONE);
                    mAccident.setLatitude(0);
                    mAccident.setLongitude(0);
                    if (mGoogleMap != null) {
                        mGoogleMap.clear();
                    }
                }
            }
        }

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.et_title:
                    mAccident.setTitle(text);
                    break;
                case R.id.et_district:
                    mAccident.setPollingStation(text);
                    break;
                case R.id.et_offender:
                    mAccident.setOffender(text);
                    break;
                case R.id.et_beneficiary:
                    mAccident.setOffender(text);
                    break;
                case R.id.et_violation_against:
                    mAccident.setVictim(text);
                    break;
                case R.id.et_violation_info:
                    mAccident.setSource(text);
                    break;
            }
        }
    }

}
