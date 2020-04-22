package com.trackcovid19.service;

import com.trackcovid19.Repository.TrackCovid19LastUpdateRepo;
import com.trackcovid19.Repository.TrackCovid19Repo;
import com.trackcovid19.model.*;
import com.trackcovid19.utils.Formatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TrackCovid19Service {
    @Autowired
    private TrackCovid19Repo trackCovid19Repo;

    @Autowired
    private TrackCovid19LastUpdateRepo trackCovid19LastUpdateRepo;



    public StateWiseData createPerState(StateWiseData stateWiseData) {
        List<StateWiseData> state = trackCovid19Repo.findByStateName(stateWiseData.getStateName());
        if (null != state && state.size() > 0) {
            state.get(0).setRecoveredCases(stateWiseData.getRecoveredCases());
            state.get(0).setDeceased(stateWiseData.getDeceased());
            state.get(0).setLastUpdated(stateWiseData.getLastUpdated());
            Date lastUpdated=stateWiseData.getLastUpdated();
            if(Formatter.isToday(lastUpdated)){
                int diff=0;
                diff=Integer.parseInt(stateWiseData.getConfirmedCases())-Integer.parseInt(state.get(0).getConfirmedCases());
                state.get(0).setChangeConfirmed(state.get(0).getChangeConfirmed()+diff);
                diff=Integer.parseInt(stateWiseData.getRecoveredCases())-Integer.parseInt(state.get(0).getRecoveredCases());
                state.get(0).setChangeRecovered(state.get(0).getChangeRecovered()+diff);
                diff=Integer.parseInt(stateWiseData.getDeceased())-Integer.parseInt(state.get(0).getDeceased());
                state.get(0).setChangeDeceased(state.get(0).getChangeDeceased()+diff);

            }else {
                int diff=0;
                diff=Integer.parseInt(stateWiseData.getConfirmedCases())-Integer.parseInt(state.get(0).getConfirmedCases());
                state.get(0).setChangeConfirmed(diff);
                diff=Integer.parseInt(stateWiseData.getRecoveredCases())-Integer.parseInt(state.get(0).getRecoveredCases());
                state.get(0).setChangeRecovered(diff);
                diff=Integer.parseInt(stateWiseData.getDeceased())-Integer.parseInt(state.get(0).getDeceased());
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

            //in milliseconds
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

            File file = new File(getClass().getClassLoader().getResource("covid.xslx").getFile());   //creating a new file instance
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file
//creating Workbook instance that refers to .xlsx file
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            while (itr.hasNext()) {
                Row row = itr.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                //iterating over each column
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

        try {
            String month = new String();
            Long totalConfirmed = null;
            CovidChartData covidChartData = new CovidChartData();
            List<CovidChartData> covidChartList = new ArrayList<>();
            covidChartData.setxAxis(new ArrayList<>());
            covidChartData.setyAxis(new ArrayList<>());
            //creating a new file instance
            //creating a new file instance
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("covid.xslx");
            //obtaining bytes from the file
//creating Workbook instance that refers to .xlsx file
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(1);     //creating a Sheet object to retrieve object
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            while (itr.hasNext()) {
                Row row = itr.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                //iterating over each column
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (cell.getColumnIndex() == 0) {
                        month = cell.getStringCellValue();
                        covidChartData.getxAxis().add(month);
                    }
                    if (cell.getColumnIndex() == 1) {

                        totalConfirmed = Math.round(cell.getNumericCellValue());
                        covidChartData.getyAxis().add(totalConfirmed);
                    }


                }

            }

            return covidChartData;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<CovidIncrease> fetchLast5Increase(){
        try {
            int change=0;
            String percentageChange=null;
            String date=null;

            List<CovidIncrease> covidIncreaseList=new ArrayList<>();
            int count=0;
            int counter=0;
            Iterator<Cell> previous=null;
            //creating a new file instance
            //creating a new file instance
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("covid.xslx");
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(1);
            int noOfRow=sheet.getPhysicalNumberOfRows()-5;//creating a Sheet object to retrieve object
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            System.out.println(sheet.getTables().size());
            while (itr.hasNext()) {
                Row row = itr.next();
                count++;
                Iterator<Cell> cellIterator = row.cellIterator();
                //iterating over each column
                if(count>noOfRow) {
                    counter++;
                    CovidIncrease covidIncrease = new CovidIncrease();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        Cell previousCell=previous.next();
                        if (cell.getColumnIndex() == 0) {
                            date = cell.getStringCellValue();
                            covidIncrease.setDate(date);
                            covidIncrease.setNum(counter);
                        }
                        if (cell.getColumnIndex() == 1) {
                            change= (int) (cell.getNumericCellValue()-previousCell.getNumericCellValue());
                            long perChange=(Math.round((change/previousCell.getNumericCellValue())*100));
                            covidIncrease.setChange(change);
                            covidIncrease.setPercentageChangeVal(Math.round(perChange));
                            covidIncrease.setPercentageChange(perChange+"%");
                        }


                    }
                    covidIncreaseList.add(covidIncrease);
                }
                previous=row.cellIterator();
            }

            return covidIncreaseList;

        } catch (Exception e) {
            e.printStackTrace();


        }
        return null;
    }
    public List<StateWiseData> fetchTop5(){
        List<StateWiseData> ls= this.getAllState();
        List<StateWiseData> newList=new ArrayList<>();
        Collections.sort(ls);
        for(int i=0;i<5;i++){
            newList.add(ls.get(i));
        }
        return newList;
    }

    public CasesCount calculateTotals(){
        int totalActiveCases=0;
        int totalRecoveredCases=0;
        int totalDeceasedCases=0;
        List<StateWiseData> data=this.getAllState();
        for(StateWiseData state : data){

            totalActiveCases=totalActiveCases+Integer.parseInt(state.getConfirmedCases());
            totalRecoveredCases=totalRecoveredCases+Integer.parseInt(state.getRecoveredCases());
            totalDeceasedCases=totalDeceasedCases+Integer.parseInt(state.getDeceased());
        }
        CasesCount casesCount=new CasesCount();
        casesCount.setTotalActiveCases(totalActiveCases);
        casesCount.setTotalRecoveredCases(totalRecoveredCases);
        casesCount.setTotalDeceasedCases(totalDeceasedCases);

        return casesCount;
    }
    public EstimatedCases estimateCoronaConfirmed(){
        EstimatedCases estimatedCases=new EstimatedCases();
        List<CovidIncrease> covidIncreaseList=this.fetchLast5Increase();
        CasesCount casesCount=this.calculateTotals();
        if(covidIncreaseList.size()>0){
            int sumOfPer= covidIncreaseList.stream().mapToInt(value -> value.getPercentageChangeVal()).sum();
            double p=casesCount.getTotalActiveCases();
            double r=sumOfPer/500.0;
            double n=1;
            double t=7;
            double amount = p * Math.pow(1 + (r / n), n * t);
            int amt=(int) amount;
            estimatedCases.setEstimatedRecoveredWeek(amt);
            return estimatedCases;
        }
        return null;
    }
}