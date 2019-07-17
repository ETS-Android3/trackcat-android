package de.trackcat;

import java.util.ArrayList;
import java.util.HashMap;

import de.trackcat.CustomElements.RecordModelForServer;
import de.trackcat.Database.Models.Route;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIClient {

    @POST("/loginAPI")
    Call<ResponseBody> getUser(@Header("Authorization") String authHeader);

    @POST("/getRecordsByIdAPI")
    Call<ResponseBody> getRecordsById(@Header("Authorization") String authHeader,@Body HashMap<String,String> json);

    @Headers({"Accept: application/json"})
    @POST("/registerAPI")
    Call<ResponseBody> registerUser(@Body HashMap<String,String> json);

    @Headers({"Accept: application/json"})
    @POST("/getUserByIdAPI")
    Call<ResponseBody> getUserById(@Header("Authorization") String authHeader,@Body HashMap<String,String> json);

    @Headers({"Accept: application/json"})
    @POST("/updateUserAPI")
    Call<ResponseBody> updateUser(@Header("Authorization") String authHeader,@Body HashMap<String,String> json);

    @Headers({"Accept: application/json"})
    @POST("/synchronizeDataAPI")
    Call<ResponseBody> synchronizeData(@Header("Authorization") String authHeader,@Body HashMap<String,String> json);

    @Headers({"Accept: application/json"})
    @POST("/changeUserPasswordAPI")
    Call<ResponseBody> changeUserPassword(@Header("Authorization") String authHeader, @Body HashMap<String,String> json);

    @Headers({"Accept: application/json"})
    @POST("/deleteUserAPI")
    Call<ResponseBody> deleteUser(@Header("Authorization") String authHeader, @Body HashMap<String,String> json);

    @Headers({"Accept: application/json"})
    @POST("/uploadTrackAPI")
    Call<ResponseBody> uploadFullTrack(@Header("Authorization") String authHeader, @Body RecordModelForServer track);

    @Headers({"Accept: application/json"})
    @POST("/synchronizeRecordsAPI")
    Call<ResponseBody> synchronizeRecords(@Header("Authorization") String authHeader, @Body ArrayList<HashMap<String, String>>  json);

    @Headers({"Accept: application/json"})
    @POST("/editRecordAPI")
    Call<ResponseBody> updateRecordName(@Header("Authorization") String authHeader, @Body  HashMap<String,String> json);

}
