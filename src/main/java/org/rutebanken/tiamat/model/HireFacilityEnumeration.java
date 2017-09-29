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

public enum HireFacilityEnumeration {

    UNKNOWN("unknown"),
    CAR_HIRE("carHire"),
    MOTOR_CYCLE_HIRE("motorCycleHire"),
    CYCLE_HIRE("cycleHire"),
    TAXI("taxi"),
    RECREATION_DEVICE_HIRE("recreationDeviceHire");
    private final String value;

    HireFacilityEnumeration(String v) {
        value = v;
    }

    public static HireFacilityEnumeration fromValue(String v) {
        for (HireFacilityEnumeration c : HireFacilityEnumeration.values()) {
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
