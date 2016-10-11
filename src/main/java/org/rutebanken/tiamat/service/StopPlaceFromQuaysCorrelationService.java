package org.rutebanken.tiamat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vividsolutions.jts.algorithm.CentroidPoint;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.nvdb.model.VegObjekt;
import org.rutebanken.tiamat.nvdb.service.NvdbQuayAugmenter;
import org.rutebanken.tiamat.nvdb.service.NvdbSearchService;
import org.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Service for generating test data by correlating quays already stored, and relate them to new stop places.
 */
@Service
@Transactional
public class StopPlaceFromQuaysCorrelationService {
    private static final Logger logger = LoggerFactory.getLogger(StopPlaceFromQuaysCorrelationService.class);

    /**
     * The max distance for checking if two quays are nearby each other.
     * http://gis.stackexchange.com/questions/28799/what-is-the-unit-of-measurement-for-buffer-calculation
     * https://en.wikipedia.org/wiki/Decimal_degrees
     */
    public static final double DISTANCE = 0.008;

    private final QuayRepository quayRepository;

    private final StopPlaceRepository stopPlaceRepository;

    private final AtomicInteger stopPlaceCounter = new AtomicInteger();

    private final GeometryFactory geometryFactory;

    private final CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;

    private final NvdbSearchService nvdbSearchService;

    private final NvdbQuayAugmenter nvdbQuayAugmenter;

    private int maxLimit;

    private int threads;

    @Autowired
    public StopPlaceFromQuaysCorrelationService(QuayRepository quayRepository,
                                                StopPlaceRepository stopPlaceRepository,
                                                GeometryFactory geometryFactory,
                                                CountyAndMunicipalityLookupService countyAndMunicipalityLookupService,
                                                NvdbSearchService nvdbSearchService, NvdbQuayAugmenter nvdbQuayAugmenter,
                                                @Value("${StopPlaceFromQuaysCorrelationService.maxLimit:1000000}") int maxLimit,
                                                @Value("${StopPlaceFromQuaysCorrelationService.threads:20}") int threads) {
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
        this.geometryFactory = geometryFactory;
        this.countyAndMunicipalityLookupService = countyAndMunicipalityLookupService;
        this.nvdbSearchService = nvdbSearchService;
        this.nvdbQuayAugmenter = nvdbQuayAugmenter;
        this.maxLimit = maxLimit;
        this.threads = threads;
    }

    /**
     * Creates stopPlace objects based on quays by combining quays with the same
     * name and close location.
     *
     * Not the most elegant implementation, but this code is only used to generate test data.
     */
    public void correlate() throws InterruptedException, ExecutionException {

        logger.info("Loading quays from repository");
        List<Quay> quays = quayRepository.findAll();

        logger.trace("Got {} quays", quays.size());

        ConcurrentMap<String, List<Quay>> distinctQuays = quays.parallelStream()
                .collect(Collectors.groupingByConcurrent(quay -> quay.getName().getValue()));

        logger.info("Got {} distinct quays based on name", distinctQuays.size());

        List<Long> quaysAlreadyProcessed = Collections.synchronizedList(new ArrayList<>());

        boolean stop = createStopPlaceFromQuays(distinctQuays, quaysAlreadyProcessed);
        if (stop) {
            logNumberOfStopPlaces();
            return;
        }

        int maxRemainingRuns = 10;
        for(int i = 0; i < maxRemainingRuns; i++) {
            Map<String, List<Quay>> remaining = findRemaining(distinctQuays, quaysAlreadyProcessed);

            if(remaining.isEmpty()) {
                logger.info("No remaining groups. Stopping.");
                break;
            }

            logger.info("Rerunning through {} groups with remaining quays", remaining.size());
            stop = createStopPlaceFromQuays(distinctQuays, quaysAlreadyProcessed);
            if (stop) {
                logNumberOfStopPlaces();
                return;
            }
        }

        logNumberOfStopPlaces();

    }

