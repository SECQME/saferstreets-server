package com.secqme.crimedata.domain.dao.jpa;

import com.secqme.crimedata.domain.dao.SaferStreetsRequestDAO;
import com.secqme.crimedata.domain.model.SaferStreetsRequestVO;

import java.util.List;

/**
 * Created by Edmund on 1/26/15.
 */
public class SaferStreetsRequestJPADAO extends BaseJPADAO<SaferStreetsRequestVO,String> implements SaferStreetsRequestDAO{

    public SaferStreetsRequestJPADAO(){
        super(SaferStreetsRequestVO.class);
    }

    @Override
    public List<SaferStreetsRequestVO> findByUserEmail(String email) {
        JPAParameter parameter = new JPAParameter()
                .setParameter("email", email);
        return executeQueryWithResultList(SaferStreetsRequestVO.QUERY_FIND_BY_USER_EMAIL, parameter);
    }

    @Override
    public List<SaferStreetsRequestVO> findByUserId(String userid) {
        JPAParameter parameter = new JPAParameter()
                .setParameter("userid", userid);
        return executeQueryWithResultList(SaferStreetsRequestVO.QUERY_FIND_BY_USER_ID, parameter);
    }

    @Override
    public List<SaferStreetsRequestVO> findByCityName(String city) {
        JPAParameter parameter = new JPAParameter()
                .setParameter("city", city);
        return executeQueryWithResultList(SaferStreetsRequestVO.QUERY_FIND_BY_CITY,parameter);
    }

    @Override
    public SaferStreetsRequestVO findByUserIdAndLocation(String userid, String city, String state, String country) {
        JPAParameter parameter =
                new JPAParameter()
                    .setParameter("userid", userid)
                    .setParameter("city", city)
                    .setParameter("state", state)
                    .setParameter("country", country);
        return executeQueryWithSingleResult(SaferStreetsRequestVO.QUERY_FIND_BY_USER_ID_AND_LOCATION, parameter);
    }

    @Override
    public SaferStreetsRequestVO findByUserEmailAndLocation(String email, String city, String state, String country) {
        JPAParameter parameter =
                new JPAParameter()
                        .setParameter("email", email)
                        .setParameter("city", city)
                        .setParameter("state", state)
                        .setParameter("country", country);
        return executeQueryWithSingleResult(SaferStreetsRequestVO.QUERY_FIND_BY_USER_EMAIL_AND_LOCATION, parameter);
    }

    @Override
    public List<SaferStreetsRequestVO> findAllGroupByUserId() {
        return executeQueryWithResultList(SaferStreetsRequestVO.QUERY_FIND_ALL_GROUP_BY_USER_ID);
    }

    @Override
    public Long findTotalRequests() {
        return (Long) executeQueryWithSingleNumberResult(SaferStreetsRequestVO.QUERY_FIND_TOTAL_REQUEST);
    }
}
