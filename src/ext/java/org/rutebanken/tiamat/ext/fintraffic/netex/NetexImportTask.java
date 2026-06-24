package org.rutebanken.tiamat.ext.fintraffic.netex;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.function.IntConsumer;

/**
 * ApplicationRunner that imports a single NeTEx file from S3 into Tiamat.
 * <p>
 * Reads the S3 object key from the {@code NETEX_S3_KEY} environment variable and the
 * optional import type from {@code NETEX_IMPORT_TYPE} (default: {@code INITIAL}).
 * Downloads the file via {@link BlobStoreService}, unmarshals it, and calls
 * {@link PublicationDeliveryImporter} directly — no HTTP, no Trivore authentication.
 * <p>
 * Must be activated via the {@code fintraffic-netex-import-task} Spring profile, which sets
 * {@code authorization.enabled=false} and {@code spring.main.web-application-type=none}.
 * Shuts down the application when complete.
 */
public class NetexImportTask implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(NetexImportTask.class);

    static final String ENV_S3_KEY = "NETEX_S3_KEY";
    static final String ENV_IMPORT_TYPE = "NETEX_IMPORT_TYPE";

    private final BlobStoreService blobStoreService;
    private final PublicationDeliveryUnmarshaller unmarshaller;
    private final PublicationDeliveryImporter importer;
    private final IntConsumer exitHandler;

    /** Production constructor — uses {@code System::exit}. */
    public NetexImportTask(
            BlobStoreService blobStoreService,
            PublicationDeliveryUnmarshaller unmarshaller,
            PublicationDeliveryImporter importer) {
        this(blobStoreService, unmarshaller, importer, System::exit);
    }

    /** Full constructor — {@code exitHandler} is injectable for testing. */
    NetexImportTask(
            BlobStoreService blobStoreService,
            PublicationDeliveryUnmarshaller unmarshaller,
            PublicationDeliveryImporter importer,
            IntConsumer exitHandler) {
        this.blobStoreService = blobStoreService;
        this.unmarshaller = unmarshaller;
        this.importer = importer;
        this.exitHandler = exitHandler;
    }

    @Override
    public void run(ApplicationArguments args) {
        String s3Key = getenv(ENV_S3_KEY);
        if (s3Key == null || s3Key.isBlank()) {
            logger.error("Environment variable {} is required but not set. Aborting.", ENV_S3_KEY);
            exitHandler.accept(1);
            return;
        }

        ImportType importType = resolveImportType(getenv(ENV_IMPORT_TYPE));
        logger.info("Starting NeTEx import: key={}, importType={}", s3Key, importType);
        Instant start = Instant.now();

        int exitCode = 1;
        try {
            logger.info("Downloading {} from S3...", s3Key);
            InputStream inputStream = blobStoreService.download(s3Key);
            if (inputStream == null) {
                throw new IllegalStateException("S3 object not found: " + s3Key);
            }

            logger.info("Unmarshalling NeTEx XML...");
            PublicationDeliveryStructure delivery = unmarshaller.unmarshal(inputStream);

            ImportParams params = new ImportParams();
            params.importType = importType;

            logger.info("Running import (importType={})...", importType);
            importer.importPublicationDelivery(delivery, params);

            Duration elapsed = Duration.between(start, Instant.now());
            logger.info("""
                ════════════════════════════════════════════════════
                NeTEx import completed successfully
                ────────────────────────────────────────────────────
                S3 key:      {}
                Import type: {}
                Duration:    {}
                ════════════════════════════════════════════════════
                """, s3Key, importType, formatDuration(elapsed));
            exitCode = 0;

        } catch (Exception e) {
            Duration elapsed = Duration.between(start, Instant.now());
            logger.error("""
                ════════════════════════════════════════════════════
                NeTEx import FAILED after {}
                ────────────────────────────────────────────────────
                S3 key:      {}
                Import type: {}
                ════════════════════════════════════════════════════
                """, formatDuration(elapsed), s3Key, importType, e);
        } finally {
            logger.info("Shutting down application...");
            exitHandler.accept(exitCode);
        }
    }

    private static ImportType resolveImportType(String value) {
        if (value == null || value.isBlank()) {
            return ImportType.INITIAL;
        }
        try {
            return ImportType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown import type '{}', defaulting to INITIAL", value);
            return ImportType.INITIAL;
        }
    }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        if (hours > 0) return String.format("%dh %dm %ds", hours, minutes, seconds);
        if (minutes > 0) return String.format("%dm %ds", minutes, seconds);
        return String.format("%ds", seconds);
    }

    /** Reads an environment variable. Overridable for testing. */
    protected String getenv(String name) {
        return System.getenv(name);
    }
}
