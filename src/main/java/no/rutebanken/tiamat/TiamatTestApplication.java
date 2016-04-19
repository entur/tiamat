package no.rutebanken.tiamat;

import no.rutebanken.tiamat.auth.TiamatSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import uk.org.netex.netex.StopPlace;

/**
 * Run integration tests for the rest interface without security
 */
@Configuration
@EnableAutoConfiguration
@EntityScan(basePackageClasses={StopPlace.class})
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TiamatSecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TiamatApplication.class)
} )
public class TiamatTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TiamatTestApplication.class, args);
    }
}
