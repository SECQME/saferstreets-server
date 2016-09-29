package com.secqme.crimedata.domain.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.*;

/**
 * User: James Khoo
 * Date: 10/14/14
 * Time: 5:43 PM
 */
    @JsonIgnoreProperties(ignoreUnknown = true)
public class SafetyReport implements Serializable {

    private SafetyRating safetyRating;
    private Map<String, List<CrimeDataSimple>> crimeDataHashMapByCrimeType;
    private Date crimeReportStartDate;
    private Date crimeRerpotEndDate;
    private Integer rowIndex;
    private Integer colIndex;
    private Double radius;
    private Integer totalCrimeCount;
    private Double averageCrimeCount;

    public SafetyReport(Integer rowIndex, Integer colIndex, Double radius,
                        SafetyRating safetyRating, List<CrimeDataSimple> crimeList,
                        Date crimeReportStartDate, Date crimeRerpotEndDate, Double averageCrimeCount) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.radius = radius;

        this.safetyRating = safetyRating;
        this.crimeReportStartDate = crimeReportStartDate;
        this.crimeRerpotEndDate = crimeRerpotEndDate;
        this.averageCrimeCount = averageCrimeCount;
        totalCrimeCount = crimeList.size();
        initCrimeHashMap(crimeList);
    }

    public SafetyReport(SafetyRating safetyRating, Date crimeReportStartDate,
                        Date crimeRerpotEndDate,List<CrimeDataSimple> crimeList) {
        this.safetyRating = safetyRating;
        this.crimeReportStartDate = crimeReportStartDate;
        this.crimeRerpotEndDate = crimeRerpotEndDate;
        totalCrimeCount = crimeList.size();
        initCrimeHashMap(crimeList);
    }

    public SafetyReport() {
    }

    public SafetyRating getSafetyRating() {
        return safetyRating;
    }

    public void setSafetyRating(SafetyRating safetyRating) {
        this.safetyRating = safetyRating;
    }

    public
    Map<String, List<CrimeDataSimple>> getCrimeDataHashMapByCrimeType() {
        return crimeDataHashMapByCrimeType;
    }

    public Date getCrimeReportStartDate() {
        return crimeReportStartDate;
    }

    public void setCrimeReportStartDate(Date crimeReportStartDate) {
        this.crimeReportStartDate = crimeReportStartDate;
    }

    public Date getCrimeRerpotEndDate() {
        return crimeRerpotEndDate;
    }

    public void setCrimeRerpotEndDate(Date crimeRerpotEndDate) {
        this.crimeRerpotEndDate = crimeRerpotEndDate;
    }

    public void setCrimeDataHashMapByCrimeType(Map<String, List<CrimeDataSimple>> crimeDataHashMapByCrimeType) {
        this.crimeDataHashMapByCrimeType = crimeDataHashMapByCrimeType;
    }

    public Integer getTotalCrimeCount() {
        return totalCrimeCount;
    }

    public void setTotalCrimeCount(Integer totalCrimeCount) {
        this.totalCrimeCount = totalCrimeCount;
    }

    public Double getAverageCrimeCount() {
        return averageCrimeCount;
    }

    public void setAverageCrimeCount(Double averageCrimeCount) {
        this.averageCrimeCount = averageCrimeCount;
    }

    public Integer getColIndex() {
        return colIndex;
    }

    public void setColIndex(Integer colIndex) {
        this.colIndex = colIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    private void initCrimeHashMap(List<CrimeDataSimple> crimeList) {
        Map<String, List<CrimeDataSimple>> unsortedCrimeTypeHashMap =
                new HashMap<String, List<CrimeDataSimple>>();

        for(CrimeDataSimple crimeDataVO : crimeList) {
            if(unsortedCrimeTypeHashMap.get(crimeDataVO.getCrimeType()) == null) {
                List<CrimeDataSimple> newCrimeList = new ArrayList<CrimeDataSimple>();
                unsortedCrimeTypeHashMap.put(crimeDataVO.getCrimeType(), newCrimeList);
            }
            unsortedCrimeTypeHashMap.get(crimeDataVO.getCrimeType()).add(crimeDataVO);
        }
        crimeDataHashMapByCrimeType = sortCrimeHashMapByNumberOfCrime(unsortedCrimeTypeHashMap);

    }


    private  Map<String, List<CrimeDataSimple>> sortCrimeHashMapByNumberOfCrime(Map<String, List<CrimeDataSimple>> unsortedCrimeTypeHashMap) {

        // Convert Map to List
        List<Map.Entry<String, List<CrimeDataSimple>>> list =
                new LinkedList<Map.Entry<String, List<CrimeDataSimple>>>(unsortedCrimeTypeHashMap.entrySet());

        // Sort list with comparator, to compare the Map values

        Collections.sort(list, new Comparator<Map.Entry<String, List<CrimeDataSimple>>>() {
            @Override
            public int compare(Map.Entry<String, List<CrimeDataSimple>> o1,
                               Map.Entry<String, List<CrimeDataSimple>> o2) {
                return o1.getValue().size() - o2.getValue().size();
            }
        });


        // Convert sorted map back to a Map
        Map<String, List<CrimeDataSimple>> sortedMap = new LinkedHashMap<String, List<CrimeDataSimple>>();
        for (Iterator<Map.Entry<String, List<CrimeDataSimple>>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, List<CrimeDataSimple>> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
