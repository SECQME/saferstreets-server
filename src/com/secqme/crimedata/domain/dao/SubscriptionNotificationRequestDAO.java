package com.secqme.crimedata.domain.dao;

import com.secqme.crimedata.domain.model.SaferStreetsRequestVO;
import com.secqme.crimedata.domain.model.SubscriptionNotificationRequestVO;

import java.util.List;

/**
 * Created by edward on 4/1/15.
 */
public interface SubscriptionNotificationRequestDAO extends BaseDAO<SubscriptionNotificationRequestVO, String> {
    public List<SubscriptionNotificationRequestVO> findByUserEmail(String email);
    public SubscriptionNotificationRequestVO findByUserEmailAndLocation(String email, String city, String state, String country);
}