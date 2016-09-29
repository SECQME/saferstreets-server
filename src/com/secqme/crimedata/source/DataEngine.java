package com.secqme.crimedata.source;

import java.util.Date;

/**
 * User: James Khoo
 * Date: 8/26/14
 * Time: 4:51 PM
 */
public interface DataEngine {
    public void getCrimeDataWithStartDataAndEndDate(Date startDate, Date endDate);
    public void getCrimeDataWithLatestDateFromDB();
}
