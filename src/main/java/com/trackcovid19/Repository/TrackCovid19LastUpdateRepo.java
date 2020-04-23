package com.trackcovid19.Repository;

import com.trackcovid19.model.LastUpdated;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrackCovid19LastUpdateRepo extends MongoRepository<LastUpdated, String> {}
