package com.example.stockmonitor.GSONclass;

public class BookModel {

    private double latestPrice;
    private int numberOfTrades;

    public void setNumberOfTrades(int numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public int getNumberOfTrades() {
        return numberOfTrades;
    }

    public double getLatestPrice() {
        return latestPrice;
    }

    public String sector;

    public String symbol;

    public String getSector() {
        return sector;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String companyName;

    public void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }




}
