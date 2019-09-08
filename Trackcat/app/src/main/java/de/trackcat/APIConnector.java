package de.trackcat;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIConnector {

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://safe-harbour.de:4242/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
