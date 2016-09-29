package com.secqme.crimedata.domain.dao.jpa;

import com.secqme.crimedata.domain.dao.BaseDAO;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * User: James Khoo
 * Date: 8/14/14
 * Time: 3:27 PM
 */
public abstract class BaseJPADAO<T, PK extends Serializable> implements BaseDAO<T, PK> {

    protected static Logger myLog = Logger.getLogger(BaseJPADAO.class);
    private final Class<T> type;
    protected EntityManagerFactory emf;
    protected final static int DEFAULT_BATCH_INSERT_SIZE = 100;


    public BaseJPADAO(Class<T> type) {
        this.type = type;
    }

    public void create(T t) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(t);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }

    public T read(PK pk) {
        T aT = null;

        EntityManager em = getEntityManager();
        try {
            aT = em.find(type, pk);
            if (aT != null) {
                // to ensure the data is refresh for every new read.
                em.refresh(aT);
            }
        } finally {
            em.close();
        }

        return aT;
    }

    public void update(T t) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(t);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete(T t) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.remove(em.merge(t));
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }

    public T refresh(T t) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            if (em.contains(t)) {
                em.refresh(t);
            } else {
                em.merge(t);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return t;
    }


    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    protected EntityManager getEntityManager() {
        EntityManager theEm = null;
        if(emf!=null) {
            theEm = emf.createEntityManager();
        }
        return theEm;
    }


    public Number executeQueryWithSingleNumberResult(String jpaQueryString) {
        return executeQueryWithSingleNumberResult(jpaQueryString, null);
    }

    public Number executeQueryWithSingleNumberResult(String jpaQueryString, JPAParameter jpaParameter) {
        EntityManager em = getEntityManager();
        StringBuilder strBuilder = new StringBuilder();
        try {
            Query query = em.createNamedQuery(jpaQueryString);
            if (jpaParameter != null) {
                for (Map.Entry<String, Object> entrySet : jpaParameter.getParameterMap().entrySet()) {
                    query = query.setParameter(entrySet.getKey(), entrySet.getValue());
                    strBuilder.append(entrySet.getKey() + "=" + entrySet.getValue() + ", ");
                }
            }

            return (Number) query.getSingleResult();
        } catch (NoResultException nre) {
            myLog.error("Error of executing query:" + jpaQueryString + ", parameters: " + strBuilder.toString() + ", cause" + nre.getMessage());
        } finally {
            em.close();
        }
        return null;
    }

    public T executeQueryWithSingleResult(String jpaQueryString) {
        return executeQueryWithSingleResult(jpaQueryString, null);
    }

    public T executeQueryWithSingleResult(String jpaQueryString, JPAParameter jpaParameter) {
        T vo = null;
        EntityManager em = getEntityManager();
        StringBuilder strBuilder = new StringBuilder();
        try {
            Query query = em.createNamedQuery(jpaQueryString);
            if (jpaParameter != null) {
                for (Map.Entry<String, Object> entrySet : jpaParameter.getParameterMap().entrySet()) {
                    query = query.setParameter(entrySet.getKey(), entrySet.getValue());
                    strBuilder.append(entrySet.getKey() + "=" + entrySet.getValue() + ", ");
                }

            }
            vo = (T) query.getSingleResult();

        } catch (NoResultException nre) {
            myLog.debug("Error of executing query:" + jpaQueryString +
                    ", parameters:" + strBuilder.toString() + ", cause" + nre.getMessage());

        } finally {
            em.close();
        }
        return vo;
    }

    public List<T> executeQueryWithResultList(String jpaQueryString) {
        return executeQueryWithResultList(jpaQueryString, null);
    }

    public List<T> executeQueryWithResultList(String jpaQueryString, JPAParameter jpaParameter) {
        List<T> voList = null;
        EntityManager em = getEntityManager();
        StringBuilder strBuilder = new StringBuilder();
        try {
            Query query = em.createNamedQuery(jpaQueryString);
            if (jpaParameter != null) {
                for (Map.Entry<String, Object> entrySet : jpaParameter.getParameterMap().entrySet()) {
                    query = query.setParameter(entrySet.getKey(), entrySet.getValue());
                    strBuilder.append(entrySet.getKey() + "=" + entrySet.getValue());
                }
            }
            voList = (List<T>) query.getResultList();
        } catch (NoResultException nre) {
            myLog.error("Error of executing query:" + jpaQueryString +
                    ", parameters:" + strBuilder.toString() + ", cause" + nre.getMessage());
        } finally {
            em.close();
        }
        return voList;
    }

}