package com.secqme.crimedata;

/**
 * User: James Khoo
 * Date: 9/25/14
 * Time: 3:53 PM
 */
public enum ErrorType {
    PATH_NOT_FOUND("path.not.found"),
    INTERNAL_SERVER_ERROR("internal.server.error"),

    CRIME_CITY_NOT_FOUND_EXCEPTION("city.not.found.exception"),
    PARAMETER_NOT_FOUND_EXCEPTION("parameter.not.found.exception"),
    PARAMETER_USERID_NOT_FOUND_EXCEPTION("parameter.userid.not.found.exception"),
    PARAMETER_USER_EMAIL_NOT_FOUND_EXCEPTION("parameter.email.not.found.exception"),
    PARAMETER_COORDINATE_OUT_OF_BOUND("paraemter.coordinate.out.of.bound"),

    USER_REQUESTED_SAFERSTREETS_SUPPORT("user.requested.saferstreets.support"),
    CITY_PARAMETER_NOT_FOUND("parameter.city.not.found.exception"),
    CITY_REQUESTED_NOT_FOUND("city.requested.not.found.exception"),
    CITY_OUT_OF_BOUND("city.out.of.bound"),
    CRIME_DATA_NOT_FOUND("crime.data.not.found");
    String errorCode;

    ErrorType(String code) {
        this.errorCode = code;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
