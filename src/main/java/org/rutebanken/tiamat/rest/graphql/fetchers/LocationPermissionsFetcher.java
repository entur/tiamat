package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.diff.generic.SubmodeEnumuration;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
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
        final Set<StopTypeEnumeration> locationAllowedStopPlaceTypes = authorizationService.getLocationAllowedStopPlaceTypes(canEditEntity, point);
        final Set<StopTypeEnumeration> locationBannedStopPlaceTypes = authorizationService.getLocationBannedStopPlaceTypes(canEditEntity, point);
        final Set<SubmodeEnumuration> locationAllowedSubmodes = authorizationService.getLocationAllowedSubmodes(canEditEntity, point);
        final Set<SubmodeEnumuration> locationBannedSubmodes = authorizationService.getLocationBannedSubmodes(canEditEntity, point);

        Set<StopTypeEnumeration> allowedStopPlaceTypeCopy = new HashSet<>(locationAllowedStopPlaceTypes);
        Set<StopTypeEnumeration> bannedStopPlaceTypeCopy = new HashSet<>(locationBannedStopPlaceTypes);
        Set<SubmodeEnumuration> allowedSubmodeCopy = new HashSet<>(locationAllowedSubmodes);
        Set<SubmodeEnumuration> bannedSubmodeCopy = new HashSet<>(locationBannedSubmodes);

        Set<StopTypeEnumeration> duplicateStopPlaceTypes = new HashSet<>(locationAllowedStopPlaceTypes);
        duplicateStopPlaceTypes.retainAll(locationBannedStopPlaceTypes);
        Set<SubmodeEnumuration> duplicateSubmodes = new HashSet<>(locationAllowedSubmodes);
        duplicateSubmodes.retainAll(locationBannedSubmodes);

        allowedStopPlaceTypeCopy.removeAll(duplicateStopPlaceTypes);
        allowedSubmodeCopy.removeAll(duplicateSubmodes);

        return new EntityPermissions.Builder()
                .allowedStopPlaceTypes(allowedStopPlaceTypeCopy)
                .bannedStopPlaceTypes(bannedStopPlaceTypeCopy)
                .allowedSubmodes(allowedSubmodeCopy)
                .bannedSubmodes(bannedSubmodeCopy)
                .canEdit(canEditEntity)
                .canDelete(false)
                .build();

    }
}
