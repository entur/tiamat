package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.netex.id.TypeFromIdResolver;
import org.rutebanken.tiamat.repository.generic.GenericEntityInVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        final double latitude = environment.getArgument(LATITUDE);
        final double longitude = environment.getArgument(LONGITUDE);

        Point point = geometryFactory.createPoint(new Coordinate(latitude, longitude));





        /**
        final boolean canEditEntities = authorizationService.canEditEntity(entityInVersionStructure);
        final boolean canDeleteEntity = authorizationService.canDeleteEntity(entityInVersionStructure);
        final Set<String> allowedStopPlaceTypes = authorizationService.getAllowedStopPlaceTypes(entityInVersionStructure);
        final Set<String> bannedStopPlaceTypes = authorizationService.getBannedStopPlaceTypes(entityInVersionStructure);
        final Set<String> allowedSubmode = authorizationService.getAllowedSubmodes(entityInVersionStructure);
        final Set<String> bannedSubmode = authorizationService.getBannedSubmodes(entityInVersionStructure);


        return new EntityPermissions(canEditEntities, canDeleteEntity, allowedStopPlaceTypes, bannedStopPlaceTypes, allowedSubmode, bannedSubmode);

         */
        return null;
    }
}
