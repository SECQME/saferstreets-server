package com.secqme.crimedata.domain.dao;

import com.secqme.crimedata.domain.model.CrimeStreetsRatingVO;

/**
 * Created by Edmund on 1/26/15.
 */
public interface CrimeStreetsRatingDAO extends BaseDAO<CrimeStreetsRatingVO, String> {

    public CrimeStreetsRatingVO getCrimeStreetsByName(String crimeStreetsID);
}
