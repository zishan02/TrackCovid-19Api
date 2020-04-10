package com.trackcovid19.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class LastUpdated {
    @Id
    private String id = "first";
    private Date lastUpdated;
    @Transient
    @JsonSerialize
    private String date;
    @Transient
    @JsonSerialize
    private String time;


    @Transient
    @JsonSerialize
    private String timeDiffText;

    @Transient
    @JsonSerialize
    private long timeDiffHr;

    @Transient
    @JsonSerialize
    private long timeDiffMn;

    public String getTimeDiffText() {
        return timeDiffText;
    }

    public void setTimeDiffText(String timeDiffText) {
        this.timeDiffText = timeDiffText;
    }

    public long getTimeDiffHr() {
        return timeDiffHr;
    }

    public void setTimeDiffHr(long timeDiffHr) {
        this.timeDiffHr = timeDiffHr;
    }

    public long getTimeDiffMn() {
        return timeDiffMn;
    }

    public void setTimeDiffMn(long timeDiffMn) {
        this.timeDiffMn = timeDiffMn;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
