package no.rutebanken.tiamat.config;

import no.rutebanken.tiamat.rest.abzu.QuayResource;
import no.rutebanken.tiamat.rest.ifopt.SiteFrameResource;
import no.rutebanken.tiamat.rest.abzu.StopPlaceResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(StopPlaceResource.class);
        register(QuayResource.class);
        register(SiteFrameResource.class);
    }
}
