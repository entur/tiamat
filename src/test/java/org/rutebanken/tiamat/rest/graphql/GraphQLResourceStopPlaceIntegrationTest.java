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

package org.rutebanken.tiamat.rest.graphql;

import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.changelog.EntityChangedEvent;
import org.rutebanken.tiamat.changelog.EntityChangedJMSListener;
import org.rutebanken.tiamat.exception.HSLErrorCodeEnumeration;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.BusSubmodeEnumeration;
import org.rutebanken.tiamat.model.Contact;
import org.rutebanken.tiamat.model.CycleStorageEnumeration;
import org.rutebanken.tiamat.model.CycleStorageEquipment;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.GenderLimitationEnumeration;
import org.rutebanken.tiamat.model.GeneralSign;
import org.rutebanken.tiamat.model.Organisation;
import org.rutebanken.tiamat.model.OrganisationTypeEnumeration;
import org.rutebanken.tiamat.model.StopPlaceOrganisationRef;
import org.rutebanken.tiamat.model.StopPlaceOrganisationRelationshipEnumeration;
import org.rutebanken.tiamat.model.hsl.AccessibilityLevelEnumeration;
import org.rutebanken.tiamat.model.hsl.ElectricityTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.GuidanceTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.HslAccessibilityProperties;
import org.rutebanken.tiamat.model.hsl.ShelterTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.HslStopTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.MapTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.PedestrianCrossingRampTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.ShelterConditionEnumeration;
import org.rutebanken.tiamat.model.hsl.ShelterWidthTypeEnumeration;
import org.rutebanken.tiamat.model.InterchangeWeightingEnumeration;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
import org.rutebanken.tiamat.model.NameTypeEnumeration;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.PrivateCodeStructure;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SanitaryEquipment;
import org.rutebanken.tiamat.model.ShelterEquipment;
import org.rutebanken.tiamat.model.SignContentEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.TramSubmodeEnumeration;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.model.WaitingRoomEquipment;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.service.stopplace.MultiModalStopPlaceEditor;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MOVE_QUAYS_TO_STOP;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.QUAY_IDS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TO_VERSION_COMMENT;
import static org.rutebanken.tiamat.rest.graphql.scalars.DateScalar.DATE_TIME_PATTERN;

public class GraphQLResourceStopPlaceIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private EntityChangedJMSListener entityChangedJMSListener;

    @Autowired
    private ExportTimeZone exportTimeZone;

    private Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    @Before
    public void cleanReceivedJMS(){
        entityChangedJMSListener.popEvents();
    }

    @Test
    public void retrieveStopPlaceWithTwoQuays() throws Exception {
        Quay quay = new Quay();
        String firstQuayName = "first quay name";
        quay.setName(new EmbeddableMultilingualString(firstQuayName));
        Quay secondQuay = new Quay();
        String secondQuayName = "second quay";
        secondQuay.setName(new EmbeddableMultilingualString(secondQuayName));


        String stopPlaceName = "StopPlace";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlace.getQuays().add(secondQuay);

        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"%s", allVersions:true) {
                            id
                            name { value }
                            ... on StopPlace {
                                quays {
                                    id
                                    name { value }
                                }
                            }
                        }
                    }""".formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
                .body("data.stopPlace[0].quays.name.value", hasItems(firstQuayName, secondQuayName))
                .body("data.stopPlace[0].quays.id", hasItems(quay.getNetexId(), secondQuay.getNetexId()));
    }

    @Test
    public void retrieveStopPlaceWithPlaceEquipment() throws Exception {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("StopPlace"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));

        PlaceEquipment placeEquipment = new PlaceEquipment();
        List installedEquipments = new ArrayList<>();

        SanitaryEquipment toalett = new SanitaryEquipment();
        toalett.setNumberOfToilets(BigInteger.valueOf(2));
        toalett.setGender(GenderLimitationEnumeration.BOTH);

        installedEquipments.add(toalett);

        ShelterEquipment shelterEquipment = new ShelterEquipment();
        shelterEquipment.setSeats(BigInteger.valueOf(3));
        shelterEquipment.setEnclosed(true);
        shelterEquipment.setStepFree(true);

        installedEquipments.add(shelterEquipment);

        CycleStorageEquipment cycleStorageEquipment = new CycleStorageEquipment();
        cycleStorageEquipment.setNumberOfSpaces(BigInteger.valueOf(4));
        cycleStorageEquipment.setCycleStorageType(CycleStorageEnumeration.BARS);
        cycleStorageEquipment.setCovered(true);

        installedEquipments.add(cycleStorageEquipment);

        WaitingRoomEquipment waitingRoomEquipment = new WaitingRoomEquipment();
        waitingRoomEquipment.setSeats(BigInteger.valueOf(5));
        waitingRoomEquipment.setHeated(true);
        waitingRoomEquipment.setStepFree(true);

        installedEquipments.add(waitingRoomEquipment);

        TicketingEquipment ticketingEquipment = new TicketingEquipment();
        ticketingEquipment.setTicketMachines(true);
        ticketingEquipment.setTicketOffice(true);
        ticketingEquipment.setNumberOfMachines(BigInteger.valueOf(6));

        installedEquipments.add(ticketingEquipment);

        GeneralSign generalSign = new GeneralSign();
        generalSign.setContent(new EmbeddableMultilingualString("General sign"));
        generalSign.setPrivateCode(new PrivateCodeStructure("1234","type"));

        installedEquipments.add(generalSign);






        placeEquipment.getInstalledEquipment().addAll(installedEquipments);

        stopPlace.setPlaceEquipments(placeEquipment);


        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"StopPlace", allVersions:true) {
                            id
                            name { value }
                            placeEquipments {
                                id
                                shelterEquipment {
                                    id
                                    seats
                                    enclosed
                                    stepFree
                                }
                                sanitaryEquipment {
                                    id
                                    numberOfToilets
                                }
                                cycleStorageEquipment {
                                    id
                                    numberOfSpaces
                                    cycleStorageType
                                }
                                waitingRoomEquipment {
                                    id
                                    seats
                                    heated
                                    stepFree
                                }
                                ticketingEquipment {
                                    id
                                    ticketMachines
                                    ticketOffice
                                    numberOfMachines
                                }
                                generalSign {
                                    id
                                    content { value }
                                    privateCode {
                                        value
                                        type
                                    }
                                }
                            }
                        }
                    }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()))
                .body("name.value", equalTo("StopPlace"))
                .body("placeEquipments.id", equalTo(placeEquipment.getNetexId()))

                //generalSign
                .body("placeEquipments.generalSign[0].id", equalTo(generalSign.getNetexId()))
                .body("placeEquipments.generalSign[0].content.value", equalTo(generalSign.getContent().getValue()))
                .body("placeEquipments.generalSign[0].privateCode.value", equalTo(generalSign.getPrivateCode().getValue()))
                //ticketingEquipment
                .body("placeEquipments.ticketingEquipment[0].id", equalTo(ticketingEquipment.getNetexId()))
                .body("placeEquipments.ticketingEquipment[0].ticketMachines", equalTo(ticketingEquipment.isTicketMachines()))
                .body("placeEquipments.ticketingEquipment[0].ticketOffice", equalTo(ticketingEquipment.isTicketOffice()))
                .body("placeEquipments.ticketingEquipment[0].numberOfMachines", equalTo(ticketingEquipment.getNumberOfMachines().intValue()))
                //waitingRoomEquipment
                .body("placeEquipments.waitingRoomEquipment[0].id", equalTo(waitingRoomEquipment.getNetexId()))
                .body("placeEquipments.waitingRoomEquipment[0].seats", equalTo(waitingRoomEquipment.getSeats().intValue()))
                .body("placeEquipments.waitingRoomEquipment[0].heated", equalTo(waitingRoomEquipment.isHeated()))
                .body("placeEquipments.waitingRoomEquipment[0].stepFree", equalTo(waitingRoomEquipment.isStepFree()))
                //shelterEquipment
                .body("placeEquipments.shelterEquipment[0].id", equalTo(shelterEquipment.getNetexId()))
                .body("placeEquipments.shelterEquipment[0].seats", equalTo(shelterEquipment.getSeats().intValue()))
                .body("placeEquipments.shelterEquipment[0].enclosed", equalTo(shelterEquipment.isEnclosed()))
                .body("placeEquipments.shelterEquipment[0].stepFree", equalTo(shelterEquipment.isStepFree()))
                //sanitaryEquipment
                .body("placeEquipments.sanitaryEquipment[0].numberOfToilets", equalTo(toalett.getNumberOfToilets().intValue()))
                .body("placeEquipments.sanitaryEquipment[0].id", equalTo(toalett.getNetexId()))
                //cycleStorageEquipment
                .body("placeEquipments.cycleStorageEquipment[0].id", equalTo(cycleStorageEquipment.getNetexId()))
                .body("placeEquipments.cycleStorageEquipment[0].numberOfSpaces", equalTo(cycleStorageEquipment.getNumberOfSpaces().intValue()));

    }

    @Test
    public void mutateStopPlaceWithPlaceEquipmentOnQuay() {

        var quay = new Quay();
        var firstQuayName = "quay to add place equipment on";
        quay.setName(new EmbeddableMultilingualString(firstQuayName));

        var stopPlaceName = "StopPlace";
        var stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {id: "%s", quays: [{id: "%s", placeEquipments: {shelterEquipment: [{seats: 3}]}}]}) {
                id
                quays {
                  id
                  placeEquipments {
                    shelterEquipment {
                      seats
                    }
                  }
                }
              }
            }
            """.formatted(stopPlace.getNetexId(),quay.getNetexId());

        executeGraphqQLQueryOnly(graphqlQuery)
                .rootPath("data.stopPlace[0].quays[0]")
                    .body("placeEquipments", notNullValue())
                .rootPath("data.stopPlace[0].quays[0].placeEquipments.shelterEquipment[0]")
                .body("seats", equalTo(3));
    }

    @Test
    public void mutateStopPlaceRemoveAllShelterEquipmentFromQuay() {
        var quay = new Quay();
        var firstQuayName = "quay to add place equipment on";
        quay.setName(new EmbeddableMultilingualString(firstQuayName));

        var stopPlaceName = "StopPlace";
        var stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        var placeEquipments = new PlaceEquipment();
        var shelter = new ShelterEquipment();
        shelter.setBicycleParking(true);
        shelter.setTrashCan(true);
        placeEquipments.getInstalledEquipment().add(shelter);
        quay.setPlaceEquipments(placeEquipments);

        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {id: "%s", quays: [{id: "%s", placeEquipments: {shelterEquipment: []}}]}) {
                id
                quays {
                  id
                  placeEquipments {
                    shelterEquipment {
                      bicycleParking
                      trashCan
                    }
                  }
                }
              }
            }
            """.formatted(stopPlace.getNetexId(),quay.getNetexId());

        executeGraphqQLQueryOnly(graphqlQuery)
                .rootPath("data.stopPlace[0].quays[0]")
                .body("placeEquipments", notNullValue())
                .rootPath("data.stopPlace[0].quays[0].placeEquipments")
                .body("shelterEquipments", nullValue());
    }

    /**
     * Use explicit parameter for original ID search
     */
    @Test
    public void searchForStopPlaceByOriginalId() throws Exception {
        String stopPlaceName = "Eselstua";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlaceRepository.save(stopPlace);

        String originalId = "RUT:Stop:1234";
        Value value = new Value(originalId);
        stopPlace.getKeyValues().put(NetexIdMapper.ORIGINAL_ID_KEY, value);
        stopPlaceRepository.save(stopPlace);


        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (importedId:"RUT:Stop:1234", allVersions:true) {
                            id
                            name { value }
                        }
                    }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    /**
     * Use query parameter for original ID search
     */
    @Test
    public void searchForStopPlaceByOriginalIdQuery() throws Exception {
        String stopPlaceName = "Fleskeberget";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlaceRepository.save(stopPlace);

        String originalId = "BRA:StopPlace:666";
        Value value = new Value(originalId);
        stopPlace.getKeyValues().put(NetexIdMapper.ORIGINAL_ID_KEY, value);
        stopPlaceRepository.save(stopPlace);


        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"BRA:StopPlace:666", allVersions:true) {
                            id
                            name { value }
                        }
                    }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }


    /**
     * Use query parameter for original ID search
     */
    // todo: remove test when legacy coordinates are removed
    @Test
    public void searchForStopPlaceWithoutLegacyCoordinates() throws Exception {
        String basename = "koordinaten";
        String nameWithLocation = basename + " nr 1";
        StopPlace stopPlaceWithCoordinates = new StopPlace(new EmbeddableMultilingualString(nameWithLocation));
        stopPlaceWithCoordinates.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));

        String nameWithoutLocation = basename + " nr 2";
        StopPlace stopPlaceWithoutCoordinates = new StopPlace(new EmbeddableMultilingualString(nameWithoutLocation));
        stopPlaceWithoutCoordinates.setCentroid(null);

        stopPlaceRepository.save(stopPlaceWithCoordinates);
        stopPlaceRepository.save(stopPlaceWithoutCoordinates);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"koordinaten", allVersions:true) {
                            id
                            name { value }
                            geometry {legacyCoordinates }
                        }
                    }""";

        // Search for stopPlace should return both StopPlaces above
        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace.find { it.id == '" + stopPlaceWithCoordinates.getNetexId() + "'}")
                    .body("name.value", equalTo(nameWithLocation))
                    .body("geometry", notNullValue())
                    .body("geometry.legacyCoordinates", hasSize(1))
                .rootPath("data.stopPlace.find { it.id == '" + stopPlaceWithoutCoordinates.getNetexId() + "'}")
                    .body("name.value", equalTo(nameWithoutLocation))
                    .body("geometry", nullValue());

        graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"koordinaten", allVersions:true, withoutLocationOnly:true) {
                            id
                            name { value }
                            geometry {legacyCoordinates }
                        }
                    }""";

        // Filtering on withoutLocationsOnly stopPlace should only return one
        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .body("data.stopPlace[0].name.value", equalTo(nameWithoutLocation))
                .body("data.stopPlace[0].geometry", nullValue());

    }

    @Test
    public void searchForStopPlaceWithoutCoordinates() throws Exception {
        String basename = "koordinaten";
        String nameWithLocation = basename + " nr 1";
        StopPlace stopPlaceWithCoordinates = new StopPlace(new EmbeddableMultilingualString(nameWithLocation));
        stopPlaceWithCoordinates.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));

        String nameWithoutLocation = basename + " nr 2";
        StopPlace stopPlaceWithoutCoordinates = new StopPlace(new EmbeddableMultilingualString(nameWithoutLocation));
        stopPlaceWithoutCoordinates.setCentroid(null);

        stopPlaceRepository.save(stopPlaceWithCoordinates);
        stopPlaceRepository.save(stopPlaceWithoutCoordinates);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"koordinaten", allVersions:true) {
                            id
                            name { value }
                            geometry { coordinates }
                        }
                    }""";


        // Search for stopPlace should return both StopPlaces above
        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace.find { it.id == '" + stopPlaceWithCoordinates.getNetexId() + "'}")
                .body("name.value", equalTo(nameWithLocation))
                .body("geometry", notNullValue())
                .body("geometry.coordinates", notNullValue())
                .rootPath("data.stopPlace.find { it.id == '" + stopPlaceWithoutCoordinates.getNetexId() + "'}")
                .body("name.value", equalTo(nameWithoutLocation))
                .body("geometry", nullValue());

        graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"koordinaten", allVersions:true, withoutLocationOnly:true) {
                            id
                            name { value }
                            geometry { coordinates }
                        }
                    }""";

        // Filtering on withoutLocationsOnly stopPlace should only return one
        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .body("data.stopPlace[0].name.value", equalTo(nameWithoutLocation))
                .body("data.stopPlace[0].geometry", nullValue());

    }

    /**
     * Search for stop place by quay original ID
     */
    @Test
    public void searchForStopPlaceByQuayOriginalIdQuery() throws Exception {
        String stopPlaceName = "Travbanen";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));


        String quayOriginalId = "BRA:Quay:187";
        Quay quay = new Quay();
        quay.getOriginalIds().add(quayOriginalId);

        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"BRA:Quay:187", allVersions:true) {
                            id
                            name { value }
                        }
                    }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceNsrIdInQuery() throws Exception {
        String stopPlaceName = "Jallafjellet";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"%s", allVersions:true) {
                            id
                            name {value}
                        }
                    }""".formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void lookupStopPlaceAllVersions() throws Exception {

        String stopPlaceName = "TestPlace";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        StopPlace copy = versionCreator.createCopy(stopPlace, StopPlace.class);
        copy = stopPlaceVersionedSaverService.saveNewVersion(stopPlace, copy);

        assertThat(stopPlace.getVersion()).isEqualTo(1);
        assertThat(copy.getVersion()).isEqualTo(2);

        String graphQlJsonQuery = """
                  {
                   stopPlace(id:"%s", allVersions:true) {
                            id
                            name {value}
                            version
                        }
                   }""".formatted(stopPlace.getNetexId());


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(2))
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[1].id", equalTo(stopPlace.getNetexId()));
    }

    @Test
    public void searchForQuayNsrIdInQuery() throws Exception {
        String stopPlaceName = "Solkroken";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        Quay quay = new Quay();
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"%s", allVersions:true) {
                            id
                            name {value}
                        }
                    }""".formatted(quay.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceNoParamsExpectAllVersions() throws Exception {
        String stopPlaceName = "Eselstua";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (allVersions:true) {
                            name { value }
                        }
                    }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceAllEmptyParams() throws Exception {
        String stopPlaceName = "Eselstua";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceRepository.save(stopPlace);


        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (id:"" countyReference:"" municipalityReference:"" allVersions:true) {
                            id
                            name {value}
                        }
                    }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceByNameContainsCaseInsensitive() throws Exception {
        String stopPlaceName = "Grytnes";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"ytNES", allVersions:true) {
                            name {value}
                        }
                    }""";


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }


    @Test
    public void searchForStopPlaceByKeyValue() throws Exception {
        String stopPlaceName = "KeyValueStop";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        String key = "testKey";
        String value = "testValue";
        stopPlace.getKeyValues().put(key, new Value(value));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (key:"testKey", values:"testValue" allVersions:true) {
                            id
                            name { value }
                            keyValues { key values }
                        }
                    }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .rootPath("data.stopPlace[0]")
                    .body("name.value", equalTo(stopPlaceName))
                    .body("keyValues[0].key", equalTo(key))
                    .body("keyValues[0].values",  hasSize(1))
                    .body("keyValues[0].values[0]", equalTo(value));
    }

    @Test
    public void searchForStopsWithDifferentStopPlaceTypeShouldHaveNoResult() {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Fyrstekakeveien"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_TRAM);
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                  {
                  stopPlace (stopPlaceType:%s) {
                            name {value}
                        }
                    }""".formatted(StopTypeEnumeration.FERRY_STOP.value());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0));
    }


    @Test
    public void searchForExpiredStopPlace() {

        String name = "Gamleveien";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));

        Instant fromDate = now.minusSeconds(10000);
        Instant toDate = now.minusSeconds(1000);

        ValidBetween validBetween = new ValidBetween(fromDate, toDate);
        stopPlace.setValidBetween(validBetween);
        stopPlaceRepository.save(stopPlace);

        //Ensure that from- and toDate is before "now"
        assertThat(fromDate.isBefore(now)).isTrue();
        assertThat(toDate.isBefore(now)).isTrue();

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"Gamleveien", pointInTime:"%s") {
                            name {value}
                        }
                    }""".formatted(stopPlace.getValidBetween().getFromDate().plusSeconds(10));
        // Verify that pointInTime within validity-period returns expected StopPlace
        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(1));

        // Verify that pointInTime *after* validity-period returns null
        graphQlJsonQuery = """
                {
                stopPlace: stopPlace (query: "Gamleveien", pointInTime:"%s") {
                    name {value}
                    }
                }""".formatted(stopPlace.getValidBetween().getToDate().plusSeconds(10).toString());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0));

        // Verify that pointInTime *before* validity-period returns null
        graphQlJsonQuery = """
                {
                stopPlace: stopPlace (query:"Gamleveien", pointInTime:"%s") {
                    name {value}
                  }
                }""".formatted(stopPlace.getValidBetween().getFromDate().minusSeconds(100).toString());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0));

        // PointInTime must be set. If not, max version is returned.
        graphQlJsonQuery = """
                  {
                  stopPlace: stopPlace (query:"Gamleveien", pointInTime:"%s") {
                    name {value}
                  }
                }""".formatted(now.toString());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0));
    }

    @Test
    public void searchForStopsWithoutQuays() {

        String name = "fuscator";
        StopPlace stopPlaceWithoutQuays = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlaceWithoutQuays.setValidBetween(new ValidBetween(now.minusMillis(10000)));
        stopPlaceRepository.save(stopPlaceWithoutQuays);

        StopPlace stopPlaceWithQuays = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlaceWithQuays.setValidBetween(new ValidBetween(now.minusMillis(10000)));
        stopPlaceWithQuays.getQuays().add(new Quay());
        stopPlaceRepository.save(stopPlaceWithQuays);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"fuscator", withoutQuaysOnly:true) {
                            id
                            name {value}
                        }
                    }""";
        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(1))
                .body("data.stopPlace[0].id", equalTo(stopPlaceWithoutQuays.getNetexId()));
    }


    @Test
    public void searchForTramStopWithMunicipalityAndCounty() {

        TopographicPlace hordaland = new TopographicPlace(new EmbeddableMultilingualString("Hordaland"));
        topographicPlaceRepository.save(hordaland);

        TopographicPlace kvinnherad = createMunicipalityWithCountyRef("Kvinnherad", hordaland);

        StopPlace stopPlace = createStopPlaceWithMunicipalityRef("Anda", kvinnherad, StopTypeEnumeration.TRAM_STATION);
        stopPlaceRepository.save(stopPlace);


        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace 
                        (stopPlaceType:%s countyReference:"%s" municipalityReference:"%s", allVersions:true) {
                            name {value}
                        }
                    }""".formatted(StopTypeEnumeration.TRAM_STATION.value(),hordaland.getNetexId(),kvinnherad.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()));
    }

    @Test
    public void searchForStopsInMunicipalityThenExpectNoResult() {
        // Stop Place not related to municipality
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Nesbru"));
        stopPlaceRepository.save(stopPlace);

        TopographicPlace asker = new TopographicPlace(new EmbeddableMultilingualString("Asker"));
        topographicPlaceRepository.save(asker);

        String graphQlJsonQuery = """
                  {
                   stopPlace (municipalityReference:"%s") {
                            name {value}
                        }
                    }""".formatted(asker.getNetexId());


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(0));
    }

    @Test
    public void searchForStopInMunicipalityOnly() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")));
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus);
        String stopPlaceName = "Nesbru";
        createStopPlaceWithMunicipalityRef(stopPlaceName, asker);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (municipalityReference: ["%s"], allVersions:true) {
                            id
                            name {value}
                        }
                    }""".formatted(asker.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .body("data.stopPlace[0].name.value",  equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopsInTwoMunicipalities() {
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", null);
        TopographicPlace baerum = createMunicipalityWithCountyRef("Bærum", null);

        createStopPlaceWithMunicipalityRef("Nesbru", asker);
        createStopPlaceWithMunicipalityRef("Slependen", baerum);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (municipalityReference: ["%s", "%s"], allVersions:true) {
                            id
                            name { value }
                            ... on StopPlace {
                                quays {
                                    id
                                    name  { value }
                                }
                            }
                       }
                   }""".formatted(baerum.getNetexId(),asker.getNetexId());


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", hasItems("Nesbru", "Slependen"));
    }

    @Test
    public void searchForStopsInTwoCountiesAndTwoMunicipalities() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")));
        TopographicPlace buskerud = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Buskerud")));

        TopographicPlace lier = createMunicipalityWithCountyRef("Lier", buskerud);
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus);

        createStopPlaceWithMunicipalityRef("Nesbru", asker);
        createStopPlaceWithMunicipalityRef("Hennumkrysset", asker);

        var graphQlJsonQuery = """
                            { stopPlace: stopPlace (allVersions:true, countyReference:["%s","%s"] municipalityReference:["%s","%s"]) {
                            id
                            name { value }
                          }
                       }""".formatted(akershus.getNetexId(),buskerud.getNetexId(),lier.getNetexId(),asker.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", hasItems("Nesbru", "Hennumkrysset"));
    }

    @Test
    public void searchForStopsInDifferentMunicipalitiesButSameCounty() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")));
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus);
        TopographicPlace baerum = createMunicipalityWithCountyRef("Bærum", akershus);

        createStopPlaceWithMunicipalityRef("Trollstua", asker);
        createStopPlaceWithMunicipalityRef("Haslum", baerum);

        var graphQlJsonQuery = """
                            { stopPlace: stopPlace
                            (allVersions:true, countyReference:["%s"]) {
                            id
                            name { value }
                          }
                       }""".formatted(akershus.getNetexId());


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", hasItems("Trollstua", "Haslum"));
    }

    @Test
    public void searchForStopById() throws Exception {

        StopPlace stopPlace = createStopPlace("Espa");
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"%s", allVersions:true) {
                            id
                            name { value }
                        }
                    }""".formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()));
    }

    @Test
    public void getTariffZonesForStop() throws Exception {

        StopPlace stopPlace = new StopPlace();

        TariffZone tariffZone = new TariffZone();
        tariffZone.setName(new EmbeddableMultilingualString("V02"));
        tariffZone.setVersion(1L);
        tariffZoneRepository.save(tariffZone);

        stopPlace.getTariffZones().add(new TariffZoneRef(tariffZone));

        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (id:"%s", allVersions:true) {
                            id
                            tariffZones { id version name { value }}
                        }
                    }""".formatted(stopPlace.getNetexId());


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                    .body("tariffZones[0].id", equalTo(tariffZone.getNetexId()))
                    .body("tariffZones[0].name.value", equalTo(tariffZone.getName().getValue()));
    }

    @Test
    public void testSimpleMutationCreateStopPlace() throws Exception {

        String name = "Testing name";
        String shortName = "Testing shortname";
        String description = "Testing description";

        Float lon =  Float.valueOf("10.11111");
        Float lat = Float.valueOf("59.11111");

        String graphQlJsonQuery = """
                                     mutation {
                                     stopPlace: %s(StopPlace: {
                                               name: { value:"%s" }
                                               shortName:{ value:"%s" }
                                               description:{ value:"%s" }
                                               stopPlaceType:%s
                                               geometry: {
                                                 type: Point
                                                 coordinates: [%s,%s]
                                               }
                                       }) {
                                  id
                                  weighting
                                  name { value }
                                  shortName { value }
                                  description { value }
                                  stopPlaceType
                                  geometry { type coordinates }
                                 }
                                }
                """.formatted(GraphQLNames.MUTATE_STOPPLACE,name, shortName, description,StopTypeEnumeration.TRAM_STATION.value(), lon, lat);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                    .body("id", notNullValue())
                    .body("name.value", equalTo(name))
                    .body("shortName.value", equalTo(shortName))
                    .body("description.value", equalTo(description))
                    .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0]", comparesEqualTo(lon))
                    .body("geometry.coordinates[1]", comparesEqualTo(lat))
                    .body("weighting", comparesEqualTo(InterchangeWeightingEnumeration.INTERCHANGE_ALLOWED.value()));

        // for unit test we don't have a real JMS listener, so we need to check the event manually
        assertThat(entityChangedJMSListener.hasReceivedEvent(null, 1L, EntityChangedEvent.CrudAction.CREATE, null)).isFalse();
    }

    @Test
    public void testMutateStopWithTariffZoneRef() throws Exception {

        var tariffZone = new TariffZone();
        tariffZone.setName(new EmbeddableMultilingualString("tariff zone"));
        tariffZone.setNetexId("CRI:TariffZone:1");
        tariffZoneRepository.save(tariffZone);

        String graphqlQuery = """
            mutation {
              stopPlace:mutateStopPlace(StopPlace: {name: {value: "Name", lang:"nor"}, tariffZones: [{ref: "%s"}]}) {
                id
                tariffZones {
                  id
                  name {
                    value
                  }
                }
              }
            }
            """.formatted(tariffZone.getNetexId());

        executeGraphqQLQueryOnly(graphqlQuery)
        .rootPath("data.stopPlace[0]")
            .body("tariffZones", is(not(empty())))
            .body("tariffZones[0].id", equalTo(tariffZone.getNetexId()))
            .body("tariffZones[0].name.value", equalTo(tariffZone.getName().getValue()));
    }


    /**
     * Test added for NRP-1851
     * @throws Exception
     */
    @Test
    public void testSimpleMutationCreateStopPlaceImportedIdWithNewLine() throws Exception {

        String name = "Testing name";
        String jsonFriendlyNewLineStr = "\\\\n";
        String shortName = "          ";
        String originalId = "   TEST:1234    ";

        String graphQlJsonQuery = """
                mutation {
                stopPlace: %s(StopPlace: {
                          name: { value:"%s" }
                          shortName:{ value:"%s" }
                          keyValues:{ key:"%s" values:"%s" }
                  }) { 
                        id
                         name { value }
                         shortName { value }
                         keyValues { key values }
                    }
                }
                """.formatted(GraphQLNames.MUTATE_STOPPLACE,
                              name,
                              shortName + jsonFriendlyNewLineStr,
                              GraphQLNames.IMPORTED_ID,
                              originalId + jsonFriendlyNewLineStr);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                .body("id", notNullValue())
                .body("name.value", equalTo(name))
                .body("shortName.value", equalTo(""))
                .body("keyValues[0].key", equalTo(GraphQLNames.IMPORTED_ID))
                .body("keyValues[0].values[0]", equalTo(originalId.trim()));

        // for unit test we don't have a real JMS listener, so we need to check the event manually
        assertThat(entityChangedJMSListener.hasReceivedEvent(null, 1L, EntityChangedEvent.CrudAction.CREATE,null)).isFalse();
    }

    @Test
    public void create_parent_stop_place() {
        var bus = new StopPlace();
        bus.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        bus.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(bus);

        var tram = new StopPlace();
        tram.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        tram.setStopPlaceType(StopTypeEnumeration.TRAM_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(tram);

        var fromDate = now.plusSeconds(100000);

        var parentStopPlaceName = "Super stop place name";
        var versionComment = "VersionComment";

        var graphQlJsonQuery = """
                 mutation {
                 stopPlace: createMultiModalStopPlace (input: {
                          stopPlaceIds:["%s" ,"%s"]
                          name: { value: "%s" }
                          validBetween: { fromDate:"%s" }
                          versionComment:"%s"
                       }) {
                          id
                          name { value }
                          children {
                           id name { value } stopPlaceType version
                          }
                          validBetween { fromDate toDate }
                          versionComment
                       }
                  } """.formatted(bus.getNetexId(),tram.getNetexId(),parentStopPlaceName,fromDate,versionComment);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", equalTo(parentStopPlaceName))
                .body("data.stopPlace.stopPlaceType", nullValue())
                .body("data.stopPlace.versionComment", equalTo(versionComment))
                .rootPath("data.stopPlace.children.find { it.id == '" + tram.getNetexId() + "'}")
                .body("version", equalTo(String.valueOf(tram.getVersion()+1)))
                .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                .body("name", nullValue())
                .rootPath("data.stopPlace.children.find { it.id == '" + bus.getNetexId() + "'}")
                .body("name", nullValue())
                .body("stopPlaceType", equalTo(StopTypeEnumeration.BUS_STATION.value()))
                .body("version", equalTo(String.valueOf(bus.getVersion()+1)));
    }


    @Transactional
    StopPlace createParentInTransaction(StopPlace existingChild, StopPlace newChild, EmbeddableMultilingualString parentStopPlaceName) {

        existingChild = stopPlaceVersionedSaverService.saveNewVersion(existingChild);
        newChild = stopPlaceVersionedSaverService.saveNewVersion(newChild);
        return multiModalStopPlaceEditor.createMultiModalParentStopPlace(List.of(existingChild.getNetexId()), parentStopPlaceName);
    }

    @Autowired
    private MultiModalStopPlaceEditor multiModalStopPlaceEditor;

    @Test
    public void add_child_to_parent_stop_place() {
        var existingChild = new StopPlace();

        existingChild.setStopPlaceType(StopTypeEnumeration.HARBOUR_PORT);

        var newChild = new StopPlace(new EmbeddableMultilingualString("new child"));
        newChild.setVersion(10L);
        newChild.setStopPlaceType(StopTypeEnumeration.LIFT_STATION);

        System.out.println("tariff zones new child: ${newChild.tariffZones}");

        var parentStopPlaceName = "parent stop place name";

        var parent = createParentInTransaction(existingChild, newChild, new EmbeddableMultilingualString(parentStopPlaceName));

        var versionComment = "VersionComment";

        // Make sure dates are after previous version of parent stop place
        final var graphQlJsonQuery = getGraphQlJsonQuery(parent, newChild, versionComment);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", equalTo(parentStopPlaceName))
                .body("data.stopPlace.stopPlaceType", nullValue())
                .body("data.stopPlace.versionComment", equalTo(versionComment))
                .body("data.stopPlace.version", equalTo("2"))

                .rootPath("data.stopPlace.children.find { it.id == '%s'}".formatted(existingChild.getNetexId()))

                    .body("name.value", nullValue())
                    // version 3 expected. 1: created, 2: added to parent stop, 3: new child added to parent stop
                    .body("version", equalTo("%s".formatted(existingChild.getVersion()+2)))
                    .body("stopPlaceType", equalTo(existingChild.getStopPlaceType().value()))

                .rootPath("data.stopPlace.children.find { it.id == '%s'}".formatted(newChild.getNetexId()))
                    .body("name.value", equalTo(newChild.getName().getValue()))
                    .body("version", equalTo("%s".formatted(newChild.getVersion()+1)))
                    .body("stopPlaceType", equalTo(newChild.getStopPlaceType().value()));

    }

    private static String getGraphQlJsonQuery(StopPlace parent, StopPlace newChild, String versionComment) {
        var fromDate = parent.getValidBetween().getFromDate().plusSeconds(1000);
        var toDate = fromDate.plusSeconds(70000);

        var graphQlJsonQuery = """
                 mutation {
                 stopPlace: addToMultiModalStopPlace (input: {
                          parentSiteRef: "%s"
                          stopPlaceIds:["%s"]
                          validBetween: { fromDate:"%s", toDate:"%s" }
                          versionComment:"%s"
                       }) {
                          id
                          name { value }
                          children {
                           id name { value } stopPlaceType version
                          }
                          validBetween { fromDate toDate }
                          version
                          versionComment
                       }
                  } """.formatted(parent.getNetexId(), newChild.getNetexId(),fromDate,toDate, versionComment);
        return graphQlJsonQuery;
    }

    @Test
    public void testSimpleMutationUpdateStopPlace() throws Exception {
        TopographicPlace parentTopographicPlace = new TopographicPlace(new EmbeddableMultilingualString("countyforinstance"));
        parentTopographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        topographicPlaceRepository.save(parentTopographicPlace);
        TopographicPlace topographicPlace = createMunicipalityWithCountyRef("somewhere in space", parentTopographicPlace);

        StopPlace stopPlace = createStopPlace("Espa");
        stopPlace.setShortName(new EmbeddableMultilingualString("E"));
        stopPlace.setDescription(new EmbeddableMultilingualString("E6s beste boller"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAllAreasWheelchairAccessible(false);
        stopPlace.setTopographicPlace(topographicPlace);
        stopPlace.setWeighting(InterchangeWeightingEnumeration.NO_INTERCHANGE);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace adjacentStopPlace = createStopPlace("Adjacent Site");
        adjacentStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.11111, 59.11111)));
        stopPlaceVersionedSaverService.saveNewVersion(adjacentStopPlace);

        String updatedName = "Testing name";
        String updatedShortName = "Testing shortname";
        String updatedDescription = "Testing description";
        Float updatedLon = Float.valueOf("10.11111");
        Float updatedLat = Float.valueOf("59.11111");

        String versionComment = "Stop place moved";

        InterchangeWeightingEnumeration weighting = InterchangeWeightingEnumeration.INTERCHANGE_ALLOWED;

        String graphQlJsonQuery = """
            mutation {
            stopPlace: mutateStopPlace(StopPlace: {
                        id: "%s"
                        name: { value: "%s" }
                        shortName: { value: "%s" }
                        description: { value:"%s" }
                        adjacentSites: [ {ref: "%s" }]
                        stopPlaceType: %s
                        versionComment: "%s"
                        geometry: {
                          type: Point
                          coordinates: [%s,%s]
                        }
                        weighting: %s
                }) {
                    id
                    name { value }
                    shortName { value }
                    description { value }
                    adjacentSites { ref }
                    stopPlaceType
                    versionComment
                    topographicPlace { id topographicPlaceType parentTopographicPlace { id topographicPlaceType }}
                    weighting
                    geometry { type coordinates }
                    validBetween { fromDate toDate }
                }
            }""".formatted(stopPlace.getNetexId(),
                updatedName,
                updatedShortName,
                updatedDescription,
                adjacentStopPlace.getNetexId(),
                StopTypeEnumeration.TRAM_STATION.value(),
                versionComment,
                updatedLon,
                updatedLat,
                weighting.value()
        );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                    .body("name.value", equalTo(updatedName))
                    .body("shortName.value", equalTo(updatedShortName))
                    .body("description.value", equalTo(updatedDescription))
                    .body("adjacentSites[0].ref", equalTo(adjacentStopPlace.getNetexId()))
                    .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                    .body("versionComment", equalTo(versionComment))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0]", comparesEqualTo(updatedLon))
                    .body("geometry.coordinates[1]", comparesEqualTo(updatedLat))
                    .body("weighting", equalTo(weighting.value()))
                    .body("topographicPlace.id", notNullValue())
                    .body("topographicPlace.topographicPlaceType", equalTo(TopographicPlaceTypeEnumeration.MUNICIPALITY.value()))
                    .body("topographicPlace.parentTopographicPlace", notNullValue())
                    .body("topographicPlace.parentTopographicPlace.id", notNullValue())
                    .body("topographicPlace.parentTopographicPlace.topographicPlaceType", equalTo(TopographicPlaceTypeEnumeration.COUNTY.value()));
    }

    @Test
    public void testTerminateStopPlaceValidity() throws Exception {
        StopPlace stopPlace = createStopPlace("Stop place soon to be invalidated");
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setValidBetween(new ValidBetween(Instant.EPOCH));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String versionComment = "Stop place not valid anymore";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

        // Mutate stop place. The new version should have valid from now.
        String fromDate = dateTimeFormatter.format(now.atZone(exportTimeZone.getDefaultTimeZoneId()));

        // The new version should be terminated in the future.
        String toDate = dateTimeFormatter.format(now.plusSeconds(2000).atZone(exportTimeZone.getDefaultTimeZoneId()));

        String graphQlJsonQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s"
                versionComment: "%s"
                validBetween: { fromDate: "%s", toDate: "%s" }
              }) {
                id
                versionComment
                validBetween { fromDate toDate }
              }
            }
            """.formatted(stopPlace.getNetexId(), versionComment, fromDate, toDate);


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0]", notNullValue())
                .rootPath("data.stopPlace[0]")
                    .body("versionComment", equalTo(versionComment))
                    .body("validBetween.fromDate", comparesEqualTo(fromDate))
                    .body("validBetween.toDate", comparesEqualTo(toDate));
    }


    @Test
    public void testSimpleMutationUpdateKeyValuesStopPlace() throws Exception {

        StopPlace stopPlace = createStopPlace("Espa");
        stopPlace.setShortName(new EmbeddableMultilingualString("E"));
        stopPlace.setDescription(new EmbeddableMultilingualString("E6s beste boller"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAllAreasWheelchairAccessible(false);
        stopPlace.setWeighting(InterchangeWeightingEnumeration.NO_INTERCHANGE);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String graphQlJsonQuery = """
                 mutation {
                  stopPlace: mutateStopPlace (StopPlace: {
                          id: "%s"
                          keyValues: [{ key: "jbvId", values: ["1234"] }]
                      }) {
                          id
                          keyValues { key values }
                      }
                  }""".formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                    .body("id", equalTo(stopPlace.getNetexId()))
                    .body("keyValues[0].key", equalTo("jbvId"))
                    .body("keyValues[0].values[0]", equalTo("1234"));
    }

    @Test
    public void testSimpleMutationUpdateAlternativeNamesStopPlace() throws Exception {
        StopPlace stopPlace = createStopPlace("Espa");
        stopPlace.setShortName(new EmbeddableMultilingualString("E"));
        stopPlace.setDescription(new EmbeddableMultilingualString("E6s beste boller"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAllAreasWheelchairAccessible(false);
        stopPlace.setWeighting(InterchangeWeightingEnumeration.NO_INTERCHANGE);

        final var translationValue = "E6s bästa bullarna";
        final var aliasValue = "Alias";

        final var translation = new AlternativeName();
        translation.setName(new EmbeddableMultilingualString(translationValue, "swe"));
        translation.setNameType(NameTypeEnumeration.TRANSLATION);
        stopPlace.getAlternativeNames().add(translation);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String graphQlJsonQuery = """
                 mutation {
                  stopPlace: mutateStopPlace (StopPlace: {
                          id: "%s"
                          alternativeNames: [{
                              name: { lang: "swe", value: "%s" },
                              nameType: translation
                          }, {
                              name: { lang: "swe", value: "%s" },
                              nameType: alias
                          }],
                      }) {
                          id
                          alternativeNames {
                              nameType,
                              name { lang, value }
                          }
                      }
                  }""".formatted(stopPlace.getNetexId(), translationValue, aliasValue);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()))
                .body("alternativeNames[0].nameType", equalTo("translation"))
                .body("alternativeNames[0].name.lang", equalTo("swe"))
                .body("alternativeNames[0].name.value", equalTo(translationValue))
                .body("alternativeNames[1].nameType", equalTo("alias"))
                .body("alternativeNames[1].name.lang", equalTo("swe"))
                .body("alternativeNames[1].name.value", equalTo(aliasValue));
    }

    @Test
    public void testSimpleStopPlaceWithAccessibilityAssesment() throws Exception {
        String stopPlaceName = "StopPlace";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(LimitationStatusEnumeration.FALSE);

        List<AccessibilityLimitation> limitations = new ArrayList<>();
        limitations.add(limitation);


        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setMobilityImpairedAccess(LimitationStatusEnumeration.TRUE);
        accessibilityAssessment.setLimitations(limitations);

        stopPlace.setAccessibilityAssessment(accessibilityAssessment);

        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        stopPlaceRepository.save(stopPlace);

        stopPlace.getNetexId();

        String graphQlJsonQuery = """
                  {
                  stopPlace:  stopPlace (query:"%s", allVersions:true) {
                            id
                            name { value }
                            accessibilityAssessment {
                                id
                                mobilityImpairedAccess
                                limitations {
                                    id
                                    wheelchairAccess
                                }
                            }
                            
                        }
                    }""".formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
                .body("data.stopPlace[0].accessibilityAssessment.mobilityImpairedAccess", equalTo(LimitationStatusEnumeration.TRUE.name()))
                .body("data.stopPlace[0].accessibilityAssessment.id", equalTo(stopPlace.getAccessibilityAssessment().getNetexId()))
                .body("data.stopPlace[0].accessibilityAssessment.limitations.id", equalTo(stopPlace.getAccessibilityAssessment().getLimitations().getFirst().getNetexId()))
                .body("data.stopPlace[0].accessibilityAssessment.limitations.wheelchairAccess", equalTo(LimitationStatusEnumeration.FALSE.name()));


    }

    @Test
    public void testSimpleMutationUpdateTransportModeStopPlace() throws Exception {

        StopPlace stopPlace = createStopPlace("Bussen");
        stopPlace.setTransportMode(VehicleModeEnumeration.BUS);
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.LOCAL_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String newTransportMode = VehicleModeEnumeration.TRAM.value();
        String newSubmode = TramSubmodeEnumeration.LOCAL_TRAM.value();
        String graphQlJsonQuery = """
                 mutation {
                  stopPlace: mutateStopPlace (StopPlace: {
                          id: "%s"
                          transportMode: %s
                          submode: %s
                      }) {
                          id
                          transportMode
                          submode
                      }
                  }""".formatted(stopPlace.getNetexId(), newTransportMode, newSubmode);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()))
                .body("transportMode", equalTo(newTransportMode))
                .body("submode", equalTo(newSubmode));

        var stopPlaces = stopPlaceRepository.findAll();
        for(StopPlace stopPlaceVersion : stopPlaces) {
            if( stopPlaceVersion.getVersion() == 1) {
                assertThat(stopPlaceVersion.getBusSubmode()).as("version 1").isNotNull().isEqualTo(BusSubmodeEnumeration.LOCAL_BUS);
                assertThat(stopPlaceVersion.getTramSubmode()).as("version 1").isNull();
            } else if (stopPlaceVersion.getVersion() == 2) {
                assertThat(stopPlaceVersion.getBusSubmode()).as("version 2").isNull();
                assertThat(stopPlaceVersion.getTramSubmode()).as("version 2").isNotNull().isEqualTo(TramSubmodeEnumeration.LOCAL_TRAM);
            }
        }
    }

    @Test
    public void testGetValidTransportModes() throws Exception {

        String graphQlJsonQuery = """
                { 
                  validTransportModes {
                    transportMode
                    submode
                  }
                }
                """;


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.validTransportModes", notNullValue())
                .body("data.validTransportModes[0].transportMode", notNullValue())
                .body("data.validTransportModes[0].submode", notNullValue());
    }


    @Test
    public void testSimpleMutationCreateQuay() throws Exception {

        StopPlace stopPlace = createStopPlace("Espa");

        stopPlaceRepository.save(stopPlace);

        String name = "Testing name";
        String shortName = "Testing shortname";
        String description = "Testing description";
        String publicCode = "publicCode 2";

        String privateCodeValue = "PB03";
        String privateCodeType = "Type";

        String versionComment = "This is the initial version of the quay";

        Float lon =  Float.valueOf("10.11111");
        Float lat =  Float.valueOf("59.11111");

        String graphQlJsonQuery = """
                mutation {
                 stopPlace:mutateStopPlace(StopPlace: {
                          id: "%s"
                          quays: [{
                            name: { value: "%s" }
                            shortName: { value: "%s" }
                            description: { value: "%s" }
                            publicCode: "%s"
                            privateCode: { value: "%s", type: "%s" }
                            versionComment: "%s"
                            geometry: {
                              type: Point
                              coordinates: [%s,%s]
                            }
                          }]
                      }) {
                          id
                          name { value }
                          quays {
                            id
                            name { value }
                            shortName { value }
                            description { value }
                            publicCode
                            privateCode { value type }
                            versionComment
                            geometry { type coordinates }
                          }
                      }
                  }""".formatted(stopPlace.getNetexId(), name, shortName, description, publicCode, privateCodeValue, privateCodeType, versionComment, lon, lat);


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", notNullValue())
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
                .rootPath("data.stopPlace[0].quays[0]")
                    .body("id", notNullValue())
                    .body("name.value", equalTo(name))
                    .body("shortName.value", equalTo(shortName))
                    .body("description.value", equalTo(description))
                    .body("publicCode", equalTo(publicCode))
                    .body("privateCode.value", equalTo(privateCodeValue))
                    .body("privateCode.type", equalTo(privateCodeType))
                    .body("privateCode.type", equalTo(privateCodeType))
                    .body("versionComment", equalTo(versionComment))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0]", comparesEqualTo(lon))
                    .body("geometry.coordinates[1]", comparesEqualTo(lat));
    }

    @Test
    public void testSimpleMutationUpdateQuay() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        Quay quay = new Quay();
        quay.setCompassBearing(Float.valueOf("90"));
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String name = "Testing name";
        String shortName = "Testing shortname";
        String description = "Testing description";

        Float lon =  Float.valueOf("10.11111");
        Float lat =  Float.valueOf("59.11111");

        Float compassBearing =  Float.valueOf("180");

        String graphQlJsonQuery = """
                mutation {
                 stopPlace:mutateStopPlace(StopPlace: {
                          id: "%s"
                          quays: [{ 
                            id: "%s"
                            name: { value: "%s" }
                            shortName: { value: "%s" }
                            description: { value: "%s" }
                            geometry: {
                              type: Point
                              coordinates: [%s,%s]
                            }
                            compassBearing: %s
                          }]
                      }) {
                          id
                          name { value }
                          quays {
                            id
                            name { value }
                            shortName { value }
                            description { value }
                            geometry { type coordinates }
                            compassBearing
                          }
                      }
                  }""".formatted(stopPlace.getNetexId(), quay.getNetexId(), name, shortName, description, lon, lat, compassBearing);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
                .rootPath("data.stopPlace[0].quays[0]")
                    .body("id", comparesEqualTo(quay.getNetexId()))
                    .body("name.value", equalTo(name))
                    .body("shortName.value", equalTo(shortName))
                    .body("description.value", equalTo(description))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0]", comparesEqualTo(lon))
                    .body("geometry.coordinates[1]", comparesEqualTo(lat))
                    .body("compassBearing", comparesEqualTo(compassBearing));
    }


    @Test
    public void testMoveQuayToNewStop() throws Exception {

        StopPlace stopPlace = new StopPlace();

        Quay quay = new Quay();
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String versionComment = "moving quays";

        String graphQlJsonQuery = """
                    mutation {
                    stopPlace: %s (%s: "%s", %s: "%s") {
                        id
                        ...on StopPlace {
                            quays {
                                id
                            }
                        }
                        versionComment
                    }
                }
              """.formatted(MOVE_QUAYS_TO_STOP,QUAY_IDS,quay.getNetexId(),TO_VERSION_COMMENT,versionComment);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.id", not(comparesEqualTo(stopPlace.getNetexId())))
                .body("data.stopPlace.versionComment", equalTo(versionComment))
                .rootPath("data.stopPlace.quays[0]")
                    .body("id", comparesEqualTo(quay.getNetexId()));
    }


    @Test
    public void testSimpleMutationAddSecondQuay() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1l);
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        Quay quay = new Quay();
        quay.setCompassBearing( Float.valueOf("90"));
        Point point = geometryFactory.createPoint(new Coordinate(11.2, 60.2));
        quay.setCentroid(point);
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String name = "Testing name";
        String shortName = "Testing shortname";
        String description = "Testing description";

        Float lon =  Float.valueOf("10.11111");
        Float lat =  Float.valueOf("59.11111");

        Float compassBearing =  Float.valueOf("180");

        String graphQlJsonQuery = """
                mutation {
                 stopPlace:mutateStopPlace(StopPlace: {
                          id: "%s"
                          quays: [{ 
                            name: { value: "%s" }
                            shortName: { value: "%s" }
                            description: { value: "%s" }
                            geometry: {
                              type: Point
                              coordinates: [%s,%s]
                            }
                            compassBearing: %s
                          }]
                      }) {
                          id
                          name { value }
                          quays {
                            id
                            name { value }
                            shortName { value }
                            description { value }
                            geometry { type coordinates }
                            compassBearing
                          }
                      }
                  }""".formatted(stopPlace.getNetexId(), name, shortName, description, lon, lat, compassBearing);


        String manuallyAddedQuayId = quay.getNetexId();


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
                .body("data.stopPlace[0].quays", hasSize(2))
                        // First Quay - added manually
                .rootPath("data.stopPlace[0].quays.find { it.id == '" + manuallyAddedQuayId + "'}")
                    .body("name", nullValue())
                    .body("shortName", nullValue())
                    .body("description", nullValue())
                    .body("geometry.type", equalTo(point.getGeometryType()))
                    .body("geometry.coordinates[0]", comparesEqualTo(Float.valueOf(String.valueOf(point.getX()))))
                    .body("geometry.coordinates[1]", comparesEqualTo(Float.valueOf(String.valueOf(point.getY()))))
                    .body("compassBearing", comparesEqualTo(quay.getCompassBearing()))
                        // Second Quay - added using GraphQL
                .rootPath("data.stopPlace[0].quays.find { it.id != '" + manuallyAddedQuayId + "'}")
                    .body("name.value", equalTo(name))
                    .body("shortName.value", equalTo(shortName))
                    .body("description.value", equalTo(description))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0]", comparesEqualTo(lon))
                    .body("geometry.coordinates[1]", comparesEqualTo(lat))
                    .body("compassBearing", comparesEqualTo(compassBearing));
        // for unit test we do not have JMS listener
        assertThat(entityChangedJMSListener.hasReceivedEvent(stopPlace.getNetexId(), stopPlace.getVersion() + 1, EntityChangedEvent.CrudAction.UPDATE, null)).isFalse();
    }



    @Test
    public void testMutationUpdateStopPlaceCreateQuayAndUpdateQuay() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        Quay quay = new Quay();
        quay.setCompassBearing( Float.valueOf("90"));
        Point point = geometryFactory.createPoint(new Coordinate(11.2, 60.2));
        quay.setCentroid(point);
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String newStopName = "Shell - E6";
        String newQuayName = "Testing name 1";
        String newQuayShortName = "Testing shortname 1";
        String newQuayDescription = "Testing description 1";

        String updatedName = "Testing name 2";
        String updatedShortName = "Testing shortname 2";
        String updatedDescription = "Testing description 2";

        Float lon =  Float.valueOf("10.11111");
        Float lat =  Float.valueOf("59.11111");

        Float compassBearing =  Float.valueOf("180");

        String graphQlJsonQuery = """
                mutation {
                 stopPlace:mutateStopPlace(StopPlace: {
                          id: "%s"
                          name: { value: "%s" }
                          quays: [{ 
                            name: { value: "%s" }
                            shortName: { value: "%s" }
                            description: { value: "%s" }
                            geometry: {
                              type: Point
                              coordinates: [%s,%s]
                            }
                            compassBearing: %s
                          }, {
                            id: "%s" 
                            name: { value: "%s" }
                            shortName: { value: "%s" }
                            description: { value: "%s" } 
                          }]
                      }) {
                          id
                          name { value }
                          quays {
                            id
                            name { value }
                            shortName { value }
                            description { value }
                            geometry { type coordinates }
                            compassBearing
                          }
                      }
                  }""".formatted(stopPlace.getNetexId(), newStopName, newQuayName, newQuayShortName, newQuayDescription, lon, lat, compassBearing,quay.getNetexId(),updatedName,updatedShortName,updatedDescription);


        String manuallyAddedQuayId = quay.getNetexId();


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(newStopName))
                .body("data.stopPlace[0].quays", hasSize(2))
                        // First Quay - added manually, then updated
                .rootPath("data.stopPlace[0].quays.find { it.id == '" + manuallyAddedQuayId + "'}")
                    .body("name.value", equalTo(updatedName))
                    .body("shortName.value", equalTo(updatedShortName))
                    .body("description.value", equalTo(updatedDescription))
                    .body("geometry.type", equalTo(point.getGeometryType()))
                    .body("geometry.coordinates[0]", comparesEqualTo( Float.valueOf(String.valueOf(point.getX()))))
                    .body("geometry.coordinates[1]", comparesEqualTo( Float.valueOf(String.valueOf(point.getY()))))
                    .body("compassBearing", comparesEqualTo(quay.getCompassBearing()))

                        // Second Quay - added using GraphQL
                .rootPath("data.stopPlace[0].quays.find { it.id != '" + manuallyAddedQuayId + "'}")
                    .body("id", not(stopPlace.getNetexId()))
                    .body("name.value", equalTo(newQuayName))
                    .body("shortName.value", equalTo(newQuayShortName))
                    .body("description.value", equalTo(newQuayDescription))
                    .body("geometry.type", equalTo(point.getGeometryType()))
                    .body("geometry.coordinates[0]", comparesEqualTo(lon))
                    .body("geometry.coordinates[1]", comparesEqualTo(lat))
                    .body("compassBearing", comparesEqualTo(compassBearing));
        // for unit test we do not have JMS listener
        assertThat(entityChangedJMSListener.hasReceivedEvent(stopPlace.getNetexId(), stopPlace.getVersion() + 1, EntityChangedEvent.CrudAction.UPDATE, null)).isFalse();
    }

    @Test
    public void testTicketMachineTicketOfficeTrueFalse() {

        var stopPlace = new StopPlace();
        TicketingEquipment ticketingEquipment = new TicketingEquipment();
        ticketingEquipment.setTicketMachines(null);
        ticketingEquipment.setTicketOffice(null);
        ticketingEquipment.setNumberOfMachines(BigInteger.valueOf(7));

        PlaceEquipment placeEquipment = new PlaceEquipment();

        placeEquipment.getInstalledEquipment().add(ticketingEquipment);

        stopPlace.setPlaceEquipments(placeEquipment);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var query = """
                  {
                  stopPlace(
                      query:"%s"
                    ) {
                  placeEquipments {
                    ticketingEquipment {
                        ticketMachines
                        numberOfMachines
                        ticketOffice
                      }
                    }
                  }
                }
        """.formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(query)
                .body("data.stopPlace[0].placeEquipments.ticketingEquipment[0].ticketMachines", is(true))
                .body("data.stopPlace[0].placeEquipments.ticketingEquipment[0].ticketOffice", Matchers.is(false));

    }

    /**
     * Test that reproduces NRP-1433
     *
     * @throws Exception
     */
    @Test
    public void testSimpleMutationUpdateStopPlaceKeepPlaceEquipmentsOnQuay() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        stopPlace.setPlaceEquipments(createPlaceEquipments());

        Quay quay = new Quay();
        quay.setCompassBearing( Float.valueOf("90"));
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        quay.setPlaceEquipments(createPlaceEquipments());
        stopPlace.getQuays().add(quay);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String name = "Testing name";
        String netexId = stopPlace.getNetexId();

        String graphQlStopPlaceQuery = """
                {
                stopPlace(id: "%s") {
                    id
                    placeEquipments {
                        waitingRoomEquipment { id }
                        sanitaryEquipment { id }
                        ticketingEquipment { id }
                        cycleStorageEquipment { id }
                        shelterEquipment { id }
                        generalSign { id }
                    }
                    ... on StopPlace {
                    quays {
                        id
                        placeEquipments {
                            waitingRoomEquipment { id }
                            sanitaryEquipment { id }
                            ticketingEquipment { id }
                            cycleStorageEquipment { id }
                            shelterEquipment { id }
                            generalSign { id }
                        }
                      }
                    }
                   }
                }
                """.formatted(netexId);


        executeGraphqQLQueryOnly(graphQlStopPlaceQuery)
                .rootPath("data.stopPlace[0]")
                    .body("id", comparesEqualTo(netexId))
                    .body("placeEquipments", notNullValue())
                    .body("placeEquipments.waitingRoomEquipment[0]", notNullValue())
                    .body("placeEquipments.sanitaryEquipment[0]", notNullValue())
                    .body("placeEquipments.ticketingEquipment[0]", notNullValue())
                    .body("placeEquipments.cycleStorageEquipment[0]", notNullValue())
                    .body("placeEquipments.shelterEquipment[0]", notNullValue())
                    .body("placeEquipments.generalSign[0]", notNullValue())
                .rootPath("data.stopPlace[0].quays[0]")
                    .body("id", notNullValue())
                    .body("placeEquipments", notNullValue())
                    .body("placeEquipments.waitingRoomEquipment[0]", notNullValue())
                    .body("placeEquipments.sanitaryEquipment[0]", notNullValue())
                    .body("placeEquipments.ticketingEquipment[0]", notNullValue())
                    .body("placeEquipments.cycleStorageEquipment[0]", notNullValue())
                    .body("placeEquipments.shelterEquipment[0]", notNullValue())
                    .body("placeEquipments.generalSign[0]", notNullValue());

        //Update StopPlace name
        String graphQlJsonQuery= """
                mutation {
                stopPlace: mutateStopPlace (StopPlace: {
                        id: "%s"
                        name: { value: "%s" }
                    }) {
                        id
                        name { value }
                        placeEquipments {
                            waitingRoomEquipment { id }
                            sanitaryEquipment { id }
                            ticketingEquipment { id }
                            cycleStorageEquipment { id }
                            shelterEquipment { id }
                            generalSign { id }
                        }
                        ... on StopPlace {
                        quays {
                            id
                            placeEquipments {
                                waitingRoomEquipment { id }
                                sanitaryEquipment { id }
                                ticketingEquipment { id }
                                cycleStorageEquipment { id }
                                shelterEquipment { id }
                                generalSign { id }
                            }
                        }
                       }
                    }
                }
                """.formatted(netexId, name);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                    .body("id", comparesEqualTo(netexId))
                    .body("name.value", comparesEqualTo(name))
                    .body("placeEquipments", notNullValue())
                    .body("placeEquipments.waitingRoomEquipment", notNullValue())
                    .body("placeEquipments.sanitaryEquipment", notNullValue())
                    .body("placeEquipments.ticketingEquipment", notNullValue())
                    .body("placeEquipments.cycleStorageEquipment", notNullValue())
                    .body("placeEquipments.shelterEquipment", notNullValue())
                    .body("placeEquipments.generalSign", notNullValue())
                .rootPath("data.stopPlace[0].quays[0]")
                    .body("id", notNullValue())
                    .body("placeEquipments", notNullValue())
                    .body("placeEquipments.waitingRoomEquipment", notNullValue())
                    .body("placeEquipments.sanitaryEquipment", notNullValue())
                    .body("placeEquipments.ticketingEquipment", notNullValue())
                    .body("placeEquipments.cycleStorageEquipment", notNullValue())
                    .body("placeEquipments.shelterEquipment", notNullValue())
                    .body("placeEquipments.generalSign", notNullValue());

    }


    @Test
    public void testSimpleSaveAlternativeNames() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        AlternativeName altName = new AlternativeName();
        altName.setNameType(NameTypeEnumeration.ALIAS);
        altName.setName(new EmbeddableMultilingualString("Navn", "no"));

        AlternativeName altName2 = new AlternativeName();
        altName2.setNameType(NameTypeEnumeration.ALIAS);
        altName2.setName(new EmbeddableMultilingualString("Name", "en"));

        stopPlace.getAlternativeNames().add(altName);
        stopPlace.getAlternativeNames().add(altName2);

        Quay quay = new Quay();
        quay.setCompassBearing( Float.valueOf("90"));
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        quay.getAlternativeNames().add(altName);
        quay.getAlternativeNames().add(altName2);

        stopPlace.getQuays().add(quay);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String name = "Testing name";
        String netexId = stopPlace.getNetexId();

        //Verify that placeEquipments have been set
        String graphQlStopPlaceQuery = """
                {
                stopPlace(id: "%s") {
                    id
                    alternativeNames {
                        nameType
                        name {
                            value
                            lang
                        }
                    }
                    ... on StopPlace {
                        quays {
                            id
                            alternativeNames {
                                nameType
                                name {
                                    value
                                    lang
                                } 
                              }
                            }
                          }
                        }
                    }
                """.formatted(netexId);

        executeGraphqQLQueryOnly(graphQlStopPlaceQuery)
                .rootPath("data.stopPlace[0]")
                    .body("id", comparesEqualTo(netexId))
                    .body("alternativeNames", notNullValue())
                    .body("alternativeNames[0].nameType", notNullValue())
                    .body("alternativeNames[0].name.value", notNullValue())
                    .body("alternativeNames[0].name.lang", notNullValue())
                    .body("alternativeNames[1].nameType", notNullValue())
                    .body("alternativeNames[1].name.value", notNullValue())
                    .body("alternativeNames[1].name.lang", notNullValue())
                .rootPath("data.stopPlace[0].quays[0]")
                    .body("id", comparesEqualTo(quay.getNetexId()))
                    .body("alternativeNames", notNullValue())
                    .body("alternativeNames[0].nameType", notNullValue())
                    .body("alternativeNames[0].name.value", notNullValue())
                    .body("alternativeNames[0].name.lang", notNullValue())
                    .body("alternativeNames[1].nameType", notNullValue())
                    .body("alternativeNames[1].name.value", notNullValue())
                    .body("alternativeNames[1].name.lang", notNullValue());

    }
    @Test
    public void testSimpleMutateAlternativeNames() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        AlternativeName altName = new AlternativeName();
        altName.setNameType(NameTypeEnumeration.ALIAS);
        altName.setName(new EmbeddableMultilingualString("Navn", "no"));

        stopPlace.getAlternativeNames().add(altName);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String netexId = stopPlace.getNetexId();

        String updatedAlternativeNameValue = "UPDATED ALIAS";
        String updatedAlternativeNameLang = "no";

        String graphQlStopPlaceQuery = """
                mutation {
                stopPlace: mutateStopPlace (StopPlace: {
                        id: "%s"
                        alternativeNames: [
                            {
                                nameType: %s
                                name: {
                                    value: "%s"
                                    lang: "%s"
                                }
                            }
                        ]
                    }) {
                        id
                        alternativeNames {
                            nameType
                            name {
                                value
                                lang
                            }
                        }
                    }
                }
                """.formatted(netexId,altName.getNameType().value(), updatedAlternativeNameValue, updatedAlternativeNameLang);

        executeGraphqQLQueryOnly(graphQlStopPlaceQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(netexId))
                .body("data.stopPlace[0].alternativeNames", notNullValue())
                .rootPath("data.stopPlace[0].alternativeNames[0]")
//                .body("nameType", equalTo(altName.getNameType())) //RestAssured apparently does not like comparing response with enums...
                .body("name.value", comparesEqualTo(updatedAlternativeNameValue))
                .body("name.lang", comparesEqualTo(updatedAlternativeNameLang));

    }


    @Test
    public void testSimpleMutatePlaceEquipmentSignPrivateCode() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String netexId = stopPlace.getNetexId();

        String type = "StopPoint";
        String value = "512";
        String graphQlStopPlaceQuery = """
                mutation {
                stopPlace: mutateStopPlace (StopPlace: {
                        id: "%s"
                        placeEquipments: {
                            generalSign:  [{
                                signContentType: TransportModePoint
                                privateCode: {
                                    value: "%s"
                                    type: "%s"
                                }
                            }]
                        }
                    }) {
                        id
                        placeEquipments {
                            generalSign {
                                privateCode {
                                    value
                                    type
                                }
                            }
                        }
                    }
                }
                """.formatted(netexId, value, type);

        executeGraphqQLQueryOnly(graphQlStopPlaceQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(netexId))
                .body("data.stopPlace[0].placeEquipments", notNullValue())
                .rootPath("data.stopPlace[0].placeEquipments")
//                .body("nameType", equalTo(altName.getNameType())) //RestAssured apparently does not like comparing response with enums...
                .body("generalSign[0]", notNullValue())
                .body("generalSign[0].privateCode.type", comparesEqualTo(type))
                    .body("generalSign[0].privateCode.value", comparesEqualTo(value));

    }

    @Test
    public void testMutatePlaceEquipmentSignHSLValues() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String netexId = stopPlace.getNetexId();
        Integer numberOfFrames = 12;
        Boolean lineSignage = true;
        Boolean mainLineSign = true;
        Boolean replacesRailSign = false;
        String lang = "fin";
        String value = "Deviation from signage guide";

        String graphQlStopPlaceQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "      id:\\\"" + netexId + "\\\"" +
                "      placeEquipments: {" +
                "        generalSign:  [{" +
                "          numberOfFrames: " + numberOfFrames + "," +
                "          lineSignage: " + lineSignage + "," +
                "          mainLineSign: " + mainLineSign + "," +
                "          replacesRailSign: " + replacesRailSign + "," +
                "          note: {" +
                "            value: \\\"" + value + "\\\"" +
                "            lang:\\\"" + lang + "\\\"" +
                "          }" +
                "        }]" +
                "      }" +
                "    }) " +
                "    {" +
                "      id" +
                "      placeEquipments {" +
                "        generalSign {" +
                "          numberOfFrames, " +
                "          lineSignage, " +
                "          mainLineSign, " +
                "          replacesRailSign, " +
                "          note { value, lang } " +
                "        }" +
                "      }" +
                "    }" +
                "  }" +
                "\",\"variables\":\"\"}";

        executeGraphQL(graphQlStopPlaceQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(netexId))
                .body("data.stopPlace[0].placeEquipments", notNullValue())
                .rootPath("data.stopPlace[0].placeEquipments")
                .body("generalSign[0]", notNullValue())
                .body("generalSign[0].numberOfFrames", comparesEqualTo(numberOfFrames))
                .body("generalSign[0].lineSignage", is(lineSignage))
                .body("generalSign[0].mainLineSign", is(mainLineSign))
                .body("generalSign[0].replacesRailSign", is(replacesRailSign))
                .body("generalSign[0].note.lang", comparesEqualTo(lang))
                .body("generalSign[0].note.value", comparesEqualTo(value));
    }

    @Test
    public void testCreateStopWithTopographicPlaceHSL() {
        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("countyforinstance", "fin"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlaceRepository.save(topographicPlace);

        String stopName = "Stop one";

        String graphQlJsonQuery = """
            mutation {
            stopPlace: mutateStopPlace(StopPlace: {
                        name: { value: "%s" }
                        topographicPlace: {
                          id: "%s"
                        }
                }) {
                    id
                    name { value }
                    topographicPlace { id topographicPlaceType name { lang value } }
                }
            }""".formatted(stopName,
                topographicPlace.getNetexId()
        );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                .body("name.value", equalTo(stopName))
                .body("topographicPlace.id", equalTo(topographicPlace.getNetexId()))
                .body("topographicPlace.topographicPlaceType", equalTo(TopographicPlaceTypeEnumeration.MUNICIPALITY.value()))
                .body("topographicPlace.name", notNullValue())
                .body("topographicPlace.name.lang", equalTo(topographicPlace.getName().getLang()))
                .body("topographicPlace.name.value", equalTo(topographicPlace.getName().getValue()));
    }

    @Test
    public void testCreateStopWithoutTopographicPlaceHSL() {
        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("countyforinstance", "fin"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlaceRepository.save(topographicPlace);

        String stopName = "Stop one";

        String graphQlJsonQuery = """
            mutation {
            stopPlace: mutateStopPlace(StopPlace: {
                        name: { value: "%s" }
                }) {
                    id
                    name { value }
                    topographicPlace { id topographicPlaceType name { lang value } }
                }
            }""".formatted(stopName
        );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                .body("name.value", equalTo(stopName))
                .body("topographicPlace", nullValue());
    }

    @Test
    public void testMutateStopChangeTopographicPlaceHSL() {
        TopographicPlace topographicPlaceOrigin = new TopographicPlace(new EmbeddableMultilingualString("original location", "fin"));
        topographicPlaceOrigin.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlaceRepository.save(topographicPlaceOrigin);

        TopographicPlace topographicPlaceTarget = new TopographicPlace(new EmbeddableMultilingualString("target location", "swe"));
        topographicPlaceTarget.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlaceRepository.save(topographicPlaceTarget);

        StopPlace stopPlace = createStopPlace("Espa");
        stopPlace.setTopographicPlace(topographicPlaceOrigin);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String graphQlJsonQuery = """
            mutation {
            stopPlace: mutateStopPlace(StopPlace: {
                        id: "%s"
                        topographicPlace: {
                          id: "%s"
                        }
                }) {
                    id
                    name { value }
                    topographicPlace { id topographicPlaceType name { lang value } }
                }
            }""".formatted(stopPlace.getNetexId(),
                topographicPlaceTarget.getNetexId()
        );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()))
                .body("name.value", equalTo(stopPlace.getName().getValue()))
                .body("topographicPlace.id", equalTo(topographicPlaceTarget.getNetexId()))
                .body("topographicPlace.topographicPlaceType", equalTo(TopographicPlaceTypeEnumeration.MUNICIPALITY.value()))
                .body("topographicPlace.name", notNullValue())
                .body("topographicPlace.name.lang", equalTo(topographicPlaceTarget.getName().getLang()))
                .body("topographicPlace.name.value", equalTo(topographicPlaceTarget.getName().getValue()));
    }

    @Test
    public void testQueryHslAccessibilityPropertiesOfStop() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("quay"));
        stopPlace.getQuays().add(quay);
        AccessibilityAssessment accessibilityAssessment = createAccessibilityAssessment();
        HslAccessibilityProperties hslAccessibilityProperties = createHslAccessibilityProperties();
        accessibilityAssessment.setHslAccessibilityProperties(hslAccessibilityProperties);
        stopPlace.setAccessibilityAssessment(accessibilityAssessment);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String graphQlJsonQuery = """
            {
              stopPlace:  stopPlace (query:"%s", allVersions:true) {
                id
                accessibilityAssessment {
                  id
                  hslAccessibilityProperties {
                    id
                    version
                    stopAreaSideSlope
                    stopAreaLengthwiseSlope
                    endRampSlope
                    shelterLaneDistance
                    curbBackOfRailDistance
                    curbDriveSideOfRailDistance
                    structureLaneDistance
                    stopElevationFromRailTop
                    stopElevationFromSidewalk
                    lowerCleatHeight
                    serviceAreaWidth
                    serviceAreaLength
                    platformEdgeWarningArea
                    guidanceTiles
                    guidanceStripe
                    serviceAreaStripes
                    sidewalkAccessibleConnection
                    stopAreaSurroundingsAccessible
                    curvedStop
                    stopType
                    shelterType
                    guidanceType
                    mapType
                    pedestrianCrossingRampType
                    accessibilityLevel
                  }
                }
              }
            }""".formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
            .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
            .rootPath("data.stopPlace[0].accessibilityAssessment.hslAccessibilityProperties")
            .body(notNullValue())
            .body("id", equalTo(stopPlace.getAccessibilityAssessment().getHslAccessibilityProperties().getNetexId()))
            .body("version", equalTo("1"))
            // Note: RestAssured parses numbers as Float by default, even if they should be doubles here. Does not matter though since float range is sufficient.
            .body("stopAreaSideSlope", equalTo(hslAccessibilityProperties.getStopAreaSideSlope().floatValue()))
            .body("stopAreaLengthwiseSlope", equalTo(hslAccessibilityProperties.getStopAreaLengthwiseSlope().floatValue()))
            .body("endRampSlope", equalTo(hslAccessibilityProperties.getEndRampSlope().floatValue()))
            .body("shelterLaneDistance", equalTo(hslAccessibilityProperties.getShelterLaneDistance().floatValue()))
            .body("curbBackOfRailDistance", equalTo(hslAccessibilityProperties.getCurbBackOfRailDistance().floatValue()))
            .body("curbDriveSideOfRailDistance", equalTo(hslAccessibilityProperties.getCurbDriveSideOfRailDistance().floatValue()))
            .body("structureLaneDistance", equalTo(hslAccessibilityProperties.getStructureLaneDistance().floatValue()))
            .body("stopElevationFromRailTop", equalTo(hslAccessibilityProperties.getStopElevationFromRailTop().floatValue()))
            .body("stopElevationFromSidewalk", equalTo(hslAccessibilityProperties.getStopElevationFromSidewalk().floatValue()))
            .body("lowerCleatHeight", equalTo(hslAccessibilityProperties.getLowerCleatHeight().floatValue()))
            .body("serviceAreaWidth", equalTo(hslAccessibilityProperties.getServiceAreaWidth().floatValue()))
            .body("serviceAreaLength", equalTo(hslAccessibilityProperties.getServiceAreaLength().floatValue()))
            .body("platformEdgeWarningArea", equalTo(hslAccessibilityProperties.isPlatformEdgeWarningArea()))
            .body("guidanceTiles", equalTo(hslAccessibilityProperties.isGuidanceTiles()))
            .body("guidanceStripe", equalTo(hslAccessibilityProperties.isGuidanceStripe()))
            .body("serviceAreaStripes", equalTo(hslAccessibilityProperties.isServiceAreaStripes()))
            .body("sidewalkAccessibleConnection", equalTo(hslAccessibilityProperties.isSidewalkAccessibleConnection()))
            .body("stopAreaSurroundingsAccessible", equalTo(hslAccessibilityProperties.isStopAreaSurroundingsAccessible()))
            .body("curvedStop", equalTo(hslAccessibilityProperties.isCurvedStop()))
            .body("stopType", equalTo(hslAccessibilityProperties.getStopType().value()))
            .body("shelterType", equalTo(hslAccessibilityProperties.getShelterType().value()))
            .body("guidanceType", equalTo(hslAccessibilityProperties.getGuidanceType().value()))
            .body("mapType", equalTo(hslAccessibilityProperties.getMapType().value()))
            .body("pedestrianCrossingRampType", equalTo(hslAccessibilityProperties.getPedestrianCrossingRampType().value()))
            .body("accessibilityLevel", equalTo(hslAccessibilityProperties.getAccessibilityLevel().value()));
    }

    @Test
    public void testInsertHslAccessibilityPropertiesForStop() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("quay"));
        stopPlace.getQuays().add(quay);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s",
                # Could insert under quays, but because there's only 1 it gets optimized and moved under stopPlace.
                accessibilityAssessment: {
                  id: "%s"
                  # Required, but irrelevant for this test.
                  limitations: { wheelchairAccess: FALSE, stepFreeAccess: FALSE, escalatorFreeAccess: FALSE, liftFreeAccess: FALSE, audibleSignalsAvailable: FALSE }
                  hslAccessibilityProperties: {
                    stopAreaSideSlope: 1
                    stopAreaLengthwiseSlope: -2
                    endRampSlope: 333.33
                    shelterLaneDistance: 44
                    curbBackOfRailDistance: 55555.55
                    curbDriveSideOfRailDistance: 1.5
                    structureLaneDistance: 2.25
                    stopElevationFromRailTop: 33.55
                    stopElevationFromSidewalk: 4.5
                    lowerCleatHeight: 5.5
                    serviceAreaWidth: 6.5
                    serviceAreaLength: 7.5
                    platformEdgeWarningArea: true
                    guidanceTiles: true
                    guidanceStripe: true
                    serviceAreaStripes: true
                    sidewalkAccessibleConnection: true
                    stopAreaSurroundingsAccessible: true
                    curvedStop: true
                    stopType: pullOut
                    shelterType: wide
                    guidanceType: braille
                    mapType: tactile
                    pedestrianCrossingRampType: RK4_LR
                    accessibilityLevel: partiallyInaccessible
                  }
                }
              }) {
                id
                accessibilityAssessment {
                  id
                  hslAccessibilityProperties {
                    id
                    version
                    stopAreaSideSlope
                    stopAreaLengthwiseSlope
                    endRampSlope
                    shelterLaneDistance
                    curbBackOfRailDistance
                    curbDriveSideOfRailDistance
                    structureLaneDistance
                    stopElevationFromRailTop
                    stopElevationFromSidewalk
                    lowerCleatHeight
                    serviceAreaWidth
                    serviceAreaLength
                    platformEdgeWarningArea
                    guidanceTiles
                    guidanceStripe
                    serviceAreaStripes
                    sidewalkAccessibleConnection
                    stopAreaSurroundingsAccessible
                    curvedStop
                    stopType
                    shelterType
                    guidanceType
                    mapType
                    pedestrianCrossingRampType
                    accessibilityLevel
                  }
                }
              }
            }
            """.formatted(
                stopPlace.getNetexId(),
                stopPlace.getAccessibilityAssessment().getNetexId()
            );

        executeGraphqQLQueryOnly(graphqlQuery)
            .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
            .rootPath("data.stopPlace[0].accessibilityAssessment.hslAccessibilityProperties")
            .body(notNullValue())
            .body("id", notNullValue())
            .body("version", equalTo("1"))
            // Note: RestAssured parses numbers as Float by default, even if they should be doubles here. Does not matter though since float range is sufficient.
            .body("stopAreaSideSlope", equalTo(1.f))
            .body("stopAreaLengthwiseSlope", equalTo(-2.f))
            .body("endRampSlope", equalTo(333.33f))
            .body("shelterLaneDistance", equalTo(44.f))
            .body("curbBackOfRailDistance", equalTo(55555.55f))
            .body("curbDriveSideOfRailDistance", equalTo(1.5f))
            .body("structureLaneDistance", equalTo(2.25f))
            .body("stopElevationFromRailTop", equalTo(33.55f))
            .body("stopElevationFromSidewalk", equalTo(4.5f))
            .body("lowerCleatHeight", equalTo(5.5f))
            .body("serviceAreaWidth", equalTo(6.5f))
            .body("serviceAreaLength", equalTo(7.5f))
            .body("platformEdgeWarningArea", equalTo(true))
            .body("guidanceTiles", equalTo(true))
            .body("guidanceStripe", equalTo(true))
            .body("serviceAreaStripes", equalTo(true))
            .body("sidewalkAccessibleConnection", equalTo(true))
            .body("stopAreaSurroundingsAccessible", equalTo(true))
            .body("curvedStop", equalTo(true))
            .body("stopType", equalTo(HslStopTypeEnumeration.PULL_OUT.value()))
            .body("shelterType", equalTo(ShelterWidthTypeEnumeration.WIDE.value()))
            .body("guidanceType", equalTo(GuidanceTypeEnumeration.BRAILLE.value()))
            .body("mapType", equalTo(MapTypeEnumeration.TACTILE.value()))
            .body("pedestrianCrossingRampType", equalTo(PedestrianCrossingRampTypeEnumeration.RK4_LR.value()))
            .body("accessibilityLevel", equalTo(AccessibilityLevelEnumeration.PARTIALLY_INACCESSIBLE.value()));
    }

    @Test
    public void testMutateHslAccessibilityPropertiesOfStop() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("quay"));
        stopPlace.getQuays().add(quay);
        AccessibilityAssessment accessibilityAssessment = createAccessibilityAssessment();
        HslAccessibilityProperties hslAccessibilityProperties = createHslAccessibilityProperties();
        accessibilityAssessment.setHslAccessibilityProperties(hslAccessibilityProperties);
        stopPlace.setAccessibilityAssessment(accessibilityAssessment);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s",
                accessibilityAssessment: {
                  id: "%s"
                  # Required, but irrelevant for this test.
                  limitations: { wheelchairAccess: FALSE, stepFreeAccess: FALSE, escalatorFreeAccess: FALSE, liftFreeAccess: FALSE, audibleSignalsAvailable: FALSE }
                  hslAccessibilityProperties: {
                    id: "%s"
                    stopAreaSideSlope: 1
                    stopAreaLengthwiseSlope: -2
                    endRampSlope: 333.33
                    shelterLaneDistance: 44
                    curbBackOfRailDistance: 55555.55
                    curbDriveSideOfRailDistance: 1.5
                    structureLaneDistance: 2.25
                    stopElevationFromRailTop: 33.55
                    stopElevationFromSidewalk: 4.5
                    lowerCleatHeight: 5.5
                    serviceAreaWidth: 6.5
                    serviceAreaLength: 7.5
                    platformEdgeWarningArea: true
                    guidanceTiles: true
                    guidanceStripe: true
                    serviceAreaStripes: true
                    sidewalkAccessibleConnection: true
                    stopAreaSurroundingsAccessible: true
                    curvedStop: true
                    stopType: busBulb
                    shelterType: narrow
                    guidanceType: none
                    mapType: other
                    pedestrianCrossingRampType: LR
                    accessibilityLevel: fullyAccessible
                  }
                }
              }) {
                id
                accessibilityAssessment {
                  id
                  hslAccessibilityProperties {
                    id
                    version
                    stopAreaSideSlope
                    stopAreaLengthwiseSlope
                    endRampSlope
                    shelterLaneDistance
                    curbBackOfRailDistance
                    curbDriveSideOfRailDistance
                    structureLaneDistance
                    stopElevationFromRailTop
                    stopElevationFromSidewalk
                    lowerCleatHeight
                    serviceAreaWidth
                    serviceAreaLength
                    platformEdgeWarningArea
                    guidanceTiles
                    guidanceStripe
                    serviceAreaStripes
                    sidewalkAccessibleConnection
                    stopAreaSurroundingsAccessible
                    curvedStop
                    stopType
                    shelterType
                    guidanceType
                    mapType
                    pedestrianCrossingRampType
                    accessibilityLevel
                  }
                }
              }
            }
            """.formatted(
                stopPlace.getNetexId(),
                stopPlace.getAccessibilityAssessment().getNetexId(),
                stopPlace.getAccessibilityAssessment().getHslAccessibilityProperties().getNetexId()
            );

        executeGraphqQLQueryOnly(graphqlQuery)
            .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
            .rootPath("data.stopPlace[0].accessibilityAssessment.hslAccessibilityProperties")
            .body(notNullValue())
            .body("id", notNullValue())
            // Note: versioning is kind of broken.
            // When mutating an existing entity, version is not incremented but a new row is created instead.
            // This behavior is similar to accessibilityLimitations however, so keeping it like this for now.
            .body("id", not(equalTo(stopPlace.getAccessibilityAssessment().getHslAccessibilityProperties().getNetexId())))
            .body("version", equalTo("1"))
            // Note: RestAssured parses numbers as Float by default, even if they should be doubles here. Does not matter though since float range is sufficient.
            .body("stopAreaSideSlope", equalTo(1.f))
            .body("stopAreaLengthwiseSlope", equalTo(-2.f))
            .body("endRampSlope", equalTo(333.33f))
            .body("shelterLaneDistance", equalTo(44.f))
            .body("curbBackOfRailDistance", equalTo(55555.55f))
            .body("curbDriveSideOfRailDistance", equalTo(1.5f))
            .body("structureLaneDistance", equalTo(2.25f))
            .body("stopElevationFromRailTop", equalTo(33.55f))
            .body("stopElevationFromSidewalk", equalTo(4.5f))
            .body("lowerCleatHeight", equalTo(5.5f))
            .body("serviceAreaWidth", equalTo(6.5f))
            .body("serviceAreaLength", equalTo(7.5f))
            .body("platformEdgeWarningArea", equalTo(true))
            .body("guidanceTiles", equalTo(true))
            .body("guidanceStripe", equalTo(true))
            .body("serviceAreaStripes", equalTo(true))
            .body("sidewalkAccessibleConnection", equalTo(true))
            .body("stopAreaSurroundingsAccessible", equalTo(true))
            .body("curvedStop", equalTo(true))
            .body("stopType", equalTo(HslStopTypeEnumeration.BUS_BULB.value()))
            .body("shelterType", equalTo(ShelterWidthTypeEnumeration.NARROW.value()))
            .body("guidanceType", equalTo(GuidanceTypeEnumeration.NONE.value()))
            .body("mapType", equalTo(MapTypeEnumeration.OTHER.value()))
            .body("pedestrianCrossingRampType", equalTo(PedestrianCrossingRampTypeEnumeration.LR.value()))
            .body("accessibilityLevel", equalTo(AccessibilityLevelEnumeration.FULLY_ACCESSIBLE.value()));
    }

    @Test
    public void testMutateHslAccessibilityPropertiesOfStopClearFields() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("quay"));
        stopPlace.getQuays().add(quay);
        AccessibilityAssessment accessibilityAssessment = createAccessibilityAssessment();
        HslAccessibilityProperties hslAccessibilityProperties = createHslAccessibilityProperties();
        accessibilityAssessment.setHslAccessibilityProperties(hslAccessibilityProperties);
        stopPlace.setAccessibilityAssessment(accessibilityAssessment);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s",
                accessibilityAssessment: {
                  id: "%s"
                  # Required, but irrelevant for this test.
                  limitations: { wheelchairAccess: FALSE, stepFreeAccess: FALSE, escalatorFreeAccess: FALSE, liftFreeAccess: FALSE, audibleSignalsAvailable: FALSE }
                  hslAccessibilityProperties: {
                    id: "%s"
                    stopAreaSideSlope: null
                    stopAreaLengthwiseSlope: null
                    endRampSlope: null
                    shelterLaneDistance: null
                    curbBackOfRailDistance: null
                    curbDriveSideOfRailDistance: null
                    structureLaneDistance: null
                    stopElevationFromRailTop: null
                    stopElevationFromSidewalk: null
                    lowerCleatHeight: null
                    serviceAreaWidth: null
                    serviceAreaLength: null
                    platformEdgeWarningArea: null
                    guidanceTiles: null
                    guidanceStripe: null
                    serviceAreaStripes: null
                    sidewalkAccessibleConnection: null
                    stopAreaSurroundingsAccessible: null
                    curvedStop: null
                    stopType: null
                    shelterType: null
                    guidanceType: null
                    mapType: null
                    pedestrianCrossingRampType: null
                    accessibilityLevel: unknown
                  }
                }
              }) {
                id
                accessibilityAssessment {
                  id
                  hslAccessibilityProperties {
                    id
                    version
                    stopAreaSideSlope
                    stopAreaLengthwiseSlope
                    endRampSlope
                    shelterLaneDistance
                    curbBackOfRailDistance
                    curbDriveSideOfRailDistance
                    structureLaneDistance
                    stopElevationFromRailTop
                    stopElevationFromSidewalk
                    lowerCleatHeight
                    serviceAreaWidth
                    serviceAreaLength
                    platformEdgeWarningArea
                    guidanceTiles
                    guidanceStripe
                    serviceAreaStripes
                    sidewalkAccessibleConnection
                    stopAreaSurroundingsAccessible
                    curvedStop
                    stopType
                    shelterType
                    guidanceType
                    mapType
                    pedestrianCrossingRampType
                    accessibilityLevel
                  }
                }
              }
            }
            """.formatted(
                stopPlace.getNetexId(),
                stopPlace.getAccessibilityAssessment().getNetexId(),
                stopPlace.getAccessibilityAssessment().getHslAccessibilityProperties().getNetexId()
            );

        executeGraphqQLQueryOnly(graphqlQuery)
            .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
            .rootPath("data.stopPlace[0].accessibilityAssessment.hslAccessibilityProperties")
            .body(notNullValue())
            .body("id", notNullValue())
            // Note: versioning is kind of broken.
            // When mutating an existing entity, version is not incremented but a new row is created instead.
            // This behavior is similar to accessibilityLimitations however, so keeping it like this for now.
            .body("id", not(equalTo(stopPlace.getAccessibilityAssessment().getHslAccessibilityProperties().getNetexId())))
            .body("version", equalTo("1"))
            .body("stopAreaSideSlope", equalTo(null))
            .body("stopAreaLengthwiseSlope", equalTo(null))
            .body("endRampSlope", equalTo(null))
            .body("shelterLaneDistance", equalTo(null))
            .body("curbBackOfRailDistance", equalTo(null))
            .body("curbDriveSideOfRailDistance", equalTo(null))
            .body("structureLaneDistance", equalTo(null))
            .body("stopElevationFromRailTop", equalTo(null))
            .body("stopElevationFromSidewalk", equalTo(null))
            .body("lowerCleatHeight", equalTo(null))
            .body("serviceAreaWidth", equalTo(null))
            .body("serviceAreaLength", equalTo(null))
            .body("platformEdgeWarningArea", equalTo(null))
            .body("guidanceTiles", equalTo(null))
            .body("guidanceStripe", equalTo(null))
            .body("serviceAreaStripes", equalTo(null))
            .body("sidewalkAccessibleConnection", equalTo(null))
            .body("stopAreaSurroundingsAccessible", equalTo(null))
            .body("curvedStop", equalTo(null))
            .body("stopType", equalTo(null))
            .body("shelterType", equalTo(null))
            .body("guidanceType", equalTo(null))
            .body("mapType", equalTo(null))
            .body("pedestrianCrossingRampType", equalTo(null))
            .body("accessibilityLevel", equalTo(AccessibilityLevelEnumeration.UNKNOWN.value())); // Not nullable
    }

    @Test
    public void testQueryStopPlaceEquipmentShelterHSL() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        PlaceEquipment placeEquipment = new PlaceEquipment();
        ShelterEquipment shelterEquipment = createShelterEquipmentHSl();
        placeEquipment.getInstalledEquipment().add(shelterEquipment);
        stopPlace.setPlaceEquipments(placeEquipment);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String graphQlJsonQuery = """
            {
              stopPlace: stopPlace (query:"%s", allVersions:true) {
                id
                placeEquipments {
                  shelterEquipment {
                    enclosed
                    shelterType
                    shelterElectricity
                    shelterLighting
                    shelterCondition
                    timetableCabinets
                    trashCan
                    shelterHasDisplay
                    bicycleParking
                    leaningRail
                    outsideBench
                    shelterFasciaBoardTaping
                  }
                }
              }
            }""".formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .rootPath("data.stopPlace[0].placeEquipments.shelterEquipment[0]")
                .body(notNullValue())
                .body("enclosed", equalTo(shelterEquipment.isEnclosed()))
                .body("shelterType", equalTo(shelterEquipment.getShelterType().value()))
                .body("shelterElectricity", equalTo(shelterEquipment.getShelterElectricity().value()))
                .body("shelterLighting", equalTo(shelterEquipment.isShelterLighting()))
                .body("shelterCondition", equalTo(shelterEquipment.getShelterCondition().value()))
                .body("timetableCabinets", equalTo(shelterEquipment.getTimetableCabinets()))
                .body("trashCan", equalTo(shelterEquipment.isTrashCan()))
                .body("shelterHasDisplay", equalTo(shelterEquipment.isShelterHasDisplay()))
                .body("bicycleParking", equalTo(shelterEquipment.isBicycleParking()))
                .body("leaningRail", equalTo(shelterEquipment.isLeaningRail()))
                .body("outsideBench", equalTo(shelterEquipment.isOutsideBench()))
                .body("shelterFasciaBoardTaping", equalTo(shelterEquipment.isShelterFasciaBoardTaping()));
    }

    @Test
    public void testInsertStopPlaceEquipmentShelterHSL() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s"
                placeEquipments: {
                  shelterEquipment: [{
                    enclosed: true
                    shelterType: steel
                    shelterElectricity: continuous
                    shelterLighting: true
                    shelterCondition: mediocre
                    timetableCabinets: 2
                    trashCan: true
                    shelterHasDisplay: false
                    bicycleParking: true
                    leaningRail: false
                    outsideBench: true
                    shelterFasciaBoardTaping: true
                  },
                  {
                    enclosed: false
                    shelterType: glass
                    shelterElectricity: light
                    shelterLighting: false
                    shelterCondition: bad
                    timetableCabinets: 1
                    trashCan: false
                    shelterHasDisplay: true
                    bicycleParking: false
                    leaningRail: true
                    outsideBench: false
                    shelterFasciaBoardTaping: false
                  }]
                }
              }) {
                id
                placeEquipments {
                  shelterEquipment {
                    enclosed
                    shelterType
                    shelterElectricity
                    shelterLighting
                    shelterCondition
                    timetableCabinets
                    trashCan
                    shelterHasDisplay
                    bicycleParking
                    leaningRail
                    outsideBench
                    shelterFasciaBoardTaping
                  }
                }
              }
            }
            """.formatted(
                stopPlace.getNetexId()
        );

        executeGraphqQLQueryOnly(graphqlQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .rootPath("data.stopPlace[0].placeEquipments.shelterEquipment[0]")
                .body(notNullValue())
                .body("enclosed", equalTo(true))
                .body("shelterType", equalTo(ShelterTypeEnumeration.STEEL.value()))
                .body("shelterElectricity", equalTo(ElectricityTypeEnumeration.CONTINUOUS.value()))
                .body("shelterLighting", equalTo(true))
                .body("shelterCondition", equalTo(ShelterConditionEnumeration.MEDIOCRE.value()))
                .body("timetableCabinets", equalTo(2))
                .body("trashCan", equalTo(true))
                .body("shelterHasDisplay", equalTo(false))
                .body("bicycleParking", equalTo(true))
                .body("leaningRail", equalTo(false))
                .body("outsideBench", equalTo(true))
                .body("shelterFasciaBoardTaping", equalTo(true))
                .rootPath("data.stopPlace[0].placeEquipments.shelterEquipment[1]")
                .body(notNullValue())
                .body("enclosed", equalTo(false))
                .body("shelterType", equalTo(ShelterTypeEnumeration.GLASS.value()))
                .body("shelterElectricity", equalTo(ElectricityTypeEnumeration.LIGHT.value()))
                .body("shelterLighting", equalTo(false))
                .body("shelterCondition", equalTo(ShelterConditionEnumeration.BAD.value()))
                .body("timetableCabinets", equalTo(1))
                .body("trashCan", equalTo(false))
                .body("shelterHasDisplay", equalTo(true))
                .body("bicycleParking", equalTo(false))
                .body("leaningRail", equalTo(true))
                .body("outsideBench", equalTo(false))
                .body("shelterFasciaBoardTaping", equalTo(false));
    }

    @Test
    public void testMutateStopPlaceEquipmentShelterHSL() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        PlaceEquipment placeEquipment = new PlaceEquipment();
        ShelterEquipment shelterEquipment = createShelterEquipmentHSl();
        placeEquipment.getInstalledEquipment().add(shelterEquipment);
        stopPlace.setPlaceEquipments(placeEquipment);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s"
                placeEquipments: {
                  shelterEquipment: [{
                    enclosed: true
                    shelterType: steel
                    shelterElectricity: continuous
                    shelterLighting: true
                    shelterCondition: mediocre
                    timetableCabinets: 2
                    trashCan: true
                    shelterHasDisplay: false
                    bicycleParking: true
                    leaningRail: false
                    outsideBench: true
                    shelterFasciaBoardTaping: true
                  }]
                }
              }) {
                id
                placeEquipments {
                  shelterEquipment {
                    enclosed
                    shelterType
                    shelterElectricity
                    shelterLighting
                    shelterCondition
                    timetableCabinets
                    trashCan
                    shelterHasDisplay
                    bicycleParking
                    leaningRail
                    outsideBench
                    shelterFasciaBoardTaping
                  }
                }
              }
            }
            """.formatted(
                stopPlace.getNetexId()
        );

        executeGraphqQLQueryOnly(graphqlQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .rootPath("data.stopPlace[0].placeEquipments.shelterEquipment[0]")
                .body(notNullValue())
                .body("enclosed", equalTo(true))
                .body("shelterType", equalTo(ShelterTypeEnumeration.STEEL.value()))
                .body("shelterElectricity", equalTo(ElectricityTypeEnumeration.CONTINUOUS.value()))
                .body("shelterLighting", equalTo(true))
                .body("shelterCondition", equalTo(ShelterConditionEnumeration.MEDIOCRE.value()))
                .body("timetableCabinets", equalTo(2))
                .body("trashCan", equalTo(true))
                .body("shelterHasDisplay", equalTo(false))
                .body("bicycleParking", equalTo(true))
                .body("leaningRail", equalTo(false))
                .body("outsideBench", equalTo(true))
                .body("shelterFasciaBoardTaping", equalTo(true));
    }

    @Test
    public void testMutateQuayEquipmentKeepsNetexId() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("stopPlaceWithEquipment"));
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("quayWithEquipment"));
        PlaceEquipment placeEquipment = new PlaceEquipment();
        ShelterEquipment shelterEquipment = new ShelterEquipment();
        shelterEquipment.setEnclosed(false);
        placeEquipment.getInstalledEquipment().add(shelterEquipment);
        quay.setPlaceEquipments(placeEquipment);
        stopPlace.setQuays(Set.of(quay));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var savedQuay = stopPlace.getQuays().stream().findAny().orElseThrow();
        var savedPlaceEquipment = savedQuay.getPlaceEquipments();
        var installedEquipment = savedPlaceEquipment.getInstalledEquipment().stream().findAny().orElseThrow();

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s"
                quays: {
                  id: "%s"
                  placeEquipments: {
                    shelterEquipment: [{
                      id: "%s"
                      enclosed: true
                    }]
                  }
                }
              }) {
                id
                quays {
                  id
                  placeEquipments {
                    shelterEquipment {
                      id
                      version
                      enclosed
                    }
                  }
                }
              }
            }
            """.formatted(
                stopPlace.getNetexId(),
                savedQuay.getNetexId(),
                installedEquipment.getNetexId()
        );

        executeGraphqQLQueryOnly(graphqlQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .rootPath("data.stopPlace[0].quays[0]")
                .body("id", equalTo(savedQuay.getNetexId()))
                .appendRootPath("placeEquipments.shelterEquipment[0]")
                .body(notNullValue())
                .body("id", equalTo(installedEquipment.getNetexId()))
                .body("version", equalTo(String.valueOf(installedEquipment.getVersion() + 1)))
                .body("enclosed", equalTo(true));
    }

    @Test
    public void testMutateQuayEquipmentReplacesShelterWhenNoNetexId() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("stopPlaceWithEquipment"));
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("quayWithEquipment"));
        PlaceEquipment placeEquipment = new PlaceEquipment();
        ShelterEquipment shelterEquipment = new ShelterEquipment();
        shelterEquipment.setEnclosed(false);
        placeEquipment.getInstalledEquipment().add(shelterEquipment);
        quay.setPlaceEquipments(placeEquipment);
        stopPlace.setQuays(Set.of(quay));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var savedQuay = stopPlace.getQuays().stream().findAny().orElseThrow();
        var savedPlaceEquipment = savedQuay.getPlaceEquipments();
        var installedEquipment = savedPlaceEquipment.getInstalledEquipment().stream().findAny().orElseThrow();

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s"
                quays: {
                  id: "%s"
                  placeEquipments: {
                    shelterEquipment: [{
                      enclosed: true
                    }]
                  }
                }
              }) {
                id
                quays {
                  id
                  placeEquipments {
                    shelterEquipment {
                      id
                      version
                      enclosed
                    }
                  }
                }
              }
            }
            """.formatted(
                stopPlace.getNetexId(),
                savedQuay.getNetexId(),
                installedEquipment.getNetexId()
        );

        executeGraphqQLQueryOnly(graphqlQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .rootPath("data.stopPlace[0].quays[0]")
                .body("id", equalTo(savedQuay.getNetexId()))
                .appendRootPath("placeEquipments.shelterEquipment[0]")
                .body(notNullValue())
                .body("id", not(equalTo(installedEquipment.getNetexId())))
                .body("version", equalTo("1"))
                .body("enclosed", equalTo(true));
    }

    @Test
    public void testMutateQuayEquipmentDoNotClearEquipmentWhenNotDefined() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("stopPlaceWithEquipment"));
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("quayWithEquipment"));
        PlaceEquipment placeEquipment = new PlaceEquipment();
        ShelterEquipment shelterEquipment = new ShelterEquipment();
        shelterEquipment.setEnclosed(false);
        placeEquipment.getInstalledEquipment().add(shelterEquipment);
        quay.setPlaceEquipments(placeEquipment);
        stopPlace.setQuays(Set.of(quay));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var savedQuay = stopPlace.getQuays().stream().findAny().orElseThrow();
        var savedPlaceEquipment = savedQuay.getPlaceEquipments();
        var installedEquipment = savedPlaceEquipment.getInstalledEquipment().stream().findAny().orElseThrow();

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s"
                quays: {
                  id: "%s"
                  placeEquipments: {
                    sanitaryEquipment: {
                      numberOfToilets: 5
                    }
                  }
                }
              }) {
                id
                quays {
                  id
                  placeEquipments {
                    shelterEquipment {
                      id
                      version
                      enclosed
                    }
                    sanitaryEquipment {
                      numberOfToilets
                    }
                  }
                }
              }
            }
            """.formatted(
                stopPlace.getNetexId(),
                savedQuay.getNetexId()
        );

        executeGraphqQLQueryOnly(graphqlQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .rootPath("data.stopPlace[0].quays[0]")
                .body("id", equalTo(savedQuay.getNetexId()))
                .appendRootPath("placeEquipments.shelterEquipment[0]")
                .body(notNullValue())
                .body("id", equalTo(installedEquipment.getNetexId()))
                .body("version", equalTo(String.valueOf(installedEquipment.getVersion() + 1)))
                .body("enclosed", equalTo(shelterEquipment.isEnclosed()));
    }

    @Test
    public void testMutateQuayEquipmentRemoveUndefined() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("stopPlaceWithEquipment"));
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("quayWithEquipment"));
        PlaceEquipment placeEquipment = new PlaceEquipment();
        ShelterEquipment shelterEquipment = new ShelterEquipment();
        shelterEquipment.setEnclosed(true);
        ShelterEquipment shelterEquipment2 = new ShelterEquipment();
        shelterEquipment2.setEnclosed(true);
        placeEquipment.getInstalledEquipment().add(shelterEquipment);
        placeEquipment.getInstalledEquipment().add(shelterEquipment2);
        quay.setPlaceEquipments(placeEquipment);
        stopPlace.setQuays(Set.of(quay));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var savedQuay = stopPlace.getQuays().stream().findAny().orElseThrow();
        var savedPlaceEquipment = savedQuay.getPlaceEquipments();
        var installedEquipment = savedPlaceEquipment.getInstalledEquipment();

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s"
                quays: {
                  id: "%s"
                  placeEquipments: {
                    shelterEquipment: [{
                      id: "%s"
                      enclosed: false
                    },
                    {
                      enclosed: true
                    }
                    ]
                  }
                }
              }) {
                id
                quays {
                  id
                  placeEquipments {
                    shelterEquipment {
                      id
                      version
                      enclosed
                    }
                  }
                }
              }
            }
            """.formatted(
                stopPlace.getNetexId(),
                savedQuay.getNetexId(),
                installedEquipment.getFirst().getNetexId()
        );

        executeGraphqQLQueryOnly(graphqlQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .rootPath("data.stopPlace[0].quays[0]")
                .body("id", equalTo(savedQuay.getNetexId()))
                .appendRootPath("placeEquipments")
                .body("shelterEquipment", hasSize(2))
                .appendRootPath("shelterEquipment[0]")
                .body(notNullValue())
                .body("id", equalTo(installedEquipment.getFirst().getNetexId()))
                .body("version", equalTo(String.valueOf(installedEquipment.getFirst().getVersion() + 1)))
                .body("enclosed", equalTo(false))
                .detachRootPath("shelterEquipment[0]")
                .appendRootPath("shelterEquipment[1]")
                .body("id", not(anyOf(equalTo(installedEquipment.getFirst().getNetexId()), equalTo(installedEquipment.getLast().getNetexId()))))
                .body("version", equalTo("1"))
                .body("enclosed", equalTo(true));
    }
    @Test
    public void testNullExistingStopPlaceEquipmentShelterHSL() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        PlaceEquipment placeEquipment = new PlaceEquipment();
        ShelterEquipment shelterEquipment = createShelterEquipmentHSl();
        placeEquipment.getInstalledEquipment().add(shelterEquipment);
        stopPlace.setPlaceEquipments(placeEquipment);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s"
                placeEquipments: {
                  shelterEquipment: [{
                    enclosed: null
                    shelterType: null
                    shelterElectricity: null
                    shelterLighting: null
                    shelterCondition: null
                    timetableCabinets: null
                    trashCan: null
                    shelterHasDisplay: null
                    bicycleParking: null
                    leaningRail: null
                    outsideBench: null
                    shelterFasciaBoardTaping: null
                  }]
                }
              }) {
                id
                placeEquipments {
                  shelterEquipment {
                    enclosed
                    shelterType
                    shelterElectricity
                    shelterLighting
                    shelterCondition
                    timetableCabinets
                    trashCan
                    shelterHasDisplay
                    bicycleParking
                    leaningRail
                    outsideBench
                    shelterFasciaBoardTaping
                  }
                }
              }
            }
            """.formatted(
                stopPlace.getNetexId()
        );

        executeGraphqQLQueryOnly(graphqlQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .rootPath("data.stopPlace[0].placeEquipments.shelterEquipment[0]")
                .body(notNullValue())
                .body("enclosed", equalTo(null))
                .body("shelterType", equalTo(null))
                .body("shelterElectricity", equalTo(null))
                .body("shelterLighting", equalTo(null))
                .body("shelterCondition", equalTo(null))
                .body("timetableCabinets", equalTo(null))
                .body("trashCan", equalTo(null))
                .body("shelterHasDisplay", equalTo(null))
                .body("bicycleParking", equalTo(null))
                .body("leaningRail", equalTo(null))
                .body("outsideBench", equalTo(null))
                .body("shelterFasciaBoardTaping", equalTo(null));
    }

    @Test
    public void testMutateStopPlaceOrganisation() {
        Organisation organisation1 = new Organisation();
        organisation1.setCompanyNumber("112233");
        organisation1.setName("Test Organisation");
        organisation1.setOrganisationType(OrganisationTypeEnumeration.OTHER);
        Contact privateContactDetails1 = new Contact();
        privateContactDetails1.setEmail("private@example.com");
        organisation1.setPrivateContactDetails(privateContactDetails1);
        organisationRepository.save(organisation1);

        Organisation organisation2 = new Organisation();
        organisation2.setCompanyNumber("56789");
        organisation2.setName("Another Organisation");
        organisation2.setOrganisationType(OrganisationTypeEnumeration.OTHER);
        Contact privateContactDetails2 = new Contact();
        privateContactDetails2.setEmail("nulle@example.com");
        organisation2.setPrivateContactDetails(privateContactDetails2);
        organisationRepository.save(organisation2);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        // Set one existing organisation for the stop place.
        stopPlace.getOrganisations().add(new StopPlaceOrganisationRef(
                organisation1,
                StopPlaceOrganisationRelationshipEnumeration.MAINTENANCE
        ));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        // Change relationship type of an existing organisation, and add a new one.
        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s"
                organisations: [
                  {
                      organisationRef: "%s",
                      relationshipType: infoUpkeep
                  },
                  {
                      organisationRef: "%s",
                      relationshipType: winterMaintenance
                  }
                ]
              }) {
                id
                organisations {
                  organisationRef
                  relationshipType
                  organisation {
                    id
                    name
                    privateContactDetails {
                      email
                    }
                  }
                }
              }
            }
            """.formatted(
                stopPlace.getNetexId(),
                organisation1.getNetexId(),
                organisation2.getNetexId()
        );

        executeGraphqQLQueryOnly(graphqlQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .rootPath("data.stopPlace[0].organisations")
                .body(notNullValue())
                .body("", hasSize(2))
                .body("[0].relationshipType", equalTo(StopPlaceOrganisationRelationshipEnumeration.INFO_UPKEEP.value()))
                .body("[0].organisation.id", equalTo(organisation1.getNetexId()))
                .body("[0].organisation.name", equalTo(organisation1.getName()))
                .body("[0].organisation.privateContactDetails.email", equalTo(organisation1.getPrivateContactDetails().getEmail()))
                .body("[1].relationshipType", equalTo(StopPlaceOrganisationRelationshipEnumeration.WINTER_MAINTENANCE.value()))
                .body("[1].organisation.id", equalTo(organisation2.getNetexId()))
                .body("[1].organisation.name", equalTo(organisation2.getName()))
                .body("[1].organisation.privateContactDetails.email", equalTo(organisation2.getPrivateContactDetails().getEmail()));
    }

    @Test
    public void testMutateStopPlaceEmptyOrganisations() {
        Organisation organisation1 = new Organisation();
        organisation1.setCompanyNumber("112233");
        organisation1.setName("Test Organisation");
        organisation1.setOrganisationType(OrganisationTypeEnumeration.OTHER);
        Contact privateContactDetails1 = new Contact();
        privateContactDetails1.setEmail("private@example.com");
        organisation1.setPrivateContactDetails(privateContactDetails1);
        organisationRepository.save(organisation1);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        // Set one existing organisation for the stop place.
        stopPlace.getOrganisations().add(new StopPlaceOrganisationRef(
                organisation1,
                StopPlaceOrganisationRelationshipEnumeration.MAINTENANCE
        ));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        // Mutate without organisations -> should remove an existing organisation relationship.
        var graphqlQuery = """
            mutation {
              stopPlace: mutateStopPlace(StopPlace: {
                id: "%s"
                organisations: []
              }) {
                id
                organisations {
                  organisationRef
                  relationshipType
                  organisation {
                    id
                    name
                    privateContactDetails {
                      email
                    }
                  }
                }
              }
            }
            """.formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(graphqlQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .rootPath("data.stopPlace[0].organisations")
                .body(notNullValue())
                .body("", hasSize(0));
    }

    @Test
    public void testMutateChildStopPlace() {

        var stop = new StopPlace();
        stop.setName(new EmbeddableMultilingualString("Stop Place"));
        stop.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59.05)));
        stop = stopPlaceVersionedSaverService.saveNewVersion(stop);

        String stationName = "Station";

        var graphQlJsonQuery = """
                mutation {
                stopPlace: createMultiModalStopPlace (input: {
                         stopPlaceIds:["%s"]
                         name: { value: "%s" }
                      }) {
                         id
                         name { value }
                         children {
                          id name { value }
                         }
                      }
                 }""".formatted(stop.getNetexId(), stationName);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", equalTo(stationName))
                .body("data.stopPlace.children[0].name.value", equalTo(stop.getName().getValue()));

        String newName = "new stop";

        var mutateChildQuery = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {
                    id: "%s"
                    name: { value: "%s" }
                  }) {
                    id
                    name { value }
                  }
                }
                """.formatted(
                stop.getNetexId(),
                newName
        );

        executeGraphqQLQueryOnly(mutateChildQuery)
                .body("data.stopPlace[0].id", equalTo(stop.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(newName));
    }

    @Test
    public void testMultimodalStopKeepsEquipmentNetexIds() {

        var stop = new StopPlace();
        stop.setName(new EmbeddableMultilingualString("Stop Place"));
        stop.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59.05)));
        var quay = new Quay();
        quay.setPublicCode("Q1234");
        var placeEquipment = new PlaceEquipment();
        var shelter = new ShelterEquipment();
        shelter.setEnclosed(true);
        placeEquipment.getInstalledEquipment().add(shelter);
        quay.setPlaceEquipments(placeEquipment);
        stop.setQuays(Set.of(quay));
        stop = stopPlaceVersionedSaverService.saveNewVersion(stop);

        String stationName = "Station";

        var graphQlJsonQuery = """
                mutation {
                stopPlace: createMultiModalStopPlace (input: {
                         stopPlaceIds:["%s"]
                         name: { value: "%s" }
                      }) {
                         id
                         name { value }
                         children {
                           id
                           name { value }
                           quays {
                             id
                             placeEquipments {
                               shelterEquipment {
                                 id
                                 enclosed
                               }
                             }
                           }
                         }
                      }
                 }""".formatted(stop.getNetexId(), stationName);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace")
                .body("name.value", equalTo(stationName))
                .body("children", hasSize(1))
                .appendRootPath("children[0]")
                .body("name.value", equalTo(stop.getName().getValue()))
                .body("quays", hasSize(1))
                .appendRootPath("quays[0]")
                .body("id", equalTo(quay.getNetexId()))
                .appendRootPath("placeEquipments")
                .body("shelterEquipment", hasSize(1))
                .appendRootPath("shelterEquipment[0]")
                .body("id", equalTo(shelter.getNetexId()))
                .body("enclosed", equalTo(shelter.isEnclosed()));
    }

    @Ignore("Needs to have property set in order to skip clearing child stop names.")
    @Test
    public void testSameNameForParentAndChild() {
        String name = "testname";

        var stop = new StopPlace();
        stop.setName(new EmbeddableMultilingualString(name));
        stop.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59.05)));
        stop = stopPlaceVersionedSaverService.saveNewVersion(stop);

        var graphQlJsonQuery = """
                mutation {
                stopPlace: createMultiModalStopPlace (input: {
                         stopPlaceIds:["%s"]
                         name: { value: "%s" }
                      }) {
                         id
                         name { value }
                         children {
                          id name { value }
                         }
                      }
                 }""".formatted(stop.getNetexId(), name);

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", equalTo(name))
                .body("data.stopPlace.children[0].name.value", equalTo(name));

        var fetchChildNameQuery = """
                {
                stopPlace:  stopPlace (id:"%s", onlyMonomodalStopPlaces:true) {
                          id
                          name { value }
                      }
                  }
                """.formatted(stop.getNetexId());

        executeGraphqQLQueryOnly(fetchChildNameQuery)
                .body("data.stopPlace[0].name.value", equalTo(name));
    }

    @Test
    public void forbidOverlappingValidityPeriodsWithSameName() {

        String startDate = "1990-01-01";
        String endDate = "1991-01-01";
        String newStartDate = "1990-06-01";
        String newEndDate = "1991-06-01";

        String stopPlaceName = "SameName";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace.getKeyValues().put("validityStart", new Value(startDate));
        stopPlace.getKeyValues().put("validityEnd", new Value(endDate));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                mutation {
                stopPlace: mutateStopPlace(StopPlace: {
                          name: { value:"%s" }
                          keyValues: [
                            { key:"validityStart" values:"%s" },
                            { key:"validityEnd" values:"%s" }
                          ]
                  }) {
                        id
                    }
                }
                """.formatted(
                        stopPlaceName,
                        newStartDate,
                        newEndDate
                );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("errors[0].extensions.errorCode", equalTo(HSLErrorCodeEnumeration.STOP_PLACE_UNIQUE_NAME.name()));
    }

    @Test
    public void forbidOverlappingValidityPeriodsWithSamePrivateCode() {

        String startDate = "1990-01-01";
        String endDate = "1991-01-01";
        String newStartDate = "1990-06-01";
        String newEndDate = "1991-06-01";

        String stopPlaceName = "OldName";
        String newStopPlaceName = "NewName";

        String privateCode = "X1234";

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace.setPrivateCode(new PrivateCodeStructure(privateCode, "COD"));
        stopPlace.getKeyValues().put("validityStart", new Value(startDate));
        stopPlace.getKeyValues().put("validityEnd", new Value(endDate));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                mutation {
                stopPlace: mutateStopPlace(StopPlace: {
                          name: { value:"%s" }
                          privateCode: { type: "COD", value: "%s" },
                          keyValues: [
                            { key:"validityStart" values:"%s" },
                            { key:"validityEnd" values:"%s" }
                          ]
                  }) {
                        id
                    }
                }
                """.formatted(
                newStopPlaceName,
                privateCode,
                newStartDate,
                newEndDate
        );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("errors[0].extensions.errorCode", equalTo(HSLErrorCodeEnumeration.STOP_PLACE_UNIQUE_PRIVATE_CODE.name()));
    }

    @Test
    public void forbidOverlappingValidityPeriodsWithNoEndDate() {

        String startDate = "1990-01-01";
        String newStartDate = "1990-06-01";
        String newEndDate = "1991-06-01";

        String stopPlaceName = "SameName";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace.getKeyValues().put("validityStart", new Value(startDate));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                mutation {
                stopPlace: mutateStopPlace(StopPlace: {
                          name: { value:"%s" }
                          keyValues: [
                            { key:"validityStart" values:"%s" },
                            { key:"validityEnd" values:"%s" }
                          ]
                  }) {
                        id
                    }
                }
                """.formatted(
                stopPlaceName,
                newStartDate,
                newEndDate
        );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("errors[0].extensions.errorCode", equalTo(HSLErrorCodeEnumeration.STOP_PLACE_UNIQUE_NAME.name()));
    }

    @Test
    public void allowNotOverlappingValidityPeriodsWithSameName() {

        String startDate = "1990-01-01";
        String endDate = "1991-01-01";
        String newStartDate = "1991-01-02";
        String newEndDate = "1992-01-01";

        String stopPlaceName = "SameName";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace.getKeyValues().put("validityStart", new Value(startDate));
        stopPlace.getKeyValues().put("validityEnd", new Value(endDate));
        stopPlace = stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                mutation {
                stopPlace: mutateStopPlace(StopPlace: {
                          name: { value:"%s" }
                          keyValues: [
                            { key:"validityStart" values:"%s" },
                            { key:"validityEnd" values:"%s" }
                          ]
                  }) {
                        id
                    }
                }
                """.formatted(
                stopPlaceName,
                newStartDate,
                newEndDate
        );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.id", not(equalTo(stopPlace.getNetexId())));
    }

    private StopPlace createStopPlaceWithMunicipalityRef(String name, TopographicPlace municipality, StopTypeEnumeration type) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlace.setStopPlaceType(type);
        if(municipality != null) {
            stopPlace.setTopographicPlace(municipality);
        }
        stopPlaceRepository.save(stopPlace);
        return stopPlace;
    }

    private StopPlace createStopPlace(String name) {
        return createStopPlaceWithMunicipalityRef(name, null);
    }

    private StopPlace createStopPlaceWithMunicipalityRef(String name, TopographicPlace municipality) {
        return createStopPlaceWithMunicipalityRef(name, municipality, null);
    }

    private TopographicPlace createMunicipalityWithCountyRef(String name, TopographicPlace county) {
        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString(name));
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        if(county != null) {
            municipality.setParentTopographicPlaceRef(new TopographicPlaceRefStructure(county));
        }
        topographicPlaceRepository.save(municipality);
        return municipality;
    }


    private PlaceEquipment createPlaceEquipments() {
        PlaceEquipment equipments = new PlaceEquipment();

        ShelterEquipment leskur = new ShelterEquipment();
        leskur.setEnclosed(false);
        leskur.setSeats(BigInteger.valueOf(2));

        WaitingRoomEquipment venterom = new WaitingRoomEquipment();
        venterom.setSeats(BigInteger.valueOf(25));

        TicketingEquipment billettAutomat = new TicketingEquipment();
        billettAutomat.setTicketMachines(true);
        billettAutomat.setNumberOfMachines(BigInteger.valueOf(2));

        SanitaryEquipment toalett = new SanitaryEquipment();
        toalett.setNumberOfToilets(BigInteger.valueOf(2));

        CycleStorageEquipment sykkelstativ = new CycleStorageEquipment();
        sykkelstativ.setCycleStorageType(CycleStorageEnumeration.RACKS);
        sykkelstativ.setNumberOfSpaces(BigInteger.TEN);

        GeneralSign skilt = new GeneralSign();
        skilt.setSignContentType(SignContentEnumeration.TRANSPORT_MODE);
        PrivateCodeStructure privCode = new PrivateCodeStructure();
        privCode.setValue("512");
        skilt.setPrivateCode(privCode);

        equipments.getInstalledEquipment().add(venterom);
        equipments.getInstalledEquipment().add(billettAutomat);
        equipments.getInstalledEquipment().add(toalett);
        equipments.getInstalledEquipment().add(leskur);
        equipments.getInstalledEquipment().add(sykkelstativ);
        equipments.getInstalledEquipment().add(skilt);
        return equipments;
    }

    private AccessibilityAssessment createAccessibilityAssessment() {
        AccessibilityLimitation accessibilityLimitation = new AccessibilityLimitation();
        accessibilityLimitation.setWheelchairAccess(LimitationStatusEnumeration.FALSE);
        accessibilityLimitation.setLiftFreeAccess(LimitationStatusEnumeration.FALSE);
        accessibilityLimitation.setEscalatorFreeAccess(LimitationStatusEnumeration.FALSE);
        accessibilityLimitation.setAudibleSignalsAvailable(LimitationStatusEnumeration.FALSE);
        accessibilityLimitation.setStepFreeAccess(LimitationStatusEnumeration.FALSE);
        List<AccessibilityLimitation> limitations = new ArrayList<>();
        limitations.add(accessibilityLimitation);

        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setLimitations(limitations);

        return accessibilityAssessment;
    }

    private HslAccessibilityProperties createHslAccessibilityProperties() {
        HslAccessibilityProperties hslAccessibilityProperties = new HslAccessibilityProperties();
        hslAccessibilityProperties.setStopAreaSideSlope(5.3);
        hslAccessibilityProperties.setStopAreaLengthwiseSlope(1.8);
        hslAccessibilityProperties.setEndRampSlope(5.);
        hslAccessibilityProperties.setShelterLaneDistance(123.);
        hslAccessibilityProperties.setCurbBackOfRailDistance(145.);
        hslAccessibilityProperties.setCurbDriveSideOfRailDistance(5.);
        hslAccessibilityProperties.setStructureLaneDistance(15.);
        hslAccessibilityProperties.setStopElevationFromRailTop(10.);
        hslAccessibilityProperties.setStopElevationFromSidewalk(7.);
        hslAccessibilityProperties.setLowerCleatHeight(5.);
        hslAccessibilityProperties.setServiceAreaWidth(4.60);
        hslAccessibilityProperties.setServiceAreaLength(5.52);
        hslAccessibilityProperties.setPlatformEdgeWarningArea(true);
        hslAccessibilityProperties.setGuidanceTiles(true);
        hslAccessibilityProperties.setGuidanceStripe(false);
        hslAccessibilityProperties.setServiceAreaStripes(false);
        hslAccessibilityProperties.setSidewalkAccessibleConnection(true);
        hslAccessibilityProperties.setStopAreaSurroundingsAccessible(false);
        hslAccessibilityProperties.setCurvedStop(true);
        hslAccessibilityProperties.setStopType(HslStopTypeEnumeration.PULL_OUT);
        hslAccessibilityProperties.setShelterType(ShelterWidthTypeEnumeration.WIDE);
        hslAccessibilityProperties.setGuidanceType(GuidanceTypeEnumeration.BRAILLE);
        hslAccessibilityProperties.setMapType(MapTypeEnumeration.TACTILE);
        hslAccessibilityProperties.setPedestrianCrossingRampType(PedestrianCrossingRampTypeEnumeration.LR);
        hslAccessibilityProperties.setAccessibilityLevel(AccessibilityLevelEnumeration.MOSTLY_ACCESSIBLE);

        return hslAccessibilityProperties;
    }

    private ShelterEquipment createShelterEquipmentHSl() {
        ShelterEquipment shelterEquipment = new ShelterEquipment();
        shelterEquipment.setEnclosed(true);
        shelterEquipment.setShelterType(ShelterTypeEnumeration.GLASS);
        shelterEquipment.setShelterElectricity(ElectricityTypeEnumeration.LIGHT);
        shelterEquipment.setShelterLighting(true);
        shelterEquipment.setShelterCondition(ShelterConditionEnumeration.GOOD);
        shelterEquipment.setTimetableCabinets(2);
        shelterEquipment.setTrashCan(true);
        shelterEquipment.setShelterHasDisplay(false);
        shelterEquipment.setBicycleParking(true);
        shelterEquipment.setLeaningRail(false);
        shelterEquipment.setOutsideBench(true);
        shelterEquipment.setShelterFasciaBoardTaping(false);
        return shelterEquipment;
    }
}
