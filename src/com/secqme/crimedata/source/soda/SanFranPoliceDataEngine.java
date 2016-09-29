package com.secqme.crimedata.source.soda;

import com.secqme.crimedata.domain.dao.CityInfoDAO;
import com.secqme.crimedata.domain.dao.CrimeDataDAO;
import com.secqme.crimedata.domain.model.CityInfo;
import com.secqme.crimedata.domain.model.CrimeDataVO;
import com.secqme.crimedata.domain.model.CrimeTypeVO;
import com.secqme.crimedata.source.DataEngine;
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
 * Created by Edmund on 3/11/15.
 */
public class SanFranPoliceDataEngine  implements DataEngine {
    private static final String CRIME_CASE_PREFIX = "SODA_SANFRAN_";

    private static final String SAN_FRANCISCO_CITY = "San Francisco";
    private static final String SAN_FRANCISCO_STATE = "California";
    private static final String SAN_FRANCISCO_COUNTRY = "US";

    private static final String SAN_FRANCISCO_CRIME_SOURCE_BASE_API = "https://data.sfgov.org/";
    private static final Integer DEFAULT_HARD_LIMIT = 1000;
    private static final String APP_TOKEN = "PtWGu546ymCkJ0BWCHf32h4aF";
    private static final String CRIME_CASE_SOURCE_URL_PREFIX = SAN_FRANCISCO_CRIME_SOURCE_BASE_API + "/resource/gxxq-x39z.json?$$app_token=" + APP_TOKEN;
    private static final String SAN_FRAN_DATA_API_URL = SAN_FRANCISCO_CRIME_SOURCE_BASE_API +"/resource/gxxq-x39z.json?$limit=" + DEFAULT_HARD_LIMIT + "&$where=date";

    private static final SimpleDateFormat SAN_FRANCISCO_DATE_FORMAT;
    private static final SimpleDateFormat INPUT_DATE_FORMAT;
    private static final TimeZone SAN_FRANCISCO_TIME_ZONE;

    private static final Logger myLog = Logger.getLogger(SanFranPoliceDataEngine.class);

    static {
        SAN_FRANCISCO_TIME_ZONE = TimeZone.getTimeZone("America/Los_Angeles");
        SAN_FRANCISCO_DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        INPUT_DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd");
        SAN_FRANCISCO_DATE_FORMAT.setTimeZone(SAN_FRANCISCO_TIME_ZONE);
    }

    private static CityInfo SAN_FRANCISCO_CITY_VO;

    private RestUtil restUtil = null;
    private CrimeDataDAO crimeDataDAO = null;
    private CrimeType crimeType;

    public static final int EPSG_WSG84 = 4326;
    public static final CoordinateReferenceSystem CRS_WGS84 = CrsRegistry.getCoordinateRefenceSystemForEPSG(EPSG_WSG84);

    public SanFranPoliceDataEngine(RestUtil restUtil, CrimeDataDAO crimeDataDAO, CrimeType crimeType, CityInfoDAO cityInfoDAO) {
        this.restUtil = restUtil;
        this.crimeDataDAO = crimeDataDAO;
        this.crimeType = crimeType;

        if (SAN_FRANCISCO_CITY_VO == null) {
            SAN_FRANCISCO_CITY_VO = cityInfoDAO.findByCountryStateCity(SAN_FRANCISCO_COUNTRY, SAN_FRANCISCO_STATE, SAN_FRANCISCO_CITY);
        }
    }

