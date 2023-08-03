package com.example.myapplication.Model;

public class StockProfile {

    private String symbol;
    private String companyName;
    private double price;
    private double changes;
    private String image;

    public StockProfile(){}

    public StockProfile(String symbol, String companyName, double price, double changes, String image) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.changes = changes;
        this.image = image;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChanges() {
        return changes;
    }

    public void setChanges(double changes) {
        this.changes = changes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
