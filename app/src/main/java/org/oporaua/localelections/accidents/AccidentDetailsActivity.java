package org.oporaua.localelections.accidents;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.oporaua.localelections.R;
import org.oporaua.localelections.data.OporaContract;
import org.oporaua.localelections.data.OporaContract.AccidentEntry;
import org.oporaua.localelections.data.OporaContract.ElectionTypeEntry;
import org.oporaua.localelections.data.OporaContract.LocalityEntry;
import org.oporaua.localelections.data.OporaContract.PartyEntry;
import org.oporaua.localelections.data.OporaContract.RegionEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AccidentDetailsActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>,
        OnMapReadyCallback {

    public final static String ARG_ACCIDENT_ID = "accident_id";

    private static final int ACCIDENT_LOADER_ID = 15;

    private static final String OPORAUA_ORG = "https://dts2015.oporaua.org/";

    private long mId;

    @Bind(R.id.accident_date_textview)
    TextView mTextViewDate;

    @Bind(R.id.accident_title_textview)
    TextView mTextViewTitle;

//    @Bind(R.id.accident_region_textview)
//    TextView mTextViewRegion;
//
//    @Bind(R.id.accident_locality_textview)
//    TextView mTextViewLocality;
//
//    @Bind(R.id.accident_elections_type_textview)
//    TextView mTextViewElectionsType;
//
//    @Bind(R.id.accident_party_offender_textview)
//    TextView mTextViewParty;

    @Bind(R.id.tags_textview)
    TextView mTagsTextView;

    @Bind(R.id.accident_source_textview)
    TextView mTextViewSource;

    @Bind(R.id.image)
    ImageView mImage;

    private static final String[] ACCIDENTS_COLUMNS = {
            AccidentEntry.TABLE_NAME + "." + AccidentEntry._ID,
            AccidentEntry.COLUMN_DATE_TEXT,
            AccidentEntry.TABLE_NAME + "." + AccidentEntry.COLUMN_TITLE,
            AccidentEntry.COLUMN_SOURCE,
            AccidentEntry.COLUMN_EVIDENCE_URL,
            AccidentEntry.COLUMN_LAT,
            AccidentEntry.COLUMN_LNG,
            RegionEntry.TABLE_NAME + "." + RegionEntry.COLUMN_TITLE,
            LocalityEntry.TABLE_NAME + "." + LocalityEntry.COLUMN_TITLE,
            ElectionTypeEntry.TABLE_NAME + "." + ElectionTypeEntry.COLUMN_TITLE,
            PartyEntry.TABLE_NAME + "." + PartyEntry.COLUMN_TITLE
    };

//    private static final int COL_ACCIDENT_ID = 0;
    private static final int COL_ACCIDENT_DATE = 1;
    private static final int COL_ACCIDENT_TITLE = 2;
    private static final int COL_ACCIDENT_SOURCE = 3;
    private static final int COL_ACCIDENT_EVIDENCE_URL = 4;
    private static final int COL_ACCIDENT_LAT = 5;
    private static final int COL_ACCIDENT_LNG = 6;
    private static final int COL_ACCIDENT_REGION = 7;
    private static final int COL_ACCIDENT_LOCALITY = 8;
    private static final int COL_ACCIDENT_ELECTIONS_TYPE = 9;
    private static final int COL_ACCIDENT_OFFENDER_PARTY = 10;

    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_details);
        ButterKnife.bind(this);
        initToolbar();
        if (getIntent().getExtras() != null) {
            mId = getIntent().getLongExtra(ARG_ACCIDENT_ID, -1);
        }
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri accidentWithDataUri = AccidentEntry.buildAccidentUri(mId);
        return new CursorLoader(
                this,
                accidentWithDataUri,
                ACCIDENTS_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String title = data.getString(COL_ACCIDENT_TITLE);
            mTextViewTitle.setText(title);

            String dateString = data.getString(COL_ACCIDENT_DATE);
            Date date = OporaContract.getDateFromDb(dateString);
            SimpleDateFormat friendlyDateFormat = new SimpleDateFormat("dd MMM yyyy 'р.'", new Locale("uk"));
            mTextViewDate.setText(friendlyDateFormat.format(date));

            String region = data.getString(COL_ACCIDENT_REGION);
//            mTextViewRegion.setText(region);

            String locality = data.getString(COL_ACCIDENT_LOCALITY);
//            mTextViewLocality.setText(locality);

            String elections = data.getString(COL_ACCIDENT_ELECTIONS_TYPE);
//            mTextViewElectionsType.setText(elections);

            String party = data.getString(COL_ACCIDENT_OFFENDER_PARTY);
//            mTextViewParty.setText(party);

            mTagsTextView.setText(String.format("%s, %s, %s, %s", region, locality, elections, party));

            String source = data.getString(COL_ACCIDENT_SOURCE);
            mTextViewSource.setText(Html.fromHtml(source));

            String url = OPORAUA_ORG + data.getString(COL_ACCIDENT_EVIDENCE_URL);

            boolean imageAvailable = url.contains(".jpg") || url.contains(".png");

            mImage.setVisibility(imageAvailable ? View.VISIBLE : View.GONE);

            if (imageAvailable) {
                Glide.with(this).load(url).placeholder(R.color.white).into(mImage);
            }

            double lat = data.getDouble(COL_ACCIDENT_LAT);
            double lng = data.getDouble(COL_ACCIDENT_LNG);
            LatLng location = new LatLng(lat, lng);

            mGoogleMap.clear();
            mGoogleMap.addMarker(new MarkerOptions().position(location));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        getSupportLoaderManager().initLoader(ACCIDENT_LOADER_ID, null, this);
    }

}
