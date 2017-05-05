package org.rutebanken.tiamat.rest.graphql;

import com.google.common.base.Preconditions;
import com.vividsolutions.jts.geom.Point;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingTypeEnumeration;
import org.rutebanken.tiamat.model.ParkingVehicleEnumeration;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.rest.graphql.resolver.GeometryResolver;
import org.rutebanken.tiamat.versioning.ParkingVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.resolver.ObjectResolver.getEmbeddableString;

@Service("parkingUpdater")
@Transactional(propagation = Propagation.REQUIRES_NEW)
class ParkingUpdater implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ParkingUpdater.class);

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private ParkingVersionedSaverService parkingVersionedSaverService;

    @Autowired
    GeometryResolver geometryResolver;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public Object get(DataFetchingEnvironment environment) {

        Map input = environment.getArgument(OUTPUT_TYPE_PARKING);
        Parking parking = null;
        if (input != null) {

            parking = createOrUpdateParking(input);
        }
        return Arrays.asList(parking);
    }

    private Parking createOrUpdateParking(Map input) {
        Parking updatedParking;
        Parking existingVersion = null;
        String netexId = (String) input.get(ID);
        if (netexId != null) {
            logger.info("Updating Parking {}", netexId);
            existingVersion = parkingRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
            Preconditions.checkArgument(existingVersion != null, "Attempting to update StopPlace [id = %s], but StopPlace does not exist.", netexId);
            updatedParking = parkingVersionedSaverService.createCopy(existingVersion, Parking.class);

        } else {
            logger.info("Creating new Parking");
            updatedParking = new Parking();
        }
        boolean isUpdated = false;
        if (input.get(NAME) != null) {
            EmbeddableMultilingualString name = getEmbeddableString((Map) input.get(NAME));
            isUpdated = isUpdated || (!name.equals(updatedParking.getName()));
            updatedParking.setName(name);
        }

        if (input.get(GEOMETRY) != null) {
            Point geoJsonPoint = geometryResolver.createGeoJsonPoint((Map) input.get(GEOMETRY));
            isUpdated = isUpdated || (!geoJsonPoint.equals(updatedParking.getCentroid()));
            updatedParking.setCentroid(geoJsonPoint);
        }

        if (input.get(TOTAL_CAPACITY) != null) {
            BigInteger totalCapacity = (BigInteger) input.get(TOTAL_CAPACITY);
            isUpdated = isUpdated || (!totalCapacity.equals(updatedParking.getTotalCapacity()));

            updatedParking.setTotalCapacity(totalCapacity);
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

        if (isUpdated) {
//            authorizationService.assertAuthorized(ROLE_EDIT_STOPS, existingVersion, updatedParking);

            updatedParking = parkingVersionedSaverService.saveNewVersion(updatedParking);

            return updatedParking;
        } else {
            logger.info("No changes - Parking {} NOT updated", netexId);
        }
        return existingVersion;
    }
}
