package com.example.myapplication.Client;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A client class that creates a Retrofit instance for making API requests to the news sentiment API.
 */
public class NewsSentimentClient {

    // Retrofit instance and Base URL of the news sentiment API
    private static Retrofit retrofit;
    private static String BASE_URL="https://eodhistoricaldata.com";


    public static Retrofit getNewsSentimentInstance() {
        // Create the Retrofit instance if it does not exist
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
