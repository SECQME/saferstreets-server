package com.secqme.crimedata.domain.dao;

import com.secqme.crimedata.domain.model.CrimeDataVO;

import java.util.Date;
import java.util.List;

/**
 * User: James Khoo
 * Date: 8/14/14
 * Time: 3:50 PM
 */
public interface CrimeDataDAO extends BaseDAO<CrimeDataVO, Long> {

    public void batchInsert(List<CrimeDataVO> crimeDataVOList);
    public CrimeDataVO findLatestCrimeDataBasedOnSource(String crimeSource, String city, String state, String country);
    public List<CrimeDataVO> findCrimeFromCityWithStartDate(String city, Date startDate);
    public List<CrimeDataVO> findCrimeFromCityInBetweenDate(String city, Date startDate, Date endDate);
    public CrimeDataVO findLatestCrimeDataFromCity(String city);
    public List<CrimeDataVO> findNullCrimeWeight();
    public List<CrimeDataVO> findAllByBatch(int startId, int endId);
}