    public void logNumberOfStopPlaces() {
        logger.info("Created {} stop places.", stopPlaceCounter.get());

    }


    /**
     * @return true if max limit has been reached.
     */
    public boolean createStopPlaceFromQuays(Map<String, List<Quay>> distinctQuays,
                                            List<Long> quaysAlreadyProcessed) throws InterruptedException, ExecutionException {
/*
        return distinctQuays.keySet()
                .parallelStream()
                .map(quayGroupName -> createStopPlaceFromQuays(distinctQuays.get(quayGroupName), quayGroupName, quaysAlreadyProcessed))
                .anyMatch(stop -> stop);
*/

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CompletionService<Boolean> completionService =
                new ExecutorCompletionService<>(executor);

        int tasks = 0;

        for (String quayGroupName : distinctQuays.keySet()) {
            completionService.submit(() -> createStopPlaceFromQuays(distinctQuays.get(quayGroupName), quayGroupName, quaysAlreadyProcessed));
            tasks++;
        }

        int received = 0;

        while (received < tasks) {
            Future<Boolean> futureBoolean = completionService.take();
            received++;
            if (futureBoolean.get()) {

                executor.shutdownNow();
                // True means stop
                return true;
            }
        }

        executor.shutdown();
        executor.awaitTermination(20, TimeUnit.SECONDS);
        return false;
    }


    public Map<String, List<Quay>> findRemaining(ConcurrentMap<String, List<Quay>> distinctQuays,
                                                 List<Long> quaysAlreadyProcessed) {

        Map<String, List<Quay>> remaining = new HashMap<>();

        for(String group : distinctQuays.keySet()) {
            distinctQuays.get(group).stream()
                    .filter(quay -> !quaysAlreadyProcessed.contains(quay.getId()))
                    .forEach(quay -> {

                        List<Quay> list = remaining.get(group);

                        if (list == null) {
                            remaining.put(group, new ArrayList<>(Arrays.asList(quay)));
                        } else {
                            list.add(quay);
                        }
                    });
        }
        return remaining;
    }

    public boolean createStopPlaceFromQuays(List<Quay> quays, String quayGroupName, List<Long> quaysAlreadyProcessed) {
        logger.trace("Processing quay with name {}", quayGroupName);

        if (stopPlaceCounter.get() >= maxLimit) {
            logger.info("stopPlaceCounter: {}, maxLimit: {}. Stopping", stopPlaceCounter, maxLimit);
            return true;
        }

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString(quayGroupName, "no", ""));

        stopPlace.setQuays(new ArrayList<>());

        quays.forEach(quay -> {

            boolean addQuay = false;

            if (quaysAlreadyProcessed.contains(quay.getId())) {

                logger.debug("Already created quay with name {} and id {}", quay.getName(), quay.getId());

            } else if (stopPlace.getQuays().isEmpty()) {

                logger.debug("There are no quays related to stop place {} yet. Will add quay.", stopPlace.getName());
                addQuay = true;

            } else if (quayIsCloseToExistingQuays(quay, stopPlace.getQuays())) {

                logger.debug("Quay {}, {} is close enough to be added",
                        quay.getName(),
                        quay.getCentroid().getLocation().getGeometryPoint().toText());
                addQuay = true;
            } else {
                logger.debug("Ignoring (for now) quay {} {}", quay.getName(), quay.getCentroid().getLocation().getGeometryPoint().toText());
            }

            if (addQuay) {
                logger.trace("About to add Quay with name {} and id {} to stop place", quay.getName(), quay.getId());

                try {
                    VegObjekt vegObjekt = nvdbSearchService.search(quay.getName().getValue(), createEnvelopeForQuay(quay));
                    if(vegObjekt != null) {
                        quay = nvdbQuayAugmenter.augmentFromNvdb(quay, vegObjekt);
                        if (quay.getQuayType() != null && stopPlace.getStopPlaceType() == null) {
                            if (quay.getQuayType().equals(QuayTypeEnumeration.BUS_BAY) || quay.getQuayType().equals(QuayTypeEnumeration.BUS_STOP)) {
                                stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
                            }
                        }
                    }
                } catch (JsonProcessingException | UnsupportedEncodingException e) {
                    logger.warn("Exception caught using the NDVB search service... {}", e.getMessage(), e);
                }

                stopPlace.getQuays().add(quay);

                quaysAlreadyProcessed.add(quay.getId());

                quayRepository.save(quay);
            }

        });

