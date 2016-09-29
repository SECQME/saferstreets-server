//package com.secqme.crimedata.manager.util;
//
//import com.secqme.crimedata.CoreException;
//import com.secqme.crimedata.domain.model.*;
//import com.secqme.crimedata.manager.HotSpotHolder;
//import com.secqme.util.cache.CacheUtil;
//import com.secqme.util.location.LocationUtil;
//import org.apache.log4j.Logger;
//
//import java.util.*;
//
///**
// * Created by Edmund on 3/11/15.
// */
//public class CrimeCalWithCrimeWeight implements CrimeHotSpotCal {
//
//    private static final Logger myLog = Logger.getLogger(CrimeCalWithCrimeWeight.class);
//
//    private static final String CITY_HOTSPOT_CACHE_PREFIX = "cityCrimeHotSpot_";
//    private CacheUtil cacheUtil;
//    private LocationUtil locationUtil;
//
//    public CrimeCalWithCrimeWeight(CacheUtil cacheUtil,LocationUtil locationUtil) {
//        this.cacheUtil = cacheUtil;
//        this.locationUtil = locationUtil;
//    }
///*
//    Generate Crime HotSpot
//        - Initialize HotSpot based on weight see function initHotSportHolder()
//        - Check hotspot neighbour to cluster them together
//        - add hotspotlist to a masterlist
// */
//    @Override
//    public List<CrimeHotSpot> generateCrimeHotSpotList(CityGridFeatures cityGridFeatures) throws CoreException {
//
//        String cityName = cityGridFeatures.get
//        Double avgCrimePerCell = cityGridFeatures.getAverageCrimePerCell();
//        // Get data from cache
//        List<CrimeHotSpot> cityCrimeHotSpotList = (List<CrimeHotSpot>) cacheUtil.getCachedObject(CITY_HOTSPOT_CACHE_PREFIX+ cityName);
//        if(cityCrimeHotSpotList == null){
//            myLog.debug("Generating Crime Hot Spot");
//            Object[][] crimeMatrix = cityGridFeatures.getCrimeMatrix();
//            HotSpotHolder[][] hotSpotHolderArray = initHotSportHolder(crimeMatrix,avgCrimePerCell);
//            List<List<HotSpotHolder>> hotSpotHolderMasterList = new ArrayList<List<HotSpotHolder>>();
//
//            for( int row = 0; row < hotSpotHolderArray.length; row ++){
//                for(int col = 0; col < hotSpotHolderArray[row].length; col ++){
//                    if(hotSpotHolderArray[row][col] != null && !hotSpotHolderArray[row][col].getChecked()){
////                        myLog.debug(" generateCrimeHotSpotList row : " + row +  " AND col : " + col);
//                        HotSpotHolder hotSpotHolder = hotSpotHolderArray[row][col];
//                        List<HotSpotHolder> hotSpotList = new ArrayList<HotSpotHolder>();
//                        addHotSpotToList(hotSpotHolder,hotSpotList);
//                        checkNeighbourHotSpotArray(row,col,hotSpotHolderArray,hotSpotList);
//                        hotSpotHolderMasterList.add(hotSpotList);
//                    }
//                }
//            }
//            cityCrimeHotSpotList = createCrimeHotSpotList(hotSpotHolderMasterList);
//            cacheUtil.storeObjectIntoCache(CITY_HOTSPOT_CACHE_PREFIX + cityName, cityCrimeHotSpotList);
////            myLog.debug("CITY_HOTSPOT_CACHE_PREFIX => " + cacheUtil.getCachedObject(CITY_HOTSPOT_CACHE_PREFIX + cityName));
//        }else{
//            myLog.debug("Getting City ->" + cityName + " Crime Hot Spot List from the cache.");
//        }
//
//        return cityCrimeHotSpotList;
//    }
//
//    @Override
//    public List<CrimeTypeCount> sortCrimeListBaseOnCrimeTypeCount(List<CrimeDataVO> crimeDataList) {
//
//        List<CrimeTypeCount> crimeTypeCountList;
//        Map<String, Integer> crimeTypeMap = new HashMap<String, Integer>();
//        for (CrimeDataVO crimeData : crimeDataList) {
//            CrimeTypeVO crimeTypeVO = crimeData.getCrimeTypeVO();
//            if (crimeTypeMap.get(crimeTypeVO.getCrimeType()) == null) {
//                crimeTypeMap.put(crimeTypeVO.getCrimeType(), 1);
//            } else {
//                Integer newCrimeCount = crimeTypeMap.get(crimeTypeVO.getCrimeType()) + 1;
//                crimeTypeMap.put(crimeTypeVO.getCrimeType(), newCrimeCount);
//            }
//        }
//
//        crimeTypeCountList = new ArrayList<CrimeTypeCount>();
//        for (String crimeType : crimeTypeMap.keySet()) {
//            CrimeTypeCount crimeTypeCount = new CrimeTypeCount(crimeType, crimeTypeMap.get(crimeType));
//            crimeTypeCountList.add(crimeTypeCount);
//        }
//
//        Collections.sort(crimeTypeCountList, new Comparator<CrimeTypeCount>() {
//            @Override
//            public int compare(CrimeTypeCount o1, CrimeTypeCount o2) {
//                return o2.getCrimeCount() - o1.getCrimeCount();
//            }
//        });
//        return crimeTypeCountList;
//    }
//
//
//    /*
//        Initialize HotSpot
//            - Calculate the weight of crime in a grid
//            - Store crime into HotspotHolders when the crimeWeight is more than average
//                * Get AverageCrimePerCell from CityCrimeMatrix.java
//     */
//    private HotSpotHolder[][] initHotSportHolder(Object[][] crimeMatrix, Double avgCrimePerCell){
//        int totalRow = crimeMatrix.length;
//        int totalCol = crimeMatrix[0].length;
//        HotSpotHolder[][] hotSpotHolders = new HotSpotHolder[totalRow][totalCol];
////
////        for( int i = 0; i < totalRow; i ++){
////            for( int j = 0; j < totalCol; j++){
////                if(((CityGridHolder) crimeMatrix[i][j]).getCrimeDataVO() != null) {
////                    int crimeWeight = 0;
////                    for (CrimeDataVO crimeDataVO : ((CityGridHolder) crimeMatrix[i][j]).getCrimeDataVO()) {
////                        if(crimeDataVO.getCrimeWeight() != null) {
////                            crimeWeight += crimeDataVO.getCrimeWeight();
////                        }
////                    }
////                    if (crimeWeight >= avgCrimePerCell) {
////                        HotSpotHolder hotSpotHolder = new HotSpotHolder(((CityGridHolder) crimeMatrix[i][j]).getCrimeDataVO(), crimeWeight);
////                        hotSpotHolders[i][j] = hotSpotHolder;
////
////                    }
////                }
////            }
////        }
//        return hotSpotHolders;
//    }
//
//    private void addHotSpotToList(HotSpotHolder hotSpotHolder, List<HotSpotHolder> hotSpotList){
//        hotSpotHolder.setChecked(true);
//        hotSpotList.add(hotSpotHolder);
//
//    }
//
//    private void checkNeighbourHotSpotArray(int row, int col, HotSpotHolder[][] hotSpotHolderArray, List<HotSpotHolder> hotSpotList) {
//        int i = row;
//        int j = col;
//        int maxRow = hotSpotHolderArray.length;
//        int maxCol = hotSpotHolderArray[0].length;
////        myLog.debug(" checkNeighbourHotSpotArray row : " + row +  " AND col : " + col);
//        // Check Up
//        if (i + 1 < maxRow && hotSpotHolderArray[i + 1][col] != null && !hotSpotHolderArray[i + 1][col].getChecked()) {
//            addHotSpotToList(hotSpotHolderArray[i + 1][col], hotSpotList);
//            checkNeighbourHotSpotArray(i + 1, col, hotSpotHolderArray, hotSpotList);
//        }
//
//        // Check Down
//        if (i - 1 >= 0 && hotSpotHolderArray[i - 1][col] != null && !hotSpotHolderArray[i - 1][col].getChecked()) {
//            addHotSpotToList(hotSpotHolderArray[i - 1][col], hotSpotList);
//            checkNeighbourHotSpotArray(i - 1, col, hotSpotHolderArray, hotSpotList);
//        }
//
//        //Check Left
//        if (j - 1 >= 0 && hotSpotHolderArray[row][j - 1] != null && !hotSpotHolderArray[row][j - 1].getChecked()) {
//            addHotSpotToList(hotSpotHolderArray[row][j - 1], hotSpotList);
//            checkNeighbourHotSpotArray(row, j - 1, hotSpotHolderArray, hotSpotList);
//        }
//
//        //Check Right
//        if (j + 1 < maxCol && hotSpotHolderArray[row][j + 1] != null && !hotSpotHolderArray[row][j + 1].getChecked()) {
//            addHotSpotToList(hotSpotHolderArray[row][j + 1], hotSpotList);
//            checkNeighbourHotSpotArray(row, j + 1, hotSpotHolderArray, hotSpotList);
//        }
//
//        //Check LeftUpper Diagonal
//        if(i + 1 < maxRow && j - 1 >= 0 && hotSpotHolderArray[i + 1][j - 1] != null && !hotSpotHolderArray[i + 1][j - 1].getChecked()){
//            addHotSpotToList(hotSpotHolderArray[i + 1][j - 1],hotSpotList);
//            checkNeighbourHotSpotArray(i + 1, j - 1, hotSpotHolderArray, hotSpotList);
//        }
//
//        // Check RightUpper Diagonal
//        if(i + 1 < maxRow && j + 1 < maxCol && hotSpotHolderArray[i + 1][j + 1] != null && !hotSpotHolderArray[i + 1][j + 1].getChecked()){
//            addHotSpotToList(hotSpotHolderArray[i + 1][j + 1],hotSpotList);
//            checkNeighbourHotSpotArray(i + 1, j + 1, hotSpotHolderArray, hotSpotList);
//        }
//
//        // Check Left Down Diagonal
//        if(i - 1 >= 0 && j - 1 >= 0 && hotSpotHolderArray[i - 1][j - 1] != null && !hotSpotHolderArray[i - 1][j - 1].getChecked()){
//            addHotSpotToList(hotSpotHolderArray[i - 1][j - 1],hotSpotList);
//            checkNeighbourHotSpotArray(i - 1, j - 1, hotSpotHolderArray, hotSpotList);
//        }
//
//        // Check Right Downn Diagonal
//        if(i - 1 >= 0 && j + 1 < maxCol && hotSpotHolderArray[i - 1][j + 1] != null && !hotSpotHolderArray[i - 1][j + 1].getChecked()){
//            addHotSpotToList(hotSpotHolderArray[i - 1][j + 1],hotSpotList);
//            checkNeighbourHotSpotArray(i - 1, j + 1, hotSpotHolderArray, hotSpotList);
//        }
//
//
//    }
//
//    private List<CrimeHotSpot> createCrimeHotSpotList(List<List<HotSpotHolder>> hotSpotHolderMasterList) {
//        List<CrimeHotSpot> crimeHotSpotList = null;
//        if (hotSpotHolderMasterList.size() > 0) {
//            crimeHotSpotList = new ArrayList<CrimeHotSpot>();
//            for (List<HotSpotHolder> hotSpotHolderList : hotSpotHolderMasterList) {
//                List<CrimeDataVO> hotSpotCrimeDataList = new ArrayList<CrimeDataVO>();
//                List<CrimeDataVO> firstCrimeDataList = hotSpotHolderList.get(0).getCrimeDataList();
//                Double minLat = firstCrimeDataList.get(0).getLatitude();
//                Double maxLat = firstCrimeDataList.get(0).getLatitude();
//                Double minLng = firstCrimeDataList.get(0).getLongitude();
//                Double maxLng = firstCrimeDataList.get(0).getLongitude();
//
//                for (HotSpotHolder hotSpotHolder : hotSpotHolderList) {
//                    for (CrimeDataVO crimeData : hotSpotHolder.getCrimeDataList()) {
//                        hotSpotCrimeDataList.add(crimeData);
//                        if (crimeData.getLatitude() <= minLat) {
//                            minLat = crimeData.getLatitude();
//                        } else if (crimeData.getLatitude() > maxLat) {
//                            maxLat = crimeData.getLatitude();
//                        }
//
//                        if (crimeData.getLongitude() <= minLng) {
//                            minLng = crimeData.getLongitude();
//                        } else if (crimeData.getLongitude() > maxLng) {
//                            maxLng = crimeData.getLongitude();
//                        }
//                    }
//                }
//                Double newLat = maxLat - ((maxLat - minLat) / 2);
//                Double newLng = maxLng - ((maxLng - minLng) / 2);
//                // round up to 500 meters, 1 km 1.5 km
//                Double radius = (Math.floor(locationUtil.distanceBetweenTwoLocation(maxLat, maxLng, newLat, newLng)) + 0.5) * 1000;
////                if(radius < 10000) {
//                    CrimeCoordinate crimeCoordinate = new CrimeCoordinate(newLat,newLng);
//                    CrimeHotSpot crimeHotSpot = new CrimeHotSpot(crimeCoordinate,radius);
//                    crimeHotSpot.setCrimeList(hotSpotCrimeDataList);
//                    crimeHotSpot.setCrimeTypeCountList(this.sortCrimeListBaseOnCrimeTypeCount(hotSpotCrimeDataList));
////                    myLog.debug("CrimeHotSpot->[" + newLat + "," + newLng + "], radius->" + radius);
//                    crimeHotSpotList.add(crimeHotSpot);
////                }
//            }
//
//        }
//        return crimeHotSpotList;
//    }
//
//
//}
