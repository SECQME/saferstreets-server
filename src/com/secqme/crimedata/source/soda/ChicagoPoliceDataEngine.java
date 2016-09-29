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
 * User: James Khoo
 * Date: 8/20/14
 * Time: 2:14 PM
 */
public class ChicagoPoliceDataEngine implements DataEngine {

    private static final String CRIME_CASE_PREFIX = "SODA_CHICAGO_";

    private static final String CHICAGO_CITY = "Chicago";
    private static final String CHICAGO_STATE = "Illinois";
    private static final String CHICAGO_COUNTRY = "US";
    private static CityInfo CHICAGO_CITY_VO;

    // Chicago Police Data impose a hard limit of 1000 data record, we needs to figure a way batch process the record
    private static final Integer DEFAULT_HARD_LIMIT = 1000;

    private static final String CHICAGO_CRIME_SOURCE = "http://data.cityofchicago.org/";
    private static final String CRIME_CASE_SOURCE_URL_PREFIX = CHICAGO_CRIME_SOURCE + "/resource/ijzp-q8t2.json?ID=";
    private static final String CHICAGO_DATA_API_URL = CHICAGO_CRIME_SOURCE + "/resource/ijzp-q8t2.json?$limit=" + DEFAULT_HARD_LIMIT + "&$where=date";

    private static final SimpleDateFormat CHICAGO_DATE_FORMAT;
    private static final SimpleDateFormat INPUT_DATE_FORMAT;
    private static final TimeZone CHICAGO_TIMEZONE;

    private LocationUtil locationUtil;

    private static final Logger myLog = Logger.getLogger(ChicagoPoliceDataEngine.class);

    public static final int EPSG_WSG84 = 4326;
    public static final CoordinateReferenceSystem CRS_WGS84 = CrsRegistry.getCoordinateRefenceSystemForEPSG(EPSG_WSG84);

    static {
        CHICAGO_TIMEZONE = TimeZone.getTimeZone("America/Chicago");
        CHICAGO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        CHICAGO_DATE_FORMAT.setTimeZone(CHICAGO_TIMEZONE);
    }

    private CrimeType crimeType;
    private RestUtil restUtil = null;
    private CrimeDataDAO crimeDataDAO = null;

    public ChicagoPoliceDataEngine(RestUtil restUtil, CrimeDataDAO crimeDataDAO, CrimeType crimeType, LocationUtil locationUtil, CityInfoDAO cityInfoDAO) {
        this.restUtil = restUtil;
        this.crimeDataDAO = crimeDataDAO;
        this.crimeType = crimeType;
        this.locationUtil = locationUtil;

        if (CHICAGO_CITY_VO == null) {
            CHICAGO_CITY_VO = cityInfoDAO.findByCountryStateCity(CHICAGO_COUNTRY, CHICAGO_STATE, CHICAGO_CITY);
        }
    }

