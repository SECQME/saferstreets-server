package com.secqme.crimedata.source;

import com.secqme.crimedata.domain.dao.CityInfoDAO;
import com.secqme.crimedata.domain.dao.CrimeDataDAO;
import com.secqme.crimedata.domain.dao.UserStreetSafetyRatingDAO;
import com.secqme.crimedata.domain.model.*;
import com.secqme.util.cache.CacheKey;
import com.secqme.util.cache.CacheUtil;
import com.secqme.util.location.LocationUtil;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.*;

/**
 * Created by Edmund on 9/23/15.
 */
public class GenerateCrimeMatrix  implements ServletContextListener {



    private Object[][] crimeMatrix;
    private CityInfo cityInfo;
    private List<CrimeDataVO> crimeDataList;
    private List<UserStreetSafetyRatingVO> userRatingList;
    private LocationUtil locationUtil;
    private Date crimeReportStartDate;
    private Date crimeReportEndDate;
    private Date userRatingEndDate;
    private Date userRatingStartDate;
    private Double maxCrimeCount=0.0;
    private Double distanceBetweenCells = 0.5;
    private Double averageCrimePerCell=0.0;
    private CacheUtil cacheUtil;
    private boolean oldReport = false;
    private static final Logger myLog = Logger.getLogger(GenerateCrimeMatrix.class);

//    private CrimeDataDAO crimeDataDAO;
//    private UserStreetSafetyRatingDAO userStreetSafetyRatingDAO;
//    private CityInfoDAO cityInfoDAO;

//    public GenerateCrimeMatrix(CityInfo cityInfo,
//                               LocationUtil locationUtil,
//                               CacheUtil cacheUtil,
//                               CrimeDataDAO crimeDataDAO,
//                               UserStreetSafetyRatingDAO userStreetSafetyRatingDAO,
//                               CityInfoDAO cityInfoDAO) {
//        this.cityInfo = cityInfo;
//        this.locationUtil = locationUtil;
//        this.cacheUtil = cacheUtil;
//        this.crimeDataDAO = crimeDataDAO;
//        this.userStreetSafetyRatingDAO = userStreetSafetyRatingDAO;
//        this.cityInfoDAO = cityInfoDAO;
//        generateCrimeData();
//    }

    public GenerateCrimeMatrix(CityInfo cityInfo,
                               List<CrimeDataVO> crimeDataList,
                               List<UserStreetSafetyRatingVO> userRatingList,
                               Double distanceBetweenCells,
                               LocationUtil locationUtil,
                               CacheUtil cacheUtil,
                               boolean oldReport) {
        this.cityInfo = cityInfo;
        this.crimeDataList = crimeDataList;
        this.userRatingList = userRatingList;
        this.locationUtil = locationUtil;
        this.distanceBetweenCells = distanceBetweenCells;
        this.cacheUtil = cacheUtil;
        this.oldReport = oldReport;
        initCrimeMatrix();
        generateCrimeCoordinate();
        generateCrimeMatrix();
        generateUserStreetsRating();
        analyzeCrimeMatrix();
        generateCrimeTrend();
//        checkPreviousSafety();
        generateCrimeMatrixCache(oldReport);
    }

//    private void generateCrimeData(){
//        List<CityInfo> cityInfos = cityInfoDAO.findAll();
//        for(CityInfo city : cityInfos){
//            CrimeDataVO latestCrime = crimeDataDAO.findLatestCrimeDataFromCity(city.getCity());
//            if(latestCrime != null){
//                Date reportEndDate = latestCrime.getCrimeDate();
//                Date reportStartDate = DateUtils.addDays(reportEndDate, -14);
//                crimeDataList = crimeDataDAO.findCrimeFromCityInBetweenDate(city.getCity(), reportStartDate, reportEndDate);
//                userRatingList = userStreetSafetyRatingDAO.getUserRatingByCity(city.getCity());
//                initCrimeMatrix();
//                generateCrimeCoordinate();
//                generateCrimeMatrix();
//                generateUserStreetsRating();
//                analyzeCrimeMatrix();
//                generateCrimeTrend();
//                generateCrimeMatrixCache();
//
//            }else{
//                System.out.println("No crime data for city " + city.getCity());
//            }
//        }
//    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private void initCrimeMatrix() {
        System.out.println("initCrimeMatrix");
        Double distanceInVertical = locationUtil.distanceBetweenTwoLocation(cityInfo.getUpperRightLat(),cityInfo.getLowerLeftLng(),
                cityInfo.getLowerLeftLat(),cityInfo.getLowerLeftLng());
        Double distanceInHorizontal = locationUtil.distanceBetweenTwoLocation(cityInfo.getUpperRightLat(),cityInfo.getLowerLeftLng(),
                cityInfo.getUpperRightLat(),cityInfo.getUpperRightLng());

