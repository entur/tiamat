package no.rutebanken.tiamat.config;

import no.rutebanken.tiamat.rest.dto.DtoQuayResource;
import no.rutebanken.tiamat.rest.netex.siteframe.SiteFrameResource;
import no.rutebanken.tiamat.rest.dto.DtoStopPlaceResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(DtoStopPlaceResource.class);
        register(DtoQuayResource.class);
        register(SiteFrameResource.class);
    }
}
