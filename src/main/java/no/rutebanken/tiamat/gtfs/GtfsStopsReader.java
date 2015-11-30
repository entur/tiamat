package no.rutebanken.tiamat.gtfs;


import org.onebusaway.gtfs.serialization.GtfsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Reads stops from gtfs csv file and stores it in the stop place model.
 */
@Service
public class GtfsStopsReader {
    private static Logger logger = LoggerFactory.getLogger(GtfsStopsReader.class);

    private static final String GTFS_FILE_PATH = "stops";

    @Autowired
    private GtfsStopEntityHandler gtfsStopEntityHandler;

    public void read() {


        logger.info("About to read GTFS entities from path '{}'", GTFS_FILE_PATH);

        GtfsReader reader = new GtfsReader();

        try {
            URL url = this.getClass().getClassLoader().getResource(GTFS_FILE_PATH);

            reader.setInputLocation(new File(url.getFile()));

            reader.addEntityHandler(gtfsStopEntityHandler);
            reader.run();


        } catch (IOException e) {
            logger.warn("Error reading gtfs file {}.", GTFS_FILE_PATH, e);
        }
    }
}
