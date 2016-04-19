package no.rutebanken.tiamat.rest.ifopt.siteframe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.SiteFrame;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SiteFrameImporter {

    private static final Logger logger = LoggerFactory.getLogger(SiteFrameImporter.class);

    private StopPlaceImporter stopPlaceImporter;
    private TopographicPlaceCreator topographicPlaceCreator;

    @Autowired
    public SiteFrameImporter(StopPlaceImporter stopPlaceImporter, TopographicPlaceCreator topographicPlaceCreator) {
        this.stopPlaceImporter = stopPlaceImporter;
        this.topographicPlaceCreator = topographicPlaceCreator;
    }


    public String importSiteFrame(SiteFrame siteFrame) {
        long startTime = System.currentTimeMillis();
        AtomicInteger stopPlacesCreated = new AtomicInteger(0);
        AtomicInteger topographicPlacesCreated = new AtomicInteger(0);

        logger.info("Received site frame for import. It contains {} topographical places and {} stop places. Starting import,",
                siteFrame.getTopographicPlaces().getTopographicPlace().size(),
                siteFrame.getStopPlaces().getStopPlace().size());

        siteFrame.getStopPlaces().getStopPlace()
                .parallelStream()
                .forEach(stopPlace -> {
                    try {
                        stopPlaceImporter.importStopPlace(stopPlace, siteFrame, topographicPlacesCreated);
                        stopPlacesCreated.incrementAndGet();
                        logStatus(stopPlacesCreated, startTime, siteFrame, topographicPlacesCreated);
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });

        String returnString = "Saved " + topographicPlacesCreated
                + " topographical places and " + stopPlacesCreated + " stop places";

        logger.info(returnString);

        topographicPlaceCreator.invalidateCache();
        return returnString;
    }

    private void logStatus(AtomicInteger stopPlacesCreated, long startTime, SiteFrame siteFrame, AtomicInteger topographicPlacesCreated) {
        if (stopPlacesCreated.get() % 100 == 0) {
            String stopPlacesPerSecond = "NA";
            long duration = System.currentTimeMillis() - startTime;
            if(duration >= 1000) {

                stopPlacesPerSecond = String.valueOf(stopPlacesCreated.get() / (duration / 1000));
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
