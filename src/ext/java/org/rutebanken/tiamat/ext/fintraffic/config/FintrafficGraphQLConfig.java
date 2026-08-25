package org.rutebanken.tiamat.ext.fintraffic.config;

import org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingUpdater;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Registers Fintraffic-specific GraphQL beans, overriding their core counterparts
 * by using the same bean name. Bean overriding is enabled globally via
 * {@code spring.main.allow-bean-definition-overriding=true}. {@code @Bean} methods
 * in {@code @Configuration} classes are registered after {@code @Service}
 * component-scanned beans, so the override is reliable without needing {@code @Primary}.
 */
@Configuration
@Profile("fintraffic")
public class FintrafficGraphQLConfig {

    @Bean("parkingUpdater")
    public FintrafficParkingUpdater parkingUpdater() {
        return new FintrafficParkingUpdater();
    }
}
