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

public enum PyschosensoryNeedEnumeration {

    VISUAL_IMPAIRMENT("visualImpairment"),
    AUDITORY_IMPAIRMENT("auditoryImpairment"),
    COGNITIVE_INPUT_IMPAIRMENT("cognitiveInputImpairment"),
    AVERSE_TO_LIFTS("averseToLifts"),
    AVERSE_TO_ESCALATORS("averseToEscalators"),
    AVERSE_TO_CONFINED_SPACES("averseToConfinedSpaces"),
    AVERSE_TO_CROWDS("averseToCrowds"),
    OTHER_PSYCHOSENSORY_NEED("otherPsychosensoryNeed");
    private final String value;

    PyschosensoryNeedEnumeration(String v) {
        value = v;
    }

    public static PyschosensoryNeedEnumeration fromValue(String v) {
        for (PyschosensoryNeedEnumeration c : PyschosensoryNeedEnumeration.values()) {
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
