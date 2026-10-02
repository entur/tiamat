package org.rutebanken.tiamat.service.farezone;

import com.google.cloud.NoCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.rutebanken.helper.gcp.BlobStoreHelper;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.config.FareZoneConfig;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.PublicationDeliveryFareFrameImporter;
import org.rutebanken.tiamat.lock.LockException;
import org.rutebanken.tiamat.lock.TimeoutMaxLeaseTimeLock;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Imports the fare zone register artifact from GCS as a replica (update by netexId, prune
 * orphans). Enabled by {@code fareZone.register.import.enabled} with a bucket set. A system job with no
 * user, so the save skips the user edit check.
 *
 * <p>Self-scheduled rather than via BackgroundJobs, which the FareFrame importer already depends on. Every
 * pod schedules it, so the import runs under a cluster lock and the other pods skip the run.
 */
@Component
public class FareZoneMembershipArtifactImporter {

    private static final Logger logger = LoggerFactory.getLogger(FareZoneMembershipArtifactImporter.class);

    static final String IMPORT_LOCK = "farezone-register-import-lock";
    // Skip the run if another pod is already importing, rather than queue behind it.
    private static final int LOCK_WAIT_SECONDS = 5;
    // Not enforced by TimeoutMaxLeaseTimeLock; the GCS client's 60s I/O timeouts bound the run instead.
    private static final int LOCK_MAX_LEASE_SECONDS = 1800;

    private final PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller;
    private final PublicationDeliveryFareFrameImporter fareFrameImporter;
    private final FareZoneConfig fareZoneConfig;
    private final TimeoutMaxLeaseTimeLock lock;

    private final String bucketName;
    private final String objectName;
    private final String projectId;
    // Storage endpoint override, e.g. a GCS emulator; google-cloud-storage does not read STORAGE_EMULATOR_HOST.
    private final String host;
    private final long initialDelaySeconds;
    private final long intervalSeconds;

    private ScheduledExecutorService executor;

    public FareZoneMembershipArtifactImporter(PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller,
                                              PublicationDeliveryFareFrameImporter fareFrameImporter,
                                              FareZoneConfig fareZoneConfig,
                                              TimeoutMaxLeaseTimeLock lock,
                                              @Value("${fareZone.register.import.bucket:}") String bucketName,
                                              @Value("${fareZone.register.import.objectName:_stops_farezones.xml}") String objectName,
                                              @Value("${fareZone.register.import.projectId:}") String projectId,
                                              @Value("${fareZone.register.import.host:}") String host,
                                              @Value("${fareZone.register.import.initialDelaySeconds:60}") long initialDelaySeconds,
                                              @Value("${fareZone.register.import.intervalSeconds:43200}") long intervalSeconds) {
        this.publicationDeliveryUnmarshaller = publicationDeliveryUnmarshaller;
        this.fareFrameImporter = fareFrameImporter;
        this.fareZoneConfig = fareZoneConfig;
        this.lock = lock;
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.projectId = projectId;
        this.host = host;
        this.initialDelaySeconds = initialDelaySeconds;
        this.intervalSeconds = intervalSeconds;
    }

    @PostConstruct
    void scheduleImport() {
        if (!fareZoneConfig.isRegisterImportEnabled()) {
            logger.info("Fare zone register import disabled (fareZone.register.import.enabled=false)");
            return;
        }
        if (StringUtils.isBlank(bucketName)) {
            logger.warn("Fare zone register import enabled but no bucket configured; import disabled");
            return;
        }
        if (fareZoneConfig.isExternalVersioning()) {
            logger.warn("Both fareZone.register.import.enabled and fareZone.externalVersioning are set; "
                    + "a FareFrame POST will also prune zones absent from it, replacing the register until the next import");
        }
        executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "farezone-artifact-importer"));
        executor.scheduleAtFixedRate(this::importArtifactSafely, initialDelaySeconds, intervalSeconds, TimeUnit.SECONDS);
        logger.info("Scheduled fare zone register artifact import of {} from bucket {} every {} s",
                objectName, bucketName, intervalSeconds);
    }

    @PreDestroy
    void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    private void importArtifactSafely() {
        try {
            // Runs on the lock-holding thread, so the lock covers the whole destructive import.
            lock.executeInLock(() -> {
                try {
                    importArtifact();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }, IMPORT_LOCK, LOCK_WAIT_SECONDS, LOCK_MAX_LEASE_SECONDS);
        } catch (LockException e) {
            logger.info("Another instance holds {}; skipping this fare zone register import run", IMPORT_LOCK);
        } catch (Exception e) {
            // A scheduled task that throws stops repeating, so failures are swallowed and logged.
            logger.error("Could not import fare zone register artifact {} from bucket {}; previous zones kept",
                    objectName, bucketName, e);
        }
    }

    private void importArtifact() throws Exception {
        InputStream in = BlobStoreHelper.getBlob(storage(), bucketName, objectName);
        if (in == null) {
            logger.warn("Fare zone register artifact {} not found in bucket {}; previous zones kept", objectName, bucketName);
            return;
        }
        try (in) {
            importFrom(in);
            logger.info("Imported fare zone register artifact {} from bucket {}", objectName, bucketName);
        }
    }

    /** Unmarshal and import the artifact from a stream. Package-visible for testing without GCS. */
    void importFrom(InputStream in) throws Exception {
        PublicationDeliveryStructure artifact = publicationDeliveryUnmarshaller.unmarshal(in);
        ImportParams params = new ImportParams();
        params.fareZoneRegisterReplica = true;
        fareFrameImporter.importPublicationDelivery(artifact, params);
    }

    private Storage storage() {
        if (StringUtils.isBlank(host)) {
            return BlobStoreHelper.getStorage(projectId);
        }
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setHost(host)
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService();
    }
}
