package com.example.myapplication.Model;

import android.provider.Telephony;

public class Sentiment {
    private String date;
    private int count;
    private double normalized;

    public Sentiment(){}

    public Sentiment(String date, int count, double normalized) {
        this.date = date;
        this.count = count;
        this.normalized = normalized;
    }

    public String getDate() {
        return date;
    }

    public int getCount() {
        return count;
    }

    public double getNormalized() {
        return normalized;
    }
}
