package org.rutebanken.tiamat.importer.log;

import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ImportLoggerTask extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(ImportLoggerTask.class);

    private final AtomicInteger stopPlacesCreated;
    private long startTime;
    private final int totalStopPlaces;
    private final AtomicInteger topographicPlacesCreated;
    private final String correlationId;

    public ImportLoggerTask(AtomicInteger stopPlacesCreatedOrUpdated, int totalStopPlaces,
                            AtomicInteger topographicPlacesCreated, String correlationId) {

        this.stopPlacesCreated = stopPlacesCreatedOrUpdated;
        this.startTime = System.currentTimeMillis();
        this.totalStopPlaces = totalStopPlaces;
        this.topographicPlacesCreated = topographicPlacesCreated;
        this.correlationId = correlationId;
    }

    @Override
    public void run() {
        if(stopPlacesCreated.get() == 0) {
            startTime = System.currentTimeMillis();
        }

        long duration = System.currentTimeMillis() - startTime;

        MDC.put(PublicationDeliveryImporter.IMPORT_CORRELATION_ID, correlationId);
        String stopPlacesPerSecond = "NA";

        if(duration >= 1000) {

            stopPlacesPerSecond = String.valueOf(stopPlacesCreated.get() / (duration / 1000f));
        }
        logger.info("Stop place {}/{} - {}% - {} spl/sec - {} topographic places", stopPlacesCreated.get(),
                totalStopPlaces,
                (stopPlacesCreated.get() * 100f) / totalStopPlaces,
                stopPlacesPerSecond,
                topographicPlacesCreated);
    }
}
