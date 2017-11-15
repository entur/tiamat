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

import javax.xml.bind.annotation.XmlEnumValue;

public enum TopographicPlaceTypeEnumeration {

    CONTINENT("continent"),
    INTERREGION("interregion"),
    COUNTRY("country"),
    PRINCIPALITY("principality"),
    STATE("state"),
    PROVINCE("province"),
    REGION("region"),
    COUNTY("county"),
    AREA("area"),
    CONURBATION("conurbation"),
    CITY("city"),
    MUNICIPALITY("municipality"),
    QUARTER("quarter"),
    SUBURB("suburb"),
    TOWN("town"),
    URBAN_CENTRE("urbanCentre"),
    DISTRICT("district"),
    PARISH("parish"),
    VILLAGE("village"),
    HAMLET("hamlet"),
    PLACE_OF_INTEREST("placeOfInterest"),
    OTHER("other"),
    UNRECORDED("unrecorded");
    private final String value;

    TopographicPlaceTypeEnumeration(String v) {
        value = v;
    }

    public static TopographicPlaceTypeEnumeration fromValue(String v) {
        for (TopographicPlaceTypeEnumeration c : TopographicPlaceTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
