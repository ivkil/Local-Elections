package org.oporaua.localelections.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.oporaua.localelections.R;
import org.oporaua.localelections.accidents.Accident;
import org.oporaua.localelections.accidents.AccidentsRestService;
import org.oporaua.localelections.accidents.Evidence;
import org.oporaua.localelections.util.Constants;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class NewAccidentActivity extends AppCompatActivity {

    private AccidentsRestService mAccidentsRestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_accident);

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

}