        if (stopPlace.getQuays().isEmpty()) {
            logger.debug("No quays were added to stop place {} {}. Skipping...", stopPlace.getName(), stopPlace.getId());
        } else {

            stopPlace.setCentroid(new SimplePoint());
            stopPlace.getCentroid().setLocation(new LocationStructure());
            stopPlace.getCentroid().getLocation().setGeometryPoint(calculateCentroidForStopPlace(stopPlace.getQuays()));

            try {
                countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace);
            } catch (Exception e) {
                logger.warn("Error loading data from Pelias: {}", e.getMessage(), e);
            }

            try {
                stopPlaceRepository.save(stopPlace);
                logger.debug("Created stop place number {} with name {} and {} quays (id {})",
                        stopPlaceCounter.incrementAndGet(), stopPlace.getName(), stopPlace.getQuays().size(), stopPlace.getId());
                if (stopPlaceCounter.get() % 100 == 0) {
                    logger.info("Stop place {}", stopPlaceCounter.get());
                }
            } catch (Exception e) {
                logger.warn("Caught exception when creating stop place with name {}", quayGroupName, e);
            }
        }
        return false;
    }

    public boolean quayIsCloseToExistingQuays(Quay otherQuay, List<Quay> existingQuays) {
        return existingQuays.stream().allMatch(q -> areClose(otherQuay, q));
    }

    /**
     * Check if two quays are close, using the DISTANCE constant.
     * <p>
     * http://www.vividsolutions.com/jts/javadoc/com/vividsolutions/jts/geom/Geometry.html#buffer(double)
     */
    public boolean areClose(Quay quay, Quay otherQuay) {

        //// FIXME
        if (quay == null || otherQuay == null && quay.getCentroid() == null || otherQuay.getCentroid() == null) {
            return false;
        }
        Geometry buffer = quay.getCentroid().getLocation().getGeometryPoint().buffer(DISTANCE);
        boolean intersects = buffer.intersects(otherQuay.getCentroid().getLocation().getGeometryPoint());

        if (intersects) {
            logger.debug("Quay {} {} is close to quay {} {}",
                    quay.getName(),
                    quay.getCentroid().getLocation().getGeometryPoint().toText(),
                    otherQuay.getName(),
                    otherQuay.getCentroid().getLocation().getGeometryPoint().toText());
            return true;
        }

        logger.debug("Quay {} {} is NOT close to quay {} {}",
                quay.getName(),
                quay.getCentroid().getLocation().getGeometryPoint().toText(),
                otherQuay.getName(),
                otherQuay.getCentroid().getLocation().getGeometryPoint().toText());
        return false;
    }

    public Envelope createEnvelopeForQuay(Quay quay) {

        Geometry buffer = quay.getCentroid().getLocation().getGeometryPoint().buffer(0.004);

        Envelope envelope = buffer.getEnvelopeInternal();
        logger.trace("Created envelope {}", envelope.toString());

        return envelope;
    }

    public Point calculateCentroidForStopPlace(List<Quay> quays) {
        CentroidPoint centroidPoint = new CentroidPoint();
        quays.stream()
            .filter(quay -> quay.getCentroid() != null)
            .forEach(quay -> centroidPoint.add(quay.getCentroid().getLocation().getGeometryPoint()));

        logger.debug("Created centroid for stop place based on {} quays. x: {}, y: {}", quays.size(),
                centroidPoint.getCentroid().x, centroidPoint.getCentroid().y);

        return geometryFactory.createPoint(centroidPoint.getCentroid());
    }

}
