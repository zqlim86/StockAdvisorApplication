package com.example.myapplication.Interface;

import com.example.myapplication.Model.Stock;
import com.example.myapplication.Model.StockInfo;
import com.example.myapplication.Model.StockProfile;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetStockInterface {

    @GET("/api/v3/profile/{ticker}?apikey=2637bc10cb03bb780927e542ee64dc35")
    Call<List<StockProfile>> getStockProfile(@Path("ticker") String ticker);

    @GET("/api/v3/income-statement/{ticker}?limit=120&apikey=2637bc10cb03bb780927e542ee64dc35")
    Call<JsonArray> getFinancials(@Path("ticker") String ticker);

    @GET("/api/v3/stock_market/gainers?apikey=2637bc10cb03bb780927e542ee64dc35")
    Call<JsonArray> getTopGainers();

    @GET("/api/v3/stock_market/losers?apikey=2637bc10cb03bb780927e542ee64dc35")
    Call<JsonArray> getTopLosers();

    @GET("/api/v3/stock_market/actives?apikey=2637bc10cb03bb780927e542ee64dc35")
    Call<JsonArray> getMostActive();

    @GET("/api/v3/technical_indicator/daily/{ticker}?period=10&type=rsi&apikey=2637bc10cb03bb780927e542ee64dc35")
    Call<List<Stock>> getStockQuote4h(@Path("ticker") String ticker);

    @GET("/api/v3/quote/{ticker}?apikey=2637bc10cb03bb780927e542ee64dc35")
    Call<List<StockInfo>> getStockInfo(@Path("ticker") String ticker);
}
