package com.secqme.crimedata.source.soda;

import com.secqme.crimedata.domain.dao.CityInfoDAO;
import com.secqme.crimedata.domain.dao.CrimeDataDAO;
import com.secqme.crimedata.domain.model.CityInfo;
import com.secqme.crimedata.domain.model.CrimeDataVO;
import com.secqme.crimedata.domain.model.CrimeTypeVO;
import com.secqme.crimedata.source.DataEngine;
import com.secqme.util.location.LocationUtil;
import com.secqme.util.rest.RestExecException;
import com.secqme.util.rest.RestUtil;
import com.secqme.util.spring.BeanType;
import com.secqme.util.spring.DefaultSpringUtil;
import org.apache.log4j.Logger;
import org.geolatte.geom.Point;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsRegistry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Edmund on 6/8/15.
 */
public class LAPoliceDataEngine implements DataEngine {

    private static final String CRIME_CASE_PREFIX = "LA_";

    private static final String LA_CITY = "Los Angeles";
    private static final String LA_STATE = "California";
    private static final String LA_COUNTRY = "US";
    private static CityInfo LA_CITY_VO;

    // Chicago Police Data impose a hard limit of 1000 data record, we needs to figure a way batch process the record
    private static final Integer DEFAULT_HARD_LIMIT = 1000;

    private static final String LA_CRIME_SOURCE = "http://data.lacity.org";
    private static final String CRIME_CASE_SOURCE_URL_PREFIX = LA_CRIME_SOURCE + "/resource/eta5-h8qx.json?ID=";
    private static final String LA_DATA_API_URL = LA_CRIME_SOURCE + "/resource/eta5-h8qx.json?$limit=" + DEFAULT_HARD_LIMIT + "&$where=date_occ";

    private static final SimpleDateFormat LA_DATE_FORMAT;
    private static final SimpleDateFormat INPUT_DATE_FORMAT;
    private static final TimeZone LA_TIMEZONE;

    public static final int EPSG_WSG84 = 4326;
    public static final CoordinateReferenceSystem CRS_WGS84 = CrsRegistry.getCoordinateRefenceSystemForEPSG(EPSG_WSG84);

    private static final Logger myLog = Logger.getLogger(LAPoliceDataEngine.class);

    static {
        LA_TIMEZONE = TimeZone.getTimeZone("America/Los_Angeles");
        LA_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        LA_DATE_FORMAT.setTimeZone(LA_TIMEZONE);
    }

    private CrimeType crimeType;
    private RestUtil restUtil = null;
    private CrimeDataDAO crimeDataDAO = null;
    private LocationUtil locationUtil;

    public LAPoliceDataEngine(CrimeType crimeType, RestUtil restUtil, CrimeDataDAO crimeDataDAO, LocationUtil locationUtil, CityInfoDAO cityInfoDAO) {
        this.crimeType = crimeType;
        this.restUtil = restUtil;
        this.crimeDataDAO = crimeDataDAO;
        this.locationUtil = locationUtil;

        if (LA_CITY_VO == null) {
            LA_CITY_VO = cityInfoDAO.findByCountryStateCity(LA_COUNTRY, LA_STATE, LA_CITY);
        }
    }

