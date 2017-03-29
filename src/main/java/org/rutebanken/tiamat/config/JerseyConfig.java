package org.rutebanken.tiamat.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.rutebanken.tiamat.rest.coordinates.CoordinatesFixerResource;
import org.rutebanken.tiamat.rest.dto.DtoQuayResource;
import org.rutebanken.tiamat.rest.dto.DtoStopPlaceResource;
import org.rutebanken.tiamat.rest.dto.DtoTopographicPlaceResource;
import org.rutebanken.tiamat.rest.exception.AccessDeniedExceptionMapper;
import org.rutebanken.tiamat.rest.graphql.GraphQLResource;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.InitialImportResource;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryResource;
import org.rutebanken.tiamat.rest.topographic_place.StopPlaceTopographicRefUpdaterResource;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(CoordinatesFixerResource.class);
        register(DtoStopPlaceResource.class);
        register(DtoQuayResource.class);
        register(DtoTopographicPlaceResource.class);
        register(PublicationDeliveryResource.class);
        register(InitialImportResource.class);
        register(GraphQLResource.class);
        register(StopPlaceTopographicRefUpdaterResource.class);

        register(AccessDeniedExceptionMapper.class);
    }
}
