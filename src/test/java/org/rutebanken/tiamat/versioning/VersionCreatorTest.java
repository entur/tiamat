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

import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.AddressablePlaceRefStructure;
import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
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


    @Ignore // Should be testing future path link saver service
    @Test
    public void createNewVersionOfPathLink() {
        Quay fromQuay = new Quay();
        fromQuay.setVersion(1L);
        fromQuay = quayRepository.save(fromQuay);

        Quay toQuay = new Quay();
        toQuay.setVersion(1L);
        toQuay = quayRepository.save(toQuay);

        PathLinkEnd pathLinkEndFromQuay = new PathLinkEnd(new AddressablePlaceRefStructure(fromQuay.getNetexId(), String.valueOf(fromQuay.getVersion())));
        PathLinkEnd pathLinkEndToQuay = new PathLinkEnd(new AddressablePlaceRefStructure(toQuay.getNetexId(), String.valueOf(toQuay.getVersion())));

        PathLink pathLink = new PathLink(pathLinkEndFromQuay, pathLinkEndToQuay);
        pathLink.setVersion(1L);

        pathLink = pathLinkRepository.save(pathLink);

        PathLink newVersion = versionCreator.createCopy(pathLink, PathLink.class);

        assertThat(newVersion.getVersion())
                .describedAs("The version of path link should have been incremented")
                .isEqualTo(pathLink.getVersion() + 1);

        newVersion = pathLinkRepository.save(newVersion);

        PathLink actualNewVersionPathLink = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());

        assertThat(actualNewVersionPathLink.getVersion()).isEqualTo(2L);
        assertThat(actualNewVersionPathLink.getFrom().getPlaceRef().getRef()).isEqualTo(fromQuay.getNetexId());
        assertThat(actualNewVersionPathLink.getTo().getPlaceRef().getRef()).isEqualTo(toQuay.getNetexId());

        PathLink actualOldVersionPathLink = pathLinkRepository.findFirstByNetexIdAndVersion(newVersion.getNetexId(), 1L);
        assertThat(actualOldVersionPathLink).isNotNull();
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

    /**
     * When copying entity with place equipments, make sure the netex id and version is removed.
     * This is because of historical reasons where we had an issue with unique constrainst, due to inconsistent place/installed equipment versions.
     * See NRP-2348 and relevant queries for detecting this issue here: https://github.com/entur/tiamat-scripts/tree/master/fix_inconsistent_equipment
     */
    @Test
    public void stopPlacePlaceEquipmentSholdBeCopiedWithoutNetexIdAndVersion() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId(randomizedTestNetexIdGenerator.generateRandomizedNetexId(stopPlace));
        stopPlace.setVersion(1L);
        stopPlace.setPlaceEquipments(createPlaceEquipment());

        StopPlace copy = versionCreator.createCopy(stopPlace, StopPlace.class);
        assertPlaceEquipmentIdAndVersionIsEmpty(copy.getPlaceEquipments());
    }

    @Test
    public void quayPlaceEquipmentSholdBeCopiedWithoutNetexIdAndVersion() {
        Quay quay = new Quay();
        quay.setNetexId(randomizedTestNetexIdGenerator.generateRandomizedNetexId(quay));
        quay.setVersion(1L);
        quay.setPlaceEquipments(createPlaceEquipment());

        Quay copy = versionCreator.createCopy(quay, Quay.class);
        assertPlaceEquipmentIdAndVersionIsEmpty(copy.getPlaceEquipments());
    }

    private void assertPlaceEquipmentIdAndVersionIsEmpty(PlaceEquipment placeEquipment) {
        assertThat(placeEquipment).isNotNull();
        assertThat(placeEquipment.getNetexId()).isNull();
        assertThat(placeEquipment.getVersion()).isZero();

        assertThat(placeEquipment.getInstalledEquipment())
                .isNotNull()
                .hasSize(1);
        InstalledEquipment_VersionStructure copiedInstalledEquipment = placeEquipment.getInstalledEquipment().getFirst();

        assertThat(copiedInstalledEquipment.getNetexId()).isNull();
        assertThat(copiedInstalledEquipment.getVersion()).isZero();
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