    public static void main(String args[]) {
        DataEngine chicagoDataEngine = (DataEngine) DefaultSpringUtil.getInstance().getBean(BeanType.CHICAGO_DATA_ENGINE);
        try {
            if (args == null || args.length == 0) {
               chicagoDataEngine.getCrimeDataWithLatestDateFromDB();

            } else if (args != null && args.length == 2) {
                Date startDate = INPUT_DATE_FORMAT.parse(args[0]);
                Date endDate = INPUT_DATE_FORMAT.parse(args[1]);
                chicagoDataEngine.getCrimeDataWithStartDataAndEndDate(startDate, endDate);
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    public void getCrimeDataWithLatestDateFromDB() {
        myLog.debug("Getting latestCrimeData base on latest date from DB");
        CrimeDataVO crimeDataVO = crimeDataDAO.findLatestCrimeDataBasedOnSource(CHICAGO_CRIME_SOURCE, CHICAGO_CITY, CHICAGO_STATE, CHICAGO_COUNTRY);

        if (crimeDataVO != null) {
            myLog.debug("Latest Crime->" + crimeDataVO);
            Date latestCrimeDate = crimeDataVO.getCrimeDate();
            String latestCrimeDateStr = encodeDateParam(latestCrimeDate);
            myLog.debug("Getting crime from this date->" + latestCrimeDateStr);
            String urlParameter = URLEncoder.encode(" > '") + latestCrimeDateStr + URLEncoder.encode("'");
            String newCrimeURL = CHICAGO_DATA_API_URL + urlParameter;
            getCrimeDataWithURL(newCrimeURL);
        } else {
            myLog.debug("No data for this city");
        }
    }

    public void getCrimeDataWithStartDataAndEndDate(Date startDate, Date endDate) {
        String startDateStr = encodeDateParam(startDate);
        String endDateStr = encodeDateParam(endDate);
        String urlParameter = URLEncoder.encode(" > '") + startDateStr + URLEncoder.encode("' and date <") + "=" +
                URLEncoder.encode(" '") + endDateStr + URLEncoder.encode("'");

        String newCrimeURL = CHICAGO_DATA_API_URL + urlParameter;
        getCrimeDataWithURL(newCrimeURL);
    }

    private String encodeDateParam(Date date) {
        return  CHICAGO_DATE_FORMAT.format(date).replace(" ", "T");
    }

    private void getCrimeDataWithURL(String url) {
        int offset = 0;
        JSONArray crimeDataArray;
        List<CrimeDataVO> crimeDataVOList;
        try {
            do {
                crimeDataVOList = new ArrayList<CrimeDataVO>();
                myLog.debug("Downloading Chicago Crime Data from Chicago CrimeData with this URL: " + url);
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
//        20151019
        boolean arrested = false;
        boolean domestic = false;
        String beat = null;
        String iucr = null;
        String district = null;
        String ward = null;
        String block = null;
        String communityArea = null;

        try {
            caseNumber = CRIME_CASE_PREFIX + crimeDataObject.getString("case_number");
//            crimeType.setName(crimeDataObject.getString("primary_type"));
            crimeType = this.crimeType.getCrimeType(crimeDataObject.getString("primary_type"));


            StringBuilder addressBuilder = new StringBuilder().append(crimeDataObject.getString("block"));
            if (crimeDataObject.has("location_description")) {
                addressBuilder.append(", type:" +
                        crimeDataObject.getString("location_description"));
            }
            if (crimeDataObject.has("district")) {
                addressBuilder.append(", district:" + crimeDataObject.getString("district"));
            }
            if (crimeDataObject.has("community_area")) {
                addressBuilder.append(", community_area:" + crimeDataObject.getString("community_area"));
            }
            addressStr = addressBuilder.toString();

            if (crimeDataObject.has("updated_on")) {
                crimeReportDate = CHICAGO_DATE_FORMAT.parse(crimeDataObject.getString("updated_on").replace("T", " "));
            }

            crimeDate = CHICAGO_DATE_FORMAT.parse(crimeDataObject.getString("date").replace("T", " "));

            StringBuilder crimeNoteBuilder = new StringBuilder();
            if (crimeDataObject.has("description")) {
                crimeNoteBuilder.append(crimeDataObject.getString("description"));
                crimeNoteBuilder.append(", arrest:" + crimeDataObject.getBoolean("arrest"));
            }

            crimeNote = crimeNoteBuilder.toString();

            if (crimeDataObject.has("location")) {
                JSONObject locationObj = crimeDataObject.getJSONObject("location");
                latitude = locationObj.getDouble("latitude");
                longitude = locationObj.getDouble("longitude");
            }
//            20151019
            if(crimeDataObject.has("arrest")){
                arrested = crimeDataObject.getBoolean("arrest");
            }

            if(crimeDataObject.has("beat")){
                beat = crimeDataObject.getString("beat");
            }

            if(crimeDataObject.has("iucr")){
                iucr = crimeDataObject.getString("iucr");
            }

            if(crimeDataObject.has("domestic")){
                domestic = crimeDataObject.getBoolean("domestic");
            }

            if(crimeDataObject.has("district")){
                district = crimeDataObject.getString("district");
            }

            if(crimeDataObject.has("ward")){
                ward = crimeDataObject.getString("ward");
            }

            if(crimeDataObject.has("community_area")){
                communityArea = crimeDataObject.getString("community_area");
            }

            if(crimeDataObject.has("block")){
                block = crimeDataObject.getString("block");
            }
            sourceURL = CRIME_CASE_SOURCE_URL_PREFIX + crimeDataObject.getString("id");
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

            crimeDataVO.setSource("SODA");
            crimeDataVO.setSourceUrl(sourceURL);
            crimeDataVO.setCity(CHICAGO_CITY_VO);
            crimeDataVO.setTimeZone(CHICAGO_TIMEZONE.getDisplayName());
            crimeDataVO.setCrimeWeight(this.crimeType.getCrimeWeight(crimeType.getName()));
            if(crimeType.getCrimeWeight() == null) {
                myLog.debug(crimeDataVO.getCrimeTypeVO().getDisplayName() );
                myLog.debug(crimeDataVO.getCrimeTypeVO().getCrimeWeight()+ "  -  CRIMEWEIGHT CRIMEWEIGHT CRIMEWEIGHT");
            }
            crimeDataVO.setCrimeDayTime(locationUtil.getCrimeDayTime(crimeDate));
//            20151019
            crimeDataVO.setArrested(arrested);
            crimeDataVO.setBeat(beat);
            crimeDataVO.setUcr(iucr);
            crimeDataVO.setDomestic(domestic);
            crimeDataVO.setDistrict(district);
            crimeDataVO.setWard(ward);
            crimeDataVO.setCommunityArea(communityArea);
            crimeDataVO.setBlock(block);


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
