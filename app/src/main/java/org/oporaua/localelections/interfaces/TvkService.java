package org.oporaua.localelections.interfaces;

import org.oporaua.localelections.model.TvkMember;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface TvkService {
    @GET("/search/index.json")
    Call<TvkMember[]> loadTvkMembers(@Query("q") String name);
}
