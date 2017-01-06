package org.rutebanken.tiamat.model;

import java.math.BigDecimal;


public class LocaleStructure {

    protected BigDecimal timeZoneOffset;
    protected String timeZone;
    protected BigDecimal summerTimeZoneOffset;
    protected String summerTimeZone;
    protected String defaultLanguage;
    protected Languages languages;

    public BigDecimal getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(BigDecimal value) {
        this.timeZoneOffset = value;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String value) {
        this.timeZone = value;
    }

    public BigDecimal getSummerTimeZoneOffset() {
        return summerTimeZoneOffset;
    }

    public void setSummerTimeZoneOffset(BigDecimal value) {
        this.summerTimeZoneOffset = value;
    }

    public String getSummerTimeZone() {
        return summerTimeZone;
    }

    public void setSummerTimeZone(String value) {
        this.summerTimeZone = value;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String value) {
        this.defaultLanguage = value;
    }

    public Languages getLanguages() {
        return languages;
    }

    public void setLanguages(Languages value) {
        this.languages = value;
    }

}
