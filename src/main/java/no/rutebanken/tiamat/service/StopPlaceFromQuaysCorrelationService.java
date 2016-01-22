package no.rutebanken.tiamat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vividsolutions.jts.algorithm.CentroidPoint;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import no.rutebanken.tiamat.nvdb.model.VegObjekt;
import no.rutebanken.tiamat.nvdb.service.NvdbQuayAugmenter;
import no.rutebanken.tiamat.nvdb.service.NvdbSearchService;
import no.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.org.netex.netex.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Service for generating test data by correlating quays already stored, and relate them to new stop places.
 */
@Service
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

    @Autowired
    public StopPlaceFromQuaysCorrelationService(QuayRepository quayRepository,
                                                StopPlaceRepository stopPlaceRepository,
                                                GeometryFactory geometryFactory,
                                                CountyAndMunicipalityLookupService countyAndMunicipalityLookupService,
                                                NvdbSearchService nvdbSearchService, NvdbQuayAugmenter nvdbQuayAugmenter,
                                                @Value("${StopPlaceFromQuaysCorrelationService.maxLimit:10}") int maxLimit) {
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
        this.geometryFactory = geometryFactory;
        this.countyAndMunicipalityLookupService = countyAndMunicipalityLookupService;
        this.nvdbSearchService = nvdbSearchService;
        this.nvdbQuayAugmenter = nvdbQuayAugmenter;
        this.maxLimit = maxLimit;
    }

    /**
     * Creates stopPlace objects based on quays by combining quays with the same
     * name and close location.
     *
     * Not the most elegant implementation, but this code is only used to generate test data.
     */
    public void correlate() {

        logger.trace("Loading quays from repository");
        List<Quay> quays = quayRepository.findAll();

        logger.trace("Got {} quays", quays.size());

        ConcurrentMap<String, List<Quay>> distinctQuays = quays.stream()
                .collect(Collectors.groupingByConcurrent(quay -> quay.getName().getValue()));

        logger.trace("Got {} distinct quays based on name", distinctQuays.size());

        List<String> quaysAlreadyProcessed = new ArrayList<>();

        boolean stop = createStopPlaceFromQuays(distinctQuays, quaysAlreadyProcessed);
        if (stop) return;

        int maxRemainingRuns = 10;
        for(int i = 0; i < maxRemainingRuns; i++) {
            Map<String, List<Quay>> remaining = findRemaining(distinctQuays, quaysAlreadyProcessed);

            if(remaining.isEmpty()) {
                logger.info("No remaining groups. Stopping.");
                break;
            }

            logger.info("Rerunning through {} groups with remaining quays", remaining.size());
            stop = createStopPlaceFromQuays(distinctQuays, quaysAlreadyProcessed);
            if (stop) return;
        }


        logger.debug("Amount of created stop places: {}", stopPlaceCounter.get());
    }

    /**
     * @return true if max limit has been reached.
     */
    public boolean createStopPlaceFromQuays(Map<String, List<Quay>> distinctQuays,
                                         List<String> quaysAlreadyProcessed ) {
        for(String quayGroupName : distinctQuays.keySet()) {
            createStopPlaceFromQuays(distinctQuays.get(quayGroupName),
                    quayGroupName, quaysAlreadyProcessed);
            if(stopPlaceCounter.get() >= maxLimit) {
                logger.info("stopPlaceCounter: {}, maxLimit: {}. Stopping", stopPlaceCounter, maxLimit);
                return true;
            }
        }
        return false;

    }

    public Map<String, List<Quay>> findRemaining(ConcurrentMap<String, List<Quay>> distinctQuays,
                                                 List<String> quaysAlreadyProcessed) {

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

    public void createStopPlaceFromQuays(List<Quay> quays, String quayGroupName, List<String> quaysAlreadyProcessed) {
        logger.trace("Processing quay with name {}", quayGroupName);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString(quayGroupName, "no", ""));

        stopPlace.setQuays(new ArrayList<>());

        quays.forEach(quay -> {

            boolean addQuay = false;

            if (quaysAlreadyProcessed.contains(quay.getId())) {

                logger.warn("Already created quay with name {} and id {}", quay.getName(), quay.getId());

            } else if (stopPlace.getQuays().isEmpty()) {

                logger.debug("There are no quays related to stop place {} yet. Will add quay.", stopPlace.getName());
                addQuay = true;

            } else if (quayIsCloseToExistingQuays(quay, stopPlace.getQuays())) {

                logger.info("Quay {}, {} is close enough to be added",
                        quay.getName(),
                        quay.getCentroid().getLocation().getGeometryPoint().toText());
                addQuay = true;
            } else {
                logger.info("Ignoring (for now) quay {} {}", quay.getName(), quay.getCentroid().getLocation().getGeometryPoint().toText());
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
            logger.warn("No quays were added to stop place {} {}. Skipping...", stopPlace.getName(), stopPlace.getId());
        } else {

            stopPlace.setCentroid(new SimplePoint());
            stopPlace.getCentroid().setLocation(new LocationStructure());
            stopPlace.getCentroid().getLocation().setGeometryPoint(calculateCentroidForStopPlace(stopPlace.getQuays()));

            try {
                countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace);
            } catch (IOException e) {
                logger.warn("Error loading data from Pelias: {}", e.getMessage(), e);
            }

            try {
                stopPlaceRepository.save(stopPlace);
                logger.debug("Created stop place number {} with name {} and {} quays (id {})",
                        stopPlaceCounter.incrementAndGet(), stopPlace.getName(), stopPlace.getQuays().size(), stopPlace.getId());
            } catch (Exception e) {
                logger.warn("Caught exception when creating stop place with name {}", quayGroupName, e);
            }
        }
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
        logger.info("Created envelope {}",envelope.toString());

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
