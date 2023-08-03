package com.example.myapplication.Client;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A client class that creates a Retrofit instance for making API requests to the stock data API.
 */
public class RetrofitClient {

    // Retrofit instance and Base URL of the stock data API
    private static Retrofit retrofit;
    private static String BASE_URL="https://financialmodelingprep.com";


    public static Retrofit getRetrofitInstance() {
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
