package com.example.myapplication.Model;

import java.util.ArrayList;

public class Stock {

    private String symbol;
    private String date;
    private double price;
    private long mktCap;

    private double open;
    private double low;
    private double high;
    private double close;
    public int volume;
    public double rsi;

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getRsi() {
        return rsi;
    }

    public void setRsi(double rsi) {
        this.rsi = rsi;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public long getMktCap() {
        return mktCap;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setMktCap(int quantity) {
        this.mktCap = mktCap;
    }

}
