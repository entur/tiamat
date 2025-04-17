package org.rutebanken.tiamat.changelog;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * This configuration disables conflicting autoconfiguration used by other variants of the changelog feature.
 */
@Configuration
@EnableAutoConfiguration(exclude = {JmsAutoConfiguration.class, ActiveMQAutoConfiguration.class})
@Profile("local-changelog")
public class LocalChangelogConfiguration {
    // intentionally left blank
}