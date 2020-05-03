package com.trackcovid19.service;

import java.util.List;

import com.trackcovid19.Repository.TrackCovid19ChartDeathRepo;
import com.trackcovid19.model.TrackCovid19DeathChart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackCovid19DeathChartService {

  @Autowired private TrackCovid19ChartDeathRepo trackCovid19ChartDeathRepo;

  public TrackCovid19DeathChart findAllData() {

    List<TrackCovid19DeathChart> trackCovid19DeathChartList = trackCovid19ChartDeathRepo.findAll();
    return trackCovid19DeathChartList.get(0);
  }
}
