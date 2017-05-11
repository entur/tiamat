package org.rutebanken.tiamat.rest.graphql;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Test;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingTypeEnumeration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GraphQLResourceParkingIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Test
    public void searchForParkingById() throws Exception {

        Parking parking = new Parking();
        parking.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        parking.setParkingType(ParkingTypeEnumeration.PARK_AND_RIDE);

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
                .body("data.parking[0].parkingType", equalTo(parking.getParkingType().value()))
        ;
    }

    @Test
    public void searchForParkingByIdAndVersion() throws Exception {

        Parking parking = new Parking();
        parking.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        ParkingTypeEnumeration originalParkingType = ParkingTypeEnumeration.PARK_AND_RIDE;
        parking.setParkingType(originalParkingType);

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
                .body("data.parking[0].parkingType", equalTo(parking.getParkingType().value()))
        ;

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
                .body("data.parking[0].parkingType", equalTo(updatedParkingTypeValue))
        ;
    }
    @Test
    public void testMutateParking() throws Exception {

        String graphQlQuery = "{\n" +
                "\"query\": \"mutation { " +
                "  parking: " + GraphQLNames.MUTATE_PARKING + " (Parking : {" +
                "     name: {" +
                "      value: \\\"Parking name\\\" " +
                "      lang: \\\"no\\\" " +
                "    }" +
                "    geometry: { " +
                "      type:Point " +
                "      coordinates:[[59.0, 10.5]] " +
                "    }" +
                "    totalCapacity:1234, " +
                "    parkingType:parkAndRide, " +
                "    parkingVehicleTypes: [car, pedalCycle]" +
                "    parentSiteRef:\\\"NSR:StopPlace:1\\\"" +
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
                "  }" +
                "}\",\"variables\": \"\"}";
        executeGraphQL(graphQlQuery)
                .body("data.parking", notNullValue())
                .root("data.parking[0]")
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
                ;

    }


}
