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

package org.rutebanken.tiamat.importer.merging;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.versioning.save.ParkingVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test parking importer with geodb and repository.
 * See also {@link MergingStopPlaceImporterTest}
 */
@Transactional
public class MergingParkingImporterTest extends TiamatIntegrationTest {

    @Autowired
    private MergingParkingImporter mergingParkingImporter;

    @Autowired
    private ParkingVersionedSaverService parkingVersionedSaverService;

    /**
     * Two parkingss with the same name and coordinates should become one parking.
     */
    @Test
    public void parkingsWithSameCoordinatesMustNotBeAddedMultipleTimes() throws ExecutionException, InterruptedException {
        String name = "Ski stasjon";

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        double parkingLatitude = 59.422556;
        double parkingLongitude = 5.265704;

        Parking firstParking = createParking(name,
                parkingLongitude, parkingLatitude, null);
        firstParking.setParkingType(ParkingTypeEnumeration.PARK_AND_RIDE);
        firstParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.CAR);
        firstParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        // Import first parking
        Parking firstImportResult = mergingParkingImporter.importParkingWithoutNetexMapping(firstParking);

        Parking secondParking = createParking(name,
                parkingLongitude, parkingLatitude, null);
        secondParking.setParkingType(ParkingTypeEnumeration.PARK_AND_RIDE);
        secondParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.CAR);
        secondParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.MINIBUS);
        secondParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        // Import second parking
        Parking importResult = mergingParkingImporter.importParkingWithoutNetexMapping(secondParking);

        assertThat(importResult.getNetexId()).isEqualTo(firstImportResult.getNetexId());
        assertThat(importResult.getVersion()).isGreaterThan(firstImportResult.getVersion());

        assertThat(importResult.getParkingVehicleTypes().size()).isEqualTo(secondParking.getParkingVehicleTypes().size());
    }

    /**
     * The second time the stop place is imported, the type must be updated if it was empty.
     */
    @Test
    public void updateParkingType() throws ExecutionException, InterruptedException {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        Point point = point(10.7096245, 59.9086885);

        Parking firstParking = new Parking();
        firstParking.setCentroid(point);
        firstParking.setName(new EmbeddableMultilingualString("Ski stasjon", "no"));
        firstParking.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-ski");
        firstParking.setParkingType(ParkingTypeEnumeration.ROADSIDE);
        firstParking.setVersion(1L);
        firstParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        parkingVersionedSaverService.saveNewVersion(firstParking);

        Parking newParking = new Parking();
        newParking.setCentroid(point);
        newParking.setName(new EmbeddableMultilingualString("Ski stasjon", "no"));
        newParking.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-ski");
        newParking.setParkingType(ParkingTypeEnumeration.PARK_AND_RIDE);
        newParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        Parking importResult = mergingParkingImporter.importParkingWithoutNetexMapping(newParking);

        assertThat(importResult.getNetexId()).isEqualTo(firstParking.getNetexId());
        assertThat(importResult.getParkingType()).isEqualTo(ParkingTypeEnumeration.PARK_AND_RIDE);
    }

    @Test
    public void detectAndMergeParkingVehicleTypesFromTwoSimilarParkings() throws ExecutionException, InterruptedException {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        Parking firstParking = new Parking();
        firstParking.setCentroid(point(60.000, 10.78));
        firstParking.setName(new EmbeddableMultilingualString("Andalsnes", "no"));
        firstParking.setVersion(1L);
        firstParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.PEDAL_CYCLE);
        firstParking.setParkingType(ParkingTypeEnumeration.PARK_AND_RIDE);
        firstParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        parkingVersionedSaverService.saveNewVersion(firstParking);

        Parking secondParking = new Parking();
        secondParking.setCentroid(point(60.000, 10.78));
        secondParking.setName(new EmbeddableMultilingualString("Andalsnes", "no"));
        secondParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.CAR);
        secondParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.PEDAL_CYCLE);
        secondParking.setParkingType(ParkingTypeEnumeration.PARK_AND_RIDE);
        secondParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        Parking importResult = mergingParkingImporter.importParkingWithoutNetexMapping(secondParking);

        assertThat(importResult.getNetexId()).isEqualTo(firstParking.getNetexId());
        assertThat(importResult.getVersion()).isEqualTo(2L);
        assertThat(importResult.getParkingVehicleTypes()).containsExactly(ParkingVehicleEnumeration.CAR, ParkingVehicleEnumeration.PEDAL_CYCLE);
    }

    @Test
    public void testHandleAlreadyExistingParkingNoChange() {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        Parking firstParking = new Parking();
        firstParking.setCentroid(point(60.000, 10.78));
        firstParking.setName(new EmbeddableMultilingualString("Andalsnes", "no"));
        firstParking.setVersion(1L);
        firstParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.CAR);
        firstParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.PEDAL_CYCLE);
        firstParking.setParkingType(ParkingTypeEnumeration.PARK_AND_RIDE);
        firstParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));


        Parking secondParking = new Parking();
        secondParking.setCentroid(point(60.000, 10.78));
        secondParking.setName(new EmbeddableMultilingualString("Andalsnes", "no"));
        secondParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.CAR);
        secondParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.PEDAL_CYCLE);
        secondParking.setParkingType(ParkingTypeEnumeration.PARK_AND_RIDE);
        secondParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        Parking parking = mergingParkingImporter.handleAlreadyExistingParking(firstParking, secondParking);

        assertThat(parking.getName().getValue()).isEqualTo(firstParking.getName().getValue());
        assertThat(parking.getParkingType()).isEqualTo(firstParking.getParkingType());
        assertThat(parking.getParkingVehicleTypes()).containsAll(firstParking.getParkingVehicleTypes());
    }

    @Test
    public void testHandleAlreadyExistingParkingNullParkingType() {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        Parking firstParking = new Parking();
        firstParking.setParkingType(null);
        firstParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        Parking secondParking = new Parking();
        secondParking.setParkingType(null);
        secondParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        Parking parking = mergingParkingImporter.handleAlreadyExistingParking(firstParking, secondParking);

        assertThat(parking).isNotNull();
        assertThat(parking.getParkingType()).isNull();
    }

    @Test
    public void testHandleAlreadyExistingParkingUpdatedParkingVehicleTypes() {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        Parking firstParking = new Parking();
        firstParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.CAR);
        firstParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        Parking secondParking = new Parking();
        secondParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.CAR);
        secondParking.getParkingVehicleTypes().add(ParkingVehicleEnumeration.PEDAL_CYCLE);
        secondParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        Parking parking = mergingParkingImporter.handleAlreadyExistingParking(firstParking, secondParking);

        assertThat(parking).isNotNull();
        assertThat(parking.getParkingVehicleTypes()).containsAll(Arrays.asList(ParkingVehicleEnumeration.CAR, ParkingVehicleEnumeration.PEDAL_CYCLE));
    }

    private Point point(double longitude, double latitude) {
        return
                geometryFactory.createPoint(
                        new Coordinate(longitude, latitude));
    }

    private Parking createParking(String name, double longitude, double latitude, String stopPlaceId) {
        Parking parking = new Parking();
        parking.setCentroid(point(longitude, latitude));
        parking.setName(new EmbeddableMultilingualString(name, ""));
        parking.setNetexId(stopPlaceId);
        return parking;
    }

}
