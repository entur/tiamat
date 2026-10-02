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
import org.rutebanken.tiamat.importer.PublicationDeliveryFareFrameImporter;
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
 * Fetches farezone-resolver's fare zone register artifact ({@code _stops_farezones.xml}) from GCS and
 * imports it through the ordinary FareFrame import path. The artifact carries the full register - zone
 * definitions, geometry and resolved members - so Tiamat holds fare zones without papsukkal delivering
 * them from distance-api, and membership falls out of the existing explicit-member derivation because
 * every zone arrives as explicitStops with resolved members.
 *
 * <p>Enabled by {@code fareZone.register.import.enabled=true}, which also makes the fare zone import run
 * as a replica (update by netexId, orphan cleanup) - a separate switch from the legacy distance-api/
 * papsukkal {@code fareZone.externalVersioning} flag, see {@link org.rutebanken.tiamat.config.FareZoneConfig}.
 * Inert unless a bucket is also configured, so a local run without GCS does nothing.
 *
 * <p>Fare zones are open data and this runs off any request, so it carries no principal. The register
 * import therefore expects {@code authorization.enabled=false} on this replica; with authorization on,
 * the import would need a trusted security context, which is deliberately not added for open data.
 *
 * <p>Deployment sets {@code fareZone.register.import.enabled=true} and {@code fareZone.membership.artifact
 * .bucket} to the resolver's bucket per environment - {@code ror-farezone-resolver-dev|tst|production}
 * (note prd uses {@code production}). {@code projectId} may stay blank (the object is addressed by bucket
 * name); the Tiamat workload SA is granted read on the bucket in the resolver's terraform.
 *
 * <p>Self-scheduled on its own executor rather than through BackgroundJobs, to avoid a cycle: the
 * FareFrame importer already depends on BackgroundJobs to trigger the ref update after import.
 */
@Component
public class FareZoneMembershipArtifactImporter {

    private static final Logger logger = LoggerFactory.getLogger(FareZoneMembershipArtifactImporter.class);

    private final PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller;
    private final PublicationDeliveryFareFrameImporter fareFrameImporter;
    private final FareZoneConfig fareZoneConfig;

    private final String bucketName;
    private final String objectName;
    private final String projectId;
    // Blank in every real environment (ADC). A fake-gcs URL in a local rig, since google-cloud-storage
    // does not read STORAGE_EMULATOR_HOST; the client then authenticates with no credentials.
    private final String host;
    private final long initialDelaySeconds;
    private final long intervalSeconds;

    private ScheduledExecutorService executor;

    public FareZoneMembershipArtifactImporter(PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller,
                                              PublicationDeliveryFareFrameImporter fareFrameImporter,
                                              FareZoneConfig fareZoneConfig,
                                              @Value("${fareZone.membership.artifact.bucket:}") String bucketName,
                                              @Value("${fareZone.membership.artifact.objectName:_stops_farezones.xml}") String objectName,
                                              @Value("${fareZone.membership.artifact.projectId:}") String projectId,
                                              @Value("${fareZone.membership.artifact.host:}") String host,
                                              @Value("${fareZone.membership.artifact.import.initialDelaySeconds:60}") long initialDelaySeconds,
                                              @Value("${fareZone.membership.artifact.import.intervalSeconds:43200}") long intervalSeconds) {
        this.publicationDeliveryUnmarshaller = publicationDeliveryUnmarshaller;
        this.fareFrameImporter = fareFrameImporter;
        this.fareZoneConfig = fareZoneConfig;
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.projectId = projectId;
        this.host = host;
        this.initialDelaySeconds = initialDelaySeconds;
        this.intervalSeconds = intervalSeconds;
    }

    public boolean isConfigured() {
        return StringUtils.isNotBlank(bucketName);
    }

    @PostConstruct
    void scheduleImport() {
        if (!fareZoneConfig.isRegisterImportEnabled()) {
            logger.info("Fare zone register import disabled (fareZone.register.import.enabled=false)");
            return;
        }
        if (!isConfigured()) {
            logger.warn("Fare zone register import enabled but no bucket configured; import disabled");
            return;
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
            importArtifact();
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
        fareFrameImporter.importPublicationDelivery(artifact);
    }

    // Real ADC via BlobStoreHelper unless a host is set (fake-gcs in a local rig; see the host field).
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
