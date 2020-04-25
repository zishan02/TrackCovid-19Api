package com.trackcovid19.service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.trackcovid19.Repository.TrackCovid19ChartDataRepo;
import com.trackcovid19.Repository.TrackCovid19LastUpdateRepo;
import com.trackcovid19.Repository.TrackCovid19Repo;
import com.trackcovid19.model.*;
import com.trackcovid19.utils.Formatter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackCovid19Service {
  @Autowired private TrackCovid19Repo trackCovid19Repo;

  @Autowired private TrackCovid19LastUpdateRepo trackCovid19LastUpdateRepo;

  @Autowired private TrackCovid19ChartDataRepo trackCovid19ChartDataRepo;

  public StateWiseData createPerState(StateWiseData stateWiseData) {
    List<StateWiseData> state = trackCovid19Repo.findByStateName(stateWiseData.getStateName());
    if (null != state && state.size() > 0) {
      state.get(0).setRecoveredCases(stateWiseData.getRecoveredCases());
      state.get(0).setDeceased(stateWiseData.getDeceased());
      state.get(0).setLastUpdated(stateWiseData.getLastUpdated());
      Date lastUpdated = stateWiseData.getLastUpdated();
      if (Formatter.isToday(lastUpdated)) {
        int diff = 0;
        diff =
            Integer.parseInt(stateWiseData.getConfirmedCases())
                - Integer.parseInt(state.get(0).getConfirmedCases());
        state.get(0).setChangeConfirmed(state.get(0).getChangeConfirmed() + diff);
        diff =
            Integer.parseInt(stateWiseData.getRecoveredCases())
                - Integer.parseInt(state.get(0).getRecoveredCases());
        state.get(0).setChangeRecovered(state.get(0).getChangeRecovered() + diff);
        diff =
            Integer.parseInt(stateWiseData.getDeceased())
                - Integer.parseInt(state.get(0).getDeceased());
        state.get(0).setChangeDeceased(state.get(0).getChangeDeceased() + diff);

      } else {
        int diff = 0;
        diff =
            Integer.parseInt(stateWiseData.getConfirmedCases())
                - Integer.parseInt(state.get(0).getConfirmedCases());
        state.get(0).setChangeConfirmed(diff);
        diff =
            Integer.parseInt(stateWiseData.getRecoveredCases())
                - Integer.parseInt(state.get(0).getRecoveredCases());
        state.get(0).setChangeRecovered(diff);
        diff =
            Integer.parseInt(stateWiseData.getDeceased())
                - Integer.parseInt(state.get(0).getDeceased());
        state.get(0).setChangeDeceased(diff);
      }
      state.get(0).setConfirmedCases(stateWiseData.getConfirmedCases());
      return trackCovid19Repo.save(state.get(0));
    }
    return trackCovid19Repo.save(stateWiseData);
  }

  public List<StateWiseData> getAllState() {
    return trackCovid19Repo.findAll();
  }

  public LastUpdated createLastUpdate(LastUpdated lastUpdated) {
    Optional<LastUpdated> ls = trackCovid19LastUpdateRepo.findById("first");
    LastUpdated lu;
    if (!ls.isPresent()) {
      lu = trackCovid19LastUpdateRepo.save(lastUpdated);
    } else {
      lu = ls.get();
      lu.setLastUpdated(new Date());
      lu = trackCovid19LastUpdateRepo.save(lu);
    }

    return lu;
  }

  public LastUpdated fetchLastUpdated() {
    Optional<LastUpdated> ls = trackCovid19LastUpdateRepo.findById("first");
    LastUpdated lu = ls.get();
    this.timeDiffCalc(lu.getLastUpdated(), new Date(), lu);
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM hh:mm:ss a");
    formatter.setTimeZone(TimeZone.getTimeZone("IST"));
    String formattedDateTime = formatter.format(lu.getLastUpdated());
    String[] s = formattedDateTime.split(" ");
    lu.setDate(s[0]);
    lu.setTime(s[1] + " " + s[2]);
    return lu;
  }

  public void timeDiffCalc(Date d1, Date d2, LastUpdated lu) {

    try {

      // in milliseconds
      long diff = d2.getTime() - d1.getTime();
      long diffMinutes = diff / (60 * 1000) % 60;
      long diffHours = diff / (60 * 60 * 1000) % 24;
      lu.setTimeDiffHr(diffHours);
      lu.setTimeDiffMn(diffMinutes);
      if (diffHours == 0) {

        lu.setTimeDiffText("About " + diffMinutes + " Minutes Ago");
      } else if (diffMinutes == 0) {

        lu.setTimeDiffText("About " + diffHours + " Hours Ago");
      } else {

        lu.setTimeDiffText("About " + diffHours + " Hours " + diffMinutes + " Minutes Ago");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void readFromXLSAndUpdate() {
    try {
      String state = new String();
      String activeCases = null;
      String recovered = null;
      String deceased = null;

      File file =
          new File(
              getClass()
                  .getClassLoader()
                  .getResource("covid.xslx")
                  .getFile()); // creating a new file instance
      FileInputStream fis = new FileInputStream(file); // obtaining bytes from the file
      // creating Workbook instance that refers to .xlsx file
      XSSFWorkbook wb = new XSSFWorkbook(fis);
      XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
      Iterator<Row> itr = sheet.iterator(); // iterating over excel file
      while (itr.hasNext()) {
        Row row = itr.next();
        Iterator<Cell> cellIterator = row.cellIterator();
        // iterating over each column
        while (cellIterator.hasNext()) {
          Cell cell = cellIterator.next();
          if (cell.getColumnIndex() == 1) {
            state = cell.getStringCellValue();
          }
          if (cell.getColumnIndex() == 2) {

            activeCases = String.valueOf(Math.round(cell.getNumericCellValue()));
          }
          if (cell.getColumnIndex() == 3) {
            recovered = String.valueOf(Math.round(cell.getNumericCellValue()));
          }
          if (cell.getColumnIndex() == 4) {
            deceased = String.valueOf(Math.round(cell.getNumericCellValue()));
          }
        }
        StateWiseData stateWiseData = new StateWiseData(state, activeCases, recovered, deceased);
        stateWiseData.setLastUpdated(new Date());
        this.createPerState(stateWiseData);
      }

      LastUpdated lastUpdated = new LastUpdated();
      lastUpdated.setLastUpdated(new Date());
      this.createLastUpdate(lastUpdated);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public CovidChartData readChartDataFromExcel() {

    List<CovidChartData> covidChartDataList = trackCovid19ChartDataRepo.findAll();
    return covidChartDataList.get(0);
  }

  public List<CovidIncrease> fetchLast5Increase() {
    int change = 0;
    int num = 0;
    List<CovidIncrease> covidIncreaseList = new ArrayList<>();
    CovidChartData data = this.readChartDataFromExcel();
    List<Long> yAxisData = data.getyAxis();
    List<String> xAxisData = data.getxAxis();
    int length = yAxisData.size();
    int windowSize = length - 6;
    for (int i = windowSize; i < length - 1; i++) {
      CovidIncrease covidIncrease = new CovidIncrease();
      change = (int) (yAxisData.get(i + 1) - yAxisData.get(i));
      long perChange = Math.round(Double.valueOf((change * 100) / yAxisData.get(i)));
      num++;
      covidIncrease.setNum(num);
      covidIncrease.setDate(xAxisData.get(i + 1));
      covidIncrease.setChange(change);
      covidIncrease.setPercentageChangeVal(perChange);
      covidIncrease.setPercentageChange(perChange + "%");
      covidIncreaseList.add(covidIncrease);
    }
    return covidIncreaseList;
  }

  public List<StateWiseData> fetchTop5() {
    List<StateWiseData> ls = this.getAllState();
    List<StateWiseData> newList = new ArrayList<>();
    Collections.sort(ls);
    for (int i = 0; i < 5; i++) {
      newList.add(ls.get(i));
    }
    return newList;
  }

  public CasesCount calculateTotals() {
    int totalActiveCases = 0;
    int totalRecoveredCases = 0;
    int totalDeceasedCases = 0;
    List<StateWiseData> data = this.getAllState();
    for (StateWiseData state : data) {

      totalActiveCases = totalActiveCases + Integer.parseInt(state.getConfirmedCases());
      totalRecoveredCases = totalRecoveredCases + Integer.parseInt(state.getRecoveredCases());
      totalDeceasedCases = totalDeceasedCases + Integer.parseInt(state.getDeceased());
    }
    CasesCount casesCount = new CasesCount();
    casesCount.setTotalActiveCases(totalActiveCases);
    casesCount.setTotalRecoveredCases(totalRecoveredCases);
    casesCount.setTotalDeceasedCases(totalDeceasedCases);

    return casesCount;
  }

  public EstimatedCases estimateCoronaConfirmed() {
    EstimatedCases estimatedCases = new EstimatedCases();
    List<CovidIncrease> covidIncreaseList = this.fetchLast5Increase();
    CasesCount casesCount = this.calculateTotals();
    if (covidIncreaseList.size() > 0) {
      double sumOfPer =
          covidIncreaseList.stream().mapToDouble(value -> value.getPercentageChangeVal()).sum();
      double p = casesCount.getTotalActiveCases();
      double r = sumOfPer / 500.0;
      double n = 1;
      double t = 7;
      double amount = p * Math.pow(1 + (r / n), n * t);
      int amt = (int) amount;
      estimatedCases.setEstimatedRecoveredWeek(amt);
      return estimatedCases;
    }
    return null;
  }

  public void writeToExcel() {

    try {
      InputStream is = this.getClass().getClassLoader().getResourceAsStream("covid.xslx");
      Workbook workbook = WorkbookFactory.create(is);

      Sheet sheet = workbook.getSheetAt(1);
      CasesCount casesCount = this.calculateTotals();
      Object[][] totalCases = {
        {Formatter.getISTDate(new Date()), casesCount.getTotalActiveCases()},
      };

      int rowCount = sheet.getLastRowNum();

      for (Object[] aBook : totalCases) {
        Row row = sheet.createRow(++rowCount);

        int columnCount = 0;

        Cell cell = row.createCell(columnCount);
        cell.setCellValue(rowCount);

        for (Object field : aBook) {
          cell = row.createCell(++columnCount);
          if (field instanceof String) {
            cell.setCellValue((String) field);
          } else if (field instanceof Integer) {
            cell.setCellValue((Integer) field);
          }
        }
      }

      is.close();

      FileOutputStream outputStream =
          new FileOutputStream(new File(this.getClass().getResource("covid.xslx").getPath()));
      workbook.write(outputStream);
      workbook.close();
      outputStream.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
