package org.rutebanken.tiamat.versioning.save;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A stop place update keeps the identity of the PlaceEquipment of the stop place and of its quays.
 * <p>
 * The test follows the path that the GraphQL mutation uses: StopPlaceUpdater reads the
 * existing version, calls VersionCreator#createCopy, changes the copy, and then calls
 * StopPlaceVersionedSaverService#saveNewVersion with both versions.
 */
public class PlaceEquipmentIdentityTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private VersionCreator versionCreator;

    @Test
    public void stopPlaceEquipmentKeepsItsIdentityAcrossAnUpdate() {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop with equipment"));
        PlaceEquipment placeEquipment = new PlaceEquipment();
        placeEquipment.getInstalledEquipment().add(new TicketingEquipment());
        stopPlace.setPlaceEquipments(placeEquipment);

        StopPlace firstSaved = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String firstEquipmentId = firstSaved.getPlaceEquipments().getNetexId();
        Long firstEquipmentVersion = firstSaved.getPlaceEquipments().getVersion();

        assertThat(firstEquipmentId).as("equipment id after the first save").isNotNull();

        StopPlace copy = versionCreator.createCopy(firstSaved, StopPlace.class);
        copy.setDescription(new EmbeddableMultilingualString("changed description"));
        StopPlace secondSaved = stopPlaceVersionedSaverService.saveNewVersion(firstSaved, copy);


        assertThat(secondSaved.getPlaceEquipments().getNetexId())
                .as("the equipment of a stop place keeps its id when the stop place is updated")
                .isEqualTo(firstEquipmentId);

        assertThat(secondSaved.getPlaceEquipments().getVersion())
                .as("the equipment version follows the stop place version")
                .isEqualTo(2L);
    }

    @Test
    public void quayEquipmentKeepsItsIdentityAcrossAnUpdate() {

        Quay quay = new Quay(new EmbeddableMultilingualString("quay with equipment"));
        PlaceEquipment quayEquipment = new PlaceEquipment();
        quayEquipment.getInstalledEquipment().add(new TicketingEquipment());
        quay.setPlaceEquipments(quayEquipment);

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop"));
        stopPlace.getQuays().add(quay);

        StopPlace firstSaved = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String firstEquipmentId = firstSaved.getQuays().iterator().next()
                .getPlaceEquipments().getNetexId();

        StopPlace copy = versionCreator.createCopy(firstSaved, StopPlace.class);
        copy.setDescription(new EmbeddableMultilingualString("changed description"));
        StopPlace secondSaved = stopPlaceVersionedSaverService.saveNewVersion(firstSaved, copy);

        String secondEquipmentId = secondSaved.getQuays().iterator().next()
                .getPlaceEquipments().getNetexId();


        assertThat(secondEquipmentId)
                .as("the equipment of a quay keeps its id when the stop place is updated")
                .isEqualTo(firstEquipmentId);
    }
}
