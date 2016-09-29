package com.secqme.crimedata.domain.model;

import com.secqme.util.location.LocationUtil;
import com.secqme.util.spring.BeanType;
import com.secqme.util.spring.DefaultSpringUtil;

import java.beans.beancontext.BeanContext;
import java.io.Serializable;

/**
 * Created by Edmund on 5/13/15.
 */
public class CrimeCoordinate implements Serializable {

    private Double latitude;
    private Double longitude;
    private boolean generateDetails;
    private Double centerLatitude;
    private Double centerLongitude;
    private Double topLeftLatitude;
    private Double topLeftLongitude;
    private Double btmRightLatitude;
    private Double btmRightLongitude;

    public CrimeCoordinate(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.generateDetails = false;
    }

    public CrimeCoordinate(Double latitude, Double longitude, boolean generateDetails) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.generateDetails = generateDetails;
        generateCoordinates();
    }

    public CrimeCoordinate() {
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isGenerateDetails() {
        return generateDetails;
    }

    public void setGenerateDetails(boolean generateDetails) {
        this.generateDetails = generateDetails;
    }

    public Double getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(Double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public Double getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(Double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public Double getTopLeftLatitude() {
        return topLeftLatitude;
    }

    public void setTopLeftLatitude(Double topLeftLatitude) {
        this.topLeftLatitude = topLeftLatitude;
    }

    public Double getTopLeftLongitude() {
        return topLeftLongitude;
    }

    public void setTopLeftLongitude(Double topLeftLongitude) {
        this.topLeftLongitude = topLeftLongitude;
    }

    public Double getBtmRightLatitude() {
        return btmRightLatitude;
    }

    public void setBtmRightLatitude(Double btmRightLatitude) {
        this.btmRightLatitude = btmRightLatitude;
    }

    public Double getBtmRightLongitude() {
        return btmRightLongitude;
    }

    public void setBtmRightLongitude(Double btmRightLongitude) {
        this.btmRightLongitude = btmRightLongitude;
    }

    private void generateCoordinates(){
        if(this.generateDetails){
            LocationUtil locationUtil = (LocationUtil) DefaultSpringUtil.getInstance().getBean(BeanType.LOCATION_UTIL);

            /*
                Generate Center Coordinate
                    Bearing : 135 Degree
                    Distance : sqrt(2) * 0.25KM
             */
            CrimeCoordinate centerCoordinate = locationUtil.newCoordinatesByDistance(this.latitude,this.longitude,135.0, Math.sqrt(2) * 0.25,false);
            this.setCenterLatitude(centerCoordinate.getLatitude());
            this.setCenterLongitude(centerCoordinate.getLongitude());

            /*
                Set Top Left Coordinate
             */
            this.setTopLeftLatitude(this.latitude);
            this.setTopLeftLongitude(this.longitude);

            /*
                Generate Bottom Right Coordinate
                    Bearing : 135 Degree
                    Distance : sqrt(2) * 0.5KM
             */
            CrimeCoordinate btmRightCoordinate = locationUtil.newCoordinatesByDistance(this.latitude,this.longitude,135.0, Math.sqrt(2) * 0.5,false);
            this.setBtmRightLatitude(btmRightCoordinate.getLatitude());
            this.setBtmRightLongitude(btmRightCoordinate.getLongitude());
        }
    }
}
