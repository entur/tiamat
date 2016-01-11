package no.rutebanken.tiamat.config;

import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.gtfs.GtfsStopsReader;
import no.rutebanken.tiamat.nvdb.service.NvdbSync;
import no.rutebanken.tiamat.service.StopPlaceFromQuaysCorrelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create some example data.
 */
@Configuration
@Profile("bootstrap")
public class BootStrap implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(BootStrap.class);

    @Autowired
    private NvdbSync nvdbSync;

    @Autowired
    private GtfsStopsReader gtfsStopsReader;

    @Autowired
    private GeometryFactory geometryFactory;
    
    @Autowired
    private StopPlaceFromQuaysCorrelationService stopPlaceFromQuaysCorrelationService;

    /**
     * Set up test data.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
//       nvdbSync.fetchNvdb();

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(() -> gtfsStopsReader.read());
        es.execute(() -> stopPlaceFromQuaysCorrelationService.correlate()); 
    }
}
