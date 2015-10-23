package org.oporaua.localelections.accidents;


import org.oporaua.localelections.data.AccidentSubtype;
import org.oporaua.localelections.data.AccidentType;
import org.oporaua.localelections.data.ElectionsType;
import org.oporaua.localelections.data.Locality;
import org.oporaua.localelections.data.Party;
import org.oporaua.localelections.data.Region;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface AccidentsRestService {

    @GET("/violations")
    Call<Accident[]> getAccidents(@Query("since_id") long sinceId);

    @GET("/violations")
    Call<Accident> getAccident(@Query("id") long id);

    @GET("/violations/accident_types")
    Call<AccidentType[]> getAccidentTypes();

    @GET("/violations/accident_subtypes")
    Call<AccidentSubtype[]> getAccidentSubtypes();

    @GET("/violations/region")
    Call<Region[]> getRegions();

    @GET("/violations/locality")
    Call<Locality[]> getLocalities();

    @GET("/violations/parties")
    Call<Party[]> getParties();

    @GET("/violations/elections_type")
    Call<ElectionsType[]> getElectionsTypes();

    @POST("/violations/add.json")
    Call<Accident> loadAccident(@Body Accident accident);

}
