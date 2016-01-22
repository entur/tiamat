package no.rutebanken.tiamat.config;

import no.rutebanken.tiamat.rest.ifopt.QuayResource;
import no.rutebanken.tiamat.rest.ifopt.SiteFrameResource;
import no.rutebanken.tiamat.rest.ifopt.StopPlaceResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {

        packages("org.glassfish.jersey.jaxb;com.fasterxml.jackson.jaxrs.xml");
        register(StopPlaceResource.class);
        register(QuayResource.class);
        register(SiteFrameResource.class);
    }
}
