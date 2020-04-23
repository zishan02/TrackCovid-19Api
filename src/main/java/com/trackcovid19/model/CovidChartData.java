package com.trackcovid19.model;

import java.util.List;

public class CovidChartData {

  private List<String> xAxis;
  private List<Long> yAxis;

  public List<String> getxAxis() {
    return xAxis;
  }

  public void setxAxis(List<String> xAxis) {
    this.xAxis = xAxis;
  }

  public List<Long> getyAxis() {
    return yAxis;
  }

  public void setyAxis(List<Long> yAxis) {
    this.yAxis = yAxis;
  }
}
