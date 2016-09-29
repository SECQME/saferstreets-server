package com.secqme.crimedata.domain.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Edmund on 1/13/15.
 */

@Entity
@Table(name="crime_streets_rating")
@NamedQueries({
        @NamedQuery(name=CrimeStreetsRatingVO.QUERY_FIND_BY_STREETS_NAME,
        query="SELECT o "
        + "FROM CrimeStreetsRatingVO  o "
        + "WHERE o.crimeStreetsID = :crimeStreetsID")
})

public class CrimeStreetsRatingVO implements Serializable {

    public static final String QUERY_FIND_BY_STREETS_NAME = "crimeStreertsRatingVO.findByStreetsName";

    @Id
    @Column(name = "crime_streets_id")
    public String crimeStreetsID;

    @Column(name = "city")
    public String city;

    @Column(name = "postcode")
    public String postcode;

    @Column(name = "state")
    public String state;

    @Column(name = "country")
    public String country;

    @Column(name = "avg_user_streets_safety_rating")
    public int avgUserStreetsSafetyRating;

    @Column(name = "crime_streets_rating")
    public String crimeStreetsRating;

    @Column(name = "grid_address")
    public String gridAddress;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    public Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    public Date updatedDate;

    public CrimeStreetsRatingVO() {
    }

    public String getCrimeStreets() {
        return crimeStreetsID;
    }

    public void setCrimeStreets(String crimeStreetsID) {
        this.crimeStreetsID = crimeStreetsID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAvgUserStreetsSafetyRating() {
        return avgUserStreetsSafetyRating;
    }

    public void setAvgUserStreetsSafetyRating(int avgUserStreetsSafetyRating) {
        this.avgUserStreetsSafetyRating = avgUserStreetsSafetyRating;
    }

    public String getCrimeStreetRating() {
        return crimeStreetsRating;
    }

    public void setCrimeStreetRating(String crimeStreetsRating) {
        this.crimeStreetsRating = crimeStreetsRating;
    }

    public String getGridAddress() {
        return gridAddress;
    }

    public void setGridAddress(String gridAddress) {
        this.gridAddress = gridAddress;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
