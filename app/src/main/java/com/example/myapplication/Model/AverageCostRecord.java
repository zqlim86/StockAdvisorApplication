package com.example.myapplication.Model;

public class AverageCostRecord {

    private String ticker;
    private double avgCost;

    public AverageCostRecord() {
        // Required empty constructor
    }

    public AverageCostRecord(String ticker, double avgCost) {
        this.ticker = ticker;
        this.avgCost = avgCost;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getAvgCost() {
        return avgCost;
    }

    public void setAvgCost(double avgCost) {
        this.avgCost = avgCost;
    }
}
