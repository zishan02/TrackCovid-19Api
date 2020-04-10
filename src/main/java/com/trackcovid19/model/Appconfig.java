package com.trackcovid19.model;



import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class Appconfig extends AbstractMongoClientConfiguration {
    @Override
    protected String getDatabaseName() {
        return "test-db";
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb+srv://trackcovid19_admin:trackcovid19_admin@cluster0-ywoia.mongodb.net/test-db");
    }

}
