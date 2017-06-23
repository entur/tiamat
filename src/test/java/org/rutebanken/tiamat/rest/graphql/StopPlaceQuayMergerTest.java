package org.rutebanken.tiamat.rest.graphql;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Test;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.Matchers.*;

public class StopPlaceQuayMergerTest extends AbstractGraphQLResourceIntegrationTest  {

    @Autowired
    StopPlaceQuayMerger stopPlaceQuayMerger;

    @Test
    @Transactional
    public void testMergeStopPlaces() {

        StopPlace fromStopPlace = new StopPlace();
        fromStopPlace.setName(new EmbeddableMultilingualString("Name"));
        fromStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        fromStopPlace.getOriginalIds().add("TEST:StopPlace:1234");
        fromStopPlace.getOriginalIds().add("TEST:StopPlace:5678");


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
        fromQuay.setCompassBearing(new Float(90));
        fromQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        fromQuay.getOriginalIds().add("TEST:Quay:123401");
        fromQuay.getOriginalIds().add("TEST:Quay:567801");

        fromStopPlace.getQuays().add(fromQuay);

        String oldVersionComment = "Old version deleted";
        fromStopPlace.setVersionComment(oldVersionComment);

        fromStopPlace.setTransportMode(VehicleModeEnumeration.BUS);

        stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace);

