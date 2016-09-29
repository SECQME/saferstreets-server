package com.secqme.crimedata.manager;

import com.secqme.crimedata.CoreException;
import com.secqme.crimedata.domain.model.CrimeTypeVO;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Edmund on 7/1/15.
 */
public interface DataManager {

    public void updateCityNeighbour(JSONObject jObj) throws CoreException;
    public List<CrimeTypeVO> getAllCrimeType();

    public JSONObject updateCrimeDayTime();
}
