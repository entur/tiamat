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

package org.rutebanken.tiamat.service.stopplace;

import jakarta.transaction.Transactional;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.GeneralSign;
import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.PrivateCodeStructure;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SignContentEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;
import static org.rutebanken.tiamat.versioning.save.DefaultVersionedSaverService.MILLIS_BETWEEN_VERSIONS;

public class StopPlaceMergerTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceMerger stopPlaceMerger;

    @Autowired
    private MultiModalStopPlaceEditor multiModalStopPlaceEditor;

    private Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    @Test
    @Transactional
    public void testMergeStopPlaces() {

        Instant atTestStart = now;

        StopPlace fromStopPlace = new StopPlace();
        fromStopPlace.setName(new EmbeddableMultilingualString("Name"));
        fromStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        fromStopPlace.getOriginalIds().add("TEST:StopPlace:1234");
        fromStopPlace.getOriginalIds().add("TEST:StopPlace:5678");
        fromStopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);


        PlaceEquipment fromPlaceEquipment = new PlaceEquipment();
        GeneralSign generalSign = new GeneralSign();
        generalSign.setSignContentType(SignContentEnumeration.TRANSPORT_MODE);
        generalSign.setPublicCode(new PrivateCodeStructure("111", "111111"));
        fromPlaceEquipment.getInstalledEquipment().add(generalSign);
        fromStopPlace.setPlaceEquipments(fromPlaceEquipment);

        String testKey = "testKey";
        String testValue = "testValue";
        fromStopPlace.getKeyValues().put(testKey, new Value(testValue));

        Quay fromQuay = new Quay();
        fromQuay.setCompassBearing(90F);
        fromQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        fromQuay.getOriginalIds().add("TEST:Quay:123401");
        fromQuay.getOriginalIds().add("TEST:Quay:567801");

        fromStopPlace.getQuays().add(fromQuay);

        String oldVersionComment = "Old version deleted";
        fromStopPlace.setVersionComment(oldVersionComment);

        fromStopPlace.setTransportMode(VehicleModeEnumeration.WATER);

        stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace);

        StopPlace toStopPlace = new StopPlace();
        toStopPlace.setName(new EmbeddableMultilingualString("Name 2"));
        toStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.11, 60.11)));
        toStopPlace.getOriginalIds().add("TEST:StopPlace:4321");
        toStopPlace.getOriginalIds().add("TEST:StopPlace:8765");
        toStopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        toStopPlace.setTransportMode(VehicleModeEnumeration.BUS);
        // Old version of toStopPlace
        Instant toStopPlaceOriginalFromDate = Instant.EPOCH;
        toStopPlace.setValidBetween(new ValidBetween(toStopPlaceOriginalFromDate));

        Quay toQuay = new Quay();
        toQuay.setCompassBearing(90F);
        toQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.21, 60.21)));
        toQuay.getOriginalIds().add("TEST:Quay:432101");
        toQuay.getOriginalIds().add("TEST:Quay:876501");

        toStopPlace.getQuays().add(toQuay);

        stopPlaceVersionedSaverService.saveNewVersion(toStopPlace);

        // Act
        StopPlace mergedStopPlace = stopPlaceMerger.mergeStopPlaces(fromStopPlace.getNetexId(), toStopPlace.getNetexId(), null, null, false);

        assertThat(mergedStopPlace).isNotNull();

        assertThat(fromStopPlace.getOriginalIds()).isNotEmpty();
        assertThat(toStopPlace.getOriginalIds()).isNotEmpty();

        assertThat(mergedStopPlace.getOriginalIds()).hasSize(fromStopPlace.getOriginalIds().size() + toStopPlace.getOriginalIds().size());

        assertThat(mergedStopPlace.getOriginalIds().containsAll(fromStopPlace.getOriginalIds()));

        assertThat(mergedStopPlace.getKeyValues().get(testKey)).isNotNull();
        assertThat(mergedStopPlace.getKeyValues().get(testKey).getItems()).hasSize(1);
        assertThat(mergedStopPlace.getKeyValues().get(testKey).getItems()).contains(testValue);

        assertThat(mergedStopPlace.getName().getValue()).matches(toStopPlace.getName().getValue());

        assertThat(mergedStopPlace.getVersionComment()).isNull();
        assertThat(mergedStopPlace.getTransportMode()).isEqualTo(VehicleModeEnumeration.BUS);

        assertThat(mergedStopPlace.getStopPlaceType()).isEqualTo(StopTypeEnumeration.BUS_STATION);

        // Equipment
        PlaceEquipment placeEquipment = mergedStopPlace.getPlaceEquipments();
        assertThat(placeEquipment).isNotNull();
        List<InstalledEquipment_VersionStructure> equipment = placeEquipment.getInstalledEquipment();
        assertThat(equipment).hasSize(1);
        assertThat(equipment).doesNotContain(generalSign); // Result from merge does not contain same object
        assertThat(equipment.getFirst()).isInstanceOf(GeneralSign.class);
        assertThat(((GeneralSign) equipment.getFirst()).getSignContentType()).isEqualTo(SignContentEnumeration.TRANSPORT_MODE);

        // assertQuays
        assertThat(mergedStopPlace.getQuays()).hasSize(2);
        mergedStopPlace.getQuays().forEach(quay -> {
            if (quay.getNetexId().equals(fromQuay.getNetexId())) {

                //The from-Quay has increased its version twice - once for terminating 'from', once for adding to 'to'
                assertThat(quay.getVersion()).isEqualTo(1 + fromQuay.getVersion());
                assertThat(quay.equals(fromQuay));

            } else if (quay.getNetexId().equals(toQuay.getNetexId())) {

                assertThat(quay.getVersion()).isEqualTo(1 + toQuay.getVersion());
                assertThat(quay.equals(toQuay));

            } else {
                fail("Unknown Quay has been added");
            }
        });

        StopPlace stopPlaceBeforeMerging = stopPlaceRepository.findFirstByNetexIdAndVersion(toStopPlace.getNetexId(), toStopPlace.getVersion());

        assertThat(mergedStopPlace.getValidBetween().getFromDate())
                .as("merged stop place from date")
                .isEqualTo(stopPlaceBeforeMerging.getValidBetween().getToDate().plusMillis(MILLIS_BETWEEN_VERSIONS))
                .as("merged stop place from date should have version from date after test started")
                .isAfterOrEqualTo(atTestStart);

        assertThat(stopPlaceBeforeMerging.getValidBetween().getFromDate())
                .as("old version of to-stopplace should not have changed from date")
                .isEqualTo(toStopPlaceOriginalFromDate);

        assertThat(stopPlaceBeforeMerging.getValidBetween().getToDate())
                .as("old version of to-stopplace should have its to date updated")
                .isAfterOrEqualTo(atTestStart);


    }

    @Test
    @Transactional
    public void testMergeStopPlacesWithTariffZones() {

        StopPlace fromStopPlace = new StopPlace();
        fromStopPlace.setName(new EmbeddableMultilingualString("Name"));

        TariffZone tariffZone1 = new TariffZone();
        tariffZone1.setNetexId("NSR:TariffZone:1");
        tariffZone1.setVersion(1L);
        tariffZoneRepository.save(tariffZone1);

        Set<TariffZoneRef> fromTzSet = new HashSet<>();
        TariffZoneRef fromTz = new TariffZoneRef();
        fromTz.setRef(tariffZone1.getNetexId());
        fromTz.setVersion(String.valueOf(tariffZone1.getVersion()));
        fromTzSet.add(fromTz);
        fromStopPlace.setTariffZones(fromTzSet);


        StopPlace toStopPlace = new StopPlace();
        toStopPlace.setName(new EmbeddableMultilingualString("Name 2"));

        TariffZone tariffZone2 = new TariffZone();
        tariffZone2.setNetexId("NSR:TariffZone:2");
        tariffZone2.setVersion(2L);
        tariffZoneRepository.save(tariffZone2);


        Set<TariffZoneRef> toTzSet = new HashSet<>();
        TariffZoneRef toTz = new TariffZoneRef();
        toTz.setRef(tariffZone2.getNetexId());
        toTz.setVersion(String.valueOf(tariffZone2.getVersion()));
        toTzSet.add(toTz);
        toStopPlace.setTariffZones(toTzSet);

        stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace);
        stopPlaceVersionedSaverService.saveNewVersion(toStopPlace);

        StopPlace mergedStopPlace = stopPlaceMerger.mergeStopPlaces(fromStopPlace.getNetexId(), toStopPlace.getNetexId(), null, null, false);

        assertThat(mergedStopPlace.getTariffZones()).hasSize(2);

    }


    @Test
    @Transactional
    public void testMergeStopPlacesShouldIgnoreValidBetween() {

        StopPlace fromStopPlace = new StopPlace();
        fromStopPlace.setName(new EmbeddableMultilingualString("Name"));
        ValidBetween fromValidBetween = new ValidBetween(now.minusSeconds(3600));
        fromStopPlace.setValidBetween(fromValidBetween);

        StopPlace toStopPlace = new StopPlace();
        toStopPlace.setName(new EmbeddableMultilingualString("Name 2"));

        ValidBetween toValidBetween = new ValidBetween(now.minusSeconds(1800));
        toStopPlace.setValidBetween(toValidBetween);


        stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace);
        stopPlaceVersionedSaverService.saveNewVersion(toStopPlace);

        StopPlace mergedStopPlace = stopPlaceMerger.mergeStopPlaces(fromStopPlace.getNetexId(), toStopPlace.getNetexId(), null, null, false);

        assertThat(mergedStopPlace.getValidBetween()).isNotNull();
        assertThat(mergedStopPlace.getValidBetween().getFromDate()).isNotNull();
        assertThat(mergedStopPlace.getValidBetween().getToDate()).isNull();

    }

    @Test
    @Transactional
    public void testMergeStopPlacesWithAlternativeNames() {

        StopPlace fromStopPlace = new StopPlace();
        fromStopPlace.setName(new EmbeddableMultilingualString("Name"));

        TariffZone tariffZone1 = new TariffZone();
        tariffZone1.setNetexId("NSR:TariffZone:1");
        tariffZone1.setVersion(1L);
        tariffZoneRepository.save(tariffZone1);


        Set<TariffZoneRef> fromTzSet = new HashSet<>();
        TariffZoneRef fromTz = new TariffZoneRef();
        fromTz.setRef(tariffZone1.getNetexId());
        fromTz.setVersion(String.valueOf(tariffZone1.getVersion()));
        fromTzSet.add(fromTz);
        fromStopPlace.setTariffZones(fromTzSet);

        AlternativeName fromAlternativeName = new AlternativeName();
        fromAlternativeName.setName(new EmbeddableMultilingualString("FROM-alternative"));
        fromStopPlace.getAlternativeNames().add(fromAlternativeName);

        StopPlace toStopPlace = new StopPlace();
        toStopPlace.setName(new EmbeddableMultilingualString("Name 2"));

        TariffZone tariffZone2 = new TariffZone();
        tariffZone2.setNetexId("NSR:TariffZone:2");
        tariffZone2.setVersion(2L);
        tariffZoneRepository.save(tariffZone2);


        Set<TariffZoneRef> toTzSet = new HashSet<>();
        TariffZoneRef toTz = new TariffZoneRef();
        toTz.setRef(tariffZone2.getNetexId());
        toTz.setVersion(String.valueOf(tariffZone2.getVersion()));
        toTzSet.add(toTz);
        toStopPlace.setTariffZones(toTzSet);

        AlternativeName toAlternativeName = new AlternativeName();
        toAlternativeName.setName(new EmbeddableMultilingualString("TO-alternative"));
        toStopPlace.getAlternativeNames().add(toAlternativeName);

        stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace);
        stopPlaceVersionedSaverService.saveNewVersion(toStopPlace);

        StopPlace mergedStopPlace = stopPlaceMerger.mergeStopPlaces(fromStopPlace.getNetexId(), toStopPlace.getNetexId(), null, null, false);

        //AlternativeName
        assertThat(mergedStopPlace.getAlternativeNames()).isNotNull();
        assertThat(mergedStopPlace.getAlternativeNames()).hasSize(2);
    }

    @Test
    @Transactional
    public void testMergeStopPlaceChildrenThrowsException() {
        StopPlace fromChild = createChildWithParent("first");
        StopPlace toChild = createChildWithParent("second");
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> stopPlaceMerger.mergeStopPlaces(fromChild.getNetexId(), toChild.getNetexId(), null, null, false));
    }

    /**
     * Test merge monomodal stop place with child of multimodal stop place
     */
    @Test
    @Transactional
    public void testMergeMonoModalStopPlaceWithMultiModalChild() {

        StopPlace fromStopPlace = new StopPlace();
        stopPlaceRepository.save(fromStopPlace);

        StopPlace toChild = createChildWithParent("second");
        String toVersionComment = "to version comment should be placed on the parent of the destination child";
        StopPlace parentOfMergedStopPlace = stopPlaceMerger.mergeStopPlaces(fromStopPlace.getNetexId(), toChild.getNetexId(), null, toVersionComment, false);

        assertThat(parentOfMergedStopPlace).as("merged stop place").isNotNull();
        assertThat(parentOfMergedStopPlace.getNetexId()).as("parent merged stop place netex id").isEqualTo(toChild.getParentSiteRef().getRef());
        assertThat(parentOfMergedStopPlace.getVersionComment()).as("parent merged stop place version comment").isEqualTo(toVersionComment);

        StopPlace actualChild = parentOfMergedStopPlace.getChildren().stream().filter(child -> child.getNetexId().equals(toChild.getNetexId())).findFirst().get();

        assertThat(actualChild).as("Child of parent").isNotNull();
        assertThat(actualChild.getParentSiteRef()).as("merged stop place parent site ref").isNotNull();
        assertThat(actualChild.getParentSiteRef().getRef()).isEqualTo(toChild.getParentSiteRef().getRef());
        assertThat(actualChild.getParentSiteRef().getVersion()).as("parent version").isEqualTo("2");
        assertThat(actualChild.getVersion()).isEqualTo(toChild.getVersion() + 1);
    }

    private StopPlace createChildWithParent(String name) {
        StopPlace child = new StopPlace();
        stopPlaceRepository.save(child);
        return multiModalStopPlaceEditor.createMultiModalParentStopPlace(Arrays.asList(child.getNetexId()), new EmbeddableMultilingualString(name)).getChildren().iterator().next();

    }

}
