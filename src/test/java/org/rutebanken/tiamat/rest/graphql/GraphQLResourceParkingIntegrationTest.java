package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.model.*;

import static org.hamcrest.Matchers.*;

public class GraphQLResourceParkingIntegrationTest extends AbstractGraphQLResourceIntegrationTest {
    @Test
    public void searchForParkingById() {

        Parking parking = new Parking();
        parking.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        parking.setParkingType(ParkingTypeEnumeration.PARK_AND_RIDE);
        parking.setParentSiteRef(new SiteRefStructure(stopPlaceRepository.save(new StopPlace()).getNetexId()));

        parkingVersionedSaverService.saveNewVersion(parking);

        String graphQlJsonQuery = """
                {
                  parking: %s(
                    id:"%s"
                  )
                  {
                    id
                    parkingType
                  }
                }
                """
                .formatted(GraphQLNames.FIND_PARKING, parking.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.parking[0].id", equalTo(parking.getNetexId()))
                .body("data.parking[0].parkingType", equalTo(parking.getParkingType().value()));
    }

    @Test
    public void searchForParkingByIdAndVersion() {

        Parking parking = new Parking();
        parking.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        ParkingTypeEnumeration originalParkingType = ParkingTypeEnumeration.PARK_AND_RIDE;
        parking.setParkingType(originalParkingType);
        parking.setParentSiteRef(new SiteRefStructure(stopPlaceRepository.save(new StopPlace()).getNetexId()));

        parking = parkingVersionedSaverService.saveNewVersion(parking);

        String netexId = parking.getNetexId();

        String version_1_GraphQlJsonQuery = """
                {
                    parking: %s (
                        id:"%s",
                        version:1
                    )
                    {
                        id
                        version
                        parkingType
                    }
                }
                """
                .formatted(GraphQLNames.FIND_PARKING, netexId);

        executeGraphQLQueryOnly(version_1_GraphQlJsonQuery)
                .body("data.parking[0].id", equalTo(netexId))
                .body("data.parking[0].version", equalTo("" + parking.getVersion()))
                .body("data.parking[0].parkingType", equalTo(parking.getParkingType().value()));

        String updatedParkingTypeValue = ParkingTypeEnumeration.PARKING_ZONE.value();
        String version_2_GraphQlJsonQuery = """
                mutation {
                    parking: %s (
                        Parking: {
                            id:"%s"
                            parkingType:%s
                        }
                    )
                    {
                        id
                        version
                        parkingType
                    }
                }
                """
                .formatted(GraphQLNames.MUTATE_PARKING, netexId, updatedParkingTypeValue);

        executeGraphQLQueryOnly(version_2_GraphQlJsonQuery)
                .body("data.parking[0].id", equalTo(netexId))
                .body("data.parking[0].version", equalTo("2"))
                .body("data.parking[0].parkingType", equalTo(updatedParkingTypeValue));
    }

    public static void main(String[] args) {
        String version_1_GraphQlJsonQuery = """
                {
                    parking: %s (
                        id:\\"%s\\",
                        version:1
                    )
                    {
                        id
                        version
                        parkingType
                    }
                }
                """
                .formatted(GraphQLNames.FIND_PARKING, 123);

        System.out.println(version_1_GraphQlJsonQuery);
    }

    @Test
    public void testMutateParkingWithParentSiteRef() {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Brummunddal"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAllAreasWheelchairAccessible(false);

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String graphQlQuery = """
                mutation {
                  parking: %s(Parking : {
                     name: {
                      value: "Parking name"
                      lang: "no"
                    }
                    parentSiteRef:"%s"
                    geometry: {
                      type:Point
                      coordinates:[[59.0, 10.5]]
                    }
                    totalCapacity:1234,
                    parkingType:parkAndRide,
                    parkingVehicleTypes: [car, pedalCycle]
                    parkingLayout:covered
                    principalCapacity:22
                    overnightParkingPermitted:true,
                    rechargingAvailable:false,
                    secure:false,
                    realTimeOccupancyAvailable:false,
                    parkingReservation:reservationAllowed,
                    bookingUrl:"https://www.rutebanken.org",
                    freeParkingOutOfHours:true,
                    parkingProperties: {,
                      parkingUserTypes:all,
                      spaces: [{
                        parkingVehicleType:car,
                        parkingStayType:unlimited,
                        numberOfSpaces:123
                      }]
                    }
                    parkingAreas: [{
                      label: {value:"Plan 1"}
                      totalCapacity:432
                      parkingProperties: {
                          parkingUserTypes:all,
                          spaces : [{
                              parkingVehicleType:car
                              parkingStayType:shortStay
                              numberOfSpaces:123
                            }]
                      }
                    }]
                  })
                  {
                    id,
                    version,
                    name {value lang},
                    parentSiteRef,
                    parkingType,
                    parkingVehicleTypes,
                    parkingLayout,
                    principalCapacity,
                    totalCapacity,
                    overnightParkingPermitted,
                    rechargingAvailable,
                    secure,
                    realTimeOccupancyAvailable,
                    parkingReservation,
                    bookingUrl,
                    freeParkingOutOfHours,
                    parkingProperties {
                      parkingUserTypes,
                      maximumStay,
                      spaces {
                        parkingVehicleType,
                        parkingStayType,
                        numberOfSpaces
                      }
                    }
                    parkingAreas {
                      label {value}
                      totalCapacity
                      parkingProperties {
                        parkingUserTypes,
                        spaces {,
                          parkingVehicleType,
                          parkingStayType,
                          numberOfSpaces
                        }
                      }
                    }
                    geometry{
                      type,
                      coordinates
                    }
                  }
                }
                """
                .formatted(GraphQLNames.MUTATE_PARKING, stopPlace.getNetexId());

        executeGraphQLQueryOnly(graphQlQuery)
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
                .body("parkingAreas.parkingProperties", notNullValue());
    }

    @Test
    public void testMutateParkingWithoutParentSiteRef() {

        StopPlace stopPlace = stopPlaceRepository.save(new StopPlace());

        String graphQlQuery = """
                mutation {
                  parking:  %s(Parking : {
                     name: {
                      value: "Parking name"
                      lang: "no"
                    }
                    parentSiteRef:"%s"
                    geometry: {
                      type:Point
                      coordinates:[[59.0, 10.5]]
                    }
                    totalCapacity:1234,
                    parkingType:parkAndRide,
                    parkingVehicleTypes: [car, pedalCycle]
                    parkingLayout:covered
                    principalCapacity:22
                    overnightParkingPermitted:true,
                    rechargingAvailable:false,
                    secure:false,
                    realTimeOccupancyAvailable:false,
                    parkingReservation:reservationAllowed,
                    bookingUrl:"https://www.rutebanken.org",
                    freeParkingOutOfHours:true,
                    parkingProperties: {,
                      parkingUserTypes:all,
                      spaces: [{
                        parkingVehicleType:car,
                        parkingStayType:unlimited,
                        numberOfSpaces:123
                      }]
                    }
                    parkingAreas: [{
                      label: {value:"Plan 1"}
                      totalCapacity:432
                      parkingProperties: {
                          parkingUserTypes:all,
                          spaces : [{
                              parkingVehicleType:car
                              parkingStayType:shortStay
                              numberOfSpaces:123
                            }]
                      }
                    }]
                  })
                  {
                    id,
                    version,
                    name {value lang},
                    parentSiteRef,
                    parkingType,
                    parkingVehicleTypes,
                    parkingLayout,
                    principalCapacity,
                    totalCapacity,
                    overnightParkingPermitted,
                    rechargingAvailable,
                    secure,
                    realTimeOccupancyAvailable,
                    parkingReservation,
                    bookingUrl,
                    freeParkingOutOfHours,
                    parkingProperties {
                      parkingUserTypes,
                      maximumStay,
                      spaces {
                        parkingVehicleType,
                        parkingStayType,
                        numberOfSpaces
                      }
                    }
                    parkingAreas {
                      label {value}
                      totalCapacity
                      parkingProperties {
                        parkingUserTypes,
                        spaces {,
                          parkingVehicleType,
                          parkingStayType,
                          numberOfSpaces
                        }
                      }
                    }
                    geometry{
                      type,
                      coordinates
                    }
                  }
                }
                """
                .formatted(GraphQLNames.MUTATE_PARKING, stopPlace.getNetexId());

        executeGraphQLQueryOnly(graphQlQuery)
                .body("data.parking", notNullValue())
                .root("data.parking[0]")
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
                .body("parkingProperties.spaces", notNullValue())
                .body("parkingAreas", notNullValue())
                .body("parkingAreas.label.value", notNullValue())
                .body("parkingAreas.totalCapacity", notNullValue())
                .body("parkingAreas.parkingProperties", notNullValue());

    }

    @Test
    public void testMutateMultipleParking() {

        StopPlace stopPlace = stopPlaceRepository.save(new StopPlace());

        String graphQlQuery = """
                mutation {
                  parking:  %s(Parking : [{
                     name: {
                      value: "Parking name"
                      lang: "no"
                    }
                    parentSiteRef:"%s"
                    geometry: {
                      type:Point
                      coordinates:[[59.0, 10.5]]
                    }
                  }, {
                     name: {
                      value: "Parking name"
                      lang: "no"
                    }
                    parentSiteRef:"%s"
                    geometry: {
                      type:Point
                      coordinates:[[59.0, 10.5]]
                    }
                  }] ) {
                    id,
                    name {value lang},
                  }
                }
                """
                .formatted(GraphQLNames.MUTATE_PARKING, stopPlace.getNetexId(), stopPlace.getNetexId());

        executeGraphQLQueryOnly(graphQlQuery)
                .body("data.parking", notNullValue())
                .body("data.parking", hasSize(2));
    }
}
