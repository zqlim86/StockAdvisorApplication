package com.example.myapplication.Interface;

import com.example.myapplication.Model.Sentiment;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsSentimentInterface {

//    @GET("/api/sentiments?s={ticker}&from=2023-01-01&to={date}&api_token=642e6f1b5958d1.90264888")
//    Call<List<Sentiment>> getNewsSentiment(@Path("ticker") String ticker, @Path("date") String date);

    @GET("/api/sentiments")
    Call<JsonObject> getNewsSentiment(@Query("s") String ticker, @Query("from") String fromDate, @Query("to") String toDate, @Query("api_token") String apiToken);
}
