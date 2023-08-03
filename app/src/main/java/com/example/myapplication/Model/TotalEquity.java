package com.example.myapplication.Model;

public class TotalEquity {
    private String ticker;
    private double equity;

    public TotalEquity() {
        // Required empty constructor
    }

    public TotalEquity(String ticker, double equity) {
        this.ticker = ticker;
        this.equity = equity;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getEquity() {
        return equity;
    }

    public void setEquity(double equity) {
        this.equity = equity;
    }
}

