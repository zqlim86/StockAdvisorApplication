package com.example.myapplication.Client;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A client class to handle API requests to a prediction service.
 */
public class PredictionClient {

    // Retrofit instance and Base URL of the prediction result API
    private static Retrofit retrofit;
    private static String BASE_URL="https://zqlim86.pagekite.me";

    public static Retrofit getPredictionInstance() {
        // Create OkHttpClient with increased timeout duration
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.MINUTES)
                .build();

        // Create the Retrofit instance if it does not exist
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
