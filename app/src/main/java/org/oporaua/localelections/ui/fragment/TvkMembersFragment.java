package org.oporaua.localelections.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.oporaua.localelections.interfaces.TvkService;
import org.oporaua.localelections.model.TvkMember;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class TvkMembersFragment extends ListFragment {


    public TvkMembersFragment() {
        // Required empty public constructor
    }

    public static TvkMembersFragment newInstance() {
        return new TvkMembersFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://tvk.oporaua.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TvkService service = retrofit.create(TvkService.class);
        Call<TvkMember[]> call = service.loadTvkMembers("Боярин Юрій");
        call.enqueue(new Callback<TvkMember[]>() {
            @Override
            public void onResponse(Response<TvkMember[]> response, Retrofit retrofit) {
                TvkMember[] tvkMembers = response.body();
                for (TvkMember tvkMember : tvkMembers) {
                    Log.d("mytag", tvkMember.getElections() + " : " + tvkMember.getParty());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
