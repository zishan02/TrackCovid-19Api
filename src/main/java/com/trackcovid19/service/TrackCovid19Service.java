package com.trackcovid19.service;

import com.trackcovid19.Repository.TrackCovid19LastUpdateRepo;
import com.trackcovid19.Repository.TrackCovid19Repo;
import com.trackcovid19.model.LastUpdated;
import com.trackcovid19.model.StateWiseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TrackCovid19Service {
    @Autowired
    private TrackCovid19Repo trackCovid19Repo;

    @Autowired
    private TrackCovid19LastUpdateRepo trackCovid19LastUpdateRepo;

    public StateWiseData createPerState(StateWiseData stateWiseData){
        List<StateWiseData> state=trackCovid19Repo.findByStateName(stateWiseData.getStateName());
        if(null!=state && state.size()>0) {
            state.get(0).setConfirmedCases(stateWiseData.getConfirmedCases());
            state.get(0).setRecoveredCases(stateWiseData.getRecoveredCases());
            state.get(0).setDeceased(stateWiseData.getDeceased());
            return trackCovid19Repo.save(state.get(0));
        }
        return trackCovid19Repo.save(stateWiseData);
    }

    public List<StateWiseData> getAllState(){
        return trackCovid19Repo.findAll();
    }

    public LastUpdated createLastUpdate(LastUpdated lastUpdated){
        Optional<LastUpdated> ls=trackCovid19LastUpdateRepo.findById("first");
        LastUpdated lu;
        if(!ls.isPresent()){
            lu= trackCovid19LastUpdateRepo.save(lastUpdated);
        }else {
            lu=ls.get();
            lu.setLastUpdated(new Date());
            lu= trackCovid19LastUpdateRepo.save(lu);

        }

        return lu;
    }
    public LastUpdated fetchLastUpdated(){
        Optional<LastUpdated> ls=trackCovid19LastUpdateRepo.findById("first");
        LastUpdated lu = ls.get();
        this.timeDiffCalc(lu.getLastUpdated(),new Date(),lu);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM hh:mm:ss a");
        String formattedDateTime=formatter.format(lu.getLastUpdated());
        String[] s=formattedDateTime.split(" ");
        lu.setDate(s[0]);
        lu.setTime(s[1]+" "+s[2]);
        return lu;
    }

    public void timeDiffCalc(Date d1, Date d2, LastUpdated lu) {


        try {

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            lu.setTimeDiffHr(diffHours);
            lu.setTimeDiffMn(diffMinutes);
            if(diffHours==0){

                lu.setTimeDiffText("About "+diffMinutes+" Minutes Ago");
            }else if(diffMinutes==0){

                lu.setTimeDiffText("About "+diffHours+ " Hours Ago");
            }else {

                lu.setTimeDiffText("About "+diffHours+" Hours "+diffMinutes+" Minutes Ago");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

}}