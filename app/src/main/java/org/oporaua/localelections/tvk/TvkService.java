package org.oporaua.localelections.tvk;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface TvkService {

    @GET("/search/index.json")
    Observable<List<TvkMember>> loadTvkMembers(@Query("q") String name);

}
