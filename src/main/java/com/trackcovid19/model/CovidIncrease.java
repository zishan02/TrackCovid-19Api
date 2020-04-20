package com.trackcovid19.model;


public class CovidIncrease {

    private String date;
    private int change;
    private String percentageChange;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public String getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(String percentageChange) {
        this.percentageChange = percentageChange;
    }
}