        StopPlace toStopPlace = new StopPlace();
        toStopPlace.setName(new EmbeddableMultilingualString("Name 2"));
        toStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.11, 60.11)));
        toStopPlace.getOriginalIds().add("TEST:StopPlace:4321");
        toStopPlace.getOriginalIds().add("TEST:StopPlace:8765");

        Quay toQuay = new Quay();
        toQuay.setCompassBearing(new Float(90));
        toQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.21, 60.21)));
        toQuay.getOriginalIds().add("TEST:Quay:432101");
        toQuay.getOriginalIds().add("TEST:Quay:876501");

        toStopPlace.getQuays().add(toQuay);

        stopPlaceVersionedSaverService.saveNewVersion(toStopPlace);

        StopPlace mergedStopPlace = stopPlaceQuayMerger.mergeStopPlaces(fromStopPlace.getNetexId(), toStopPlace.getNetexId(), null, null, false);

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

        // Equipment
        PlaceEquipment placeEquipment = mergedStopPlace.getPlaceEquipments();
        assertThat(placeEquipment).isNotNull();
        List<InstalledEquipment_VersionStructure> equipment = placeEquipment.getInstalledEquipment();
        assertThat(equipment).hasSize(1);
        assertThat(equipment).doesNotContain(generalSign); // Result from merge does not contain same object
        assertThat(equipment.get(0)).isInstanceOf(GeneralSign.class);
        assertThat(((GeneralSign)equipment.get(0)).getSignContentType()).isEqualTo(SignContentEnumeration.TRANSPORT_MODE);

        // assertQuays
        assertThat(mergedStopPlace.getQuays()).hasSize(2);
        mergedStopPlace.getQuays().forEach(quay -> {
            if (quay.getNetexId().equals(fromQuay.getNetexId())) {

                //The from-Quay has increased its version twice - once for terminating 'from', once for adding to 'to'
                assertThat(quay.getVersion()).isEqualTo(1 + fromQuay.getVersion());
                assertThat(quay.equals(fromQuay));

            } else if (quay.getNetexId().equals(toQuay.getNetexId())){

                assertThat(quay.getVersion()).isEqualTo(1 + toQuay.getVersion());
                assertThat(quay.equals(toQuay));

            } else {
                fail("Unknown Quay has been added");
            }
        });
    }

    @Test
    @Transactional
    public void testMergeStopPlacesWithTariffZones() {

        StopPlace fromStopPlace = new StopPlace();
        fromStopPlace.setName(new EmbeddableMultilingualString("Name"));
        
        Set<TariffZoneRef> fromTzSet = new HashSet<>();
        TariffZoneRef fromTz = new TariffZoneRef();
        fromTz.setRef("NSR:TZ:1");
        fromTz.setVersion("1");
        fromTzSet.add(fromTz);
        fromStopPlace.setTariffZones(fromTzSet);


        StopPlace toStopPlace = new StopPlace();
        toStopPlace.setName(new EmbeddableMultilingualString("Name 2"));

        Set<TariffZoneRef> toTzSet = new HashSet<>();
        TariffZoneRef toTz = new TariffZoneRef();
        toTz.setRef("NSR:TZ:2");
        toTz.setVersion("2");
        toTzSet.add(toTz);
        toStopPlace.setTariffZones(toTzSet);

        stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace);
        stopPlaceVersionedSaverService.saveNewVersion(toStopPlace);

        StopPlace mergedStopPlace = stopPlaceQuayMerger.mergeStopPlaces(fromStopPlace.getNetexId(), toStopPlace.getNetexId(), null, null, false);

        assertThat(mergedStopPlace.getTariffZones()).hasSize(2);

    }


    @Test
    @Transactional
    public void testMergeStopPlacesShouldIgnoreValidBetween() {

        StopPlace fromStopPlace = new StopPlace();
        fromStopPlace.setName(new EmbeddableMultilingualString("Name"));
        ValidBetween fromValidBetween = new ValidBetween(Instant.now().minusSeconds(3600));
        fromStopPlace.setValidBetween(fromValidBetween);

        StopPlace toStopPlace = new StopPlace();
        toStopPlace.setName(new EmbeddableMultilingualString("Name 2"));

        ValidBetween toValidBetween = new ValidBetween(Instant.now().minusSeconds(1800));
        toStopPlace.setValidBetween(toValidBetween);


        stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace);
        stopPlaceVersionedSaverService.saveNewVersion(toStopPlace);

        StopPlace mergedStopPlace = stopPlaceQuayMerger.mergeStopPlaces(fromStopPlace.getNetexId(), toStopPlace.getNetexId(), null, null, false);

        assertThat(mergedStopPlace.getValidBetween()).isNotNull();
        assertThat(mergedStopPlace.getValidBetween().getFromDate()).isNotNull();
        assertThat(mergedStopPlace.getValidBetween().getToDate()).isNull();

    }

    @Test
    @Transactional
    public void testMergeStopPlacesWithAlternativeNames() {

        StopPlace fromStopPlace = new StopPlace();
        fromStopPlace.setName(new EmbeddableMultilingualString("Name"));

        Set<TariffZoneRef> fromTzSet = new HashSet<>();
        TariffZoneRef fromTz = new TariffZoneRef();
        fromTz.setRef("NSR:TZ:1");
        fromTz.setVersion("1");
        fromTzSet.add(fromTz);
        fromStopPlace.setTariffZones(fromTzSet);

        AlternativeName fromAlternativeName = new AlternativeName();
        fromAlternativeName.setName(new EmbeddableMultilingualString("FROM-alternative"));
        fromStopPlace.getAlternativeNames().add(fromAlternativeName);

        StopPlace toStopPlace = new StopPlace();
        toStopPlace.setName(new EmbeddableMultilingualString("Name 2"));

        Set<TariffZoneRef> toTzSet = new HashSet<>();
        TariffZoneRef toTz = new TariffZoneRef();
        toTz.setRef("NSR:TZ:2");
        toTz.setVersion("2");
        toTzSet.add(toTz);
        toStopPlace.setTariffZones(toTzSet);

        AlternativeName toAlternativeName = new AlternativeName();
        toAlternativeName.setName(new EmbeddableMultilingualString("TO-alternative"));
        toStopPlace.getAlternativeNames().add(toAlternativeName);

        stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace);
        stopPlaceVersionedSaverService.saveNewVersion(toStopPlace);

        StopPlace mergedStopPlace = stopPlaceQuayMerger.mergeStopPlaces(fromStopPlace.getNetexId(), toStopPlace.getNetexId(), null, null, false);

        //AlternativeName
        assertThat(mergedStopPlace.getAlternativeNames()).isNotNull();
        assertThat(mergedStopPlace.getAlternativeNames()).hasSize(2);
    }

    @Test
    public void testMergeKeyValues() {
        String fromOriginalId = "1234";
        String toOriginalId = "4321";
        String testKey = "test-key";
        String otherTestKey = "other-test-key";
        String testValue = "test";

        Map<String, Value> fromKeyValues = new HashMap<>();

        fromKeyValues.put(NetexIdMapper.ORIGINAL_ID_KEY, new Value(fromOriginalId));
        fromKeyValues.put(testKey, new Value(testValue));

        Map<String, Value> toKeyValues = new HashMap<>();
        toKeyValues.put(NetexIdMapper.ORIGINAL_ID_KEY, new Value(fromOriginalId, toOriginalId));
        toKeyValues.put(otherTestKey, new Value(testValue));

        stopPlaceQuayMerger.mergeKeyValues(fromKeyValues, toKeyValues);

        assertThat(toKeyValues).hasSize(3);
        assertThat(toKeyValues.get(NetexIdMapper.ORIGINAL_ID_KEY)).isNotNull();
        assertThat(toKeyValues.get(NetexIdMapper.ORIGINAL_ID_KEY).getItems()).hasSize(2);
        assertThat(toKeyValues.get(NetexIdMapper.ORIGINAL_ID_KEY).getItems()).contains(fromOriginalId, toOriginalId);
        assertThat(toKeyValues.get(testKey).getItems()).contains(testValue);
        assertThat(toKeyValues.get(otherTestKey).getItems()).contains(testValue);

    }

    @Test
    public void testMergePlaceEquipment() {


        PlaceEquipment fromPlaceEquipment = new PlaceEquipment();
        GeneralSign generalSign = new GeneralSign();
        generalSign.setSignContentType(SignContentEnumeration.TRANSPORT_MODE);
        generalSign.setPublicCode(new PrivateCodeStructure("111", "111111"));
        fromPlaceEquipment.getInstalledEquipment().add(generalSign);


        PlaceEquipment toPlaceEquipment = new PlaceEquipment();
        GeneralSign generalSign2 = new GeneralSign();
        generalSign2.setSignContentType(SignContentEnumeration.TRANSPORT_MODE);
        generalSign2.setPublicCode(new PrivateCodeStructure("222", "222222"));
        toPlaceEquipment.getInstalledEquipment().add(generalSign2);


        stopPlaceQuayMerger.mergePlaceEquipments(fromPlaceEquipment, toPlaceEquipment);

        List<InstalledEquipment_VersionStructure> equipment = toPlaceEquipment.getInstalledEquipment();
        assertThat(equipment).hasSize(2);
        assertThat(equipment).contains(generalSign2);
        assertThat(equipment).doesNotContain(generalSign);

    }

    @Test
    public void testMergeStopPlacesUsingGraphQL() {

        StopPlace fromStopPlace = new StopPlace();
        fromStopPlace.setName(new EmbeddableMultilingualString("Name"));
        fromStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        fromStopPlace.getOriginalIds().add("TEST:StopPlace:1234");
        fromStopPlace.getOriginalIds().add("TEST:StopPlace:5678");

        AlternativeName altName = new AlternativeName();
        altName.setNameType(NameTypeEnumeration.ALIAS);
        altName.setName(new EmbeddableMultilingualString("Navn", "no"));

        AlternativeName altName2 = new AlternativeName();
        altName2.setNameType(NameTypeEnumeration.ALIAS);
        altName2.setName(new EmbeddableMultilingualString("Name", "en"));

        fromStopPlace.getAlternativeNames().add(altName);
        fromStopPlace.getAlternativeNames().add(altName2);

        Quay fromQuay = new Quay();
        fromQuay.setCompassBearing(new Float(90));
        fromQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        fromQuay.getOriginalIds().add("TEST:Quay:123401");
        fromQuay.getOriginalIds().add("TEST:Quay:567801");

        fromStopPlace.getQuays().add(fromQuay);


        StopPlace toStopPlace = new StopPlace();
        toStopPlace.setName(new EmbeddableMultilingualString("Name 2"));
        toStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.11, 60.11)));
        toStopPlace.getOriginalIds().add("TEST:StopPlace:4321");
        toStopPlace.getOriginalIds().add("TEST:StopPlace:8765");

        AlternativeName toAltName = new AlternativeName();
        toAltName.setNameType(NameTypeEnumeration.ALIAS);
        toAltName.setName(new EmbeddableMultilingualString("Navn2", "no"));

        toStopPlace.getAlternativeNames().add(toAltName);

        Quay toQuay = new Quay();
        toQuay.setCompassBearing(new Float(90));
        toQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.21, 60.21)));
        toQuay.getAlternativeNames().add(toAltName);
        toQuay.getOriginalIds().add("TEST:Quay:432101");
        toQuay.getOriginalIds().add("TEST:Quay:876501");

        toStopPlace.getQuays().add(toQuay);


        fromStopPlace = saveStopPlaceTransactional(fromStopPlace);
        toStopPlace = saveStopPlaceTransactional(toStopPlace);

        //Calling GraphQL-api to merge StopPlaces
        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: mergeStopPlaces (" +
                "          fromStopPlaceId:\\\"" + fromStopPlace.getNetexId() + "\\\", " +
                "          toStopPlaceId:\\\"" + toStopPlace.getNetexId() + "\\\"" +
                "       ) { " +
                "  id " +
                "  importedId " +
                "  name { value } " +
                "  quays {" +
                "    id " +
                "    geometry { type coordinates } " +
                "    compassBearing " +
                "  } " +
                " } " +
                "}}\",\"variables\":\"\"}";


        Set<String> originalIds = new HashSet<>();
        originalIds.addAll(fromStopPlace.getOriginalIds());
        originalIds.addAll(toStopPlace.getOriginalIds());

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace.id", comparesEqualTo(toStopPlace.getNetexId()))
                .body("data.stopPlace.importedId", containsInAnyOrder(originalIds.toArray()))
                .body("data.stopPlace.quays", hasSize(fromStopPlace.getQuays().size() + toStopPlace.getQuays().size()));


    }

    @Test
    @Transactional
    public void testMergeQuays() {

        StopPlace fromStopPlace = new StopPlace();
        fromStopPlace.setName(new EmbeddableMultilingualString("Name"));
        fromStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        fromStopPlace.getOriginalIds().add("TEST:StopPlace:1234");
        fromStopPlace.getOriginalIds().add("TEST:StopPlace:5678");

        Quay fromQuay = new Quay();
        fromQuay.setCompassBearing(new Float(90));
        fromQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        fromQuay.getOriginalIds().add("TEST:Quay:123401");
        fromQuay.getOriginalIds().add("TEST:Quay:567801");

        String testKey = "testKey";
        String testValue = "testValue";
        fromQuay.getKeyValues().put(testKey, new Value(testValue));

        PlaceEquipment equipment = new PlaceEquipment();
        ShelterEquipment shelter = new ShelterEquipment();
        shelter.setSeats(BigInteger.ONE);
        shelter.setAirConditioned(false);
        equipment.getInstalledEquipment().add(shelter);
        fromQuay.setPlaceEquipments(equipment);

        Quay toQuay = new Quay();
        toQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.21, 60.21)));
        toQuay.getOriginalIds().add("TEST:Quay:432101");
        toQuay.getOriginalIds().add("TEST:Quay:876501");

        Quay quayToKeepUnaltered = new Quay();
        quayToKeepUnaltered.setCompassBearing(new Float(180));
        quayToKeepUnaltered.setCentroid(geometryFactory.createPoint(new Coordinate(11.211, 60.211)));
        quayToKeepUnaltered.getOriginalIds().add("TEST:Quay:432102");

        fromStopPlace.getQuays().add(fromQuay);
        fromStopPlace.getQuays().add(toQuay);
        fromStopPlace.getQuays().add(quayToKeepUnaltered);

        stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace);

        StopPlace stopPlaceWithMergedQuays = stopPlaceQuayMerger.mergeQuays(fromStopPlace.getNetexId(), fromQuay.getNetexId(), toQuay.getNetexId(), null, false);

        assertThat(stopPlaceWithMergedQuays).isNotNull();
        assertThat(stopPlaceWithMergedQuays.getOriginalIds()).isNotEmpty();

        // assertQuays
        assertThat(stopPlaceWithMergedQuays.getQuays()).hasSize(2);
        stopPlaceWithMergedQuays.getQuays().forEach(quay -> {
            if (quay.getNetexId().equals(toQuay.getNetexId())){

                assertThat(quay.getVersion()).isEqualTo(1 + toQuay.getVersion());

                assertThat(quay.getCompassBearing()).isEqualTo(fromQuay.getCompassBearing());
                assertThat(quay.getKeyValues().get(testKey)).isNotNull();
                assertThat(quay.getKeyValues().get(testKey).getItems()).contains(testValue);

                assertThat(quay.getPlaceEquipments()).isNotNull();
                assertThat(quay.getPlaceEquipments().getInstalledEquipment()).isNotNull();
                assertThat(quay.getPlaceEquipments().getInstalledEquipment()).hasSize(1);
                assertThat(quay.getPlaceEquipments().getInstalledEquipment().get(0)).isInstanceOf(ShelterEquipment.class);

                assertThat(!quay.equals(toQuay));
                assertThat(!quay.equals(fromQuay));

            } else if (quay.getNetexId().equals(quayToKeepUnaltered.getNetexId())){

                assertThat(quay.equals(quayToKeepUnaltered));

            } else {
                fail("Unknown Quay has been added");
            }
        });

        StopPlace firstVersion = stopPlaceRepository.findFirstByNetexIdAndVersion(fromStopPlace.getNetexId(), 1);
        StopPlace secondVersion = stopPlaceRepository.findFirstByNetexIdAndVersion(fromStopPlace.getNetexId(), 2);

        assertThat(firstVersion).isNotNull();
        assertThat(secondVersion).isNotNull();
        assertThat(firstVersion.getValidBetween().getToDate()).isLessThan(Instant.now());
        assertThat(secondVersion.getValidBetween().getToDate()).isNull();
        assertThat(firstVersion.getQuays()).hasSize(3);
        assertThat(secondVersion.getQuays()).hasSize(2);

    }

    @Test
    public void testMergeQuaysUsingGraphQL() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        stopPlace.getOriginalIds().add("TEST:StopPlace:1234");
        stopPlace.getOriginalIds().add("TEST:StopPlace:5678");

        Quay fromQuay = new Quay();
        fromQuay.setCompassBearing(new Float(90));
        fromQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        fromQuay.getOriginalIds().add("TEST:Quay:123401");
        fromQuay.getOriginalIds().add("TEST:Quay:567801");


        Quay toQuay = new Quay();
        toQuay.setCompassBearing(new Float(90));
        toQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.21, 60.21)));
        toQuay.getOriginalIds().add("TEST:Quay:432101");
        toQuay.getOriginalIds().add("TEST:Quay:876501");

        Quay quayToKeepUnaltered = new Quay();
        quayToKeepUnaltered.setCompassBearing(new Float(180));
        quayToKeepUnaltered.setCentroid(geometryFactory.createPoint(new Coordinate(11.211, 60.211)));
        quayToKeepUnaltered.getOriginalIds().add("TEST:Quay:432102");

        stopPlace.getQuays().add(fromQuay);
        stopPlace.getQuays().add(toQuay);
        stopPlace.getQuays().add(quayToKeepUnaltered);

        stopPlace = saveStopPlaceTransactional(stopPlace);

        assertThat(stopPlace.getQuays()).hasSize(3);

        //Calling GraphQL-api to merge Quays
        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: mergeQuays (" +
                "          stopPlaceId:\\\"" + stopPlace.getNetexId() + "\\\", " +
                "          fromQuayId:\\\"" + fromQuay.getNetexId() + "\\\"" +
                "          toQuayId:\\\"" + toQuay.getNetexId() + "\\\"" +
                "       ) { " +
                "  id " +
                "  importedId " +
                "  name { value } " +
                "  quays {" +
                "    id " +
                "    geometry { type coordinates } " +
                "    compassBearing " +
                "    importedId " +
                "  } " +
                " } " +
                "}}\",\"variables\":\"\"}";


        Set<String> originalIds = new HashSet<>();
        originalIds.addAll(toQuay.getOriginalIds());
        originalIds.addAll(fromQuay.getOriginalIds());

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace.id", comparesEqualTo(stopPlace.getNetexId()))
                .body("data.stopPlace.quays", hasSize(2))
                .root("data.stopPlace.quays.find { it.id == '" +toQuay.getNetexId() + "'}")
                        .body("importedId", containsInAnyOrder(originalIds.toArray()))
                .root("data.stopPlace.quays.find { it.id == '" + quayToKeepUnaltered.getNetexId() + "'}")
                        .body("importedId", containsInAnyOrder(quayToKeepUnaltered.getOriginalIds().toArray()))
        ;
    }

    /*
     * Wrapping save-operation in separate method to complete transaction before GraphQL-request is called
     */
    @Transactional
    private StopPlace saveStopPlaceTransactional(StopPlace stopPlace) {
        return stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
    }
}
