package com.secqme.crimedata.domain.model;

import java.util.Date;

/**
 * Created by Edmund on 16/11/2015.
 * Simplify crime matrix report
 */
public class CrimeDataSimple {
    private Long id;
    private String crimeCaseId;
    private String crimeType;
    private Long crimeTypeId;
    private Date occurredAt;
    private Double latitude;
    private Double longitude;
    private CrimeDayTime crimeDayTime;
    private String ward;
    private String communityArea;
    private String district;
    private String block;
    private Double crimeWeight;
    private String displayName;
    private Boolean violent;

    public CrimeDataSimple( CrimeDataVO crimeData) {
        this.id = crimeData.getId();
        this.crimeCaseId = crimeData.getCrimeCaseID();
        this.crimeType = crimeData.getCrimeTypeVO().getName();
        this.crimeTypeId = crimeData.getCrimeTypeVO().getId();
        this.occurredAt = crimeData.getCrimeDate();
        this.latitude = crimeData.getLatitude();
        this.longitude = crimeData.getLongitude();
        this.crimeDayTime = crimeData.getCrimeDayTime();
        this.ward = crimeData.getWard();
        this.communityArea = crimeData.getCommunityArea();
        this.district = crimeData.getDistrict();
        this.block = crimeData.getBlock();
        this.crimeWeight = crimeData.getCrimeWeight();
        this.displayName = crimeData.getCrimeTypeVO().getDisplayName();
        this.violent = crimeData.getCrimeTypeVO().isViolent();
    }

    public CrimeDataSimple() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrimeCaseId() {
        return crimeCaseId;
    }

    public void setCrimeCaseId(String crimeCaseId) {
        this.crimeCaseId = crimeCaseId;
    }

    public String getCrimeType() {
        return crimeType;
    }

    public void setCrimeType(String crimeType) {
        this.crimeType = crimeType;
    }

    public Long getCrimeTypeId() {
        return crimeTypeId;
    }

    public void setCrimeTypeId(Long crimeTypeId) {
        this.crimeTypeId = crimeTypeId;
    }

    public Date getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Date occurredAt) {
        this.occurredAt = occurredAt;
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

    public CrimeDayTime getCrimeDayTime() {
        return crimeDayTime;
    }

    public void setCrimeDayTime(CrimeDayTime crimeDayTime) {
        this.crimeDayTime = crimeDayTime;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getCommunityArea() {
        return communityArea;
    }

    public void setCommunityArea(String communityArea) {
        this.communityArea = communityArea;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public Double getCrimeWeight() {
        return crimeWeight;
    }

    public void setCrimeWeight(Double crimeWeight) {
        this.crimeWeight = crimeWeight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean isViolent() {
        return violent;
    }

    public void setViolent(Boolean violent) {
        this.violent = violent;
    }
}
