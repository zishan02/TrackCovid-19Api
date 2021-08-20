package com.trackcovid19.api;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.trackcovid19.Repository.TrackCovid19Repo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@ComponentScan(basePackages = "com.trackcovid19")
@SpringBootApplication
@EnableScheduling
@ImportResource("classpath:trackcovid-19-bean.xml")
@EnableMongoRepositories(basePackageClasses = TrackCovid19Repo.class)
public class ApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiApplication.class, args);
  }

  @Bean
  FirebaseMessaging firebaseMessaging() throws IOException {
    GoogleCredentials googleCredentials = GoogleCredentials
            .fromStream(new ClassPathResource("vaccinenote.json").getInputStream());
    FirebaseOptions firebaseOptions = FirebaseOptions
            .builder()
            .setCredentials(googleCredentials)
            .build();
    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "vaccinenotifier-a7c53");
    return com.google.firebase.messaging.FirebaseMessaging.getInstance(app);
  }
}
