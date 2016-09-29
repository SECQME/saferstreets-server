package com.secqme.crimedata.domain.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Edmund on 10/5/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrimeTrend implements Serializable {

    private String crimeType;
    private Integer crimeCount;
    private Double crimeWeight;

    public CrimeTrend(String crimeType, Integer crimeCount,Double crimeWeight) {
        this.crimeType = crimeType;
        this.crimeCount = crimeCount;
        this.crimeWeight = crimeWeight;
    }

    public CrimeTrend() {
    }

    public String getCrimeType() {
        return crimeType;
    }

    public void setCrimeType(String crimeType) {
        this.crimeType = crimeType;
    }

    public Integer getCrimeCount() {
        return crimeCount;
    }

    public void setCrimeCount(Integer crimeCount) {
        this.crimeCount = crimeCount;
    }

    public Double getCrimeWeight() {
        return crimeWeight;
    }

    public void setCrimeWeight(Double crimeWeight) {
        this.crimeWeight = crimeWeight;
    }
}
