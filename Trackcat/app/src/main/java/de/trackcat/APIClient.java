package de.trackcat;

import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIClient {

    @POST("/loginAPI")
    Call<ResponseBody> getUser(@Header("Authorization") String authHeader);

    @Headers({"Accept: application/json"})
    @POST("/registerAPI")
    Call<String> registerUser(@Body HashMap<String,String> json);

    @Headers({"Accept: application/json"})
    @POST("/getUserByEmailAPI")
    Call<ResponseBody> getUserByEmail(@Body HashMap<String,String> json);

    @Headers({"Accept: application/json"})
    @POST("/updateUserAPI")
    Call<ResponseBody> updateUser(@Body HashMap<String,String> json);

    @Headers({"Accept: application/json"})
    @POST("/synchronizeDataAPI")
    Call<ResponseBody> synchronizeData(@Body HashMap<String,String> json);

}
