package org.oporaua.localelections.interfaces;


import org.oporaua.localelections.model.Accident;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

public interface AccidentsService {
    @GET("/violations")
    Call<List<Accident>> loadAccidents();
}
