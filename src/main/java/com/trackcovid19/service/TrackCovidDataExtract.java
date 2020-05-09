package com.trackcovid19.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.trackcovid19.Repository.TrackCovid19ChartDataRepo;
import com.trackcovid19.Repository.TrackCovid19ChartDeathRepo;
import com.trackcovid19.model.*;
import com.trackcovid19.utils.Formatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;

public class TrackCovidDataExtract {

  @Autowired private TrackCovid19Service trackCovid19Service;

  @Autowired private Environment env;

  @Autowired private TrackCovid19ChartDataRepo trackCovid19ChartDataRepo;

  @Autowired private TrackCovid19ChartDeathRepo trackCovid19ChartDeathRepo;

  @Scheduled(cron = "0 0 9,18 * * *", zone = "IST")
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

      for (int i = 1; i < 34; i++) { // first row is the col names so skip it.
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

  @Scheduled(cron = "0 10 09 * * *", zone = "IST")
  public void scheduleChartDataUpdate() {
    CasesCount casesCount = trackCovid19Service.calculateTotals();
    Optional<CovidChartData> data = trackCovid19ChartDataRepo.findById("5ea418303d084b2ff1a8adef");
    final Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -1);
    data.get().getxAxis().add(Formatter.getISTDate(cal.getTime()));
    data.get().getyAxis().add(Long.valueOf(casesCount.getTotalActiveCases()));
    trackCovid19ChartDataRepo.save(data.get());
  }

  public CovidChartData removeLastData() {
    List<CovidChartData> covidChartDatas = trackCovid19ChartDataRepo.findAll();
    if (null != covidChartDatas && covidChartDatas.size() > 0) {
      List<String> xAxis = covidChartDatas.get(0).getxAxis();
      List<Long> yAxis = covidChartDatas.get(0).getyAxis();
      xAxis.remove(xAxis.size() - 1);
      yAxis.remove(yAxis.size() - 1);
    }
    return trackCovid19ChartDataRepo.save(covidChartDatas.get(0));
  }

  @Scheduled(cron = "0 10 20 * * *", zone = "IST")
  public void updateData() {
    CasesCount casesCount = trackCovid19Service.calculateTotals();
    final Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -1);
    List<TrackCovid19DeathChart> trackCovid19DeathCharts = trackCovid19ChartDeathRepo.findAll();
    if (null != trackCovid19DeathCharts && trackCovid19DeathCharts.size() > 0) {
      TrackCovid19DeathChart trackCovid19DeathChart = trackCovid19DeathCharts.get(0);
      List<String> xAxis = trackCovid19DeathChart.getDate();
      List<Integer> yAxis = trackCovid19DeathChart.getTotalDeaths();
      xAxis.add(Formatter.getISTDate(cal.getTime()));
      yAxis.add(casesCount.getTotalDeceasedCases());
      trackCovid19ChartDeathRepo.save(trackCovid19DeathChart);
    }
    System.out.println("Executed : TrackCovid19DeathChartData");
  }
}
