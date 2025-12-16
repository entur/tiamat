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

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingTypeEnumeration;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

public class GraphQLResourceParkingIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Test
    public void searchForParkingById() throws Exception {

        Parking parking = new Parking();
        parking.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        parking.setParkingType(ParkingTypeEnumeration.PARK_AND_RIDE);
        parking.setParentSiteRef(new SiteRefStructure(stopPlaceRepository.save(new StopPlace()).getNetexId()));

        parkingVersionedSaverService.saveNewVersion(parking);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  parking: " + GraphQLNames.FIND_PARKING + " (id:\\\"" + parking.getNetexId() + "\\\") { " +
                "    id " +
                "    parkingType " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.parking[0].id", equalTo(parking.getNetexId()))
                .body("data.parking[0].parkingType", equalTo(parking.getParkingType().value()));

    }

    @Test
    public void searchForParkingByIdAndVersion() throws Exception {

        Parking parking = new Parking();
        parking.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        ParkingTypeEnumeration originalParkingType = ParkingTypeEnumeration.PARK_AND_RIDE;
        parking.setParkingType(originalParkingType);
        parking.setParentSiteRef(new SiteRefStructure(stopPlaceRepository.save(new StopPlace()).getNetexId()));
        parking = parkingVersionedSaverService.saveNewVersion(parking);

        String netexId = parking.getNetexId();

        String version_1_GraphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  parking: " + GraphQLNames.FIND_PARKING + " (id:\\\"" + netexId + "\\\", version:1 ) { " +
                "    id " +
                "    version " +
                "    parkingType " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}";

        executeGraphQL(version_1_GraphQlJsonQuery)
                .body("data.parking[0].id", equalTo(netexId))
                .body("data.parking[0].version", equalTo(""+parking.getVersion()))
                .body("data.parking[0].parkingType", equalTo(parking.getParkingType().value()));


        String updatedParkingTypeValue = ParkingTypeEnumeration.PARKING_ZONE.value();
        String version_2_GraphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  parking:" + GraphQLNames.MUTATE_PARKING + " (Parking: {" +
                "        id:\\\"" + netexId + "\\\" " +
                "        parkingType: " + updatedParkingTypeValue +
                "       }) { " +
                "      id " +
                "      version " +
                "      parkingType " +
                "    } " +
                "}\"," +
                "\"variables\":\"\"}";

        executeGraphQL(version_2_GraphQlJsonQuery)
                .body("data.parking[0].id", equalTo(netexId))
                .body("data.parking[0].version", equalTo("2"))
                .body("data.parking[0].parkingType", equalTo(updatedParkingTypeValue));

    }

    @Test
    public void testMutateParkingWithParentSiteRef() throws Exception {


        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Brummunddal"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAllAreasWheelchairAccessible(false);

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String graphQlQuery = "{\n" +
                "\"query\": \"mutation { " +
                "  parking: " + GraphQLNames.MUTATE_PARKING + " (Parking : {" +
                "     name: {" +
                "      value: \\\"Parking name\\\" " +
                "      lang: \\\"no\\\" " +
                "    }" +
                "    geometry: { " +
                "      type:Point " +
                "      coordinates:[59.0, 10.5] " +
                "    }" +
                "    totalCapacity:1234, " +
                "    parkingType:parkAndRide, " +
                "    parkingVehicleTypes: [car, pedalCycle]" +
                "    parentSiteRef:\\\"%s\\\"".formatted(stopPlace.getNetexId()) +
                "    parkingLayout:covered " +
                "    principalCapacity:22 " +
                "    overnightParkingPermitted:true, " +
                "    rechargingAvailable:false, " +
                "    secure:false, " +
                "    realTimeOccupancyAvailable:false, " +
                "    parkingReservation:reservationAllowed, " +
                "    bookingUrl:\\\"https://www.rutebanken.org\\\", " +
                "    freeParkingOutOfHours:true, " +
                "    parkingProperties: {, " +
                "      parkingUserTypes:all," +
                "      spaces: [{" +
                "        parkingVehicleType:car," +
                "        parkingStayType:unlimited," +
                "        numberOfSpaces:123" +
                "      }]" +
                "    }" +
                "    parkingAreas: [{" +
                "      label: {value:\\\"Plan 1\\\"}" +
                "      totalCapacity:432" +
                "      parkingProperties: {" +
                "          parkingUserTypes:all, " +
                "          spaces : [{" +
                "              parkingVehicleType:car" +
                "              parkingStayType:shortStay" +
                "              numberOfSpaces:123" +
                "            }]" +
                "      }" +
                "      }]" +
                "    accessibilityAssessment: {" +
                "          limitations: {" +
                "            wheelchairAccess:UNKNOWN," +
                "            stepFreeAccess:TRUE," +
                "            escalatorFreeAccess:UNKNOWN," +
                "            liftFreeAccess:UNKNOWN," +
                "            audibleSignalsAvailable:UNKNOWN," +
                "            visualSignsAvailable:UNKNOWN" +
                "          }," +
                "     }," +
                "}) {" +
                "    id, " +
                "    version, " +
                "    name {value lang}, " +
                "    parentSiteRef, " +
                "    parkingType," +
                "    parkingVehicleTypes," +
                "    parkingLayout," +
                "    principalCapacity," +
                "    totalCapacity," +
                "    overnightParkingPermitted," +
                "    rechargingAvailable," +
                "    secure," +
                "    realTimeOccupancyAvailable," +
                "    parkingReservation," +
                "    bookingUrl," +
                "    freeParkingOutOfHours," +
                "    parkingProperties {" +
                "      parkingUserTypes," +
                "      maximumStay," +
                "      spaces {" +
                "        parkingVehicleType" +
                ",       parkingStayType," +
                "        numberOfSpaces" +
                "      }" +
                "    }" +
                "    parkingAreas {" +
                "      label {value}" +
                "      totalCapacity" +
                "      parkingProperties {" +
                "        parkingUserTypes," +
//                "        maximumStay," +
                "        spaces {," +
                "          parkingVehicleType," +
                "          parkingStayType," +
                "          numberOfSpaces" +
                "        }" +
                "      }" +
                "    }" +
                "    geometry{" +
                "      type," +
                "      coordinates" +
                "    }" +
                "    accessibilityAssessment { " +
                "       limitations {" +
                "        wheelchairAccess" +
                "        stepFreeAccess" +
                "        escalatorFreeAccess" +
                "        liftFreeAccess" +
                "        audibleSignalsAvailable" +
                "        visualSignsAvailable" +
                "      }" +
                "    }"+
                "  }" +
                "}\",\"variables\": \"\"}";
        executeGraphQL(graphQlQuery)
                .body("data.parking", notNullValue())
                .rootPath("data.parking[0]")
                    .body("id", notNullValue())
                    .body("version", notNullValue())
                    .body("name.value", notNullValue())
                    .body("name.lang", notNullValue())
                    .body("geometry.type", notNullValue())
                    .body("geometry.coordinates", notNullValue())
                    .body("parentSiteRef", notNullValue())
                    .body("parkingType", notNullValue())
                    .body("parkingVehicleTypes", notNullValue())
                    .body("parkingVehicleTypes", notNullValue())
                    .body("parkingLayout", notNullValue())
                    .body("principalCapacity", notNullValue())
                    .body("totalCapacity", notNullValue())
                    .body("overnightParkingPermitted", notNullValue())
                    .body("rechargingAvailable", notNullValue())
                    .body("secure", notNullValue())
                    .body("realTimeOccupancyAvailable", notNullValue())
                    .body("parkingReservation", notNullValue())
                    .body("bookingUrl", notNullValue())
                    .body("parkingProperties.parkingUserTypes", notNullValue())
                    .body("parkingProperties.spaces", notNullValue())
                    .body("parkingAreas", notNullValue())
                    .body("parkingAreas.label.value", notNullValue())
                    .body("parkingAreas.totalCapacity", notNullValue())
                    .body("parkingAreas.parkingProperties", notNullValue())
                    .body("accessibilityAssessment", notNullValue());
    }


    @Test
    public void testMutateParkingWithoutParentSiteRef() throws Exception {

        StopPlace stopPlace = stopPlaceRepository.save(new StopPlace());

        String graphQlQuery = "{\n" +
                "\"query\": \"mutation { " +
                "  parking: " + GraphQLNames.MUTATE_PARKING + " (Parking : {" +

                "     name: {" +
                "      value: \\\"Parking name\\\" " +
                "      lang: \\\"no\\\" " +
                "    }" +
                "    parentSiteRef:\\\"%s\\\"".formatted(stopPlace.getNetexId()) +
                "    geometry: { " +
                "      type:Point " +
                "      coordinates:[59.0, 10.5] " +
                "    }" +
                // totalCapacity is calculated automatically totalCapacity = Sum of all ParkingProperties>Spaces>noOfSpaces
                //"    totalCapacity:1234, " +
                "    parkingType:parkAndRide, " +
                "    parkingVehicleTypes: [car, pedalCycle]" +
                "    parkingLayout:covered " +
                "    principalCapacity:22 " +
                "    overnightParkingPermitted:true, " +
                "    rechargingAvailable:false, " +
                "    secure:false, " +
                "    realTimeOccupancyAvailable:false, " +
                "    parkingReservation:reservationAllowed, " +
                "    bookingUrl:\\\"https://www.rutebanken.org\\\", " +
                "    freeParkingOutOfHours:true, " +
                "    parkingProperties: {, " +
                "      parkingUserTypes:all," +
                "      spaces: [{" +
                "        parkingVehicleType:car," +
                "        parkingStayType:unlimited," +
                "        numberOfSpaces:123" +
                "      }," +
                "      {" +
                "        parkingVehicleType:pedalCycle," +
                "        parkingStayType:unlimited," +
                "        numberOfSpaces:100" +
                "      }]" +
                "    }" +
                "    parkingAreas: [{" +
                "      label: {value:\\\"Plan 1\\\"}" +
                "      totalCapacity:432" +
                "      parkingProperties: {" +
                "          parkingUserTypes:all, " +
                "          spaces : [{" +
                "              parkingVehicleType:car" +
                "              parkingStayType:shortStay" +
                "              numberOfSpaces:123" +
                "            }]" +
                "      }" +
                "      }]" +
                "    accessibilityAssessment: {" +
                "          limitations: {" +
                "            wheelchairAccess:UNKNOWN," +
                "            stepFreeAccess:TRUE," +
                "            escalatorFreeAccess:UNKNOWN," +
                "            liftFreeAccess:UNKNOWN," +
                "            audibleSignalsAvailable:UNKNOWN," +
                "            visualSignsAvailable:UNKNOWN" +
                "          }," +
                "     }," +
                "}) {" +
                "    id, " +
                "    version, " +
                "    name {value lang}, " +
                "    parentSiteRef, " +
                "    parkingType," +
                "    parkingVehicleTypes," +
                "    parkingLayout," +
                "    principalCapacity," +
                "    totalCapacity," +
                "    overnightParkingPermitted," +
                "    rechargingAvailable," +
                "    secure," +
                "    realTimeOccupancyAvailable," +
                "    parkingReservation," +
                "    bookingUrl," +
                "    freeParkingOutOfHours," +
                "    parkingProperties {" +
                "      parkingUserTypes," +
                "      maximumStay," +
                "      spaces {" +
                "        parkingVehicleType" +
                ",       parkingStayType," +
                "        numberOfSpaces" +
                "      }" +
                "    }" +
                "    parkingAreas {" +
                "      label {value}" +
                "      totalCapacity" +
                "      parkingProperties {" +
                "        parkingUserTypes," +
//                "        maximumStay," +
                "        spaces {," +
                "          parkingVehicleType," +
                "          parkingStayType," +
                "          numberOfSpaces" +
                "        }" +
                "      }" +
                "    }" +
                "    geometry{" +
                "      type," +
                "      coordinates" +
                "    }" +
                "    accessibilityAssessment { " +
                "       limitations {" +
                "        wheelchairAccess" +
                "        stepFreeAccess" +
                "        escalatorFreeAccess" +
                "        liftFreeAccess" +
                "        audibleSignalsAvailable" +
                "        visualSignsAvailable" +
                "      }" +
                "    }"+
                "  }" +
                "}\",\"variables\": \"\"}";
        executeGraphQL(graphQlQuery)
                .body("data.parking", notNullValue())
                .rootPath("data.parking[0]")
                    .body("id", notNullValue())
                    .body("version", notNullValue())
                    .body("name.value", notNullValue())
                    .body("name.lang", notNullValue())
                    .body("geometry.type", notNullValue())
                    .body("geometry.coordinates", notNullValue())
                    .body("parentSiteRef", equalTo(stopPlace.getNetexId()))
                    .body("parkingType", notNullValue())
                    .body("parkingVehicleTypes", notNullValue())
                    .body("parkingVehicleTypes", notNullValue())
                    .body("parkingLayout", notNullValue())
                    .body("principalCapacity", notNullValue())
                    .body("totalCapacity", notNullValue())
                    .body("overnightParkingPermitted", notNullValue())
                    .body("rechargingAvailable", notNullValue())
                    .body("secure", notNullValue())
                    .body("realTimeOccupancyAvailable", notNullValue())
                    .body("parkingReservation", notNullValue())
                    .body("bookingUrl", notNullValue())
                    .body("parkingProperties.parkingUserTypes", notNullValue())
                    .body("parkingProperties[0].spaces[0].numberOfSpaces", equalTo(123))
                    .body("parkingProperties[0].spaces[0].parkingVehicleType", equalTo("car"))
                    .body("parkingAreas", notNullValue())
                    .body("parkingAreas.label.value", notNullValue())
                    .body("totalCapacity", equalTo(223))
                    .body("parkingAreas.parkingProperties", notNullValue())
                    .body("accessibilityAssessment", notNullValue());

    }


    @Test
    public void testMutateMultipleParkings() throws Exception {

        StopPlace stopPlace = stopPlaceRepository.save(new StopPlace());

        String graphQlQuery = "{\n" +
                "\"query\": \"mutation { " +
                "  parking: " + GraphQLNames.MUTATE_PARKING + " (Parking : [{" +
                "     name: {" +
                "      value: \\\"Parking name\\\" " +
                "      lang: \\\"no\\\" " +
                "    }" +
                "    parentSiteRef:\\\"%s\\\"".formatted(stopPlace.getNetexId()) +
                "    geometry: { " +
                "      type:Point " +
                "      coordinates:[59.0, 10.5] " +
                "    }" +
                "  }, {" +
                "     name: {" +
                "      value: \\\"Parking name\\\" " +
                "      lang: \\\"no\\\" " +
                "    }" +
                "    parentSiteRef:\\\"%s\\\"".formatted(stopPlace.getNetexId()) +
                "    geometry: { " +
                "      type:Point " +
                "      coordinates:[59.0, 10.5] " +
                "    }" +
                "  }] ) {" +
                "    id, " +
                "    name {value lang}, " +

                "  }" +
                "}\",\"variables\": \"\"}";

        executeGraphQL(graphQlQuery)
                .body("data.parking", notNullValue())
                .body("data.parking", hasSize(2));


    }


}
