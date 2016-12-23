package org.oporaua.localelections.tvk;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface TvkService {

    @GET("/search/index.json")
    Observable<List<TvkMember>> loadTvkMembers(@Query("q") String name);

}
