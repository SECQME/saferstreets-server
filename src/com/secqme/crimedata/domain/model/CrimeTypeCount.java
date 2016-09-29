package com.secqme.crimedata.domain.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * User: James Khoo
 * Date: 10/28/14
 * Time: 2:26 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrimeTypeCount implements Serializable {
    private String crimeType;
    private Integer crimeCount;

    public CrimeTypeCount() {
    }

    public CrimeTypeCount(String crimeType, Integer crimeCount) {
        this.crimeType = crimeType;
        this.crimeCount = crimeCount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrimeTypeCount that = (CrimeTypeCount) o;

        if (!crimeCount.equals(that.crimeCount)) return false;
        if (crimeType.equals(that.crimeType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = crimeType.hashCode();
        result = 31 * result + crimeCount.hashCode();
        return result;
    }
}
