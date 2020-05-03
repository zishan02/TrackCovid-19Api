package com.trackcovid19.Repository;

import com.trackcovid19.model.TrackCovid19DeathChart;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrackCovid19ChartDeathRepo
    extends MongoRepository<TrackCovid19DeathChart, String> {}
