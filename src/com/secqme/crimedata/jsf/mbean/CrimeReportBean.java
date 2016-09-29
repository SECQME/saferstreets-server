package com.secqme.crimedata.jsf.mbean;

import com.secqme.crimedata.CoreException;
import com.secqme.crimedata.domain.dao.jpa.JPAHelper;
import com.secqme.crimedata.domain.model.*;
import com.secqme.crimedata.jsf.util.MessageController;
import com.secqme.crimedata.manager.DataManager;
import com.secqme.crimedata.manager.util.CrimeHotSpotCal;
import com.secqme.crimedata.manager.CrimeManager;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: James Khoo
 * Date: 10/3/14
 * Time: 4:59 PM
 */
public class CrimeReportBean implements Serializable {

    private final static Integer MAX_CRIME_TYPE_TO_SHOW = 3;
    private static String CITY_TOP_CRIME_TREND_SQL =
            "SELECT crimeType, " +
                    "count(*) As crimeCount " +
                    "FROM crimeData " +
                    "WHERE crimeType in ( " +
                    " CRIME_TYPE_LIST " +
                    ") " +
                    "AND city = \'THE_CITY\' " +
                    "AND crimeDate >= \'CRIME_START_DATE\' " +
                    "AND crimeDate <= \'CRIME_END_DATE' " +
                    "GROUP BY  crimeType " +
                    "ORDER BY  crimeType ASC ";
    private static List<String> crimeTypeList;
    private static SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    List<CrimeTypeCount> crimeTypeCountList;
    private List<CrimeTypeVO> crimeTypeVOList;
    private Logger myLog = Logger.getLogger(CrimeReportBean.class);
    private Date reportStartDate;
    private Date reportEndDate;
    private Date latestCrimeDate;
    private String cityName;
    private Integer reportDurationInDays = 14;
    private CrimeManager crimeManager;
    private DataManager dataManager;
    private List<CrimeDataVO> crimeList;
    private List<CrimeHotSpot> crimeHotSpotList;
    private JPAHelper jpaHelper;
    private String searchAddrStr = null;
    private String crimeTrendChartString = null;
    private CrimeTypeVO crimeTypeVO;
    private String description = null;
    private String displayName = null;
    private double crimeWeight = 0.0;
    private String crimeDayTime;
    private final static Double DEFAULT_WEIGHTED_AVERAGE = 0.85;


    public CrimeReportBean() {
    }

    public CrimeTypeVO getCrimeTypeVO() {
        return crimeTypeVO;
    }

