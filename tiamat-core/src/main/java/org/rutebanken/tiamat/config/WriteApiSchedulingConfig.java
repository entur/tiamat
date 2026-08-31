package org.rutebanken.tiamat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables scheduling for the write API's timeout sweeper.
 * <p>
 * Gated on the write API being enabled so that a deployment without it does not acquire a
 * scheduler it has no use for: nothing else in Tiamat is scheduled.
 */
@Configuration
@ConditionalOnProperty(name = "tiamat.write-api.enabled", havingValue = "true")
@EnableScheduling
public class WriteApiSchedulingConfig {
}
