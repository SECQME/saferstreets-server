package com.secqme.crimedata.domain.dao;

import com.secqme.crimedata.domain.model.UserStreetSafetyRatingVO;

import java.util.List;

/**
 * Created by Edmund on 1/2/15.
 */
public interface UserStreetSafetyRatingDAO extends BaseDAO<UserStreetSafetyRatingVO, String>{

    public List<UserStreetSafetyRatingVO> getUserRatingById(String userid);
    public List<UserStreetSafetyRatingVO> getUserRatingByCity(String cityName);
}
