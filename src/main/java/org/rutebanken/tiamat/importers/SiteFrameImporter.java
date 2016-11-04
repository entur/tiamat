package org.rutebanken.tiamat.importers;

import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netexmapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class SiteFrameImporter {

    private static final Logger logger = LoggerFactory.getLogger(SiteFrameImporter.class);

    private TopographicPlaceCreator topographicPlaceCreator;

    private NetexMapper netexMapper;

    @Autowired
    public SiteFrameImporter(TopographicPlaceCreator topographicPlaceCreator, NetexMapper netexMapper) {
        this.topographicPlaceCreator = topographicPlaceCreator;
        this.netexMapper = netexMapper;
    }

    public org.rutebanken.netex.model.SiteFrame importSiteFrame(SiteFrame siteFrame, StopPlaceImporter stopPlaceImporter) {
        long startTime = System.currentTimeMillis();
        AtomicInteger stopPlacesCreated = new AtomicInteger(0);
        AtomicInteger topographicPlacesCreated = new AtomicInteger(0);

        logger.info("Received site frame for import: {}", siteFrame);

        Timer timer = new Timer(this.getClass().getName()+"-logger");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                logStatus(stopPlacesCreated, startTime, siteFrame, topographicPlacesCreated);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 2000, 2000);

        try {
            org.rutebanken.netex.model.SiteFrame netexSiteFrame = new org.rutebanken.netex.model.SiteFrame();
            if(siteFrame.getStopPlaces() != null) {
                List<org.rutebanken.netex.model.StopPlace> createdStopPlaces = siteFrame.getStopPlaces().getStopPlace()
                        .stream()
                        .map(stopPlace ->
                                importStopPlace(stopPlaceImporter, stopPlace, siteFrame, topographicPlacesCreated, stopPlacesCreated)
                        )
                        .collect(Collectors.toList());

                logger.info("Saved {} topographical places and {} stop places", topographicPlacesCreated, stopPlacesCreated);

                topographicPlaceCreator.invalidateCache();
                netexSiteFrame.withStopPlaces(
                        new StopPlacesInFrame_RelStructure()
                                .withStopPlace(createdStopPlaces)
                );
            } else {
                logger.info("Site frame does not contain any stop places: ", siteFrame);
            }
            return netexSiteFrame;
        } finally {
            timerTask.cancel();
        }
    }

    private org.rutebanken.netex.model.StopPlace importStopPlace(StopPlaceImporter stopPlaceImporter, StopPlace stopPlace, SiteFrame siteFrame, AtomicInteger topographicPlacesCreated, AtomicInteger stopPlacesCreated) {
        try {
            StopPlace importedStopPlace = stopPlaceImporter.importStopPlace(stopPlace, siteFrame, topographicPlacesCreated);
            stopPlacesCreated.incrementAndGet();
            return netexMapper.mapToNetexModel(importedStopPlace);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void logStatus(AtomicInteger stopPlacesCreated, long startTime, SiteFrame siteFrame, AtomicInteger topographicPlacesCreated) {
        long duration = System.currentTimeMillis() - startTime;

        String stopPlacesPerSecond = "NA";

        if(duration >= 1000) {

            stopPlacesPerSecond = String.valueOf(stopPlacesCreated.get() / (duration / 1000f));
        }
        int total = siteFrame.getStopPlaces().getStopPlace().size();
        logger.info("Stop place {}/{} - {}% - {} spl/sec - {} topographic places", stopPlacesCreated.get(),
                siteFrame.getStopPlaces().getStopPlace().size(),
                (stopPlacesCreated.get() * 100f) / total,
                stopPlacesPerSecond,
                topographicPlacesCreated);

    }
}
