package com.secqme.crimedata.manager;

import com.secqme.crimedata.domain.model.CrimeDataVO;

import java.io.Serializable;
import java.util.List;

/**
 * User: James Khoo
 * Date: 11/11/14
 * Time: 4:08 PM
 */
public class HotSpotHolder implements Serializable {
    private List<CrimeDataVO> crimeDataList;
    private Boolean checked = false;
    private Integer crimeWeight ;

    public HotSpotHolder(List<CrimeDataVO> theCrimeList) {
        this.crimeDataList = theCrimeList;
        checked = false;
    }

    public HotSpotHolder(List<CrimeDataVO> crimeDataList, Integer crimeWeight) {
        this.crimeDataList = crimeDataList;
        this.crimeWeight = crimeWeight;
        checked = false;
    }


    public List<CrimeDataVO> getCrimeDataList() {
        return crimeDataList;
    }

    public void setCrimeDataList(List<CrimeDataVO> crimeDataList) {
        this.crimeDataList = crimeDataList;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Integer getCrimeWeight() {
        return crimeWeight;
    }

    public void setCrimeWeight(Integer crimeWeight) {
        this.crimeWeight = crimeWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HotSpotHolder that = (HotSpotHolder) o;

        if (!checked.equals(that.checked)) return false;
        if (!crimeDataList.equals(that.crimeDataList)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = crimeDataList.hashCode();
        result = 31 * result + checked.hashCode();
        return result;
    }
}
