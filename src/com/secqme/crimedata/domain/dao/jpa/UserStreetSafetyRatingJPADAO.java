package com.secqme.crimedata.domain.dao.jpa;

import com.secqme.crimedata.domain.dao.UserStreetSafetyRatingDAO;
import com.secqme.crimedata.domain.model.UserStreetSafetyRatingVO;

import java.util.List;

/**
 * Created by Edmund on 1/2/15.
 */
public class UserStreetSafetyRatingJPADAO extends BaseJPADAO<UserStreetSafetyRatingVO, String> implements UserStreetSafetyRatingDAO{

    public UserStreetSafetyRatingJPADAO() {
        super(UserStreetSafetyRatingVO.class);
    }

    @Override
    public List<UserStreetSafetyRatingVO> getUserRatingById(String userid) {
        JPAParameter parameter = new JPAParameter()
                .setParameter("userid",userid);
        return executeQueryWithResultList(UserStreetSafetyRatingVO.QUERY_FIND_BY_USER_ID,parameter);
    }

    @Override
    public List<UserStreetSafetyRatingVO> getUserRatingByCity(String cityName) {
        JPAParameter parameter = new JPAParameter()
                .setParameter("city", cityName);
        return executeQueryWithResultList(UserStreetSafetyRatingVO.QUERY_FIND_BY_CITY_NAME,parameter);
    }
}
