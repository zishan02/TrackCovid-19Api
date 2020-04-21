package com.trackcovid19.controller;

import com.trackcovid19.model.*;
import com.trackcovid19.service.TrackCovid19Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
public class TrackCovid19Controller {
    @Autowired
    private TrackCovid19Service trackCovid19Service;

    @CrossOrigin
    @GetMapping("/fetchTableData")
    public List<StateWiseData> fetchAll() {
        return trackCovid19Service.getAllState();
    }

    @CrossOrigin
    @PostMapping("/AddState")
    public StateWiseData addState(@Valid @RequestBody StateWiseData stateWiseData) {
        StateWiseData stateData=trackCovid19Service.createPerState(stateWiseData);
        LastUpdated lastUpdated=new LastUpdated();
        lastUpdated.setLastUpdated(new Date());


        if(null!=stateData){
            LastUpdated lastData=trackCovid19Service.createLastUpdate(lastUpdated);
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
        return  trackCovid19Service.calculateTotals();
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
    public int fetchEstimatedCases() {
        return trackCovid19Service.estimateCoronaConfirmed();
    }
}
