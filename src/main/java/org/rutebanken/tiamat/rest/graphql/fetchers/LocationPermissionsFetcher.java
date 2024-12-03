package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.authorization.EntityPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LATITUDE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LONGITUDE;

@Component
public class LocationPermissionsFetcher implements DataFetcher {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private GeometryFactory geometryFactory;

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        final double latitude = ((BigDecimal) environment.getArgument(LATITUDE)).doubleValue();
        final double longitude = ((BigDecimal)environment.getArgument(LONGITUDE)).doubleValue();

        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        final boolean canEditEntity = authorizationService.canEditEntity(point);
        final Set<String> locationAllowedStopPlaceTypes = authorizationService.getLocationAllowedStopPlaceTypes(canEditEntity, point);
        final Set<String> locationBannedStopPlaceTypes = authorizationService.getLocationBannedStopPlaceTypes(canEditEntity, point);
        final Set<String> locationAllowedSubmodes = authorizationService.getLocationAllowedSubmodes(canEditEntity, point);
        final Set<String> locationBannedSubmodes = authorizationService.getLocationBannedSubmodes(canEditEntity, point);


        Set<String> allowedStopPlaceTypeCopy = new HashSet<>(locationAllowedStopPlaceTypes);
        Set<String> bannedStopPlaceTypeCopy = new HashSet<>(locationBannedStopPlaceTypes);
        Set<String> allowedSubmodeCopy = new HashSet<>(locationAllowedSubmodes);
        Set<String> bannedSubmodeCopy = new HashSet<>(locationBannedSubmodes);

        Set<String> duplicateStopPlaceTypes = new HashSet<>(locationAllowedStopPlaceTypes);
        duplicateStopPlaceTypes.retainAll(locationBannedStopPlaceTypes);
        Set<String> duplicateSubmodes = new HashSet<>(locationAllowedSubmodes);
        duplicateSubmodes.retainAll(locationBannedSubmodes);

        allowedStopPlaceTypeCopy.removeAll(duplicateStopPlaceTypes);
        bannedStopPlaceTypeCopy.removeAll(duplicateStopPlaceTypes);
        allowedSubmodeCopy.removeAll(duplicateSubmodes);
        bannedSubmodeCopy.removeAll(duplicateSubmodes);
        if(allowedStopPlaceTypeCopy.isEmpty()) {
            allowedStopPlaceTypeCopy.add("*");

        }
        if(allowedSubmodeCopy.isEmpty()) {
            allowedSubmodeCopy.add("*");
        }

        return new EntityPermissions(canEditEntity,false, allowedStopPlaceTypeCopy, bannedStopPlaceTypeCopy, allowedSubmodeCopy, bannedSubmodeCopy);

    }
}