        Integer xLength = (int) Math.ceil(distanceInHorizontal / distanceBetweenCells);
        Integer yLength = (int) Math.ceil(distanceInVertical / distanceBetweenCells);

        myLog.debug(String.format("City name: %s, %s, %s", cityInfo.getName(), cityInfo.getState(), cityInfo.getCountry()));
        myLog.debug(String.format("Upper right: %f, %f", cityInfo.getUpperRightLat(), cityInfo.getUpperRightLng()));
        myLog.debug(String.format("Lower right: %f, %f", cityInfo.getLowerLeftLat(), cityInfo.getLowerLeftLng()));
        myLog.debug("xLength : " + xLength);
        myLog.debug("yLength : " + yLength);

        crimeMatrix = new Object[yLength][xLength];

        for (int row = 0; row < crimeMatrix.length; row++) {
            for (int column = 0; column < crimeMatrix[row].length; column++) {
                crimeMatrix[row][column] = new CityGridHolder(null,null,null,
                        null,null,null,
                        null,null,null);
            }
        }



    }

    private void generateCrimeCoordinate(){
        /*
            Use Top Left Coordinate to generate CrimeCoordinate
         */
        Double initialLatitude = cityInfo.getUpperRightLat();
        Double initialLongitude = cityInfo.getLowerLeftLng();
//        System.out.println("initialLatitude : " + initialLatitude + " initialLongitude :" + initialLongitude);
        if(crimeMatrix == null){
            initCrimeMatrix();
//            generateCrimeMatrix();
        }

        for(int row = 0; row < crimeMatrix.length; row ++ ){
            // Move Downwards
            if(row != 0){
                CrimeCoordinate coordinate = locationUtil.newCoordinatesByDistance(initialLatitude,initialLongitude,180.0,0.5,true);
                ((CityGridHolder) crimeMatrix[row][0]).setCrimeCoordinate(coordinate);
                initialLatitude = coordinate.getLatitude();
                initialLongitude = coordinate.getLongitude();
//                System.out.println(row + " row : " + initialLatitude + "0 column :" + initialLongitude);
            }else{
                ((CityGridHolder) crimeMatrix[0][0]).setCrimeCoordinate(new CrimeCoordinate(initialLatitude,initialLongitude,true));
            }
            for(int column = 1; column < crimeMatrix[0].length; column ++){
                // Move Left Side
                CrimeCoordinate coordinate = locationUtil.newCoordinatesByDistance(initialLatitude,initialLongitude,90.00194,0.5*(column),true);
                ((CityGridHolder) crimeMatrix[row][column]).setCrimeCoordinate(coordinate);
//                System.out.println(row + " row : " + coordinate.getLatitude() + " " + column +" column :" + coordinate.getLongitude());
            }
        }
    }
    private void generateCrimeMatrix() {
        if(crimeDataList.size() > 0) {
            crimeReportStartDate = crimeDataList.get(0).getCrimeDate();
            crimeReportEndDate = crimeDataList.get(0).getCrimeDate();

            if (crimeMatrix == null) {
                initCrimeMatrix();
            }

            System.out.println("Generating CrimeDataVO");
            for (CrimeDataVO crimeDataVO : crimeDataList) {
                if (crimeDataVO.getCrimeDate() != null) {
                    if (crimeDataVO.getCrimeDate().getTime() < crimeReportStartDate.getTime()) {
                        crimeReportStartDate = crimeDataVO.getCrimeDate();
                    }

                    if (crimeDataVO.getCrimeDate().getTime() > crimeReportEndDate.getTime()) {
                        crimeReportEndDate = crimeDataVO.getCrimeDate();
                    }
                }

                if (crimeDataVO.getLatitude() != 0.00 && crimeDataVO.getLongitude() != 0.00) {
                    int rowIndex = (crimeMatrix.length - 1) - ((int) Math.floor((locationUtil.distanceBetweenTwoLocation(crimeDataVO.getLatitude(), crimeDataVO.getLongitude(),
                            cityInfo.getLowerLeftLat(), crimeDataVO.getLongitude())) / distanceBetweenCells));
                    int colIndex = (int) Math.floor((locationUtil.distanceBetweenTwoLocation(crimeDataVO.getLatitude(), crimeDataVO.getLongitude(),
                            crimeDataVO.getLatitude(), cityInfo.getLowerLeftLng())) / distanceBetweenCells);


                    List<CrimeDataSimple> crimeList;
                    if (rowIndex < crimeMatrix.length && colIndex < crimeMatrix[0].length && rowIndex >= 0 && colIndex >= 0) {
                        if(cityInfo.isCrimeDayTimeReport()) {
                            if (crimeDataVO.getCrimeDayTime().equals(CrimeDayTime.DAYTIME)) {
                                if (((CityGridHolder) crimeMatrix[rowIndex][colIndex]).getDaytimeReport() == null) {
                                    crimeList = new ArrayList<CrimeDataSimple>();
                                    crimeList.add(new CrimeDataSimple(crimeDataVO));
                                } else {
                                    crimeList = ((CityGridHolder) crimeMatrix[rowIndex][colIndex]).getDaytimeReport();
                                    crimeList.add(new CrimeDataSimple(crimeDataVO));
                                }
                                ((CityGridHolder) crimeMatrix[rowIndex][colIndex]).setDaytimeReport(crimeList);
                            } else {
                                if (((CityGridHolder) crimeMatrix[rowIndex][colIndex]).getDarkReport() == null) {
                                    crimeList = new ArrayList<CrimeDataSimple>();
                                    crimeList.add(new CrimeDataSimple(crimeDataVO));
                                } else {
                                    crimeList = ((CityGridHolder) crimeMatrix[rowIndex][colIndex]).getDarkReport();
                                    crimeList.add(new CrimeDataSimple(crimeDataVO));
                                }
                                ((CityGridHolder) crimeMatrix[rowIndex][colIndex]).setDarkReport(crimeList);
                            }
                        }else{
                            if (((CityGridHolder) crimeMatrix[rowIndex][colIndex]).getDaytimeReport() == null) {
                                crimeList = new ArrayList<CrimeDataSimple>();
                                crimeList.add(new CrimeDataSimple(crimeDataVO));
                            } else {
                                crimeList = ((CityGridHolder) crimeMatrix[rowIndex][colIndex]).getDaytimeReport();
                                crimeList.add(new CrimeDataSimple(crimeDataVO));
                            }
                            ((CityGridHolder) crimeMatrix[rowIndex][colIndex]).setDaytimeReport(crimeList);
                        }


//                        CrimeCoordinate coordinate = ((CityGridHolder) crimeMatrix[rowIndex][colIndex]).getCrimeCoordinate();
//                        System.out.println("rowIndex : " + rowIndex);
//                        System.out.println("colIndex : " + colIndex);
//                        System.out.println("Center Latitude : " + coordinate.getCenterLatitude());
//                        System.out.println("Center Longitude : " + coordinate.getCenterLongtitude());
//                        System.out.println("CrimeDataVO Latitude : " +  crimeDataVO.getLatitude());
//                        System.out.println("CrimeDataVO Longitude : " + crimeDataVO.getLongitude());
//                        System.out.println("Distance between two point : " + locationUtil.distanceBetweenTwoLocation(
//                                coordinate.getCenterLatitude(),
//                                coordinate.getCenterLongtitude(),
//                                crimeDataVO.getLatitude(),
//                                crimeDataVO.getLongitude()
//                        ));
//                        System.out.println();
                    }
                }
            }
        }
    }

    private void generateUserStreetsRating(){
        if(userRatingList.size() > 0) {
            userRatingStartDate = userRatingList.get(0).getUserRatingTime();
            userRatingEndDate = userRatingList.get(0).getUserRatingTime();

            for (UserStreetSafetyRatingVO ratingVO : userRatingList) {
                if (ratingVO.getUserRatingTime() != null) {
                    if (ratingVO.getUserRatingTime().getTime() < userRatingStartDate.getTime()) {
                        userRatingStartDate = ratingVO.getUserRatingTime();
                    }

                    if (ratingVO.getUserRatingTime().getTime() > userRatingEndDate.getTime()) {
                        userRatingEndDate = ratingVO.getUserRatingTime();
                    }
                }


                if (ratingVO.getLatitude() != 0.00 && ratingVO.getLongitude() != 0.00) {
                    int colIndex = (crimeMatrix.length - 1) - ((int) Math.floor((locationUtil.distanceBetweenTwoLocation(ratingVO.getLatitude(), ratingVO.getLongitude(),
                            cityInfo.getLowerLeftLat(), ratingVO.getLongitude())) / distanceBetweenCells) );
                    int rowIndex = (int) Math.floor((locationUtil.distanceBetweenTwoLocation(ratingVO.getLatitude(), ratingVO.getLongitude(),
                            ratingVO.getLatitude(), cityInfo.getLowerLeftLng())) / distanceBetweenCells);

                    if (rowIndex < crimeMatrix.length && colIndex < crimeMatrix[0].length) {

                        List<UserStreetSafetyRatingVO> ratingList;
                        if (((CityGridHolder) crimeMatrix[rowIndex][colIndex]).getUserStreetSafetyRatingVO() == null) {
                            ratingList = new ArrayList<UserStreetSafetyRatingVO>();
                            ratingList.add(ratingVO);
                        } else {
                            ratingList = ((CityGridHolder) crimeMatrix[rowIndex][colIndex]).getUserStreetSafetyRatingVO();
                            ratingList.add(ratingVO);
                        }

                        // Add crimeData to the matrix
                        ((CityGridHolder) crimeMatrix[rowIndex][colIndex]).setUserStreetSafetyRatingVO(ratingList);

                    }
                }
            }
        }
    }

    private void analyzeCrimeMatrix(){
        System.out.println("analyzeCrimeMatrix");
        int totalNumOfCrimeGrid = 0;
//        int totalCrimeCount = 0;
        Double totalCrimeWeight = 0.0;
        // Analysing The Crime Matrix

        for(int row = 0; row < crimeMatrix.length; row++){
            for (int col=0; col < crimeMatrix[row].length; col++) {
                if(crimeMatrix[row][col] != null) {
                    Double crimeCount = 0.0;

                    totalNumOfCrimeGrid++;
                    List<CrimeDataSimple> daytime = ((CityGridHolder) crimeMatrix[row][col]).getDaytimeReport();
                    List<CrimeDataSimple> dark = ((CityGridHolder) crimeMatrix[row][col]).getDarkReport();
                    if(daytime != null) {
                        for (CrimeDataSimple crimeDataVO : daytime) {
                            if (crimeDataVO.getCrimeWeight() != null) {
                                crimeCount += crimeDataVO.getCrimeWeight();
                                totalCrimeWeight += crimeDataVO.getCrimeWeight();
                            }
                        }
                        if (crimeCount > maxCrimeCount) {
                            maxCrimeCount = crimeCount;
                        }
                    }
                    if(dark != null){
                        for (CrimeDataSimple crimeDataVO : dark) {
                            if (crimeDataVO.getCrimeWeight() != null) {
                                crimeCount += crimeDataVO.getCrimeWeight();
                                totalCrimeWeight += crimeDataVO.getCrimeWeight();
                            }
                        }
                        if (crimeCount > maxCrimeCount) {
                            maxCrimeCount = crimeCount;
                        }
                    }
                }
            }
        }
        averageCrimePerCell = totalCrimeWeight/totalNumOfCrimeGrid;
    }

    public void generateCrimeTrend(){
        System.out.println("generateCrimeTrend");
        for(int row = 0; row < crimeMatrix.length; row ++){
            for(int col = 0; col < crimeMatrix[0].length; col ++){
                List<CrimeDataSimple> crimeList = null;
                if(((CityGridHolder)crimeMatrix[row][col]).getDaytimeReport() != null){
//                    System.out.println("Daytime");
                    crimeList = ((CityGridHolder)crimeMatrix[row][col]).getDaytimeReport();
                    ((CityGridHolder)crimeMatrix[row][col]).setDaytimeCrimeTrend(getCrimeTrend(crimeList));
                    ((CityGridHolder)crimeMatrix[row][col]).setDaytimeSafetyReport(getSafetyReport(row, col, crimeList));
                }
                if(((CityGridHolder)crimeMatrix[row][col]).getDarkReport() != null){
//                    System.out.println("Night");
                    crimeList = ((CityGridHolder)crimeMatrix[row][col]).getDarkReport();
                    ((CityGridHolder)crimeMatrix[row][col]).setDarkCrimeTrend(getCrimeTrend(crimeList));
                    ((CityGridHolder)crimeMatrix[row][col]).setDarkSafetyReport(getSafetyReport(row, col, crimeList));
                }

            }
        }
    }

    private List<CrimeTrend> getCrimeTrend(List<CrimeDataSimple> crimeList){
        List<CrimeTrend> crimeTrends = new ArrayList<CrimeTrend>();
        if(crimeList != null){
            List<String> crimeTrend = new ArrayList<String>();
            TreeMap<String, Integer> crimeMap = new TreeMap<String, Integer>();
            TreeMap<String, Double> crimeWeightMap = new TreeMap<String, Double>();
            for(CrimeDataSimple crime : crimeList){
                crimeTrend.add(crime.getCrimeType().replace("_"," ").toLowerCase());
                crimeWeightMap.put(crime.getCrimeType().replace("_"," ").toLowerCase(),crime.getCrimeWeight());
            }
            for(String crimeName : crimeTrend){
                Integer count =  crimeMap.get(crimeName);
                crimeMap.put(crimeName,(count == null) ? 1: count+ 1);
            }

            for(Map.Entry<String,Integer> entry : crimeMap.entrySet()){
                Double crimeWeight = crimeWeightMap.get(entry.getKey()) == null ? 0.0 : crimeWeightMap.get(entry.getKey());
                CrimeTrend crime = new CrimeTrend(entry.getKey(),entry.getValue(),crimeWeight);
                crimeTrends.add(crime);
            }

        }
        return crimeTrends;
    }

    private SafetyReport getSafetyReport(int rowIndex, int colIndex,List<CrimeDataSimple>  crimeList){
        List<CrimeDataSimple> crimeWithinTheAreaList = new ArrayList<CrimeDataSimple>();
        SafetyRating safetyRating;
        SafetyReport safetyReport;
        double weightCount = 0.0;
        if(crimeList != null && crimeList.size() > 0){
            for(CrimeDataSimple crime : crimeList){
                crimeWithinTheAreaList.add(crime);
                weightCount += crime.getCrimeWeight();
            }
//            if (weightCount >= averageCrimePerCell) {
//                safetyRating = SafetyRating.LOW_SAFETY;
//            } else if (weightCount > 0 &&
//                    crimeWithinTheAreaList.size() < averageCrimePerCell) {
//                safetyRating = SafetyRating.MODERATE;
//            } else {
//                safetyRating = SafetyRating.MODERATELY_SAFE;
//            }
//            UPDATE ON 20160115-0935
            if (weightCount >= 90) {
                safetyRating = SafetyRating.LOW_SAFETY;
            } else if (weightCount >= 50 && weightCount <= 89 ) {
                safetyRating = SafetyRating.MODERATE;
            } else {
                safetyRating = SafetyRating.MODERATELY_SAFE;
            }
        }else{
//                    myLog.debug("CrimeData List is null / less than 1 therefore set to MODERATELY SAFE");
            safetyRating = SafetyRating.MODERATELY_SAFE;

        }
//        myLog.debug(safetyRating);
        safetyReport = new SafetyReport(rowIndex, colIndex, distanceBetweenCells * 1000,
                safetyRating, crimeWithinTheAreaList,
                crimeReportStartDate, crimeReportEndDate,
                averageCrimePerCell);
        return safetyReport;
    }


    private void generateCrimeMatrixCache(boolean oldReport){
//        try {
            System.out.println("generateCrimeMatrixCache");
            ObjectMapper mapper = new ObjectMapper();
            CityGridFeatures cityGridFeatures = new CityGridFeatures(distanceBetweenCells,
                    crimeReportStartDate, crimeReportEndDate, userRatingEndDate,
                    userRatingStartDate, maxCrimeCount, averageCrimePerCell, crimeMatrix.length, crimeMatrix[0].length);
//        cacheUtil.displayDetails();
            for (int i = 0; i < crimeMatrix.length; i++) {
                for (int j = 0; j < crimeMatrix[0].length; j++) {
                    if(oldReport){
                        cacheUtil.storeObjectIntoCache(CacheKey.CITY_CRIME_GRID + cityInfo.getName().replace(" ", "_").toLowerCase() + "_" + i + "_" + j +
                                CacheKey.OLD_CACHE,
                                (CityGridHolder) crimeMatrix[i][j]);
                    }else {
                        cacheUtil.storeObjectIntoCache(CacheKey.CITY_CRIME_GRID + cityInfo.getName().replace(" ", "_").toLowerCase() + "_" + i + "_" + j,
                                (CityGridHolder) crimeMatrix[i][j]);
                    }

                }
            }
//        cacheUtil.displayDetails();
            if(oldReport){
                cacheUtil.storeObjectIntoCache(CacheKey.CITY_GRID_FEATSURE_CACHE_PREFIX + cityInfo.getName().replace(" ", "_").toLowerCase() +
                        CacheKey.OLD_CACHE,
                        (CityGridFeatures) cityGridFeatures);
            }else {
                cacheUtil.storeObjectIntoCache(CacheKey.CITY_GRID_FEATSURE_CACHE_PREFIX + cityInfo.getName().replace(" ", "_").toLowerCase(),
                       (CityGridFeatures) cityGridFeatures);
            }
//        }catch(IOException ex){
//            ex.printStackTrace();
//        }
    }

    private void checkPreviousSafety() {
        String cityName = cityInfo.getName().replace(" ","_").toLowerCase();
        System.out.println("checkPreviousSafety for " + cityName );
        CrimeDayTime dayTime = locationUtil.checkCityTimeZone(cityInfo.getCityTimeZone());
        boolean stillDangerous = false;
        for(int row = 0; row < crimeMatrix.length; row ++){
            for(int col = 0; col < crimeMatrix[0].length; col ++) {
                CityGridHolder previousGrid = (CityGridHolder) cacheUtil.getCachedObject(CacheKey.CITY_CRIME_GRID + cityName + "_" + row + "_"+col,CityGridHolder.class);
                CityGridHolder currentGrid = (CityGridHolder) crimeMatrix[row][col];
                List<CrimeTrend> historyCrimeList;
                List<CrimeTrend> currentCrimeList;

                if (currentGrid != null && previousGrid != null) {
//                    System.out.println("Checking Grid on (" +row+","+col+")" );
                    if (dayTime.equals(CrimeDayTime.DAYTIME)) {
                        historyCrimeList = previousGrid.getDaytimeCrimeTrend();
                        currentCrimeList = currentGrid.getDaytimeCrimeTrend();
                    } else {
                        historyCrimeList = previousGrid.getDarkCrimeTrend();
                        currentCrimeList = currentGrid.getDarkCrimeTrend();
                    }
//                    System.out.println("Prepared Crime Trend");
                    TreeMap<String, Integer> currentMap = new TreeMap<String, Integer>();
                    TreeMap<String, Integer> previousMap = new TreeMap<String, Integer>();
//                    System.out.println("Check Crime Trend");
                    if(historyCrimeList != null && currentCrimeList != null) {
                        for (CrimeTrend crime : historyCrimeList) {
                            previousMap.put(crime.getCrimeType(), crime.getCrimeCount());
                        }
                        for (CrimeTrend crime : currentCrimeList) {
                            currentMap.put(crime.getCrimeType(), crime.getCrimeCount());
                        }
//                        System.out.println("Process for spike");
                        for (Map.Entry<String, Integer> entry : currentMap.entrySet()) {
                            if (previousMap.get(entry.getKey()) != null) {
                                Integer previousCrimeCount = previousMap.get(entry.getKey());
                                if (previousCrimeCount > entry.getValue()) {
                                    stillDangerous = true;
                                }
                            }
                        }
                    }
                }
                if(stillDangerous){
                    if(dayTime.equals(CrimeDayTime.DAYTIME)){
                        currentGrid.setDaytimeSafetyReport(previousGrid.getDaytimeSafetyReport());
                    }else{
                        currentGrid.setDarkSafetyReport(previousGrid.getDarkSafetyReport());
                    }
                }

            }
        }

    }

}
