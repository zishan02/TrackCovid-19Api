package com.trackcovid19.model;

import java.util.List;

import org.springframework.data.annotation.Id;

public class TrackCovid19DeathChart {
  @Id private String id;
  private List<String> date;
  private List<Integer> totalDeaths;

  public List<String> getDate() {
    return date;
  }

  public void setDate(List<String> date) {
    this.date = date;
  }

  public List<Integer> getTotalDeaths() {
    return totalDeaths;
  }

  public void setTotalDeaths(List<Integer> totalDeaths) {
    this.totalDeaths = totalDeaths;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
