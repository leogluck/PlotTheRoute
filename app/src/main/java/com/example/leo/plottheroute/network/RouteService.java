package com.example.leo.plottheroute.network;

import com.example.leo.plottheroute.model.RouteDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
/**
 * Created by Leo on 24.01.2018
 */

public interface RouteService {
    @GET("json")
    Call<RouteDetails> getDirection(@Query("origin") String startPointLatLng,
            @Query("destination") String endPointLatLng,
            @Query("key") String apiKey);

}
