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

import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
import org.rutebanken.tiamat.model.LocalService;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.SiteComponent_VersionStructure;
import org.rutebanken.tiamat.model.SiteElement;
import org.rutebanken.tiamat.model.Site_VersionStructure;
import org.rutebanken.tiamat.service.AlternativeNameUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ACCESSIBILITY_ASSESSMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ALTERNATIVE_NAMES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GEOMETRY;

@Component
public class SiteElementMapper {

    private static final Logger logger = LoggerFactory.getLogger(SiteElementMapper.class);

    @Autowired
    private AlternativeNameMapper alternativeNameMapper;

    @Autowired
    private AlternativeNameUpdater alternativeNameUpdater;

    @Autowired
    private AccessibilityLimitationMapper accessibilityLimitationMapper;

    @Autowired
    private PlaceEquipmentMapper placeEquipmentMapper;

    @Autowired
    private LocalServicesMapper localServicesMapper;

    @Autowired
    private GeometryMapper geometryMapper;

    public boolean populate(Map input, SiteElement siteElement) {

        boolean isUpdated = false;

        if (input.get(ALTERNATIVE_NAMES) != null) {
            List alternativeNames = (List) input.get(ALTERNATIVE_NAMES);
            List<AlternativeName> mappedAlternativeNames = alternativeNameMapper.mapAlternativeNames(alternativeNames);

            if (alternativeNameUpdater.updateAlternativeNames(siteElement, mappedAlternativeNames)) {
                isUpdated = true;
            } else {
                logger.info("AlternativeName not changed");
            }
        }

        if (input.get(GEOMETRY) != null) {

            siteElement.setCentroid(geometryMapper.createGeoJsonPoint((Map) input.get(GEOMETRY)));
            isUpdated = true;
        }

        if (input.get(ACCESSIBILITY_ASSESSMENT) != null) {
            AccessibilityAssessment accessibilityAssessment = siteElement.getAccessibilityAssessment();
            if (accessibilityAssessment == null) {
                accessibilityAssessment = new AccessibilityAssessment();
            }

            Map<String, Object> accessibilityAssessmentInput = (Map) input.get(ACCESSIBILITY_ASSESSMENT);
            List<AccessibilityLimitation> limitations = accessibilityAssessment.getLimitations();
            AccessibilityLimitation limitation;
            if (limitations == null || limitations.isEmpty()) {
                limitations = new ArrayList<>();
                limitation = new AccessibilityLimitation();
            } else {
                limitation = limitations.getFirst();
            }

            AccessibilityLimitation limitationFromInput = accessibilityLimitationMapper.map((Map<String, LimitationStatusEnumeration>) accessibilityAssessmentInput.get("limitations"));

            //Only flag as updated if limitations are updated
            if (limitationFromInput.getWheelchairAccess() != limitation.getWheelchairAccess() |
                    limitationFromInput.getAudibleSignalsAvailable() != limitation.getAudibleSignalsAvailable() |
                    limitationFromInput.getVisualSignsAvailable() != limitation.getVisualSignsAvailable() |
                    limitationFromInput.getStepFreeAccess() != limitation.getStepFreeAccess() |
                    limitationFromInput.getLiftFreeAccess() != limitation.getLiftFreeAccess() |
                    limitationFromInput.getEscalatorFreeAccess() != limitation.getEscalatorFreeAccess()) {

                limitation.setWheelchairAccess(limitationFromInput.getWheelchairAccess());
                limitation.setAudibleSignalsAvailable(limitationFromInput.getAudibleSignalsAvailable());
                limitation.setVisualSignsAvailable(limitationFromInput.getVisualSignsAvailable());
                limitation.setStepFreeAccess(limitationFromInput.getStepFreeAccess());
                limitation.setLiftFreeAccess(limitationFromInput.getLiftFreeAccess());
                limitation.setEscalatorFreeAccess(limitationFromInput.getEscalatorFreeAccess());


                limitations.clear();
                limitations.add(limitation);
                accessibilityAssessment.setLimitations(limitations);

                siteElement.setAccessibilityAssessment(accessibilityAssessment);

                isUpdated = true;
            }
        }

        Optional<PlaceEquipment> placeEquipment = placeEquipmentMapper.map(input);

        if (placeEquipment.isPresent()) {
            if (siteElement instanceof Site_VersionStructure siteVersionStructure) {
                siteVersionStructure.setPlaceEquipments(placeEquipment.get());
            } else if (siteElement instanceof SiteComponent_VersionStructure siteVersionStructure) {
                siteVersionStructure.setPlaceEquipments(placeEquipment.get());
            } else {
                logger.warn("Cannot set place equipment for site element. Cannot detect type: {}", siteElement.getClass().getSimpleName());
            }

            isUpdated = true;
        }

        Optional<List<LocalService>> localServices = localServicesMapper.map(input);
        if(localServices.isPresent()) {
            if(siteElement instanceof Site_VersionStructure siteVersionStructure) {
                siteVersionStructure.setLocalServices(localServices.get());
            } else if (siteElement instanceof SiteComponent_VersionStructure siteComponentVersionStructure) {
                siteComponentVersionStructure.setLocalServices(localServices.get());
            } else {
                logger.warn("Cannot set local services for site element. Cannot detect type: {}", siteElement.getClass().getSimpleName());
            }

            isUpdated = true;
        }

        return isUpdated;
    }
}
