package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.authorization.EntityPermissions;
import org.rutebanken.tiamat.netex.id.TypeFromIdResolver;
import org.rutebanken.tiamat.repository.generic.GenericEntityInVersionRepository;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LATITUDE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LONGITUDE;

@Component
public class LocationPermissionsFetcher implements DataFetcher {

    @Autowired
    private GenericEntityInVersionRepository genericEntityInVersionRepository;

    @Autowired
    private TypeFromIdResolver typeFromIdResolver;


    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private TopographicPlaceLookupService topographicPlaceLookupService;

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

        return new EntityPermissions(canEditEntity,false, locationAllowedStopPlaceTypes, locationBannedStopPlaceTypes, locationAllowedSubmodes, locationBannedSubmodes);

    }
}
