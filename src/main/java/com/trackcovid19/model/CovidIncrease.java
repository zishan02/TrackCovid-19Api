package com.trackcovid19.model;

public class CovidIncrease {

  private int num;
  private String date;
  private int change;
  private String percentageChange;
  private Long percentageChangeVal;

  public Long getPercentageChangeVal() {
    return percentageChangeVal;
  }

  public void setPercentageChangeVal(Long percentageChangeVal) {
    this.percentageChangeVal = percentageChangeVal;
  }

  public int getNum() {
    return num;
  }

  public void setNum(int num) {
    this.num = num;
  }

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
