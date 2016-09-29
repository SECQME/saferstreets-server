package com.secqme.crimedata.domain.dao.jpa;

import com.secqme.crimedata.domain.dao.SaferStreetsRequestDAO;
import com.secqme.crimedata.domain.dao.SubscriptionNotificationRequestDAO;
import com.secqme.crimedata.domain.model.SaferStreetsRequestVO;
import com.secqme.crimedata.domain.model.SubscriptionNotificationRequestVO;

import java.util.List;

/**
 * Created by edward on 3/1/15.
 */
public class SubscriptionNotificationRequestJPADAO extends BaseJPADAO<SubscriptionNotificationRequestVO, String> implements SubscriptionNotificationRequestDAO {

    public SubscriptionNotificationRequestJPADAO(){
        super(SubscriptionNotificationRequestVO.class);
    }

    @Override
    public List<SubscriptionNotificationRequestVO> findByUserEmail(String email) {
        JPAParameter parameter = new JPAParameter()
                .setParameter("email", email);
        return executeQueryWithResultList(SubscriptionNotificationRequestVO.QUERY_FIND_BY_USER_EMAIL, parameter);
    }

    @Override
    public SubscriptionNotificationRequestVO findByUserEmailAndLocation(String email, String city, String state, String country) {
        JPAParameter parameter =
                new JPAParameter()
                        .setParameter("email", email)
                        .setParameter("city", city)
                        .setParameter("state", state)
                        .setParameter("country", country);
        return executeQueryWithSingleResult(SubscriptionNotificationRequestVO.QUERY_FIND_BY_USER_EMAIL_AND_LOCATION, parameter);
    }
}
