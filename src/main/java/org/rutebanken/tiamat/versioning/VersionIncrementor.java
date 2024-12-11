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

import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.BoardingPosition;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.Organisation;
import org.rutebanken.tiamat.model.InfoSpot;
import org.rutebanken.tiamat.model.hsl.HslAccessibilityProperties;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.SiteElement;
import org.rutebanken.tiamat.model.StopPlace;
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
                AccessibilityLimitation limitation = accessibilityAssessment.getLimitations().getFirst();
                initiateOrIncrement(limitation);
            }

            HslAccessibilityProperties hslAccessibilityProperties = accessibilityAssessment.getHslAccessibilityProperties();
            if (hslAccessibilityProperties != null) {
                initiateOrIncrement(hslAccessibilityProperties);
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

                if (quay.getBoardingPositions() != null) {
                    quay.getBoardingPositions().forEach(this::initiateOrIncrementBoardingPositions);
                }
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

    public void initiateOrIncrementBoardingPositions(BoardingPosition boardingPosition) {
        if (boardingPosition != null) {
            initiateOrIncrement(boardingPosition);
        }
    }

    public Organisation initiateOrIncrementVersions(Organisation organisation) {
        initiateOrIncrement(organisation);
        if (organisation.getContactDetails() != null) {
            initiateOrIncrement(organisation.getContactDetails());
        }
        if (organisation.getPrivateContactDetails() != null) {
            initiateOrIncrement(organisation.getPrivateContactDetails());
        }
        return organisation;
    }

    public void initiateOrIncrementInfoSpot(InfoSpot infoSpot) {
        if (infoSpot != null) {
            initiateOrIncrement(infoSpot);
        }
    }

    public void initiateOrIncrementGroupOfStopPlaces(GroupOfStopPlaces groupOfStopPlaces) {
        if (groupOfStopPlaces != null) {
            initiateOrIncrement(groupOfStopPlaces);
            initiateOrIncrementAlternativeNamesVersion(groupOfStopPlaces.getAlternativeNames());
        }
    }
}
