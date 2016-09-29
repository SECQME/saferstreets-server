package com.secqme.crimedata.manager;

import com.secqme.crimedata.CoreException;
import com.secqme.crimedata.domain.model.*;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Edmund on 9/22/15.
 */
public interface CrimeManager {

    public List<CrimeDataVO> getDefaultCrimeDataList(String cityName);
    public List<CrimeDataVO> getCrimeDataList(String cityName, Date startDate, Date endDate, List<String> crimeTypes);
    public List<UserStreetSafetyRatingVO> getUserStreetsRating(String cityName);
    public void generateCityCrimeMatrix(String cityName,boolean oldReport);
    public HashMap<String, CityInfo> getCityHashMap();

    public SafetyReport getSafetyReport(String cityName,Integer rowIndex, Integer colIndex) throws CoreException;
    public SafetyReport getSafetyReportByDayTime(String cityName,Integer rowIndex, Integer colIndex,CrimeDayTime crimeDayTime);
    public int minorTweakOnUserLat(int row, int col, double latitude, double longitude, String cityName);

    public boolean checkSubscriptionNotificationRequest(String email,String cityName, String state,String country);
    public boolean addNewSubscriptionNotificationRequest(JSONObject jsonObject);

    public boolean checkSaferStreetSupportRequest(String email,String userid,String cityName,String state,String country);
    public boolean addNewSaferStreetSupportRequest(JSONObject jsonObject);
    public boolean addNewUserStreetSafetyRating(JSONObject jsonObject);

    public long getTotalUserRequest();
    public boolean checkSupportCity(String cityName, String state,String country);
    public boolean checkSupportCity(double latitude, double longitude);
    public JSONObject updateCrimeDataWithCrimeWeight();
    public void updateCrimeTypeDetails(CrimeTypeVO crimeType);

    public CityGridFeatures getCityGridFeatures(String cityName);
    public CityGridHolder getCityGridHolder(String cityName, Integer rowIndex, Integer colIndex);


    public List<CrimeTypeVO> getCrimeTypeWithDescription(Date updatedDate);
}
