package com.trackcovid19.Repository;

import java.util.List;

import com.trackcovid19.model.StateWiseData;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackCovid19Repo extends MongoRepository<StateWiseData, String> {

  List<StateWiseData> findByStateName(String stateName);
}
