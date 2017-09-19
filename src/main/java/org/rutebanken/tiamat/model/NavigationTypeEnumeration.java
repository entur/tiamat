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

public enum NavigationTypeEnumeration {

    HALL_TO_QUAY("hallToQuay"),
    HALL_TO_STREET("hallToStreet"),
    QUAY_TO_HALL("quayToHall"),
    QUAY_TO_QUAY("quayToQuay"),
    QUAY_TO_STREET("quayToStreet"),
    STREET_TO_HALL("streetToHall"),
    STREET_TO_QUAY("streetToQuay"),
    STREET_TO_SPACE("streetToSpace"),
    SPACE_TO_STREET("spaceToStreet"),
    SPACE_TO_HALL("spaceToHall"),
    HALL_TO_SPACE("hallToSpace"),
    SPACE_TO_SPACE("spaceToSpace"),
    OTHER("other");
    private final String value;

    NavigationTypeEnumeration(String v) {
        value = v;
    }

    public static NavigationTypeEnumeration fromValue(String v) {
        for (NavigationTypeEnumeration c : NavigationTypeEnumeration.values()) {
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
