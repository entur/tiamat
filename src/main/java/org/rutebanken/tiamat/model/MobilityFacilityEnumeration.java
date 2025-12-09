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

public enum MobilityFacilityEnumeration {
    UNKNOWN("unknown"),
    LOW_FLOOR("lowFloor"),
    STEP_FREE_ACCESS("stepFreeAccess"),
    SUITABLE_FOR_PUSHCHAIRS("suitableForPushchairs"),
    SUITABLE_FOR_WHEELCHAIRS("suitableForWheelchairs"),
    SUITABLE_FOR_HEAVILIY_DISABLED("suitableForHeaviliyDisabled"),
    BOARDING_ASSISTANCE("boardingAssistance"),
    ONBOARD_ASSISTANCE("onboardAssistance"),
    UNACCOMPANIED_MINOR_ASSISTANCE("unaccompaniedMinorAssistance"),
    TACTILE_PLATFORM_EDGES("tactilePlatformEdges"),
    TACTILE_GUIDING_STRIPS("tactileGuidingStrips");
    private final String value;

    MobilityFacilityEnumeration(String v) {
        this.value = v;
    }

    public static MobilityFacilityEnumeration fromValue(String v) {
        for (MobilityFacilityEnumeration c : MobilityFacilityEnumeration.values()) {
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
