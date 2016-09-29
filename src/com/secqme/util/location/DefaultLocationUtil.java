package com.secqme.util.location;

import com.secqme.crimedata.domain.model.CrimeCoordinate;
import com.secqme.crimedata.domain.model.CrimeDayTime;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * User: James Khoo
 * Date: 10/14/14
 * Time: 5:17 PM
 */
public class DefaultLocationUtil implements LocationUtil {
    private static final Logger myLog = Logger.getLogger(DefaultLocationUtil.class);

    public static final String REVERSE_GEOCODING_RESULT = "long_name";
    public static final String CHICAGO_CITY = "Chicago";
    public static final Double EARTH_RADIUS = 6371.0; // In Kilometres
    private String reverseGeoCodingUrl;

    public DefaultLocationUtil(String reverseGeoCodingUrl){
        this.reverseGeoCodingUrl = reverseGeoCodingUrl;
    }

    /*
        Editted on 20150514 - 0816
        Changed to Haversine Formula to calculate distance between two location

     */

    public double distanceBetweenTwoLocation(double lat1, double lon1, double lat2, double lon2) {
        double theta1 = Math.toRadians(lat1);
        double theta2 = Math.toRadians(lat2);
        double avgLamda = Math.toRadians(lon2 - lon1);

        return Math.acos(Math.sin(theta1) * Math.sin(theta2) + Math.cos(theta1) * Math.cos(theta2) * Math.cos(avgLamda)) * EARTH_RADIUS;
    }

    @Override
    public double calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        double theta1 = Math.toRadians(lat1);
        double theta2 = Math.toRadians(lat2);
        double avgLamda = Math.toRadians(lon2 - lon1);

        return Math.atan2(Math.cos(theta1) * Math.sin(theta2) - Math.sin(theta1) * Math.cos(theta2) * Math.cos(avgLamda),
                Math.sin(avgLamda) * Math.cos(theta2));
    }

    /*
            *** Distance's metric unit is KM
            * Set GenerateDetails to true to generate center, Top Left and Bottom Right Coordinates
         */
    @Override
    public CrimeCoordinate newCoordinatesByDistance(Double latitude, Double longitude, Double bearing, Double distance, boolean generateDetails) {
        double dist = distance/EARTH_RADIUS;
        double brng = Math.toRadians(bearing);
        double lat1 = Math.toRadians(latitude);
        double lon1 = Math.toRadians(longitude);

        double lat2 = Math.asin( Math.sin(lat1)*Math.cos(dist) + Math.cos(lat1)*Math.sin(dist)*Math.cos(brng) );
        double a = Math.atan2(Math.sin(brng)*Math.sin(dist)*Math.cos(lat1), Math.cos(dist)-Math.sin(lat1)*Math.sin(lat2));
        double lon2 = lon1 + a;
        lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;
        CrimeCoordinate crimeCoordinate = new CrimeCoordinate(round(Math.toDegrees(lat2),7),round(Math.toDegrees(lon2),7),generateDetails);
        return crimeCoordinate;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

//    int rowIndex = (crimeMatrix.length - 1) - ((int) Math.floor((locationUtil.distanceBetweenTwoLocation(latitude, longitude,
//            cityHashMap.get(cityName).getLowerLeftLat(), longitude)) / crimeCellDistanceInKM));
//    int colIndex = (int) Math.floor((locationUtil.distanceBetweenTwoLocation(latitude, longitude,
//            latitude, cityHashMap.get(cityName).getLowerLeftLng())) / crimeCellDistanceInKM);

    @Override
    public int calculateRowIndex(int matrixLength, double lat1, double lng1, double lat2, double lng2, double crimeCellDistanceInKM) {
        return (matrixLength - 1) - ((int) Math.floor((this.distanceBetweenTwoLocation(lat1,lng1,lat2,lng2))/ crimeCellDistanceInKM));
    }

    @Override
    public int calculateColumnIndex(double lat1, double lng1, double lat2, double lng2, double crimeCellDistanceInKM) {
        return ((int) Math.floor((this.distanceBetweenTwoLocation(lat1,lng1,lat2,lng2)) / crimeCellDistanceInKM));
    }

    @Override
    public boolean isChichagoUser(double lat, double lng) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            reverseGeoCodingUrl = reverseGeoCodingUrl.replace("{USER_LATITUDE}",Double.toString(lat))
                    .replace("{USER_LONGITUDE}",Double.toString(lng));
            HttpPost httpPost = new HttpPost(reverseGeoCodingUrl);
            HttpResponse response = httpClient.execute(httpPost);
            JSONObject jObj = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            JSONArray jsonArray = jObj.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for(int i = 0; i < jsonArray.length(); i ++){

                if(jsonArray.getJSONObject(i).getString(REVERSE_GEOCODING_RESULT).contains(CHICAGO_CITY)){
                    return true;
                }
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(JSONException ex){
            ex.printStackTrace();
        }

        return false;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public CrimeDayTime checkCityTimeZone(String cityTimeZone){

        if(cityTimeZone != null){
//            myLog.debug(cityTimeZone);
            TimeZone timeZone = TimeZone.getTimeZone(cityTimeZone);
            if(timeZone != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MILLISECOND, -(calendar.getTimeZone().getRawOffset()));
                calendar.add(Calendar.MILLISECOND, timeZone.getRawOffset());
                Date cityDate = new Date(calendar.getTimeInMillis());
//                myLog.debug(cityTimeZone + " current time is " + cityDate.getHours() + ":" + cityDate.getMinutes());
               return this.getCrimeDayTime(cityDate);
            }else{
                myLog.debug("timezone is null");
                return null;
            }
        }else{
            return CrimeDayTime.DAYTIME;
        }
    }

    @Override
    public CrimeDayTime getCrimeDayTime(Date crimeDate) {
        if(crimeDate.getHours() >= 7 && crimeDate.getHours() <= 18){
            return CrimeDayTime.DAYTIME;
        }else if(crimeDate.getHours()>=19 && crimeDate.getHours() <= 23){
            return CrimeDayTime.DARK;
        }else if (crimeDate.getHours() >= 0 && crimeDate.getHours() <= 6){
            return CrimeDayTime.DARK;
        }else{
            return CrimeDayTime.DAYTIME;
        }
    }
}
