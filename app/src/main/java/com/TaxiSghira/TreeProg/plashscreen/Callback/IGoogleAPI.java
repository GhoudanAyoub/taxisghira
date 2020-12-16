package com.TaxiSghira.TreeProg.plashscreen.Callback;

import com.TaxiSghira.TreeProg.plashscreen.Module.routes;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IGoogleAPI {
    @GET("directions/v5/mapbox/{profile}/{coordinates}")
    Observable<routes> getDirection(
            @Path("profile") String profile,
            @Path("coordinates") String coordinate,
            @Query("access_token") String access_token
    );
}
