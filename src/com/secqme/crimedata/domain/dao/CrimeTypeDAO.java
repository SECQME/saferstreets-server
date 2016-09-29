package com.secqme.crimedata.domain.dao;

import com.secqme.crimedata.domain.model.CrimeTypeVO;

import java.util.List;

/**
 * Created by Edmund on 3/30/15.
 */
public interface CrimeTypeDAO extends BaseDAO<CrimeTypeVO, String> {

    public List<CrimeTypeVO> findAll();
}
