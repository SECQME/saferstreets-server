package com.secqme.crimedata.domain.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Edmund on 1/2/15.
 */
@Entity
@Table(name = "user_street_safety_rating_logs")
@NamedQueries({
        @NamedQuery(name=UserStreetSafetyRatingVO.QUERY_FIND_BY_USER_ID,
        query = "SELECT o "
        + "FROM UserStreetSafetyRatingVO o "
        + "WHERE o.userid = :userid"),
        @NamedQuery(name=UserStreetSafetyRatingVO.QUERY_FIND_BY_CITY_NAME,
        query = "SELECT o "
        + "FROM UserStreetSafetyRatingVO o "
        + "WHERE o.city = :city")
})
public class UserStreetSafetyRatingVO implements Serializable {

    public static final String QUERY_FIND_BY_USER_ID = "userStreetSafetyRatingVO.findByUserId";
    public static final String QUERY_FIND_BY_CITY_NAME = "userStreetSafetyRatingVO.findByCityName";

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_street_safety_rating_log_id_seq")
    @SequenceGenerator(
            name = "user_street_safety_rating_log_id_seq",
            sequenceName = "user_street_safety_rating_log_id_seq",
            allocationSize = 1)
    @Column(name = "id", updatable=false)
    public Long id;

    @Column(name = "crime_streets_id")
    public String crimeStreetsID;

    @Column(name = "latitude")
    public double latitude;

    @Column(name = "longitude")
    public double longitude;

    @Column(name = "user_street_safety_rating")
    public int userStreetSafetyRating;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "user_rating_time")
    public Date userRatingTime;

    @Column(name = "userid")
    public String userid;

    @Column(name = "city")
    public String city;

    @Column(name = "state")
    public String state;

    @Column(name = "country")
    public String country;

    @Column(name = "postcode")
    public String postcode;

    @Column(name = "accuracy")
    public int accuracy;

    public UserStreetSafetyRatingVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrimeStreetsID() {
        return crimeStreetsID;
    }

    public void setCrimeStreetsID(String crimeStreetsID) {
        this.crimeStreetsID = crimeStreetsID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getUserStreetSafetyRating() {
        return userStreetSafetyRating;
    }

    public void setUserStreetSafetyRating(int userStreetSafetyRating) {
        this.userStreetSafetyRating = userStreetSafetyRating;
    }

    public Date getUserRatingTime() {
        return userRatingTime;
    }

    public void setUserRatingTime(Date userRatingTime) {
        this.userRatingTime = userRatingTime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }
}
