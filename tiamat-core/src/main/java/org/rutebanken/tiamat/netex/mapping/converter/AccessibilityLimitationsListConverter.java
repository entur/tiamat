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

package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
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
    public AccessibilityLimitations_RelStructure convertTo(List<AccessibilityLimitation> accessibilityLimitations,
                                                           Type<AccessibilityLimitations_RelStructure> type,
                                                           MappingContext mappingContext) {
        if(accessibilityLimitations == null || accessibilityLimitations.isEmpty()) {
            return null;
        }

        AccessibilityLimitations_RelStructure limitationsRelStructure = new AccessibilityLimitations_RelStructure();
        accessibilityLimitations.forEach(limitation -> {
            limitationsRelStructure.setAccessibilityLimitation(
                    mapperFacade.map(limitation, org.rutebanken.netex.model.AccessibilityLimitation.class)
            );
        });

        return limitationsRelStructure;
    }

    @Override
    public List<AccessibilityLimitation> convertFrom(AccessibilityLimitations_RelStructure accessibilityLimitations_relStructure,
                                                     Type<List<AccessibilityLimitation>> type,
                                                     MappingContext mappingContext) {
        List<AccessibilityLimitation> accessibilityLimitations = new ArrayList<>();

        if(accessibilityLimitations_relStructure.getAccessibilityLimitation() != null) {
            org.rutebanken.netex.model.AccessibilityLimitation accessibilityLimitation = accessibilityLimitations_relStructure.getAccessibilityLimitation();
            AccessibilityLimitation tiamatLimitation = mapperFacade.map(accessibilityLimitation, AccessibilityLimitation.class);
            accessibilityLimitations.add(tiamatLimitation);
        }

        return accessibilityLimitations;
    }
}
