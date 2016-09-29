package com.secqme.crimedata.domain.dao.jpa;

import com.secqme.crimedata.domain.dao.CrimeStreetsRatingDAO;
import com.secqme.crimedata.domain.model.CrimeStreetsRatingVO;

import java.util.List;

/**
 * Created by Edmund on 1/26/15.
 */
public class CrimeStreetsRatingJPADAO extends BaseJPADAO<CrimeStreetsRatingVO, String> implements CrimeStreetsRatingDAO {

    public CrimeStreetsRatingJPADAO(){
        super(CrimeStreetsRatingVO.class);
    }

    @Override
    public CrimeStreetsRatingVO getCrimeStreetsByName(String crimeStreetsID) {

        JPAParameter parameter = new JPAParameter()
                .setParameter("crimeStreetsID", crimeStreetsID);
        List<CrimeStreetsRatingVO> list = executeQueryWithResultList(CrimeStreetsRatingVO.QUERY_FIND_BY_STREETS_NAME,parameter);
        if(list.size() > 0){
            return list.get(0);
        }else {
            return null;
        }
    }
}
