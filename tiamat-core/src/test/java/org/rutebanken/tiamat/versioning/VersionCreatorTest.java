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

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.id.RandomizedTestNetexIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class VersionCreatorTest extends TiamatIntegrationTest {

    @Autowired
    private VersionCreator versionCreator;

    @Autowired
    private RandomizedTestNetexIdGenerator randomizedTestNetexIdGenerator;

    @Test
    public void versionCommentShouldNotBeCopied() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setVersionComment("stopp flyttet 100 meter nordover");
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);
        assertThat(newVersion.getVersionComment()).isNull();
    }


    @Test
    public void changedByShouldNotBeCopied() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setChangedBy("testuser");
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);
        assertThat(newVersion.getChangedBy()).isNull();
    }

    @Test
    public void validbetweenShouldNotBeCopied() {
        StopPlace stopPlace = new StopPlace();

        stopPlace.setValidBetween(new ValidBetween(Instant.EPOCH, Instant.now()));

        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);
        assertThat(newVersion.getValidBetween()).isNull();
    }


    @Test
    public void createCopyOfStopWithGeometry() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(59.0, 11.1)));
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);
        assertThat(newVersion.getCentroid()).isNotNull();
    }

    @Test
    public void unsavedNewVersionShouldNotHavePrimaryKey() throws NoSuchFieldException, IllegalAccessException {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);

        // Save first version
        stopPlace = stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.flush();

        // Create new version
        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);

        Object actualStopPlaceId = getIdValue(newVersion);
        assertThat(actualStopPlaceId).isNull();
    }

    @Test
    public void deepCopiedObjectShouldHaveOriginalId() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.getOriginalIds().add("original-id");
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);
        assertThat(newVersion.getOriginalIds()).hasSize(1);
    }

    private Object getIdValue(IdentifiedEntity entity) throws NoSuchFieldException, IllegalAccessException {
        Field field = IdentifiedEntity.class.getDeclaredField("id");
        field.setAccessible(true);
        return field.get(entity);
    }

    @Test
    public void createNewVersionOfStopWithChangeInstance() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setChanged(Instant.now());
        stopPlace = stopPlaceRepository.save(stopPlace);
        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);
        assertThat(newVersion.getChanged()).isNotNull();
    }


    @Test
    public void createCopyOfStopPlaceWithChildren() {
        StopPlace parent = new StopPlace();
        parent.setVersion(1L);
        parent.setParentStopPlace(true);
        stopPlaceRepository.save(parent);

        StopPlace child1 = new StopPlace();
        child1.setCreated(Instant.now());
        child1.setVersion(1L);
        child1.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));
        stopPlaceRepository.save(child1);

        StopPlace child2 = new StopPlace();
        child2.setVersion(1L);
        child2.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));
        stopPlaceRepository.save(child2);

        parent.getChildren().add(child1);
        parent.getChildren().add(child2);

        stopPlaceRepository.save(parent);

        StopPlace parentCopy = versionCreator.createCopy(parent, StopPlace.class);
        assertThat(parentCopy.getChildren()).hasSize(2);

    }

    @Test
    public void stopPlacePlaceEquipmentShouldBeCopiedWithNetexIdAndVersion() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId(randomizedTestNetexIdGenerator.generateRandomizedNetexId(stopPlace));
        stopPlace.setVersion(1L);
        PlaceEquipment original = createPlaceEquipment();
        stopPlace.setPlaceEquipments(original);

        StopPlace copy = versionCreator.createCopy(stopPlace, StopPlace.class);
        assertPlaceEquipmentIdAndVersionIsKept(original, copy.getPlaceEquipments());
    }

    @Test
    public void quayPlaceEquipmentShouldBeCopiedWithNetexIdAndVersion() {
        Quay quay = new Quay();
        quay.setNetexId(randomizedTestNetexIdGenerator.generateRandomizedNetexId(quay));
        quay.setVersion(1L);
        PlaceEquipment original = createPlaceEquipment();
        quay.setPlaceEquipments(original);

        Quay copy = versionCreator.createCopy(quay, Quay.class);
        assertPlaceEquipmentIdAndVersionIsKept(original, copy.getPlaceEquipments());
    }

    private void assertPlaceEquipmentIdAndVersionIsKept(PlaceEquipment original, PlaceEquipment copied) {
        assertThat(copied).isNotNull().isNotSameAs(original);
        assertThat(copied.getNetexId()).isEqualTo(original.getNetexId());
        assertThat(copied.getVersion()).isEqualTo(original.getVersion());

        assertThat(copied.getInstalledEquipment()).hasSize(1);
        InstalledEquipment_VersionStructure copiedInstalledEquipment = copied.getInstalledEquipment().getFirst();
        InstalledEquipment_VersionStructure originalInstalledEquipment = original.getInstalledEquipment().getFirst();

        assertThat(copiedInstalledEquipment).isNotSameAs(originalInstalledEquipment);
        assertThat(copiedInstalledEquipment.getNetexId()).isEqualTo(originalInstalledEquipment.getNetexId());
        assertThat(copiedInstalledEquipment.getVersion()).isEqualTo(originalInstalledEquipment.getVersion());
    }

    private PlaceEquipment createPlaceEquipment() {
        PlaceEquipment placeEquipment = new PlaceEquipment();
        placeEquipment.setNetexId(randomizedTestNetexIdGenerator.generateRandomizedNetexId(placeEquipment));
        placeEquipment.setVersion(1L);

        TicketingEquipment ticketingEquipment = new TicketingEquipment();
        ticketingEquipment.setNetexId(randomizedTestNetexIdGenerator.generateRandomizedNetexId(ticketingEquipment));
        ticketingEquipment.setVersion(1L);

        placeEquipment.getInstalledEquipment().add(ticketingEquipment);
        return placeEquipment;
    }

}