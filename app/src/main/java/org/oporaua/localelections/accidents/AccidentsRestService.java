package org.oporaua.localelections.accidents;


import org.oporaua.localelections.data.model.AccidentSubtype;
import org.oporaua.localelections.data.model.AccidentType;
import org.oporaua.localelections.data.model.ElectionsType;
import org.oporaua.localelections.data.model.Locality;
import org.oporaua.localelections.data.model.Party;
import org.oporaua.localelections.data.model.Region;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

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

    @POST("/violations/add.json")
    Observable<Accident> loadAccidentRx(@Body AccidentPost accident);

//                                        @Part("evidence") RequestBody typedFile);

}
