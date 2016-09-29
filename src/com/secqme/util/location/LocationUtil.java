package com.secqme.util.location;

import com.secqme.crimedata.domain.model.CrimeCoordinate;
import com.secqme.crimedata.domain.model.CrimeDayTime;

import java.util.Date;

/**
 * User: James Khoo
 * Date: 10/14/14
 * Time: 3:06 PM
 */
public interface LocationUtil {

   // https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20geo.placemaker%20WHERE%20documentContent%20%3D%20%22I%20have%20to%20go%20to%20Taman%20Desa%2C%20KL%20to%20Work%20everyday%22%20AND%0A%20%20%20%20%20%20documentType%3D%22text%2Fplain%22&format=json&diagnostics=true&callback=
   public double distanceBetweenTwoLocation(double lat1, double lon1, double lat2, double lon2);
   public double calculateBearing(double lat1, double lon1, double lat2, double lon2);
   public CrimeCoordinate newCoordinatesByDistance(Double latitude, Double longitude, Double bearing,Double distance, boolean generateDetails);
   public int calculateRowIndex(int matrixLength,double lat1,double lat2, double lng1, double lng2, double crimeCellDistanceInKM);
   public int calculateColumnIndex(double lat1, double lat2, double lng1, double lng2 ,double crimeCellDistanceInKM);

   public boolean isChichagoUser(double lat, double lng);

   public CrimeDayTime checkCityTimeZone(String cityTimeZone);
   public CrimeDayTime getCrimeDayTime(Date crimeDate);

}
