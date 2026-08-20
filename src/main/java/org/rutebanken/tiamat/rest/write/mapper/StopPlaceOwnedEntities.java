package org.rutebanken.tiamat.rest.write.mapper;

import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.PostalAddress;
import org.rutebanken.tiamat.model.Quay;

import java.util.List;

/**
 * The entities that exist only as part of a stop place, rather than in their own right.
 * <p>
 * Both write API copiers treat these as a group, and differ only in whether the copy keeps their
 * identity: a stop place built from a submitted payload gets new ones throughout, while an update
 * of an existing stop place keeps them and only takes a new version.
 */
final class StopPlaceOwnedEntities {

    static final List<Class<? extends EntityInVersionStructure>> ALL = List.of(
            PlaceEquipment.class,
            InstalledEquipment_VersionStructure.class,
            Quay.class,
            AccessibilityAssessment.class,
            AccessibilityLimitation.class,
            PostalAddress.class
    );

    private StopPlaceOwnedEntities() {
    }
}
