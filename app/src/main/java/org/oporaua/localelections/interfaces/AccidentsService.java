package org.oporaua.localelections.interfaces;


import org.oporaua.localelections.model.Accident;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface AccidentsService {

    @GET("/violations")
    Call<List<Accident>> getAccidents(@Query("since_id") long sinceId);

    @GET("/violations")
    Call<Accident> getAccident(@Query("id") long id);

}
