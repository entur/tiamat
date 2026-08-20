package org.rutebanken.tiamat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Settings for {@link org.rutebanken.tiamat.importer.fetch.FareZonePoller}. Grouped here so the
 * poller does not carry a long list of {@code @Value} constructor parameters.
 *
 * <p>There is deliberately no import-type or versioning setting: the poller applies the feed through
 * {@link org.rutebanken.tiamat.importer.fetch.FareZoneSnapshotImporter}, which is always an
 * authoritative full replace. Combinations that silently did nothing while still recording the feed
 * as imported (an ID_MATCH import, or polling with external versioning switched off) are therefore
 * not expressible.
 */
@Component
public class FareZonePollerConfig {

    private final boolean enabled;
    private final String url;
    private final Duration initialDelay;
    private final Duration interval;
    private final Duration connectTimeout;
    private final Duration requestTimeout;

    public FareZonePollerConfig(
            @Value("${netex.fareZonePoller.enabled:false}") boolean enabled,
            @Value("${netex.fareZonePoller.url:https://api.entur.io/distance/netex/fare-zones}") String url,
            @Value("${netex.fareZonePoller.initialDelaySeconds:60}") long initialDelaySeconds,
            @Value("${netex.fareZonePoller.intervalSeconds:3600}") long intervalSeconds,
            @Value("${netex.fareZonePoller.connectTimeoutSeconds:30}") long connectTimeoutSeconds,
            @Value("${netex.fareZonePoller.requestTimeoutSeconds:180}") long requestTimeoutSeconds) {
        this.enabled = enabled;
        this.url = url;
        this.initialDelay = Duration.ofSeconds(initialDelaySeconds);
        this.interval = Duration.ofSeconds(intervalSeconds);
        this.connectTimeout = Duration.ofSeconds(connectTimeoutSeconds);
        this.requestTimeout = Duration.ofSeconds(requestTimeoutSeconds);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getUrl() {
        return url;
    }

    public Duration getInitialDelay() {
        return initialDelay;
    }

    public Duration getInterval() {
        return interval;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }
}
