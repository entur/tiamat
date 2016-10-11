package org.rutebanken.tiamat.config;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeometryFactoryConfig {

    private static final int SRID = 4326;

    @Bean
    public GeometryFactory geometryFactory() {

        return new GeometryFactory(new PrecisionModel(), SRID);

    }
}
