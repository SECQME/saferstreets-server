package com.secqme.crimedata.rs;

import com.secqme.crimedata.CoreException;
import com.secqme.crimedata.domain.model.CityGridFeatures;
import com.secqme.crimedata.domain.model.CityInfo;
import com.secqme.crimedata.domain.model.CrimeDataVO;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * Created by Edmund on 7/1/15.
 */
@Path("/manage")
public class DataManageResource extends BaseResource{
    private final static Logger myLog = Logger.getLogger(DataManageResource.class);

//    Request Body
//    {
//        "city" : "Kuala Lumpur",
//        "neighbour" :
//        [
//            {
//                "city":"Petaling Jaya"
//            },
//            {
//                "city":"Sri Petaling"
//            }
//        ]
//    }

    @POST
    @Path("/cityinfo")
    @Produces("application/json")
    public void manageCityNeighbour(String reqBody){
        myLog.debug(reqBody);
        try {
            if (reqBody != null && reqBody.length() > 0) {
                 dataManager.updateCityNeighbour(new JSONObject(reqBody));
            }else{
                myLog.debug("Request Body is null");
            }
        }catch(CoreException ex){
            ex.getMessageAsJSON();
        }catch(JSONException ex){
            ex.printStackTrace();
        }
    }

    @POST
    @Path("/updatecrimedaytime")
    @Produces("application/json")
    public String updateCrimeDayTime(String reqBody){
       return dataManager.updateCrimeDayTime().toString();
    }


    @POST
    @Path("/checktimezone")
    @Produces("application/json")
    public String checkTimeZone(String reqBody){
        JSONObject result = new JSONObject();
        try {
            JSONObject timezone = new JSONObject(reqBody);
            myLog.debug(locationUtil.checkCityTimeZone(timezone.getString("timeZone")));
            result.put("DayTime", locationUtil.checkCityTimeZone(timezone.getString("timeZone")));
        }catch(JSONException ex){
            ex.printStackTrace();
        }
    return  result.toString();
    }

    @POST
    @Path("/generatecrimematrix")
    @Produces("application/json")
    public String generateCrimeMatrix(String reqBody){
        CityGridFeatures cityGridFeatures = null;
        try{
            JSONObject request = new JSONObject(validateReqBody(reqBody));
            String cityName = (request.has(CommonJSONKey.STREET_CITY) ? request.getString(CommonJSONKey.STREET_CITY) : null);
            boolean oldReport = (request.has(CommonJSONKey.JSONOBJECT_ODL_REPORT) ? request.getBoolean(CommonJSONKey.JSONOBJECT_ODL_REPORT): false);
            if(cityName != null){
                if(oldReport){
                    crimeManager.generateCityCrimeMatrix(cityName, true);
                    cityGridFeatures = crimeManager.getCityGridFeatures(cityName);
                }else {
                    crimeManager.generateCityCrimeMatrix(cityName,false);
                    cityGridFeatures = crimeManager.getCityGridFeatures(cityName);
                }
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        if(cityGridFeatures != null){
            return OK_STATUS;
        }else{
            return NOT_OK_RESULT;
        }
    }

}
