package org.oporaua.localelections.ui.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.oporaua.localelections.FilterSpinnerAdapter;
import org.oporaua.localelections.R;
import org.oporaua.localelections.accidents.Accident;
import org.oporaua.localelections.accidents.AccidentsRestService;
import org.oporaua.localelections.accidents.Evidence;
import org.oporaua.localelections.data.OporaContract.AccidentSubtypeEntry;
import org.oporaua.localelections.data.OporaContract.AccidentTypeEntry;
import org.oporaua.localelections.data.OporaContract.ElectionTypeEntry;
import org.oporaua.localelections.data.OporaContract.LocalityEntry;
import org.oporaua.localelections.data.OporaContract.PartyEntry;
import org.oporaua.localelections.data.OporaContract.RegionEntry;
import org.oporaua.localelections.util.Constants;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class NewAccidentActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {

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
    EditText mBeneficiarEditText;

    @Bind(R.id.sp_beneficiary_party)
    Spinner mBeneficiarSpinner;

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
    Spinner mOffenderPertySpinner;

    @Bind(R.id.sp_violation_against_party)
    Spinner mAgainstPertySpinner;
    private FilterSpinnerAdapter mSpinnerAccidentTypesAdapter;
    private FilterSpinnerAdapter mSpinnerAccidentSubtypesAdapter;
    private FilterSpinnerAdapter mSpinnerRegionsAdapter;
    private FilterSpinnerAdapter mSpinnerLocalitiesAdapter;
    private FilterSpinnerAdapter mSpinnerPartiesAdapter;
    private FilterSpinnerAdapter mSpinnerElectionsTypesAdapter;

    private long mCurrentAccidentId = -1;
    private long mCurrentRegionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_accident);
        ButterKnife.bind(this);
        initToolbar();
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

        getSupportLoaderManager().initLoader(LOADER_ACCIDENTS_TYPES, null, this);
        getSupportLoaderManager().initLoader(LOADER_ACCIDENTS_SUBTYPES, null, this);
        getSupportLoaderManager().initLoader(LOADER_REGIONS, null, this);
        getSupportLoaderManager().initLoader(LOADER_LOCALITIES, null, this);
        getSupportLoaderManager().initLoader(LOADER_PARTIES, null, this);
        getSupportLoaderManager().initLoader(LOADER_ELECTIONS_TYPES, null, this);
        initSpinners();

        //loadAccident();
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
        mViolationSubTypeSpinner.setAdapter(mSpinnerAccidentSubtypesAdapter);
        mRegionSpinner.setAdapter(mSpinnerRegionsAdapter);
        mRegionSpinner.setOnItemSelectedListener(this);
        mCitySpinner.setAdapter(mSpinnerLocalitiesAdapter);
        mOffenderPertySpinner.setAdapter(mSpinnerPartiesAdapter);
        mBeneficiarSpinner.setAdapter(mSpinnerPartiesAdapter);
        mAgainstPertySpinner.setAdapter(mSpinnerPartiesAdapter);
        mElectionTypeSpinner.setAdapter(mSpinnerElectionsTypesAdapter);
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
        switch (parent.getId()) {
            case R.id.sp_violation_type:
                mCurrentAccidentId = cursor.getLong(0);
                getSupportLoaderManager().restartLoader(LOADER_ACCIDENTS_SUBTYPES, null, this);
                break;
            case R.id.sp_region:
                mCurrentRegionId = cursor.getLong(0);
                getSupportLoaderManager().restartLoader(LOADER_LOCALITIES, null, this);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
