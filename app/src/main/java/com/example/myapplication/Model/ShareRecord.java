package com.example.myapplication.Model;

public class ShareRecord {
    private String date;
    private String ticker;
    private int quantity;
    private double price;
    private double total;

    public ShareRecord() {
        // empty constructor needed for Firebase
    }

    public ShareRecord(String date, String ticker, int quantity, double price) {
        this.date = date;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
        this.total = quantity * price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.total = quantity * price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        this.total = quantity * price;

    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