    public static void main(String args[]) {
        DataEngine laDataEngine = (DataEngine) DefaultSpringUtil.getInstance().getBean(BeanType.LA_DATA_ENGINE);
        try {
            if (args == null || args.length == 0) {
                laDataEngine.getCrimeDataWithLatestDateFromDB();

            } else if (args != null && args.length == 2) {
                Date startDate = INPUT_DATE_FORMAT.parse(args[0]);
                Date endDate = INPUT_DATE_FORMAT.parse(args[1]);
                laDataEngine.getCrimeDataWithStartDataAndEndDate(startDate, endDate);
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    @Override
    public void getCrimeDataWithStartDataAndEndDate(Date startDate, Date endDate) {
        String startDateStr = encodeDateParam(startDate);
        String endDateStr = encodeDateParam(endDate);
        String urlParameter = URLEncoder.encode(" > '") + startDateStr + URLEncoder.encode("' and date_occ <") + "=" +
                URLEncoder.encode(" '") + endDateStr + URLEncoder.encode("'");

        String newCrimeURL = LA_DATA_API_URL + urlParameter;
        getCrimeDataWithURL(newCrimeURL);
    }

    @Override
    public void getCrimeDataWithLatestDateFromDB() {
        myLog.debug("Getting latestCrimeData base on latest date from DB");
        CrimeDataVO crimeDataVO = crimeDataDAO.findLatestCrimeDataBasedOnSource(LA_CRIME_SOURCE, LA_CITY, LA_STATE, LA_COUNTRY);

        if (crimeDataVO != null) {
            myLog.debug("Latest Crime->" + crimeDataVO);
            Date latestCrimeDate = crimeDataVO.getCrimeDate();
            String latestCrimeDateStr = encodeDateParam(latestCrimeDate);
            myLog.debug("Getting crime from this date->" + latestCrimeDateStr);
            String urlParameter = URLEncoder.encode(" > '") + latestCrimeDateStr + URLEncoder.encode("'");
            String newCrimeURL = LA_DATA_API_URL + urlParameter;
            getCrimeDataWithURL(newCrimeURL);
        } else {
            myLog.debug("No data for this city");
        }
    }

    private String encodeDateParam(Date date) {
        return  LA_DATE_FORMAT.format(date).replace(" ", "T");
    }

    private void getCrimeDataWithURL(String url) {
        int offset = 0;
        JSONArray crimeDataArray;
        List<CrimeDataVO> crimeDataVOList;
        try {
            do {
                crimeDataVOList = new ArrayList<CrimeDataVO>();
                myLog.debug("Downloading LosAngeles Crime Data from LosAngeles CrimeData with this URL: " + url);
                String crimeResult = restUtil.executeGet(url + "&$offset=" + offset, null);
//                myLog.debug(crimeResult);
                crimeDataArray = new JSONArray(crimeResult);
                myLog.debug("Total :" + crimeDataArray.length() + " Crime Data Found");
                if (crimeDataArray.length() > 0) {
                    for (int i = 0; i < crimeDataArray.length(); i++) {
                        CrimeDataVO aCrimeData = convertData(crimeDataArray.getJSONObject(i));
                        if (aCrimeData != null) {
                            if(!aCrimeData.getCrimeTypeVO().getName().equals("TRAFFIC DR #")) {
                                crimeDataVOList.add(aCrimeData);
                            }
                        }
                    }
                    myLog.debug("Populating total of:" + crimeDataVOList.size() + " into our DB");
                    crimeDataDAO.batchInsert(crimeDataVOList);
                }
                offset += DEFAULT_HARD_LIMIT;
            } while (crimeDataArray.length() > 0);

        } catch (RestExecException e) {
            e.printStackTrace();
        } catch (JSONException je) {
            je.printStackTrace();
        }

    }

    private CrimeDataVO convertData(JSONObject crimeDataObject) {
        CrimeDataVO crimeDataVO = null;
        String caseNumber;
        CrimeTypeVO crimeType = new CrimeTypeVO();
        String addressStr;
        Date crimeReportDate = null;
        Date crimeDate;
        String crimeNote;
        Double latitude = 0.0;
        Double longitude = 0.0;
        String sourceURL;


        try {
            caseNumber = CRIME_CASE_PREFIX + crimeDataObject.getString("dr_no");

            // Get crimeType - Dirty way
            if(crimeDataObject.has("crm_cd_desc")){
                String temp = crimeDataObject.getString("crm_cd_desc");
//                myLog.debug("Crime Desc -> " + temp);
                crimeType = (this.crimeType.getCrimeType(temp));
//                myLog.debug("CrimeType -> " + crimeType.getCrimeType());
//                myLog.debug("\n");
            }

            StringBuilder addressBuilder = new StringBuilder().append(crimeDataObject.getString("location"));

            if (crimeDataObject.has("cross_street")) {
                addressBuilder.append(", cross street:" + crimeDataObject.getString("cross_street"));
            }
            addressStr = addressBuilder.toString();

            if (crimeDataObject.has("date_rptd")) {
                crimeReportDate = LA_DATE_FORMAT.parse(crimeDataObject.getString("date_rptd").replace("T", " "));
            }

            crimeDate = LA_DATE_FORMAT.parse(crimeDataObject.getString("date_occ").replace("T", " "));

            StringBuilder crimeNoteBuilder = new StringBuilder();
            if (crimeDataObject.has("crm_cd_desc")) {
                crimeNoteBuilder.append(crimeDataObject.getString("crm_cd_desc"));
            }
            crimeNote = crimeNoteBuilder.toString();

            if (crimeDataObject.has("location_1")) {
                JSONObject locationObj = crimeDataObject.getJSONObject("location_1");
                latitude = locationObj.getDouble("latitude");
                longitude = locationObj.getDouble("longitude");
            }
//            sourceURL = CRIME_CASE_SOURCE_URL_PREFIX + crimeDataObject.getString("id");
            crimeDataVO = new CrimeDataVO();
            crimeDataVO.setCrimeCaseID(caseNumber);
            crimeDataVO.setCrimeTypeVO(crimeType);
            crimeDataVO.setAddress(addressStr);
            crimeDataVO.setReportDate(crimeReportDate);
            crimeDataVO.setCrimeDate(crimeDate);
            crimeDataVO.setNote(crimeNote);
//            crimeDataVO.setLatitude(latitude);
//            crimeDataVO.setLongitude(longitude);
//            crimeDataVO.setSource("SODA");
//            crimeDataVO.setSourceUrl(sourceURL);

            Point location = org.geolatte.geom.builder.DSL.point(EPSG_WSG84, org.geolatte.geom.builder.DSL.c(longitude, latitude));
            crimeDataVO.setLocation(location);
            crimeDataVO.setCity(LA_CITY_VO);
            crimeDataVO.setTimeZone(LA_TIMEZONE.getDisplayName());
            crimeDataVO.setCrimeWeight(this.crimeType.getCrimeWeight(crimeType.getName()));
            crimeDataVO.setCrimeDayTime(locationUtil.getCrimeDayTime(crimeDate));
        } catch (JSONException je) {
            je.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException pe) {
            pe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return crimeDataVO;


    }
}
