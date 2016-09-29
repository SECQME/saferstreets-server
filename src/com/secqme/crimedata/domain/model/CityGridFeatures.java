package com.secqme.crimedata.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.gson.JsonParseException;
import com.secqme.util.cache.CacheKey;
import com.secqme.util.cache.CacheUtil;
import com.secqme.util.location.LocationUtil;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import javax.persistence.Cache;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * User: James Khoo
 * Date: 10/9/14
 * Time: 4:58 PM
 */
public class CityGridFeatures implements Serializable,Cloneable {
    // Lat, Lnt
    // 0.01 == 1 KM
    // 0.005 == 500 meter

    private Double distanceBetweenCells;
    private Date crimeReportStartDate;
    private Date crimeReportEndDate;
    private Date userRatingEndDate;
    private Date userRatingStartDate;
    private Double maxCrimeCount = 0.0;
    private Double averageCrimePerCell = 0.0;
    private Integer rowDimension = 0;
    private Integer colDimension = 0;

    public CityGridFeatures(Double distanceBetweenCells,
                            Date crimeReportStartDate,
                            Date crimeReportEndDate,
                            Date userRatingEndDate,
                            Date userRatingStartDate,
                            Double maxCrimeCount,
                            Double averageCrimePerCell,
                            Integer rowDimension,
                            Integer colDimension) {
        this.distanceBetweenCells = distanceBetweenCells;
        this.crimeReportStartDate = crimeReportStartDate;
        this.crimeReportEndDate = crimeReportEndDate;
        this.userRatingEndDate = userRatingEndDate;
        this.userRatingStartDate = userRatingStartDate;
        this.maxCrimeCount = maxCrimeCount;
        this.averageCrimePerCell = averageCrimePerCell;
        this.rowDimension = rowDimension;
        this.colDimension = colDimension;
    }

    public CityGridFeatures() {
    }

//    @JsonCreator
//    public static CityGridFeatures Create(String jObj) throws JsonParseException, JsonMappingException, IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        CityGridFeatures module = mapper.readValue(jObj, CityGridFeatures.class);
//        return module;
//    }

    public Double getDistanceBetweenCells() {
        return distanceBetweenCells;
    }

    public void setDistanceBetweenCells(Double distanceBetweenCells) {
        this.distanceBetweenCells = distanceBetweenCells;
    }
    public Date getCrimeReportStartDate() {
        return crimeReportStartDate;
    }

    public void setCrimeReportStartDate(Date crimeReportStartDate) {
        this.crimeReportStartDate = crimeReportStartDate;
    }

    public Date getCrimeReportEndDate() {
        return crimeReportEndDate;
    }

    public void setCrimeReportEndDate(Date crimeReportEndDate) {
        this.crimeReportEndDate = crimeReportEndDate;
    }

    public Date getUserRatingEndDate() {
        return userRatingEndDate;
    }

    public void setUserRatingEndDate(Date userRatingEndDate) {
        this.userRatingEndDate = userRatingEndDate;
    }

    public Date getUserRatingStartDate() {
        return userRatingStartDate;
    }

    public void setUserRatingStartDate(Date userRatingStartDate) {
        this.userRatingStartDate = userRatingStartDate;
    }

    public Double getMaxCrimeCount() {
        return maxCrimeCount;
    }

    public void setMaxCrimeCount(Double maxCrimeCount) {
        this.maxCrimeCount = maxCrimeCount;
    }

    public Double getAverageCrimePerCell() {
        return averageCrimePerCell;
    }

    public void setAverageCrimePerCell(Double averageCrimePerCell) {
        this.averageCrimePerCell = averageCrimePerCell;
    }

    public Integer getRowDimension() {
        return rowDimension;
    }

    public void setRowDimension(Integer rowDimension) {
        this.rowDimension = rowDimension;
    }

    public Integer getColDimension() {
        return colDimension;
    }

    public void setColDimension(Integer colDimension) {
        this.colDimension = colDimension;
    }
}
