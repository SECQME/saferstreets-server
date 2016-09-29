

package com.secqme.util.spring;

/**
 *
 * @author jameskhoo
 */
public enum BeanType {
    CACHE_UTIL("cacheUtil"),
    CRIME_DATA_DAO("crimeDataDAO"),
    CITY_INFO_DAO("cityInfoDAO"),
    CHICAGO_DATA_ENGINE("chicagoDataEngine"),
    SAN_FRAN_DATA_ENGINE("sanFranDataEngine"),
    LA_DATA_ENGINE("laDataEngine"),
    CRIME_MANAGER("crimeManager"),
    JSON_MODEL_FACTORY("jsonModelFactory"),
    LOCATION_UTIL("locationUtil"),
    DATA_MANAGER("dataManager"),
    REST_UTIL("restUtil");

    private String beanName = null;

    BeanType(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return this.beanName;
    }

}
