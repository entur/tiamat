package no.rutebanken.tiamat.springconfig;

import javax.ws.rs.ApplicationPath;

import no.rutebanken.tiamat.rest.ifopt.StopPlaceResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import no.rutebanken.tiamat.rest.example.ExampleResource;

@Configuration
@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(ExampleResource.class);
        register(StopPlaceResource.class);
    }
}
