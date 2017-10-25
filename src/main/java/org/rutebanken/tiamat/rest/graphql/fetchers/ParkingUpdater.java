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

package org.rutebanken.tiamat.rest.graphql.fetchers;

import com.google.common.base.Preconditions;
import com.vividsolutions.jts.geom.Point;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.rest.graphql.mappers.GeometryMapper;
import org.rutebanken.tiamat.rest.graphql.mappers.ValidBetweenMapper;
import org.rutebanken.tiamat.versioning.ParkingVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;

@Service("parkingUpdater")
@Transactional
class ParkingUpdater implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ParkingUpdater.class);

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private ParkingVersionedSaverService parkingVersionedSaverService;

    @Autowired
    private GeometryMapper geometryMapper;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    @Autowired
    private ValidBetweenMapper validBetweenMapper;

    @Override
    public Object get(DataFetchingEnvironment environment) {

        List<Map> input = environment.getArgument(OUTPUT_TYPE_PARKING);
        List<Parking> parkings = null;
        if (input != null) {
            parkings = input.stream()
             .map(m -> createOrUpdateParking(m))
            .collect(Collectors.toList());
        }
        return parkings;
    }

    private Parking createOrUpdateParking(Map input) {
        Parking updatedParking;
        Parking existingVersion = null;
        String netexId = (String) input.get(ID);
        if (netexId != null) {
            logger.info("Updating Parking {}", netexId);
            existingVersion = parkingRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
            Preconditions.checkArgument(existingVersion != null, "Attempting to update Parking [id = %s], but Parking does not exist.", netexId);
            updatedParking = parkingVersionedSaverService.createCopy(existingVersion, Parking.class);

        } else {
            logger.info("Creating new Parking");
            updatedParking = new Parking();
        }
        boolean isUpdated = populateParking(input, updatedParking);

        if (isUpdated) {
            authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(existingVersion, updatedParking));

            logger.info("Saving new version of parking {}", updatedParking);
            updatedParking = parkingVersionedSaverService.saveNewVersion(updatedParking);

            return updatedParking;
        } else {
            logger.info("No changes - Parking {} NOT updated", netexId);
        }
        return existingVersion;
    }

    private boolean populateParking(Map input, Parking updatedParking) {
        boolean isUpdated = false;
        if (input.get(NAME) != null) {
            EmbeddableMultilingualString name = getEmbeddableString((Map) input.get(NAME));
            isUpdated = isUpdated || (!name.equals(updatedParking.getName()));
            updatedParking.setName(name);
        }

        if (input.get(VALID_BETWEEN) != null) {
            updatedParking.setValidBetween(validBetweenMapper.map((Map) input.get(VALID_BETWEEN)));
            isUpdated = true;
        }

        if (input.get(GEOMETRY) != null) {
            Point geoJsonPoint = geometryMapper.createGeoJsonPoint((Map) input.get(GEOMETRY));
            isUpdated = isUpdated || (!geoJsonPoint.equals(updatedParking.getCentroid()));
            updatedParking.setCentroid(geoJsonPoint);
        }

        if (input.get(PARENT_SITE_REF) != null) {
            SiteRefStructure parentSiteRef = new SiteRefStructure();
            parentSiteRef.setRef((String) input.get(PARENT_SITE_REF));

            isUpdated = isUpdated || (!parentSiteRef.equals(updatedParking.getParentSiteRef()));

            updatedParking.setParentSiteRef(parentSiteRef);
        }

        if (input.get(TOTAL_CAPACITY) != null) {
            BigInteger totalCapacity = (BigInteger) input.get(TOTAL_CAPACITY);
            isUpdated = isUpdated || (!totalCapacity.equals(updatedParking.getTotalCapacity()));

            updatedParking.setTotalCapacity(totalCapacity);
        }

        if (input.get(PRINCIPAL_CAPACITY) != null) {
            BigInteger principalCapacity = (BigInteger) input.get(PRINCIPAL_CAPACITY);
            isUpdated = isUpdated || (!principalCapacity.equals(updatedParking.getPrincipalCapacity()));

            updatedParking.setPrincipalCapacity(principalCapacity);
        }

        if (input.get(PARKING_TYPE) != null) {
            ParkingTypeEnumeration parkingType = (ParkingTypeEnumeration) input.get(PARKING_TYPE);
            isUpdated = isUpdated || (!parkingType.equals(updatedParking.getParkingType()));
            updatedParking.setParkingType(parkingType);
        }
        if (input.get(PARKING_VEHICLE_TYPES) != null) {
            List<ParkingVehicleEnumeration> vehicleTypes = (List<ParkingVehicleEnumeration>) input.get(PARKING_VEHICLE_TYPES);
            isUpdated = isUpdated || !(updatedParking.getParkingVehicleTypes().containsAll(vehicleTypes) &&
                    vehicleTypes.containsAll(updatedParking.getParkingVehicleTypes()));

            updatedParking.getParkingVehicleTypes().clear();
            updatedParking.getParkingVehicleTypes().addAll(vehicleTypes);
        }

        if (input.get(PARKING_LAYOUT) != null) {
            ParkingLayoutEnumeration parkingLayout = (ParkingLayoutEnumeration) input.get(PARKING_LAYOUT);
            isUpdated = isUpdated || (!parkingLayout.equals(updatedParking.getParkingLayout()));
            updatedParking.setParkingLayout(parkingLayout);
        }

        if (input.get(OVERNIGHT_PARKING_PERMITTED) != null) {
            Boolean overnightParkingPermitted = (Boolean) input.get(OVERNIGHT_PARKING_PERMITTED);
            isUpdated = isUpdated || (!overnightParkingPermitted.equals(updatedParking.isOvernightParkingPermitted()));
            updatedParking.setOvernightParkingPermitted(overnightParkingPermitted);
        }

        if (input.get(RECHARGING_AVAILABLE) != null) {
            Boolean rechargingAvailable = (Boolean) input.get(RECHARGING_AVAILABLE);
            isUpdated = isUpdated || (!rechargingAvailable.equals(updatedParking.isRechargingAvailable()));
            updatedParking.setRechargingAvailable(rechargingAvailable);
        }

        if (input.get(SECURE) != null) {
            Boolean isSecure = (Boolean) input.get(SECURE);
            isUpdated = isUpdated || (!isSecure.equals(updatedParking.isSecure()));
            updatedParking.setSecure(isSecure);
        }

        if (input.get(REAL_TIME_OCCUPANCY_AVAILABLE) != null) {
            Boolean isRealtimeOccupancyAvailable = (Boolean) input.get(REAL_TIME_OCCUPANCY_AVAILABLE);
            isUpdated = isUpdated || (!isRealtimeOccupancyAvailable.equals(updatedParking.isRealTimeOccupancyAvailable()));
            updatedParking.setRealTimeOccupancyAvailable(isRealtimeOccupancyAvailable);
        }

        if (input.get(FREE_PARKING_OUT_OF_HOURS) != null) {
            Boolean freeParkingOutOfHours = (Boolean) input.get(FREE_PARKING_OUT_OF_HOURS);
            isUpdated = isUpdated || (!freeParkingOutOfHours.equals(updatedParking.isFreeParkingOutOfHours()));
            updatedParking.setFreeParkingOutOfHours(freeParkingOutOfHours);
        }

        if (input.get(PARKING_RESERVATION) != null) {
            ParkingReservationEnumeration parkingReservation = (ParkingReservationEnumeration) input.get(PARKING_RESERVATION);
            isUpdated = isUpdated || (!parkingReservation.equals(updatedParking.getParkingReservation()));
            updatedParking.setParkingReservation(parkingReservation);
        }

        if (input.get(BOOKING_URL) != null) {
            String bookingUrl = (String) input.get(BOOKING_URL);
            isUpdated = isUpdated || (!bookingUrl.equals(updatedParking.getBookingUrl()));
            updatedParking.setBookingUrl(bookingUrl);
        }

        if (input.get(PARKING_PROPERTIES) != null) {
            List<ParkingProperties> parkingPropertiesList = resolveParkingPropertiesList((List) input.get(PARKING_PROPERTIES));
            isUpdated = true;
            updatedParking.setParkingProperties(parkingPropertiesList);
        }

        if (input.get(PARKING_AREAS) != null) {
            List<ParkingArea> parkingAreasList = resolveParkingAreasList((List) input.get(PARKING_AREAS));
            isUpdated = true;
            updatedParking.setParkingAreas(parkingAreasList);
        }
        return isUpdated;
    }

    private List<ParkingProperties> resolveParkingPropertiesList(List propertyList) {
        List<ParkingProperties> result = new ArrayList<>();
        for (Object property : propertyList) {
            result.add(resolveSingleParkingProperties((Map) property));
        }

        return result;
    }

    private ParkingProperties resolveSingleParkingProperties(Map input) {
        ParkingProperties p = new ParkingProperties();

        p.getParkingUserTypes().addAll((Collection<? extends ParkingUserEnumeration>) input.get(PARKING_USER_TYPES));
        p.setSpaces(resolveParkingCapacities((List) input.get(SPACES)));

        //p.setMaximumStay(input.get(MAXIMUM_STAY));

        return p;
    }

    private List<ParkingCapacity> resolveParkingCapacities(List input) {
        List<ParkingCapacity> result = new ArrayList<>();
        for (Object property : input) {
            result.add(resolveSingleParkingCapacity((Map) property));
        }

        return result;
    }

    private ParkingCapacity resolveSingleParkingCapacity(Map input) {
        ParkingCapacity capacity = new ParkingCapacity();
        capacity.setParkingVehicleType((ParkingVehicleEnumeration) input.get(PARKING_VEHICLE_TYPE));
        capacity.setParkingStayType((ParkingStayEnumeration) input.get(PARKING_STAY_TYPE));
        capacity.setNumberOfSpaces((BigInteger) input.get(NUMBER_OF_SPACES));
        return capacity;
    }

    private List<ParkingArea> resolveParkingAreasList(List list) {
        List<ParkingArea> result = new ArrayList<>();
        for (Object property : list) {
            result.add(resolveSingleParkingArea((Map) property));
        }

        return result;
    }

    private ParkingArea resolveSingleParkingArea(Map input) {
        ParkingArea area = new ParkingArea();
        area.setLabel(getEmbeddableString((Map) input.get(LABEL)));
        area.setTotalCapacity((BigInteger) input.get(TOTAL_CAPACITY));
        area.setParkingProperties(resolveSingleParkingProperties((Map) input.get(PARKING_PROPERTIES)));
        return area;
    }
}