    public void setCrimeTypeVO(CrimeTypeVO crimeTypeVO) {
        this.crimeTypeVO = crimeTypeVO;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getCrimeWeight() {
        return crimeWeight;
    }

    public void setCrimeWeight(double crimeWeight) {
        this.crimeWeight = crimeWeight;
    }

    public static List<String> getCrimeTypeList() {
        return crimeTypeList;
    }

    public static void setCrimeTypeList(List<String> crimeTypeList) {
        CrimeReportBean.crimeTypeList = crimeTypeList;
    }

    public JPAHelper getJpaHelper() {
        return jpaHelper;
    }

    public void setJpaHelper(JPAHelper jpaHelper) {
        this.jpaHelper = jpaHelper;
    }

    public List<CrimeTypeCount> getCrimeTypeCountList() {
        return crimeTypeCountList;
    }

    public void setCrimeTypeCountList(List<CrimeTypeCount> crimeTypeCountList) {
        this.crimeTypeCountList = crimeTypeCountList;
    }

//    private void calculateAllCrimeTypes() {
//            crimeTypeCountList = crimeCalUtil.sortCrimeListBaseOnCrimeTypeCount(getCrimeList());
//
//    }


    public String getSearchAddrStr() {
        return searchAddrStr;
    }

    public void setSearchAddrStr(String searchAddrStr) {
        this.searchAddrStr = searchAddrStr;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        myLog.debug("~~~ SET cityName = " + cityName);
        this.cityName = cityName;
    }

    public CrimeManager getCrimeManager() {
        return crimeManager;
    }

    public void setCrimeManager(CrimeManager crimeManager) {
        this.crimeManager = crimeManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    public Date getReportStartDate() {
        return reportStartDate;
    }

    public void setReportStartDate(Date reportStartDate) {
        this.reportStartDate = reportStartDate;
    }

    public Date getReportEndDate() {
        return reportEndDate;
    }

    public void setReportEndDate(Date reportEndDate) {
        this.reportEndDate = reportEndDate;
    }

    public List<CrimeDataVO> getCrimeList() {
        return crimeList;
    }

    public void setCrimeList(List<CrimeDataVO> crimeList) {
        this.crimeList = crimeList;
    }

    public Integer getReportDurationInDays() {
        return reportDurationInDays;
    }

    public String getCrimeDayTime() {
        return crimeDayTime;
    }

    public void setCrimeDayTime(String crimeDayTime) {
        this.crimeDayTime = crimeDayTime;
    }

    public void setReportDurationInDays(Integer reportDurationInDays) {
        this.reportDurationInDays = reportDurationInDays;
    }

    public int getCrimeTrendCount(){
//        myLog.debug("getCrimeTrendCount => " + (getCrimeTypeCountList().size() > 3 ? 3 : getCrimeTypeCountList().size()));
        return getCrimeTypeCountList().size() > 3 ? 3 : getCrimeTypeCountList().size();
    }

    public String getCrimeDataGoogleMapPoints() {
        StringBuffer strBuffer = new StringBuffer();
//        int index = 0;
////        List<CrimeDataVO> subList = crimeList.subList(0, 1000);
//        if(crimeList != null && crimeList.size() > 0)
//        for (CrimeDataVO crimeDataVO : crimeList) {
//            if (!crimeDataVO.getLocString().equals("0.0, 0.0")) {
//                strBuffer.append("new google.maps.LatLng(" + crimeDataVO.getLocString() + ")");
//                if (index < (crimeList.size() - 1)) {
//                    strBuffer.append(",\n");
//                }
//            }
//            index++;
//        }
        return strBuffer.toString();
    }


    public String getCrimeHotSpotGoogleMapPoints() {
        StringBuilder stringBuilder = new StringBuilder();
//        [ {center: new google.maps.LatLng(41.8875547225, -87.76444249299999), distance: 1500}];
        // comment out heat map

        int index = 0;
        if(crimeHotSpotList != null && crimeHotSpotList.size() > 0) {
            for(CrimeHotSpot crimeHotSpot : crimeHotSpotList) {
                stringBuilder.append("{ center: new google.maps.LatLng(" + crimeHotSpot.getCrimeCoordinate().getLatitude() +
                                     "," + crimeHotSpot.getCrimeCoordinate().getLongitude() + "), distance: " +
                                     crimeHotSpot.getRadius() + " }");
                if(index < (crimeHotSpotList.size() -1)) {
                    stringBuilder.append(",\n");
                }
            }
            index++;
        }
//        myLog.debug("Crime HotSpot Str->" + stringBuilder.toString());
        return stringBuilder.toString();
    }
//
//    public String getCrimeGridGoogleMapPoints(){
//        StringBuilder stringBuilder = new StringBuilder();
//        try {
//            Object[][] crimeMatrix = crimeManager.getLatestCityCrimeMatrix(cityName).getCrimeMatrix();
//            for(int idx = 0; idx < crimeMatrix.length; idx ++ ){
//                for(int idx2 = 0; idx2<crimeMatrix[0].length; idx2 ++){
//                    CrimeCoordinate crimeCoordinate = ((CityGridHolder) crimeMatrix[idx][idx2]).getCrimeCoordinate();
//                    stringBuilder.append("new google.maps.LatLngBounds("+
//                    "new google.maps.LatLng(" + crimeCoordinate.getBtmRightLatitude()+","+crimeCoordinate.getTopLeftLongitude()+"),"+
//                    "new google.maps.LatLng(" + crimeCoordinate.getTopLeftLatitude()+","+crimeCoordinate.getBtmRightLongitude()+"))");
//                    if(idx2 < crimeMatrix[0].length - 1){
//                        stringBuilder.append(",\n");
//                    }
//                }
//                if(idx < crimeMatrix.length - 1){
//                    stringBuilder.append(",\n");
//                }
//            }
//
//        }catch(CoreException ex){
//            ex.printStackTrace();
//        }
//        return stringBuilder.toString();
//    }

    /**
     * Change to support Morning DayTime Night CrimeReport
     * @return
     */
    public String getCrimeGridSafety(){
        StringBuilder stringBuilder = new StringBuilder();
//        try{
//            Object[][] crimeMatrix = crimeManager.getLatestCityCrimeMatrix(cityName).getCrimeMatrix();
//
//            CrimeDayTime dayTime = CrimeDayTime.valueOf(crimeDayTime);
//
//            for(int idx = 0; idx < crimeMatrix.length; idx ++ ){
//                for(int idx2 = 0; idx2<crimeMatrix[0].length; idx2 ++){
////                    CrimeCoordinate crimeCoordinate = ((CityGridHolder) crimeMatrix[idx][idx2]).getCrimeCoordinate();
//                    List<CrimeDataVO> crimeList = ((CityGridHolder) crimeMatrix[idx][idx2]).getCrimeDataVO();
//
//                    SafetyReport safetyreport = crimeManager.getSafetyReportByDayTime(cityName,
//                            crimeList, dayTime);
//                    if(safetyreport != null) {
//                        if (safetyreport.getSafetyRating().equals(SafetyRating.LOW_SAFETY)) {
//                            stringBuilder.append("'#CD2626'");
//                        } else if (safetyreport.getSafetyRating().equals(SafetyRating.MODERATE)) {
//                            stringBuilder.append("'#FFC125'");
//                        } else if (safetyreport.getSafetyRating().equals(SafetyRating.MODERATELY_SAFE)) {
//                            stringBuilder.append("'#B4EEB4'");
//                        } else {
//                            stringBuilder.append("'#B4EEB4'");
//                        }
//                    }else{
//                        stringBuilder.append("'#B4EEB4'");
//                    }
//                    if(idx2 < crimeMatrix[0].length - 1){
//                        stringBuilder.append(",\n");
//                    }
//                }
//                if(idx < crimeMatrix.length - 1){
//                    stringBuilder.append(",\n");
//                }
//            }
//
//        }catch(CoreException ex){
//            ex.getMessageAsJSON();
//        }
        return stringBuilder.toString();
    }


    public void jumpToPreviosReportDate() {
        if (reportStartDate != null) {
            reportStartDate = DateUtils.addDays(reportStartDate, -reportDurationInDays);
            reportEndDate = DateUtils.addDays(reportStartDate, reportDurationInDays);
        } else {
            reportEndDate = new Date();
            reportStartDate = DateUtils.addDays(reportEndDate, -reportDurationInDays);
        }

        extractCrimeData();
    }

    public void jumpToNextReportDate() {
        if (reportStartDate != null) {
            reportStartDate = DateUtils.addDays(reportStartDate, reportDurationInDays);
            reportEndDate = DateUtils.addDays(reportStartDate, reportDurationInDays);
        } else {
            reportEndDate = new Date();
            reportStartDate = DateUtils.addDays(reportEndDate, -reportDurationInDays);
        }

        extractCrimeData();

    }

    public void extractCrimeData() {
//        try {
//            myLog.debug("Extracting Crime Data for City->" + cityName);
//            if (latestCrimeDate == null) {
//                CrimeDataVO latestCrimeDataVO = crimeManager.getLatestCrimeDateByCity(cityName);
//                if (latestCrimeDataVO != null) {
//                    latestCrimeDate = latestCrimeDataVO.getCrimeDate();
//                }
//            }
//
//
//            if (reportStartDate == null && reportEndDate == null) {
//                crimeList = crimeManager.getDefaultCrimeDataList(cityName);
//                if (crimeList != null && crimeList.size() > 0) {
//                    reportStartDate = crimeList.get(crimeList.size() - 1).getCrimeDate();
//                    reportEndDate = crimeList.get(0).getCrimeDate();
//                }
//            } else {
//                crimeList = crimeManager.getCrimeDataList(cityName, reportStartDate, reportEndDate, crimeTypeList);
//            }
//            if (crimeList != null && crimeList.size() > 0) {
//                myLog.debug("Total " + crimeList.size() + " Crime Data extracted");
////                calculateAllCrimeTypes();
////                crimeHotSpotList = crimeCalUtil.generateCrimeHotSpotList(crimeManager.getLatestCityCrimeMatrix(cityName));
//            }
//
//        } catch (CoreException ce) {
//            myLog.error("Error extracting CrimeData for this City :" + cityName + ", reason:" + ce.getMessage());
//            MessageController.addCoreExceptionError(null, ce);
//        }

    }

    public List<CrimeTypeVO> getAllCrimeType(){
        crimeTypeVOList = dataManager.getAllCrimeType();
        return crimeTypeVOList;
    }

    public void saveAction(){
        for(CrimeTypeVO crime : crimeTypeVOList){
           crimeManager.updateCrimeTypeDetails(crime);
        }

    }
}
