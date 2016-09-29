package com.secqme.crimedata.domain.dao.jpa;

import org.json.JSONArray;

import java.sql.SQLException;
import java.util.List;

/**
 * User: James Khoo
 * Date: 8/14/14
 * Time: 3:30 PM
 */
public interface JPAHelper {
    public JSONArray execSelectStmt(String sqlStatement) throws SQLException;
    public void batchInsert(List<String> insertSQLQueryList) throws SQLException;


}