    public static void main(String args[]) {
        DataEngine sanFranDataEngine = (DataEngine) DefaultSpringUtil.getInstance().getBean(BeanType.SAN_FRAN_DATA_ENGINE);
        try {
            if (args == null || args.length == 0) {
                sanFranDataEngine.getCrimeDataWithLatestDateFromDB();
            } else if (args != null && args.length == 2) {
                Date startDate = INPUT_DATE_FORMAT.parse(args[0]);
                Date endDate = INPUT_DATE_FORMAT.parse(args[1]);
                sanFranDataEngine.getCrimeDataWithStartDataAndEndDate(startDate,endDate);
            }
            System.exit(0);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void getCrimeDataWithStartDataAndEndDate(Date startDate, Date endDate) {
        String startDateStr = INPUT_DATE_FORMAT.format(startDate);
        String endDateStr = INPUT_DATE_FORMAT.format(endDate);
        String urlParameter = URLEncoder.encode(" > '" + startDateStr + "' and date <") + "=" +
                URLEncoder.encode(" '" + endDateStr + "'");

        String newCrimeURL = SAN_FRAN_DATA_API_URL + urlParameter;
        myLog.debug(newCrimeURL);
        getCrimeDataWithURL(newCrimeURL);

    }

    @Override
    public void getCrimeDataWithLatestDateFromDB() {
        myLog.debug("Getting lastestCrimeData base on latest date from DB");
        CrimeDataVO crimeDataVO = crimeDataDAO.findLatestCrimeDataBasedOnSource(SAN_FRANCISCO_CRIME_SOURCE_BASE_API, SAN_FRANCISCO_CITY, SAN_FRANCISCO_STATE, SAN_FRANCISCO_COUNTRY);
        if (crimeDataVO != null) {
            Date latestCrimeDate = crimeDataVO.getCrimeDate();
            String latestCrimeDateStr = SAN_FRANCISCO_DATE_FORMAT.format(latestCrimeDate).replace(" ", "T");
            String urlParameter = URLEncoder.encode(" > ") + latestCrimeDateStr + "'";
            String newCrimeURL = SAN_FRAN_DATA_API_URL + urlParameter;
            getCrimeDataWithURL(newCrimeURL);
        } else {
            myLog.debug("No data for this city");
        }
    }

    private void getCrimeDataWithURL(String url) {
        int offset = 0;
        JSONArray crimeDataArray;
        List<CrimeDataVO> crimeDataVOList;
        try {
            do {
                crimeDataVOList = new ArrayList<CrimeDataVO>();
                myLog.debug("Downloading San Francisco Crime Data from Chicago CrimeData with this URL" + url);
                String crimeResult = restUtil.executeGet(url + "&$offset=" + offset, null);
                crimeDataArray = new JSONArray(crimeResult);
                myLog.debug("Total :" + crimeDataArray.length() + " Crime Data Found");
                if (crimeDataArray.length() > 0) {
                    for (int i = 0; i < crimeDataArray.length(); i++) {
                        CrimeDataVO aCrimeData = convertData(crimeDataArray.getJSONObject(i));
                        if (aCrimeData != null) {
                            crimeDataVOList.add(aCrimeData);
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
//        myLog.debug(crimeDataObject.toString());
        try {

            caseNumber = CRIME_CASE_PREFIX + crimeDataObject.getString("incidntnum");
            crimeType.setName(crimeDataObject.getString("category"));

            StringBuilder addressBuilder = new StringBuilder();
            if (crimeDataObject.has("location_description")) {
                addressBuilder.append(", type:" +
                        crimeDataObject.getString("location_description"));
            }
            if (crimeDataObject.has("address")) {
                addressBuilder.append(", address:" + crimeDataObject.getString("address"));
            }
            if (crimeDataObject.has("pddistrict")) {
                addressBuilder.append(", district:" + crimeDataObject.getString("pddistrict"));
            }
            addressStr = addressBuilder.toString();

            if (crimeDataObject.has("date")) {
                crimeReportDate = SAN_FRANCISCO_DATE_FORMAT.parse(crimeDataObject.getString("date").replace("T", " "));
            }

            crimeDate = SAN_FRANCISCO_DATE_FORMAT.parse(crimeDataObject.getString("date").replace("T", " "));

            StringBuilder crimeNoteBuilder = new StringBuilder();
            if (crimeDataObject.has("description")) {
                crimeNoteBuilder.append(crimeDataObject.getString("description"));
//                crimeNoteBuilder.append(", arrest:" + crimeDataObject.getBoolean("arrest"));
            }
            crimeNote = crimeNoteBuilder.toString();

//            if (crimeDataObject.has("location")) {
//                JSONObject locationObj = crimeDataObject.getJSONObject("location");
                latitude = crimeDataObject.getDouble("y");
                longitude = crimeDataObject.getDouble("x");
//            }
            sourceURL = CRIME_CASE_SOURCE_URL_PREFIX + crimeDataObject.getString("incidntnum");
            crimeDataVO = new CrimeDataVO();
            crimeDataVO.setCrimeCaseID(caseNumber);
            crimeDataVO.setCrimeTypeVO(crimeType);
            crimeDataVO.setAddress(addressStr);
            crimeDataVO.setReportDate(crimeReportDate);
            crimeDataVO.setCrimeDate(crimeDate);
            crimeDataVO.setNote(crimeNote);
//            crimeDataVO.setLatitude(latitude);
//            crimeDataVO.setLongitude(longitude);

            Point location = org.geolatte.geom.builder.DSL.point(EPSG_WSG84, org.geolatte.geom.builder.DSL.c(longitude, latitude));
            crimeDataVO.setLocation(location);

            crimeDataVO.setSource(sourceURL);
            crimeDataVO.setCity(SAN_FRANCISCO_CITY_VO);
            crimeDataVO.setTimeZone(SAN_FRANCISCO_TIME_ZONE.getDisplayName());
            crimeDataVO.setCrimeWeight(this.crimeType.getCrimeWeight(crimeType.getName()));
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return crimeDataVO;

    }
}

