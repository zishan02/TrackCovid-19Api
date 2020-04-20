package com.trackcovid19.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.Documented;
import java.util.Comparator;
import java.util.Date;

@Document
public class StateWiseData implements Comparable<StateWiseData> {
    @Id
    private String id;

    private String stateName;

    private String confirmedCases;

    private String recoveredCases;

    private String deceased;

    private Date lastUpdated;

    private int changeConfirmed;

    private int changeRecovered;

    private int changeDeceased;

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getChangeConfirmed() {
        return changeConfirmed;
    }

    public void setChangeConfirmed(int changeConfirmed) {
        this.changeConfirmed = changeConfirmed;
    }

    public int getChangeRecovered() {
        return changeRecovered;
    }

    public void setChangeRecovered(int changeRecovered) {
        this.changeRecovered = changeRecovered;
    }

    public int getChangeDeceased() {
        return changeDeceased;
    }

    public void setChangeDeceased(int changeDeceased) {
        this.changeDeceased = changeDeceased;
    }

    public StateWiseData(String stateName, String confirmedCases, String recoveredCases, String deceased) {
        this.stateName = stateName;
        this.confirmedCases = confirmedCases;
        this.recoveredCases = recoveredCases;
        this.deceased = deceased;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getConfirmedCases() {
        return confirmedCases;
    }

    public void setConfirmedCases(String confirmedCases) {
        this.confirmedCases = confirmedCases;
    }

    public String getRecoveredCases() {
        return recoveredCases;
    }

    public void setRecoveredCases(String recoveredCases) {
        this.recoveredCases = recoveredCases;
    }

    public String getDeceased() {
        return deceased;
    }

    public void setDeceased(String deceased) {
        this.deceased = deceased;
    }

    @Override
    public String toString() {
        return "StateWiseData{" +
                "stateName='" + stateName + '\'' +
                ", confirmedCases='" + confirmedCases + '\'' +
                ", recoveredCases='" + recoveredCases + '\'' +
                ", deceased='" + deceased + '\'' +
                '}';
    }

    @Override
    public int compareTo(StateWiseData o) {
       return Integer.parseInt(o.getConfirmedCases())-Integer.parseInt(this.getConfirmedCases());
    }
}
