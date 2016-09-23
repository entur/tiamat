package no.rutebanken.tiamat.importers;

import no.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import no.rutebanken.tiamat.model.SiteFrame;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SiteFrameImporter {

    private static final Logger logger = LoggerFactory.getLogger(SiteFrameImporter.class);

    private TopographicPlaceCreator topographicPlaceCreator;

    @Autowired
    public SiteFrameImporter(TopographicPlaceCreator topographicPlaceCreator) {
        this.topographicPlaceCreator = topographicPlaceCreator;
    }

    public SiteFrame importSiteFrame(SiteFrame siteFrame, StopPlaceImporter stopPlaceImporter) {
        long startTime = System.currentTimeMillis();
        AtomicInteger stopPlacesCreated = new AtomicInteger(0);
        AtomicInteger topographicPlacesCreated = new AtomicInteger(0);

        logger.info("Received site frame for import. It contains {} topographical places and {} stop places. Starting import,",
                siteFrame.getTopographicPlaces() != null ? siteFrame.getTopographicPlaces().getTopographicPlace().size() : 0,
                siteFrame.getStopPlaces().getStopPlace().size());

        Timer timer = new Timer(this.getClass().getName()+"-logger");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                logStatus(stopPlacesCreated, startTime, siteFrame, topographicPlacesCreated);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 2000, 2000);
        List<StopPlace> createdStopPlaces = new CopyOnWriteArrayList<>();
        siteFrame.getStopPlaces().getStopPlace()
                .parallelStream()
                .forEach(stopPlace -> {
                    try {
                        StopPlace created = stopPlaceImporter.importStopPlace(stopPlace, siteFrame, topographicPlacesCreated);
                        createdStopPlaces.add(created);
                        stopPlacesCreated.incrementAndGet();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
        timerTask.cancel();
        siteFrame.getStopPlaces().getStopPlace().clear();
        siteFrame.getStopPlaces().getStopPlace().addAll(createdStopPlaces);

        logger.info("Saved {} topographical places and {} stop places", topographicPlacesCreated, stopPlacesCreated);

        topographicPlaceCreator.invalidateCache(); // TODO: time eviction
        return siteFrame;
    }

    private void logStatus(AtomicInteger stopPlacesCreated, long startTime, SiteFrame siteFrame, AtomicInteger topographicPlacesCreated) {
        long duration = System.currentTimeMillis() - startTime;
        if (stopPlacesCreated.get() % 100 == 0 || duration % 1000 == 0) {
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
}
