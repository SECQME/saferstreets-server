package com.secqme.crimedata.domain.dao.jpa;

import org.apache.log4j.Logger;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.*;
import java.util.List;

/**
 * User: James Khoo
 * Date: 8/14/14
 * Time: 3:34 PM
 */
public class DefaultJPAHelper implements JPAHelper {

    protected static Logger myLog = Logger.getLogger(DefaultJPAHelper.class);
    private EntityManagerFactory emf;


    public DefaultJPAHelper(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void batchInsert(List<String> insertSQLQueryList) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            conn = getDBConnection(em);
            stmt = conn.createStatement();
            for (String insertSQL : insertSQLQueryList) {
                stmt.addBatch(insertSQL);
            }
            stmt.executeBatch();
        } catch (SQLException ex) {
            myLog.error("SQL Error->" + ex.getMessage(), ex);
        } finally {
            if (conn != null) {
                try {
                    stmt.close();
                    conn.close();
                    em.close();
                } catch (SQLException ex) {
                    myLog.error("SQL Error when closing->" + ex.getMessage(), ex);
                }
            }
        }
    }

    public JSONArray execSelectStmt(String sqlStatement) {
        ResultSet resultSet = null;
        Connection conn = null;
        Statement stmt = null;
        JSONArray jArray = null;
        EntityManager em = null;
        try {
            myLog.debug("Execute SQL->" + sqlStatement);
            em = getEntityManager();
            conn = getDBConnection(em);
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(sqlStatement);
            jArray = convert(resultSet);

        } catch (JSONException ex) {
            myLog.error("JSON Error->" + ex.getMessage(), ex);
        } catch (SQLException ex) {
            myLog.error("SQL Error->" + ex.getMessage(), ex);
        } finally {
            if (conn != null) {
                em.close();
            }
        }
        return jArray;

    }

    public Connection getDBConnection(EntityManager em) {
        UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl) ((JpaEntityManager) em.getDelegate()).getActiveSession();
        unitOfWork.beginEarlyTransaction();
        Accessor accessor = unitOfWork.getAccessor();
        accessor.incrementCallCount(unitOfWork.getParent());
        accessor.decrementCallCount();
        return accessor.getConnection();
    }

    private JSONArray convert(ResultSet rs) throws SQLException, JSONException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();

        while (rs.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();

            for (int i = 1; i < numColumns + 1; i++) {
                String column_name = rsmd.getColumnName(i);

                if (rsmd.getColumnType(i) == java.sql.Types.ARRAY) {
                    obj.put(column_name, rs.getArray(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT) {
                    obj.put(column_name, rs.getInt(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN) {
                    obj.put(column_name, rs.getBoolean(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.BLOB) {
                    obj.put(column_name, rs.getBlob(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE) {
                    obj.put(column_name, rs.getDouble(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT) {
                    obj.put(column_name, rs.getFloat(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER) {
                    obj.put(column_name, rs.getInt(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR) {
                    obj.put(column_name, rs.getNString(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR) {
                    obj.put(column_name, rs.getString(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT) {
                    obj.put(column_name, rs.getInt(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT) {
                    obj.put(column_name, rs.getInt(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.DATE) {
                    obj.put(column_name, rs.getDate(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP) {
                    obj.put(column_name, rs.getTimestamp(column_name));
                } else {
                    obj.put(column_name, rs.getObject(column_name));
                }
            }

            json.put(obj);
        }

        return json;
    }

    protected EntityManager getEntityManager() {
        EntityManager theEm = null;
        if (emf != null) {
            theEm = emf.createEntityManager();
        }
        return theEm;
    }


}
