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

package org.rutebanken.tiamat.versioning;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.rutebanken.tiamat.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VersionIncrementor {

    public static final long INITIAL_VERSION = 1;

    private static final Logger logger = LoggerFactory.getLogger(VersionIncrementor.class);

    public void incrementVersion(EntityInVersionStructure entity) {
        Long version = entity.getVersion();

        if (version == null || version < INITIAL_VERSION) {
            version = INITIAL_VERSION;
        } else {
            version++;
        }

        logger.debug("Incrementing version to {} for {}", version, entity);
        entity.setVersion(version);
    }

    private <T extends EntityInVersionStructure> T initiateFirstVersion(EntityInVersionStructure entityInVersionStructure, Class<T> type) {
        logger.debug("Initiating first version for entity {}", entityInVersionStructure.getClass().getSimpleName());
        entityInVersionStructure.setVersion(VersionIncrementor.INITIAL_VERSION);
        return type.cast(entityInVersionStructure);
    }

    public void initiateOrIncrementAccessibilityAssesmentVersion(SiteElement siteElement) {
        AccessibilityAssessment accessibilityAssessment = siteElement.getAccessibilityAssessment();

        if (accessibilityAssessment != null) {
            initiateOrIncrement(accessibilityAssessment);

            if (accessibilityAssessment.getLimitations() != null && !accessibilityAssessment.getLimitations().isEmpty()) {
                AccessibilityLimitation limitation = accessibilityAssessment.getLimitations().get(0);
                initiateOrIncrement(limitation);
            }
        }
    }

    public void initiateOrIncrementAlternativeNamesVersion(List<AlternativeName> alternativeNames) {

        if (alternativeNames != null) {
            alternativeNames.forEach(alternativeName -> initiateOrIncrement(alternativeName));
        }
    }

    public void initiateOrIncrement(EntityInVersionStructure entityInVersionStructure) {
        if(entityInVersionStructure.getNetexId() == null) {
            initiateFirstVersion(entityInVersionStructure, EntityInVersionStructure.class);
        } else {
            incrementVersion(entityInVersionStructure);
        }
    }

    private void initiateOrIncrementSiteElementVersion(SiteElement siteElement) {
        initiateOrIncrement(siteElement);
        initiateOrIncrementAccessibilityAssesmentVersion(siteElement);
        initiateOrIncrementAlternativeNamesVersion(siteElement.getAlternativeNames());
    }


    /**
     * Increment versions for stop place with children.
     * The object must have their netexId set, or else they will get an initial version
     * @param stopPlace with quays and accessibility assessment
     * @param validFrom
     * @return modified StopPlace
     */
    public StopPlace initiateOrIncrementVersions(StopPlace stopPlace) {
        initiateOrIncrementSiteElementVersion(stopPlace);
        initiateOrIncrementPlaceEquipment(stopPlace.getPlaceEquipments());

        if (stopPlace.getQuays() != null) {
            logger.debug("Initiating first versions for {} quays", stopPlace.getQuays().size());
            stopPlace.getQuays().forEach(quay -> {
                initiateOrIncrementSiteElementVersion(quay);
                initiateOrIncrementPlaceEquipment(quay.getPlaceEquipments());
            });
        }

        if(stopPlace.getChildren() != null) {
            logger.debug("Initiating versions for {} child stop places. Parent: {}", stopPlace.getChildren().size(), stopPlace.getNetexId());
            stopPlace.getChildren().forEach(this::initiateOrIncrementVersions);
        }

        return stopPlace;
    }

    public void initiateOrIncrementPlaceEquipment(PlaceEquipment placeEquipment) {
        if(placeEquipment != null) {
            initiateOrIncrement(placeEquipment);
            if(placeEquipment.getInstalledEquipment() != null) {
                placeEquipment.getInstalledEquipment().forEach(this::initiateOrIncrement);
            }
        }
    }
}
