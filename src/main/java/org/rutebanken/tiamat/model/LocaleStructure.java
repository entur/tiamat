/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
