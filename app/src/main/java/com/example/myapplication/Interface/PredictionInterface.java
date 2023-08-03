package com.example.myapplication.Interface;

import com.example.myapplication.Model.Prediction;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PredictionInterface {

    @POST("/predict/{ticker}")
    Call<List<Prediction>> getPrediction(@Path("ticker") String ticker);

}
