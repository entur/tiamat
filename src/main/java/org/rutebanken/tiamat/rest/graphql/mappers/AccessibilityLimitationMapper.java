package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ESCALATOR_FREE_ACCESS;

@Component
public class AccessibilityLimitationMapper {

    public AccessibilityLimitation map(Map<String, LimitationStatusEnumeration> limitationsInput) {
        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(limitationsInput.get(WHEELCHAIR_ACCESS));
        limitation.setAudibleSignalsAvailable(limitationsInput.get(AUDIBLE_SIGNALS_AVAILABLE));
        limitation.setStepFreeAccess(limitationsInput.get(STEP_FREE_ACCESS));
        limitation.setLiftFreeAccess(limitationsInput.get(LIFT_FREE_ACCESS));
        limitation.setEscalatorFreeAccess(limitationsInput.get(ESCALATOR_FREE_ACCESS));
        return limitation;
    }
}
