package com.secqme.crimedata.domain.model;

import java.io.Serializable;

/**
 * User: James Khoo
 * Date: 10/14/14
 * Time: 5:31 PM
 */
public enum SafetyRating implements Serializable {
    LOW_SAFETY("danger.safety.rating"),
    MODERATE("moderate.safety.rating"),
    MODERATELY_SAFE("ok.safety.rating");

    SafetyRating(String langCode) {
        this.langCode = langCode;
    }

    String langCode;

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }
}
