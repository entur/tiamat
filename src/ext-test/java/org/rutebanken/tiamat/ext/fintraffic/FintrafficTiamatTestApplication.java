package org.rutebanken.tiamat.ext.fintraffic;

import org.rutebanken.tiamat.auth.TiamatSecurityConfig;
import org.rutebanken.tiamat.config.ApplicationContextProvider;
import org.rutebanken.tiamat.ext.fintraffic.auth.FintrafficSecurityConfig;
import org.rutebanken.tiamat.ext.fintraffic.config.FintrafficTestContextConfiguration;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot test application for Fintraffic extension integration tests.
 * Extends the core TiamatTestApplication setup with:
 * <ul>
 *   <li>Entity scan covering {@link FintrafficParking} (ext model)</li>
 *   <li>Component scan covering the ext package tree</li>
 * </ul>
 * Security is excluded; authorization is mocked in individual tests.
 */
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
@EnableTransactionManagement
@EnableCaching
@EntityScan(basePackageClasses = {StopPlace.class, FintrafficParking.class, Jsr310JpaConverters.class})
@ComponentScan(
        basePackages = "org.rutebanken.tiamat",
        excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TiamatSecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = FintrafficSecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ApplicationContextProvider.class)
})
@Import(FintrafficTestContextConfiguration.class)
public class FintrafficTiamatTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(FintrafficTiamatTestApplication.class, args);
    }
}
