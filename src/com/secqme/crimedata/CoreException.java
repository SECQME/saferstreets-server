package com.secqme.crimedata;

import com.secqme.util.spring.DefaultSpringUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: James Khoo
 * Date: 9/25/14
 * Time: 3:52 PM
 */
public class CoreException extends RuntimeException {
    private ErrorType errorType;

    private Object[] errorMessageParameters;


    public CoreException(ErrorType errType, Object... errMsgParameters) {
        this(errType, null, errMsgParameters);
    }

    public CoreException(ErrorType errType, Throwable cause, Object[] errMsgParameters) {

        // TODO, lookup the i18n for English local mconnectedcore_message.properties
        // and format the message accordingly;
        super(cause);
        this.errorType = errType;
        this.errorMessageParameters = errMsgParameters == null ? null : errMsgParameters.clone();

    }


    public CoreException(ErrorType errType) {
        super();
        this.errorType = errType;

    }

    public String getMessage() {
        String msg = null;
        if (errorMessageParameters != null) {
            msg = DefaultSpringUtil.getInstance().getMessage(errorType.getErrorCode(), errorMessageParameters);
        } else {
            msg = DefaultSpringUtil.getInstance().getMessage(errorType.getErrorCode());
        }

        return msg;
    }

    public JSONObject getMessageAsJSON() {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("errorCode", this.errorType.getErrorCode());
            jobj.put("errorMessage", this.getMessage());
        } catch (JSONException je) {
        }

        return jobj;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public Object[] getErrorMessageParameters() {
        return errorMessageParameters == null ? null : errorMessageParameters.clone();
    }

    public void setErrorMessageParameters(Object[] errMsgParameters) {
        this.errorMessageParameters = errMsgParameters == null ? null : errMsgParameters.clone();
    }
}
