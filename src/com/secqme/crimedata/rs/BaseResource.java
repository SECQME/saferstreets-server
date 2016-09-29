package com.secqme.crimedata.rs;

import com.secqme.crimedata.domain.model.CrimeDataSimple;
import com.secqme.crimedata.domain.model.CrimeDataVO;
import com.secqme.crimedata.domain.model.UserStreetSafetyRatingVO;
import com.secqme.crimedata.manager.CrimeManager;
import com.secqme.crimedata.manager.DataManager;
import com.secqme.util.json.JSONModelFactory;
import com.secqme.util.location.LocationUtil;
import com.secqme.util.spring.BeanType;
import com.secqme.util.spring.DefaultSpringUtil;
import com.secqme.util.spring.SpringUtil;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * User: James Khoo
 * Date: 9/25/14
 * Time: 3:49 PM
 */
public class BaseResource {
    public final static String OK_STATUS = "{\"status\":\"ok\"}";
    public final static String NOT_OK_RESULT = "{\"status\":\"Error\"}";


    private final static Logger myLog = Logger.getLogger(BaseResource.class);
    protected final static SpringUtil beanUtil = DefaultSpringUtil.getInstance();

    protected static JSONModelFactory jsonModelFactory = null;
    protected static CrimeManager crimeManager = null;
    protected static DataManager dataManager = null;
    protected static LocationUtil locationUtil  = null;


    public BaseResource() {
        jsonModelFactory = (JSONModelFactory) beanUtil.getBean(BeanType.JSON_MODEL_FACTORY);
        crimeManager = (CrimeManager) beanUtil.getBean(BeanType.CRIME_MANAGER);
        dataManager = (DataManager) beanUtil.getBean(BeanType.DATA_MANAGER);
        locationUtil = (LocationUtil) beanUtil.getBean(BeanType.LOCATION_UTIL);
    }

    public String validateReqBody(String reqBody){

        if(!reqBody.startsWith("{")){
            if(!reqBody.endsWith("}")) {
                reqBody = "{" + reqBody + "}";
            }else{
                reqBody = "{ " + reqBody;
            }
        }else if(!reqBody.endsWith("}")){
            reqBody = reqBody + "}";
        }

        return reqBody;
    }


    public Double calculateUserRating(List<UserStreetSafetyRatingVO> userRatingList){
        Double totalRating = 0.0;
        if(userRatingList != null) {
            for (int idx = 0; idx < userRatingList.size(); idx++) {
                totalRating += (userRatingList.get(idx) != null) ? userRatingList.get(idx).getUserStreetSafetyRating() : 0;
            }
            return totalRating / userRatingList.size();
        }else {
            return 0.0;
        }
    }


    public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public JSONArray getCurrentGridCrimeList(List<CrimeDataSimple> crimeList){
        List<String> tempCrimeList = new ArrayList<String>();
        JSONArray jsonArray = new JSONArray();
        try {
            if(crimeList != null) {
                for (int i = 0; i < crimeList.size(); i++) {
                    if(crimeList.get(i).getCrimeWeight() != null) {
                        if(crimeList.get(i).getCrimeWeight() > 0.0) {
                            if(crimeList.get(i).getDisplayName() != null) {
                                String crimeType = crimeList.get(i).getDisplayName().replace("_", " ");
                                tempCrimeList.add(WordUtils.capitalize(crimeType.toLowerCase()));
                            }
                        }
                    }
                }
                TreeMap<String, Integer> crimeTrend = new TreeMap<String, Integer>();

                for (String tempCrime : tempCrimeList) {
                    Integer count = crimeTrend.get(tempCrime);
                    crimeTrend.put(tempCrime, (count == null) ? 1 : count + 1);
                }
                int count = 0;
                for (Map.Entry<String, Integer> entry : sortByComparator(crimeTrend).entrySet()) {
//            System.out.println(entry.getKey() + " => "
//                    + entry.getValue());
                    if (count < 4) {
                        JSONObject jObj = new JSONObject();
                        jObj.put(CommonJSONKey.JSONOBJECT_CRIMETYPE, entry.getKey());
                        jObj.put(CommonJSONKey.JSONOBJECT_CRIMECOUNT, entry.getValue());
                        jsonArray.put(jObj);
                        count++;
                    }
                }
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }

        return jsonArray;
    }


    public String trimCityName(String cityName){
        if(cityName != null) {
            return cityName.replace(" ", "_").toLowerCase();
        }else{
            return null;
        }
    }

}
