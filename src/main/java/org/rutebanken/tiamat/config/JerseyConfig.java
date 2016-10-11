package org.rutebanken.tiamat.config;

import org.rutebanken.tiamat.rest.dto.DtoQuayResource;
import org.rutebanken.tiamat.rest.dto.DtoStopPlaceResource;
import org.rutebanken.tiamat.rest.dto.DtoTopographicPlaceResource;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryResource;
import org.rutebanken.tiamat.rest.netex.siteframe.SiteFrameResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(DtoStopPlaceResource.class);
        register(DtoQuayResource.class);
        register(DtoTopographicPlaceResource.class);
        register(SiteFrameResource.class);
        register(PublicationDeliveryResource.class);
    }
}
