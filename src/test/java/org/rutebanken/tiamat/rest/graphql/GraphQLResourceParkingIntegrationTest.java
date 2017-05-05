package org.rutebanken.tiamat.rest.graphql;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Test;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingTypeEnumeration;

import static org.hamcrest.Matchers.equalTo;

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
}
