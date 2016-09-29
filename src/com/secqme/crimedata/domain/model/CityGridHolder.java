package com.secqme.crimedata.domain.model;

import com.google.gson.JsonParseException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Edmund on 5/15/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityGridHolder implements Serializable {

    private CrimeCoordinate crimeCoordinate;
    private List<CrimeDataSimple> daytimeReport;
    private List<CrimeDataSimple> darkReport;
    private List<CrimeStreetsRatingVO> crimeStreetsRatingVO;
    private List<UserStreetSafetyRatingVO> userStreetSafetyRatingVO;
    private List<CrimeTrend> daytimeCrimeTrend;
    private List<CrimeTrend> darkCrimeTrend;
    private SafetyReport daytimeSafetyReport;
    private SafetyReport darkSafetyReport;


    public CityGridHolder(CrimeCoordinate crimeCoordinate,
                          List<CrimeDataSimple> daytimeReport,
                          List<CrimeDataSimple> darkReport,
                          List<CrimeStreetsRatingVO> crimeStreetsRatingVO,
                          List<UserStreetSafetyRatingVO> userStreetSafetyRatingVO,
                          List<CrimeTrend> daytimeCrimeTrend,
                          List<CrimeTrend> darkCrimeTrend,
                          SafetyReport daytimeSafetyReport,
                          SafetyReport darkSafetyReport) {
        this.crimeCoordinate = crimeCoordinate;
        this.daytimeReport = daytimeReport;
        this.darkReport = darkReport;
        this.crimeStreetsRatingVO = crimeStreetsRatingVO;
        this.userStreetSafetyRatingVO = userStreetSafetyRatingVO;
        this.daytimeCrimeTrend = daytimeCrimeTrend;
        this.darkCrimeTrend = darkCrimeTrend;
        this.daytimeSafetyReport = daytimeSafetyReport;
        this.darkSafetyReport = darkSafetyReport;
    }

    public CityGridHolder() {
    }

    @JsonCreator
    public static CityGridHolder Create(String jObj) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        CityGridHolder module = mapper.readValue(jObj, CityGridHolder.class);
        return module;
    }

    public CrimeCoordinate getCrimeCoordinate() {
        return crimeCoordinate;
    }

    public void setCrimeCoordinate(CrimeCoordinate crimeCoordinate) {
        this.crimeCoordinate = crimeCoordinate;
    }

    public List<CrimeStreetsRatingVO> getCrimeStreetsRatingVO() {
        return crimeStreetsRatingVO;
    }

    public void setCrimeStreetsRatingVO(List<CrimeStreetsRatingVO> crimeStreetsRatingVO) {
        this.crimeStreetsRatingVO = crimeStreetsRatingVO;
    }

    public List<UserStreetSafetyRatingVO> getUserStreetSafetyRatingVO() {
        return userStreetSafetyRatingVO;
    }

    public void setUserStreetSafetyRatingVO(List<UserStreetSafetyRatingVO> userStreetSafetyRatingVO) {
        this.userStreetSafetyRatingVO = userStreetSafetyRatingVO;
    }

    public List<CrimeDataSimple> getDaytimeReport() {
        return daytimeReport;
    }

    public void setDaytimeReport(List<CrimeDataSimple> daytimeReport) {
        this.daytimeReport = daytimeReport;
    }

    public List<CrimeDataSimple> getDarkReport() {
        return darkReport;
    }

    public void setDarkReport(List<CrimeDataSimple> darkReport) {
        this.darkReport = darkReport;
    }

    public List<CrimeTrend> getDaytimeCrimeTrend() {
        return daytimeCrimeTrend;
    }

    public void setDaytimeCrimeTrend(List<CrimeTrend> daytimeCrimeTrend) {
        this.daytimeCrimeTrend = daytimeCrimeTrend;
    }

    public List<CrimeTrend> getDarkCrimeTrend() {
        return darkCrimeTrend;
    }

    public void setDarkCrimeTrend(List<CrimeTrend> darkCrimeTrend) {
        this.darkCrimeTrend = darkCrimeTrend;
    }

    public SafetyReport getDaytimeSafetyReport() {
        return daytimeSafetyReport;
    }

    public void setDaytimeSafetyReport(SafetyReport daytimeSafetyReport) {
        this.daytimeSafetyReport = daytimeSafetyReport;
    }

    public SafetyReport getDarkSafetyReport() {
        return darkSafetyReport;
    }

    public void setDarkSafetyReport(SafetyReport darkSafetyReport) {
        this.darkSafetyReport = darkSafetyReport;
    }
}
