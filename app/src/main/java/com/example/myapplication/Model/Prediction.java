package com.example.myapplication.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Prediction {

    private String date;
    private float close;
    private float rsi;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getClosePrice() {
        return close;
    }

    public void setClosePrice(float close) {
        this.close = close;
    }

    public float getRsi() {
        return rsi;
    }

    public void setRsi(float rsi) {
        this.rsi = rsi;
    }
}
