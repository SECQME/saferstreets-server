package com.secqme.crimedata.domain.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by edward on 4/1/15.
 */
@Entity
@Table(name="subscription_notification_request")
@NamedQueries({
        @NamedQuery(name = SubscriptionNotificationRequestVO.QUERY_FIND_BY_USER_EMAIL,
                query = "SELECT o " +
                        "FROM SubscriptionNotificationRequestVO o " +
                        "WHERE o.email = :email"),
        @NamedQuery(name = SubscriptionNotificationRequestVO.QUERY_FIND_BY_USER_EMAIL_AND_LOCATION,
                query = "SELECT o " +
                        "FROM SubscriptionNotificationRequestVO  o " +
                        "WHERE o.email = :email " +
                        "AND o.city = :city " +
                        "AND o.state LIKE :state " +
                        "AND o.country LIKE :country")

})
public class SubscriptionNotificationRequestVO implements Serializable {

    public static final String QUERY_FIND_BY_USER_EMAIL = "subscriptionNotificationRequestVO.findByUserEmail";
    public static final String QUERY_FIND_BY_USER_EMAIL_AND_LOCATION ="subscriptionNotificationRequestVO.findByUserEmailAndLocation";

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "subscription_notification_request_id_seq")
    @SequenceGenerator(
            name = "subscription_notification_request_id_seq",
            sequenceName = "subscription_notification_request_id_seq",
            allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_time")
    private Date requestTime;

    public SubscriptionNotificationRequestVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
