package com.secqme.crimedata.domain.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Edmund on 1/26/15.
 */

@Entity
@Table(name="safer_streets_request")
@NamedQueries({
        @NamedQuery(name = SaferStreetsRequestVO.QUERY_FIND_BY_USER_EMAIL,
                query = "SELECT o " +
                        "FROM SaferStreetsRequestVO o " +
                        "WHERE o.email = :email"),
        @NamedQuery(name = SaferStreetsRequestVO.QUERY_FIND_BY_USER_ID,
                query = "SELECT o " +
                        "FROM SaferStreetsRequestVO o " +
                        "WHERE o.userid = :userid"),
        @NamedQuery(name = SaferStreetsRequestVO.QUERY_FIND_BY_CITY,
                query = "SELECT o " +
                        "FROM SaferStreetsRequestVO  o " +
                        "WHERE o.city = :city"),
        @NamedQuery(name = SaferStreetsRequestVO.QUERY_FIND_BY_USER_ID_AND_LOCATION,
                query = "SELECT o " +
                        "FROM SaferStreetsRequestVO  o " +
                        "WHERE o.userid = :userid " +
                        "AND o.city = :city " +
                        "AND o.state LIKE :state " +
                        "AND o.country LIKE :country"),
        @NamedQuery(name = SaferStreetsRequestVO.QUERY_FIND_BY_USER_EMAIL_AND_LOCATION,
                query = "SELECT o " +
                        "FROM SaferStreetsRequestVO  o " +
                        "WHERE o.email = :email " +
                        "AND o.city = :city " +
                        "AND o.state LIKE :state " +
                        "AND o.country LIKE :country"),
        @NamedQuery(name = SaferStreetsRequestVO.QUERY_FIND_ALL_GROUP_BY_USER_ID,
                query = "SELECT o " +
                        "FROM SaferStreetsRequestVO o " +
                        "GROUP BY o.userid"),
        @NamedQuery(name = SaferStreetsRequestVO.QUERY_FIND_TOTAL_REQUEST,
                query = "SELECT COUNT(o.id) " +
                        "FROM SaferStreetsRequestVO o")

})
public class SaferStreetsRequestVO implements Serializable {

    public static final String QUERY_FIND_BY_USER_EMAIL = "saferstreetsrequestvo.findByUserEmail";
    public static final String QUERY_FIND_BY_USER_ID = "saferstreetsrequestvo.findByUserId";
    public static final String QUERY_FIND_BY_CITY = "saferstreetsrequestvo.findByCityName";
    public static final String QUERY_FIND_BY_USER_ID_AND_LOCATION ="saferstreetsrequestvo.findByUserIdAndLocation";
    public static final String QUERY_FIND_BY_USER_EMAIL_AND_LOCATION ="saferstreetsrequestvo.findByUserEmailAndLocation";
    public static final String QUERY_FIND_ALL_GROUP_BY_USER_ID ="saferstreetsrequestvo.findAllGroupByUserid";
    public static final String QUERY_FIND_TOTAL_REQUEST ="saferstreetsrequestvo.findTotalRequests";

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "safer_streets_request_id_seq")
    @SequenceGenerator(
            name = "safer_streets_request_id_seq",
            sequenceName = "safer_streets_request_id_seq",
            allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "userid")
    private String userid;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "postcode")
    private String postcode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_time")
    private Date requestTime;

    public SaferStreetsRequestVO() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
