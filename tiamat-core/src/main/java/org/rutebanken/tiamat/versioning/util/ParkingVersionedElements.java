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

package org.rutebanken.tiamat.versioning.util;

import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingProperties;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.SiteElement;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Walks a parking and every entity below it that carries a version.
 * <p>
 * Each of these entities has a unique constraint on (netex_id, version), so every caller that
 * changes versions must reach all of them. One traversal keeps those callers consistent.
 */
@Component
public class ParkingVersionedElements {

    /**
     * Apply the visitor to the parking and to each versioned entity below it.
     *
     * @param parking the parking to walk
     * @param visitor the action to apply to each entity, the parking included
     */
    public void forEach(Parking parking, Consumer<EntityInVersionStructure> visitor) {
        visitSiteElement(parking, visitor);
        visitPlaceEquipment(parking.getPlaceEquipments(), visitor);

        if (parking.getParkingProperties() != null) {
            parking.getParkingProperties().forEach(properties -> visitParkingProperties(properties, visitor));
        }

        if (parking.getParkingAreas() != null) {
            parking.getParkingAreas().forEach(parkingArea -> {
                visitSiteElement(parkingArea, visitor);
                visitPlaceEquipment(parkingArea.getPlaceEquipments(), visitor);
                visitParkingProperties(parkingArea.getParkingProperties(), visitor);
            });
        }

        if (parking.getVehicleEntrances() != null) {
            parking.getVehicleEntrances().forEach(entrance -> {
                visitSiteElement(entrance, visitor);
                visitPlaceEquipment(entrance.getPlaceEquipments(), visitor);
            });
        }
    }

    private void visitSiteElement(SiteElement siteElement, Consumer<EntityInVersionStructure> visitor) {
        visitor.accept(siteElement);

        AccessibilityAssessment accessibilityAssessment = siteElement.getAccessibilityAssessment();
        if (accessibilityAssessment != null) {
            visitor.accept(accessibilityAssessment);

            if (accessibilityAssessment.getLimitations() != null && !accessibilityAssessment.getLimitations().isEmpty()) {
                visitor.accept(accessibilityAssessment.getLimitations().getFirst());
            }
        }

        if (siteElement.getAlternativeNames() != null) {
            siteElement.getAlternativeNames().forEach(visitor);
        }
    }

    private void visitPlaceEquipment(PlaceEquipment placeEquipment, Consumer<EntityInVersionStructure> visitor) {
        if (placeEquipment == null) {
            return;
        }

        visitor.accept(placeEquipment);

        if (placeEquipment.getInstalledEquipment() != null) {
            placeEquipment.getInstalledEquipment().forEach(visitor);
        }
    }

    private void visitParkingProperties(ParkingProperties parkingProperties, Consumer<EntityInVersionStructure> visitor) {
        if (parkingProperties == null) {
            return;
        }

        visitor.accept(parkingProperties);

        if (parkingProperties.getSpaces() != null) {
            parkingProperties.getSpaces().forEach(visitor);
        }
    }
}
