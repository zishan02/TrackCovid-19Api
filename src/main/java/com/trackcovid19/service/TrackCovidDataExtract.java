package com.trackcovid19.service;

import com.trackcovid19.model.LastUpdated;
import com.trackcovid19.model.StateWiseData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TrackCovidDataExtract {

    @Autowired
    private TrackCovid19Service trackCovid19Service;

    @Autowired
    private Environment env;

    @Scheduled(cron = "0 30 8,17 * * *", zone = "IST")
    public void extractTableData() {
        try {
            String url = env.getProperty("app.web");
            Document doc = Jsoup.connect(url).get();
            Element table = doc.select("table").get(0);
            Elements rows = table.select("tr");
            String stateName = null;
            String confirmed = null;
            String recovered = null;
            String deceased = null;

            for (int i = 1; i < 33; i++) { //first row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");
                stateName = cols.get(1).text();
                confirmed = cols.get(2).text();
                recovered = cols.get(3).text();
                deceased = cols.get(4).text();
                if (stateName.contains("#")) {
                    stateName = stateName.replace("#", "");
                }
                StateWiseData stateWiseData = new StateWiseData(stateName, confirmed, recovered, deceased);
                stateWiseData.setLastUpdated(new Date());
                trackCovid19Service.createPerState(stateWiseData);

            }
            LastUpdated lastUpdated = new LastUpdated();
            lastUpdated.setLastUpdated(new Date());
            trackCovid19Service.createLastUpdate(lastUpdated);
            System.out.println("Executed");
        } catch (Exception e) {

            e.printStackTrace();
        }

    }


}
