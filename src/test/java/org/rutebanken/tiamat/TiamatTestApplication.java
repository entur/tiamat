package org.rutebanken.tiamat;


import org.rutebanken.tiamat.auth.RoleAssignmentExtractorConfig;
import org.rutebanken.tiamat.auth.TiamatSecurityConfig;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Run integration tests for the rest interface without security
 */
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableCaching
@EntityScan(basePackageClasses={StopPlace.class, Jsr310JpaConverters.class})
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TiamatSecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TiamatApplication.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = RoleAssignmentExtractorConfig.class)
} )
public class TiamatTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TiamatTestApplication.class, args);
    }
}
