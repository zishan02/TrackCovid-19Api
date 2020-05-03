package com.trackcovid19.service;

import java.util.List;

import com.trackcovid19.Repository.TrackCovid19ChartDeathRepo;
import com.trackcovid19.model.TrackCovid19DeathChart;

import org.springframework.beans.factory.annotation.Autowired;

public class TrackCovid19DeathChartService {

  @Autowired private TrackCovid19ChartDeathRepo trackCovid19ChartDeathRepo;

  public TrackCovid19DeathChart findAllData() {

    List<TrackCovid19DeathChart> trackCovid19DeathChartList = trackCovid19ChartDeathRepo.findAll();
    return trackCovid19DeathChartList.get(0);
  }

  public TrackCovid19DeathChart updateData(TrackCovid19DeathChart data) {
    List<TrackCovid19DeathChart> trackCovid19DeathCharts = trackCovid19ChartDeathRepo.findAll();
    TrackCovid19DeathChart trackCovid19DeathChart = trackCovid19DeathCharts.get(0);
    if (null != trackCovid19DeathChart) {
      List<String> xAxis = trackCovid19DeathChart.getDate();
      List<Integer> yAxis = trackCovid19DeathChart.getTotalDeaths();
      xAxis.add(data.getDate().get(0));
      yAxis.add(data.getTotalDeaths().get(0));
      return trackCovid19ChartDeathRepo.save(trackCovid19DeathChart);
    }
    return trackCovid19ChartDeathRepo.save(data);
  }
}
