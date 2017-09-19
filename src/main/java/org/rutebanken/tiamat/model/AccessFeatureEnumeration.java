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

public enum AccessFeatureEnumeration {

    LIFT("lift"),
    ESCALATOR("escalator"),
    FREIGHT_ELEVATOR("freightElevator"),
    TRAVELATOR("travelator"),
    RAMP("ramp"),
    STAIRS("stairs"),
    SERIES_OF_STAIRS("seriesOfStairs"),
    SHUTTLE("shuttle"),
    CROSSING("crossing"),
    BARRIER("barrier"),
    NARROW_ENTRANCE("narrowEntrance"),
    HALL("hall"),
    CONCOURSE("concourse"),
    CONFINED_SPACE("confinedSpace"),
    QUEUE_MANAGEMENT("queueManagement"),
    NONE("none"),
    UNKNOWN("unknown"),
    OTHER("other"),
    OPEN_SPACE("openSpace"),
    STREET("street"),
    PAVEMENT("pavement"),
    FOOTPATH("footpath"),
    PASSAGE("passage");
    private final String value;

    AccessFeatureEnumeration(String v) {
        value = v;
    }

    public static AccessFeatureEnumeration fromValue(String v) {
        for (AccessFeatureEnumeration c : AccessFeatureEnumeration.values()) {
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
