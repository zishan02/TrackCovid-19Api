package com.trackcovid19.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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
import org.springframework.web.client.RestTemplate;

public class TrackCovidDataExtract {

  @Autowired private TrackCovid19Service trackCovid19Service;

  @Autowired private Environment env;

  @Autowired private TrackCovid19ChartDataRepo trackCovid19ChartDataRepo;

  @Autowired private TrackCovid19ChartDeathRepo trackCovid19ChartDeathRepo;

  @Autowired
  private FirebaseMessagingService firebaseMessagingService;

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
  @Scheduled(cron = "0 * * * * *", zone = "IST")
  public void findVaccineForLucknow() {
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusDays(7);
    long numOfDays = ChronoUnit.DAYS.between(startDate, endDate);
    List<LocalDate> listOfDates2 = LongStream.range(0, numOfDays)
            .mapToObj(startDate::plusDays)
            .collect(Collectors.toList());
    for (int i = 0; i < listOfDates2.size(); i++) {
      LocalDate date = listOfDates2.get(i);
      String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
      System.out.println(formattedDate);
      String uri = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByPin?pincode=226030&date=" + formattedDate;
      RestTemplate restTemplate = new RestTemplate();
      VaccinePincode result = restTemplate.getForObject(uri, VaccinePincode.class);
      System.out.println(result.getSessions().toString());
      if (null != result && result.getSessions().size() > 0) {
        List<Session> vacc = result.getSessions().stream().filter(session -> "SPUTNIK V".equalsIgnoreCase(session.getVaccine())).collect(Collectors.toList());
        System.out.println(vacc.get(0).getAvailableCapacityDose1());
        List avail = vacc.stream().filter(session -> session.getAvailableCapacity() > 0).collect(Collectors.toList());

        if (avail.size() >0 ) {
          System.out.println("TRUE");
          Note note=new Note();
          note.setSubject("SPUTNIK AVAILABLE at Medanta");
          note.setContent("SPUTNIK Available");
          note.setData(new HashMap<String,String>());
          try {
            firebaseMessagingService.sendNotification(note, "weather");
          }catch (Exception e){

          }
          return;
        }
      }
      System.out.println("TESTING");
    }
    //System.out.println("printing the output of Vaccine finder API" + result.getSessions().get(0).getVaccine());

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
