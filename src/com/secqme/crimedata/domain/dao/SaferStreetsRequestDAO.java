package com.secqme.crimedata.domain.dao;

import com.secqme.crimedata.domain.model.SaferStreetsRequestVO;

import java.util.List;

/**
 * Created by Edmund on 1/26/15.
 */
public interface SaferStreetsRequestDAO extends BaseDAO<SaferStreetsRequestVO, String> {
    public List<SaferStreetsRequestVO> findByUserId(String userid);
    public List<SaferStreetsRequestVO> findByUserEmail(String email);
    public List<SaferStreetsRequestVO> findByCityName(String city);
    public SaferStreetsRequestVO findByUserIdAndLocation(String userid, String city, String state, String country);
    public SaferStreetsRequestVO findByUserEmailAndLocation(String email, String city, String state, String country);
    public List<SaferStreetsRequestVO> findAllGroupByUserId();
    public Long findTotalRequests();
}