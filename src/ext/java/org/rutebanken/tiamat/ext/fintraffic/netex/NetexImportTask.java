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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
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
    static final String ENV_DISABLE_PRE_POST_PROCESSING = "NETEX_DISABLE_PRE_POST_PROCESSING";
    static final String STATUS_S3_KEY = "netex/processing/status";
    static final String STATUS_DONE = "done";
    static final String STATUS_FAILED = "failed";

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
        final ImportConfig config;
        try {
            config = readConfig();
        } catch (IllegalStateException e) {
            exitHandler.accept(1);
            return;
        }

        logger.info("Starting NeTEx import: key={}, importType={}, disablePrePostProcessing={}, source={}",
                config.s3Key(), config.importType(), config.disablePrePostProcessing(),
                config.localFile() ? "local file" : "S3");

        int exitCode = 1;
        Instant start = Instant.now();
        try {
            InputStream inputStream = openSource(config.s3Key(), config.localFile());
            PublicationDeliveryStructure delivery = unmarshal(inputStream);
            runImport(delivery, config.importType(), config.disablePrePostProcessing());
            logSuccess(config.s3Key(), config.importType(), Duration.between(start, Instant.now()));
            writeStatus(STATUS_DONE, config.localFile());
            exitCode = 0;
        } catch (Exception e) {
            logFailure(config.s3Key(), config.importType(), Duration.between(start, Instant.now()), e);
            writeStatus(STATUS_FAILED, config.localFile());
        } finally {
            logger.info("Shutting down application...");
            exitHandler.accept(exitCode);
        }
    }

    private ImportConfig readConfig() {
        String s3Key = requireEnv(ENV_S3_KEY);
        ImportType importType = requireImportType(requireEnv(ENV_IMPORT_TYPE));
        boolean disablePrePostProcessing = "true".equalsIgnoreCase(requireEnv(ENV_DISABLE_PRE_POST_PROCESSING));
        return new ImportConfig(s3Key, importType, disablePrePostProcessing, isLocalPath(s3Key));
    }

    private String requireEnv(String name) {
        String value = getenv(name);
        if (value == null || value.isBlank()) {
            logger.error("Environment variable {} is required but not set. Aborting.", name);
            throw new IllegalStateException("Missing required environment variable: " + name);
        }
        return value;
    }

    private static ImportType requireImportType(String value) {
        ImportType type = resolveImportType(value);
        if (type == null) {
            logger.error("Unknown import type '{}'. Valid values: {}. Aborting.", value, Arrays.toString(ImportType.values()));
            throw new IllegalStateException("Invalid import type: " + value);
        }
        return type;
    }

    private record ImportConfig(String s3Key, ImportType importType, boolean disablePrePostProcessing, boolean localFile) {}

    private InputStream openSource(String s3Key, boolean localFile) throws Exception {
        if (localFile) {
            String filePath = s3Key.startsWith("file://") ? s3Key.substring("file://".length()) : s3Key;
            logger.info("Opening local file {}...", filePath);
            if (!Files.exists(Paths.get(filePath))) {
                throw new IllegalStateException("Local file not found: " + filePath);
            }
            return new FileInputStream(filePath);
        } else {
            logger.info("Downloading {} from S3...", s3Key);
            InputStream stream = blobStoreService.download(s3Key);
            if (stream == null) {
                throw new IllegalStateException("S3 object not found: " + s3Key);
            }
            return stream;
        }
    }

    private PublicationDeliveryStructure unmarshal(InputStream inputStream) throws Exception {
        logger.info("Unmarshalling NeTEx XML...");
        return unmarshaller.unmarshal(inputStream);
    }

    private void runImport(PublicationDeliveryStructure delivery, ImportType importType,
                           boolean disablePrePostProcessing) throws Exception {
        ImportParams params = new ImportParams();
        params.importType = importType;
        params.disablePreAndPostProcessing = disablePrePostProcessing;
        logger.info("Running import (importType={}, disablePrePostProcessing={})...",
                importType, disablePrePostProcessing);
        importer.importPublicationDelivery(delivery, params);
    }

    private static void logSuccess(String s3Key, ImportType importType, Duration elapsed) {
        logger.info("""
            ════════════════════════════════════════════════════
            NeTEx import completed successfully
            ────────────────────────────────────────────────────
            S3 key:      {}
            Import type: {}
            Duration:    {}
            ════════════════════════════════════════════════════
            """, s3Key, importType, formatDuration(elapsed));
    }

    private static void logFailure(String s3Key, ImportType importType, Duration elapsed, Exception e) {
        logger.error("""
            ════════════════════════════════════════════════════
            NeTEx import FAILED after {}
            ────────────────────────────────────────────────────
            S3 key:      {}
            Import type: {}
            ════════════════════════════════════════════════════
            """, formatDuration(elapsed), s3Key, importType, e);
    }

    private void writeStatus(String status, boolean localFile) {
        if (localFile) {
            return;
        }
        try {
            byte[] bytes = status.getBytes(StandardCharsets.UTF_8);
            blobStoreService.upload(STATUS_S3_KEY, new ByteArrayInputStream(bytes));
            logger.info("Wrote status '{}' to {}", status, STATUS_S3_KEY);
        } catch (Exception e) {
            logger.error("Failed to write status '{}' to {}", status, STATUS_S3_KEY, e);
        }
    }

    static boolean isLocalPath(String key) {
        return key.startsWith("/") || key.startsWith("./") || key.startsWith("../") || key.startsWith("file://");
    }

    private static ImportType resolveImportType(String value) {
        try {
            return ImportType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
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
