package com.trackcovid19.model;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class CovidChartData {
  @Id
  private String id;
  private ArrayList<String> xAxis;
  private ArrayList<Long> yAxis;

  public ArrayList<String> getxAxis() {
    return xAxis;
  }

  public void setxAxis(ArrayList<String> xAxis) {
    this.xAxis = xAxis;
  }

  public ArrayList<Long> getyAxis() {
    return yAxis;
  }

  public void setyAxis(ArrayList<Long> yAxis) {
    this.yAxis = yAxis;
  }
}
