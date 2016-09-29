package com.secqme.crimedata.domain.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Edmund on 10/29/14.
 */
public class CrimeHotSpot implements Serializable{

    private CrimeCoordinate crimeCoordinate;
    private Double radius;
    private List<CrimeDataVO> crimeList;
    private List<CrimeTypeCount> crimeTypeCountList;

    public CrimeHotSpot(){
    }

    public CrimeHotSpot(CrimeCoordinate crimeCoordinate, Double radius) {
        this.crimeCoordinate = crimeCoordinate;
        this.radius = radius;
    }

    public List<CrimeDataVO> getCrimeList() {
        return crimeList;
    }

    public void setCrimeList(List<CrimeDataVO> crimeList) {
        this.crimeList = crimeList;
    }

    public Double getRadius() { return radius; }

    public void setRadius(Double radius) {  this.radius = radius; }


    public List<CrimeTypeCount> getCrimeTypeCountList() {
        return crimeTypeCountList;
    }

    public void setCrimeTypeCountList(List<CrimeTypeCount> crimeTypeCountList) {
        this.crimeTypeCountList = crimeTypeCountList;
    }

    public CrimeCoordinate getCrimeCoordinate() {
        return crimeCoordinate;
    }

    public void setCrimeCoordinate(CrimeCoordinate crimeCoordinate) {
        this.crimeCoordinate = crimeCoordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrimeHotSpot that = (CrimeHotSpot) o;

        if (!crimeCoordinate.getLatitude().equals(that.crimeCoordinate.getLatitude())) return false;
        if (!crimeCoordinate.getLongitude().equals(that.crimeCoordinate.getLongitude())) return false;
        if (!radius.equals(that.radius)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = radius.hashCode();
        result = 31 * result + crimeCoordinate.getLatitude().hashCode();
        result = 31 * result + crimeCoordinate.getLongitude().hashCode();
        return result;
    }
}