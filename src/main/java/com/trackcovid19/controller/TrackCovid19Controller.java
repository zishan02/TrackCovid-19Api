package com.trackcovid19.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.trackcovid19.model.*;
import com.trackcovid19.service.FirebaseMessagingService;
import com.trackcovid19.service.TrackCovid19DeathChartService;
import com.trackcovid19.service.TrackCovid19Service;
import com.trackcovid19.service.TrackCovidDataExtract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TrackCovid19Controller {
  @Autowired private TrackCovid19Service trackCovid19Service;

  @Autowired TrackCovidDataExtract trackCovidDataExtract;

  @Autowired TrackCovid19DeathChartService trackCovid19DeathChartService;

@Autowired
  private  FirebaseMessagingService firebaseService;


  @CrossOrigin
  @GetMapping("/fetchTableData")
  public List<StateWiseData> fetchAll() {
    List<StateWiseData> ls = trackCovid19Service.getAllState();
    Collections.sort(ls);
    return ls;
  }

  @CrossOrigin
  @PostMapping("/AddState")
  public StateWiseData addState(@Valid @RequestBody StateWiseData stateWiseData) {
    StateWiseData stateData = trackCovid19Service.createPerState(stateWiseData);
    LastUpdated lastUpdated = new LastUpdated();
    lastUpdated.setLastUpdated(new Date());

    if (null != stateData) {
      trackCovid19Service.createLastUpdate(lastUpdated);
    }
    return stateData;
  }

  @CrossOrigin
  @GetMapping("/fetchLastUpdate")
  public LastUpdated fetchLastUpdate() {
    return trackCovid19Service.fetchLastUpdated();
  }

  @CrossOrigin
  @GetMapping("/fetchCountCases")
  public CasesCount countTotalCases() {
    return trackCovid19Service.calculateTotals();
  }

  @CrossOrigin
  @GetMapping("/uploadExcel")
  public void uploadExcel() {
    trackCovid19Service.readFromXLSAndUpdate();
  }

  @CrossOrigin
  @GetMapping("/fetchChartData")
  public CovidChartData fetchChartData() {
    return trackCovid19Service.readChartDataFromExcel();
  }

  @CrossOrigin
  @GetMapping("/removeChartData")
  public String removeChartData() {
    CovidChartData covidChartData = trackCovidDataExtract.removeLastData();
    return covidChartData != null ? "Success" : "Failure";
  }

  @CrossOrigin
  @GetMapping("/fetchRateIncrease")
  public List<CovidIncrease> fetchRateIncrease() {
    return trackCovid19Service.fetchLast5Increase();
  }

  @CrossOrigin
  @GetMapping("/fetchTop5")
  public List<StateWiseData> fetchTop5() {
    return trackCovid19Service.fetchTop5();
  }

  @CrossOrigin
  @GetMapping("/fetchEstimatedCases")
  public EstimatedCases fetchEstimatedCases() {
    return trackCovid19Service.estimateCoronaConfirmed();
  }

  @CrossOrigin
  @GetMapping("/extractTableData")
  public void extractTableData() {
    trackCovidDataExtract.extractTableData();
  }

  @CrossOrigin
  @PostMapping("/testuri")
  public void testMethod(@Valid @RequestBody TrackCovid19DeathChart deathChart) {
    // trackCovid19DeathChartService.updateData(deathChart);
  }

  @CrossOrigin
  @GetMapping("/fetchdeathdata")
  public TrackCovid19DeathChart fetchDeathData() {
    return trackCovid19DeathChartService.findAllData();
  }

  @CrossOrigin
  @PostMapping("/send-notification")
  public String sendNotification(@RequestBody Note note,
                                 @RequestParam String topic) throws FirebaseMessagingException {
    return firebaseService.sendNotification(note, topic);
  }
}
