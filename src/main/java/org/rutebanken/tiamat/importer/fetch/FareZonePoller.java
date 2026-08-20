package org.rutebanken.tiamat.importer.fetch;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.auth.SystemSecurityContextService;
import org.rutebanken.tiamat.config.FareZonePollerConfig;
import org.rutebanken.tiamat.lock.PostgresAdvisoryLock;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Periodically pulls a NeTEx fare-zone feed from an external HTTP endpoint and applies it, so that
 * Tiamat acts as a replica of an external fare-zone master (see
 * {@code https://api.entur.io/distance/netex/fare-zones}).
 *
 * <p>The delivery is applied by {@link FareZoneSnapshotImporter}: an authoritative full replace of
 * the FareZones and GroupOfTariffZones, in one transaction, rejecting a delivery that carries
 * anything else. It deliberately does not go through the general publication delivery importer,
 * which would let unexpected feed content be written to the rest of the register.
 *
 * <p>The import has no end-user request behind it, so it runs as a trusted system import
 * ({@link SystemSecurityContextService}). Tiamat runs several replicas, so a poll is serialised
 * across them by a {@link PostgresAdvisoryLock} - a database lock rather than a Hazelcast one,
 * because the replicas do not form a Hazelcast cluster. The lock is taken before the fetch, so only
 * one replica pulls the feed per tick and a slow response cannot be applied on top of a newer
 * snapshot that another replica imported meanwhile. The SHA-256 of the last applied body is recorded
 * in the database alongside the data it describes, so an unchanged feed is not re-imported.
 *
 * <p>Disabled by default; enable with {@code netex.fareZonePoller.enabled=true}.
 */
@Component
public class FareZonePoller {

    private static final Logger logger = LoggerFactory.getLogger(FareZonePoller.class);

    public enum PollResult {IMPORTED, SKIPPED, FAILED, DISABLED}

    static final String LOCK_NAME = "fare-zone-poller";

    /**
     * Advisory lock id for {@link #LOCK_NAME}. Arbitrary but fixed: it is the identity of the lock
     * across replicas, and shows up as this number in {@code pg_locks}.
     */
    public static final long LOCK_KEY = 4_198_231_057L;

    private final FareZonePollerConfig config;
    private final PublicationDeliveryUnmarshaller unmarshaller;
    private final FareZoneSnapshotImporter fareZoneSnapshotImporter;
    private final SystemSecurityContextService systemSecurityContextService;
    private final PostgresAdvisoryLock postgresAdvisoryLock;
    private final BackgroundJobs backgroundJobs;
    private final HttpClient httpClient;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(runnable -> new Thread(runnable, "fare-zone-poller"));

    private final AtomicLong runCounter = new AtomicLong();

    public FareZonePoller(FareZonePollerConfig config,
                          PublicationDeliveryUnmarshaller unmarshaller,
                          FareZoneSnapshotImporter fareZoneSnapshotImporter,
                          SystemSecurityContextService systemSecurityContextService,
                          PostgresAdvisoryLock postgresAdvisoryLock,
                          BackgroundJobs backgroundJobs) {
        this.config = config;
        this.unmarshaller = unmarshaller;
        this.fareZoneSnapshotImporter = fareZoneSnapshotImporter;
        this.systemSecurityContextService = systemSecurityContextService;
        this.postgresAdvisoryLock = postgresAdvisoryLock;
        this.backgroundJobs = backgroundJobs;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(config.getConnectTimeout())
                .build();
    }

    @PostConstruct
    public void scheduleFareZonePolling() {
        if (!config.isEnabled()) {
            logger.info("FareZone poller disabled (netex.fareZonePoller.enabled=false)");
            return;
        }
        logger.info("Scheduling FareZone poller: url={}, initialDelay={}, interval={}",
                config.getUrl(), config.getInitialDelay(), config.getInterval());
        scheduler.scheduleWithFixedDelay(this::pollSafely,
                config.getInitialDelay().toSeconds(), config.getInterval().toSeconds(), TimeUnit.SECONDS);
    }

    private void pollSafely() {
        try {
            pollOnce();
        } catch (Throwable t) {
            // Never let an exception escape and cancel the recurring schedule.
            logger.error("Unexpected error during FareZone poll", t);
        }
    }

    /**
     * Fetch and apply the feed once, under the cluster lock. Safe to call manually (for example from
     * a test or an on-demand trigger). Serialised in-process, and across replicas by the lock.
     */
    public synchronized PollResult pollOnce() {
        if (!config.isEnabled()) {
            return PollResult.DISABLED;
        }
        long run = runCounter.incrementAndGet();

        return postgresAdvisoryLock.tryWithLock(LOCK_KEY, LOCK_NAME, () -> fetchAndImport(run))
                .orElseGet(() -> {
                    logger.info("FareZone poller lock held by another instance, skipping (run {})", run);
                    return PollResult.SKIPPED;
                });
    }

    private PollResult fetchAndImport(long run) {
        final byte[] body;
        final String hash;
        try {
            body = fetch();
            hash = sha256(body);
        } catch (Exception e) {
            logger.error("FareZone fetch failed on run {}: {}", run, e.getMessage(), e);
            return PollResult.FAILED;
        }

        if (hash.equals(fareZoneSnapshotImporter.lastImportedHash())) {
            logger.info("FareZone feed unchanged (hash {}), skipping import (run {})", shortHash(hash), run);
            return PollResult.SKIPPED;
        }

        final PublicationDeliveryStructure delivery;
        try (InputStream inputStream = new ByteArrayInputStream(body)) {
            delivery = unmarshaller.unmarshal(inputStream);
        } catch (Exception e) {
            logger.error("Failed to unmarshal FareZone feed on run {}: {}", run, e.getMessage(), e);
            return PollResult.FAILED;
        }

        try {
            // The hash is recorded in the same transaction as the snapshot, so a failure leaves the
            // previous hash in place and the next tick retries.
            systemSecurityContextService.runAsSystemImport(
                    () -> fareZoneSnapshotImporter.replaceSnapshot(delivery, hash));
        } catch (Exception e) {
            logger.error("FareZone import failed on run {}: {}", run, e.getMessage(), e);
            return PollResult.FAILED;
        }

        // Stop places reference fare zones, so they are repopulated after the snapshot is committed.
        backgroundJobs.triggerStopPlaceUpdate();

        logger.info("Imported FareZone feed ({} bytes, hash {}) on run {}", body.length, shortHash(hash), run);
        return PollResult.IMPORTED;
    }

    private byte[] fetch() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getUrl()))
                .timeout(config.getRequestTimeout())
                .header("Accept", "application/xml")
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Unexpected HTTP status " + response.statusCode() + " fetching " + config.getUrl());
        }
        byte[] body = response.body();
        if (body == null || body.length == 0) {
            throw new IllegalStateException("Empty response body fetching " + config.getUrl());
        }
        return body;
    }

    private static String sha256(byte[] body) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(body));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String shortHash(String hash) {
        return hash.length() > 12 ? hash.substring(0, 12) : hash;
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
