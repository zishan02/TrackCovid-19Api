package com.trackcovid19.Repository;

import com.trackcovid19.model.StateWiseData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackCovid19Repo extends MongoRepository<StateWiseData, String> {

    List<StateWiseData> findByStateName(String stateName);
}
