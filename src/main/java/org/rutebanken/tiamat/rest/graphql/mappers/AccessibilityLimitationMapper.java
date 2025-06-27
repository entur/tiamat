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

package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.AUDIBLE_SIGNALS_AVAILABLE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ESCALATOR_FREE_ACCESS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LIFT_FREE_ACCESS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STEP_FREE_ACCESS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VISUAL_SIGNS_AVAILABLE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WHEELCHAIR_ACCESS;

@Component
public class AccessibilityLimitationMapper {

    public AccessibilityLimitation map(Map<String, LimitationStatusEnumeration> limitationsInput) {
        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(limitationsInput.get(WHEELCHAIR_ACCESS));
        limitation.setAudibleSignalsAvailable(limitationsInput.get(AUDIBLE_SIGNALS_AVAILABLE));
        limitation.setVisualSignsAvailable(limitationsInput.get(VISUAL_SIGNS_AVAILABLE));
        limitation.setStepFreeAccess(limitationsInput.get(STEP_FREE_ACCESS));
        limitation.setLiftFreeAccess(limitationsInput.get(LIFT_FREE_ACCESS));
        limitation.setEscalatorFreeAccess(limitationsInput.get(ESCALATOR_FREE_ACCESS));
        return limitation;
    }
}
