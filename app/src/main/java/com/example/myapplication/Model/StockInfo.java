package com.example.myapplication.Model;

public class StockInfo {

    private String symbol;
    private String name;
    private double price;
    private double changesPercentage;
    private double dayLow;
    private double dayHigh;
    private long volume;

    public StockInfo(){}

    public StockInfo(String symbol, String name, double price, double changesPercentage, double dayLow, double dayHigh, double volume){
        this.symbol = symbol;
        this.name = name;
        this. price = price;
        this.changesPercentage = changesPercentage;
        this.dayLow = dayLow;
        this.dayHigh = dayHigh;
        this.volume = (long) volume;

    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChangesPercentage() {
        return changesPercentage;
    }

    public void setChangesPercentage(double changesPercentage) {
        this.changesPercentage = changesPercentage;
    }

    public double getDayLow() {
        return dayLow;
    }

    public void setDayLow(double dayLow) {
        this.dayLow = dayLow;
    }

    public double getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(double dayHigh) {
        this.dayHigh = dayHigh;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }
}
