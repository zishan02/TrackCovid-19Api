package com.trackcovid19.Repository;

import com.trackcovid19.model.CovidChartData;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrackCovid19ChartDataRepo extends MongoRepository<CovidChartData, String> {}
