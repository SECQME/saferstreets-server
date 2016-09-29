package com.secqme.crimedata.domain.dao.jpa;

import com.secqme.crimedata.domain.dao.CrimeTypeDAO;
import com.secqme.crimedata.domain.model.CrimeDataVO;
import com.secqme.crimedata.domain.model.CrimeTypeVO;

import java.util.List;

/**
 * Created by Edmund on 3/30/15.
 */
public class CrimeTypeJPADAO extends BaseJPADAO<CrimeTypeVO,String> implements CrimeTypeDAO {

    public CrimeTypeJPADAO() {
        super(CrimeTypeVO.class);
    }

    @Override
    public List<CrimeTypeVO> findAll() {
        return executeQueryWithResultList(CrimeTypeVO.QUERY_FIND_ALL);
    }

}
