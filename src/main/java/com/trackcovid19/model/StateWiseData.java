package com.trackcovid19.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.Documented;

@Document
public class StateWiseData {
    @Id
    private String id;

    private String stateName;

    private String confirmedCases;

    private String recoveredCases;

    private String deceased;

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
}
