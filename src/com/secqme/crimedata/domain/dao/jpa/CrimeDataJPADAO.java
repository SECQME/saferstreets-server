package com.secqme.crimedata.domain.dao.jpa;

import com.secqme.crimedata.domain.dao.CrimeDataDAO;
import com.secqme.crimedata.domain.model.CrimeDataVO;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * User: James Khoo
 * Date: 8/14/14
 * Time: 3:51 PM
 */
public class CrimeDataJPADAO extends BaseJPADAO<CrimeDataVO, Long> implements CrimeDataDAO {
    private static Logger myLog = Logger.getLogger(CrimeDataJPADAO.class);

    private static final String findLatestCrimeFromCrimeSource = "SELECT o " +
            "FROM CrimeDataVO o " +
            "WHERE o.city.name = :city " +
            "AND o.source LIKE :source " +
            "ORDER BY o.occurredAt DESC";

    private static final String findLatestCrimeFromCityQuery = "SELECT o " +
            "FROM CrimeDataVO o " +
            "WHERE o.occurredAt = (SELECT MAX(t.occurredAt) FROM CrimeDataVO t WHERE t.city.name = 'THE_CITY') " +
            "AND o.city.name = 'THE_CITY'";

    public CrimeDataJPADAO() {
        super(CrimeDataVO.class);
    }

    public List<CrimeDataVO> findCrimeFromCityWithStartDate(String city, Date startDate) {
        JPAParameter parameters = new JPAParameter().setParameter("startDate", startDate)
                  .setParameter("city", city);
        return executeQueryWithResultList(CrimeDataVO.QUERY_FIND_LATEST_CRIME_BY_CITY_WITH_START_DATE, parameters);
    }

    public List<CrimeDataVO> findCrimeFromCityInBetweenDate(String city, Date startDate, Date endDate) {
        JPAParameter parameters = new JPAParameter().setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("city", city);
        return executeQueryWithResultList(CrimeDataVO.QUERY_FIND_LATEST_CRIME_BY_CITY_WITH_START_DATE_AND_END_DATE, parameters);
    }

    @Override
    public CrimeDataVO findLatestCrimeDataBasedOnSource(String crimeSource, String city, String state, String country) {
        CrimeDataVO crimeDataVO = null;

        EntityManager em = getEntityManager();
        try {
            Query query = em.createQuery(findLatestCrimeFromCrimeSource);
            query.setParameter("source", crimeSource + "%");
            query.setParameter("city", city);
            query.setFirstResult(0);
            query.setMaxResults(1);

            crimeDataVO = (CrimeDataVO) query.getSingleResult();
            myLog.debug(crimeDataVO);
        } catch (NoResultException nre) {
            myLog.error("Error of executing query:" + nre);
        } finally {
            em.close();
        }

        return crimeDataVO;
    }


    public CrimeDataVO findLatestCrimeDataFromCity(String city) {
        CrimeDataVO crimeDataVO = null;
        EntityManager em = getEntityManager();
        String finalQuery = findLatestCrimeFromCityQuery.replace("THE_CITY", city);
        try {
            Query query = em.createQuery(finalQuery);
            List<CrimeDataVO> crimeList  = (List<CrimeDataVO>) query.getResultList();
            if(crimeList != null && crimeList.size() > 0) {
                crimeDataVO = crimeList.get(0);
            }
        } catch (NoResultException nre) {
            myLog.error("Error of executing query:" + finalQuery);

        } finally {
            em.close();
        }

        return crimeDataVO;
    }

    @Override
    public List<CrimeDataVO> findNullCrimeWeight() {
        return executeQueryWithResultList(CrimeDataVO.QUERY_FIND_LATEST_CRIME_BY_CRIME_WEIGHT);
    }

    public void batchInsert(List<CrimeDataVO> crimeDataVOList) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int index = 0;

            for (CrimeDataVO crimeDataVO : crimeDataVOList) {
                em.persist(crimeDataVO);
                index++;

                if (index == DEFAULT_BATCH_INSERT_SIZE) {
                    em.flush();
                    em.clear();
                    index = 0;
                }

            }
            tx.commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<CrimeDataVO> findAllByBatch(int startId, int endId) {
        JPAParameter params = new JPAParameter()
                .setParameter("startId",startId)
                .setParameter("endId",endId);
       return executeQueryWithResultList(CrimeDataVO.QUERY_FIND_ALL_BY_BATCH,params);
    }
}
