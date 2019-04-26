package de.trackcat;

import org.json.JSONObject;

import java.util.HashMap;

import de.trackcat.Database.Models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIClient {

    @POST("/loginAPI")
    Call<String> getUser(@Header("Authorization") String authHeader);

    @Headers({"Accept: application/json"})
    @POST("/registerAPI")
    Call<String> registerUser(@Body HashMap<String,String> json);



}
