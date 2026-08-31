package org.rutebanken.tiamat.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Enables method security for tests that need to exercise @PreAuthorize.
 * <p>
 * TiamatSecurityConfig is the only @EnableMethodSecurity in the application and is
 * @Profile("!test"), so method security is off throughout the test suite. This configuration is
 * gated behind its own profile rather than declared as a nested @TestConfiguration, because a
 * nested one is picked up by component scanning and turns method security on for every test
 * context in the JVM.
 */
@Configuration
@Profile(MethodSecurityTestConfig.PROFILE)
@EnableMethodSecurity
public class MethodSecurityTestConfig {

    public static final String PROFILE = "method-security";
}
