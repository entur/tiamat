package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.AccessibilityLimitations_RelStructure;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccessibilityLimitationsListConverter extends BidirectionalConverter<List<AccessibilityLimitation>, org.rutebanken.netex.model.AccessibilityLimitations_RelStructure> {

    @Override
    public AccessibilityLimitations_RelStructure convertTo(List<AccessibilityLimitation> accessibilityLimitations, Type<AccessibilityLimitations_RelStructure> type) {
        if(accessibilityLimitations == null || accessibilityLimitations.isEmpty()) {
            return null;
        }

        AccessibilityLimitations_RelStructure limitationsRelStructure = new AccessibilityLimitations_RelStructure();
        accessibilityLimitations.forEach(limitation -> {
            limitationsRelStructure.getAccessibilityLimitation().add(
                    mapperFacade.map(limitation, org.rutebanken.netex.model.AccessibilityLimitation.class)
            );
        });

        return limitationsRelStructure;
    }

    @Override
    public List<AccessibilityLimitation> convertFrom(AccessibilityLimitations_RelStructure accessibilityLimitations_relStructure, Type<List<AccessibilityLimitation>> type) {
        List<AccessibilityLimitation> accessibilityLimitations = new ArrayList<>();

        if(accessibilityLimitations_relStructure.getAccessibilityLimitation() != null) {
            accessibilityLimitations_relStructure.getAccessibilityLimitation().stream()
                    .filter(object -> object instanceof org.rutebanken.netex.model.AccessibilityLimitation)
                    .map(accessibilityLimitation -> {
                        AccessibilityLimitation tiamatLimitation = mapperFacade.map(accessibilityLimitation, AccessibilityLimitation.class);
                        return tiamatLimitation;
                    })
                    .forEach(limitation -> accessibilityLimitations.add(limitation));
        }

        return accessibilityLimitations;
    }
}
