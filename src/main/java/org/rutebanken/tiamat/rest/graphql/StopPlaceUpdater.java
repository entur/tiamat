package org.rutebanken.tiamat.rest.graphql;

import com.google.api.client.util.Preconditions;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service("stopPlaceUpdater")
class StopPlaceUpdater implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceUpdater.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private GeometryFactory geometryFactory;


    @Override
    public Object get(DataFetchingEnvironment environment) {
        List<Field> fields = environment.getFields();
        StopPlace stopPlace = null;
        for (Field field : fields) {
            if (field.getName().equals(MUTATE_STOPPLACE)) {
                stopPlace = createOrUpdateStopPlace(environment);
            }
        }
        return Arrays.asList(stopPlace);
    }

    private StopPlace createOrUpdateStopPlace(DataFetchingEnvironment environment) {
        StopPlace stopPlace = null;
        if (environment.getArgument(OUTPUT_TYPE_STOPPLACE) != null) {
            Map input = environment.getArgument(OUTPUT_TYPE_STOPPLACE);

            String nsrId = (String) input.get(ID);
            if (nsrId != null) {
                logger.info("Updating StopPlace {}", nsrId);
                stopPlace = stopPlaceRepository.findOne(NetexIdMapper.getTiamatId(nsrId));

                Preconditions.checkArgument(stopPlace != null, "Attempting to update StopPlace [id = %s], but StopPlace does not exist.", nsrId);

            } else {
                logger.info("Creating new StopPlace");
                stopPlace = new StopPlace();
                stopPlace.setCreated(ZonedDateTime.now());
            }

            if (stopPlace != null) {
                boolean hasValuesChanged = populateStopPlaceFromInput(input, stopPlace);

                if (hasValuesChanged) {
                    if (stopPlace.getQuays() != null) {
                        /*
                         * Explicitly saving new Quays  when updating and creating new Quays in the same request.
                         * Already existing quays are attempted to be inserted causing ConstraintViolationException.
                         *
                         * It is necessary to call saveAndFlush(quay) to enforce database-constraints and updating
                         * references on StopPlace-object.
                         *
                         */
                        stopPlace.getQuays().stream()
                                .filter(quay -> quay.getId() == null)
                                .forEach(quay -> quayRepository.saveAndFlush(quay));
                    }
                    stopPlace.setChanged(ZonedDateTime.now());
                    stopPlace = stopPlaceRepository.save(stopPlace);

                }
            }
        }
        return stopPlace;
    }

    /**
     *
     * @param input
     * @param stopPlace
     * @return true if StopPlace or any og the attached Quays are updated
     */
    private boolean populateStopPlaceFromInput(Map input, StopPlace stopPlace) {
        boolean isUpdated = populate(input, stopPlace);

        if (input.get(STOP_PLACE_TYPE) != null) {
            stopPlace.setStopPlaceType((StopTypeEnumeration) input.get(STOP_PLACE_TYPE));
            isUpdated = true;
        }

        if (input.get(QUAYS) != null) {
            List quays = (List) input.get(QUAYS);
            for (Object quayObject : quays) {

                Map quayInputMap = (Map) quayObject;
                if (populateQuayFromInput(stopPlace, quayInputMap)) {
                    isUpdated = true;
                } else {
                    logger.info("Quay not changed");
                }
            }
        }
        if (isUpdated) {
            stopPlace.setChanged(ZonedDateTime.now());
        }

        return isUpdated;
    }

    private boolean populateQuayFromInput(StopPlace stopPlace, Map quayInputMap) {
        Quay quay;
        if (quayInputMap.get(ID) != null) {
            Optional<Quay> existingQuay = stopPlace.getQuays().stream()
                    .filter(q -> q.getId() != null)
                    .filter(q -> q.getId().equals(NetexIdMapper.getTiamatId((String) quayInputMap.get(ID)))).findFirst();

            Preconditions.checkArgument(existingQuay.isPresent(),
                    "Attempting to update Quay [id = %s] on StopPlace [id = %s] , but Quay does not exist on StopPlace",
                    quayInputMap.get(ID),
                    NetexIdMapper.getNetexId(stopPlace, stopPlace.getId()));

            quay = existingQuay.get();
            logger.info("Updating Quay {} for StopPlace {}", quay.getId(), stopPlace.getId());
        } else {
            quay = new Quay();
            quay.setCreated(ZonedDateTime.now());

            logger.info("Creating new Quay");
        }
        boolean isQuayUpdated = populate(quayInputMap, quay);

        if (quayInputMap.get(COMPASS_BEARING) != null) {
            quay.setCompassBearing(((BigDecimal) quayInputMap.get(COMPASS_BEARING)).floatValue());
            isQuayUpdated = true;
        }

        if (isQuayUpdated) {
            quay.setChanged(ZonedDateTime.now());

            if (quay.getId() == null) {
                stopPlace.getQuays().add(quay);
            }
        }
        return isQuayUpdated;
    }

    private boolean populate(Map input, SiteElement_VersionStructure entity) {
        boolean isUpdated = false;

        if (input.get(NAME) != null) {
            entity.setName(getEmbeddableString((Map) input.get(NAME)));
            isUpdated = true;
        }
        if (input.get(SHORT_NAME) != null) {
            entity.setShortName(getEmbeddableString((Map) input.get(SHORT_NAME)));
            isUpdated = true;
        }
        if (input.get(DESCRIPTION) != null) {
            entity.setDescription(getEmbeddableString((Map) input.get(DESCRIPTION)));
            isUpdated = true;
        }
        if (input.get(ALL_AREAS_WHEELCHAIR_ACCESSIBLE) != null) {
            entity.setAllAreasWheelchairAccessible((Boolean) input.get(ALL_AREAS_WHEELCHAIR_ACCESSIBLE));
            isUpdated = true;
        }

        if (input.get(LOCATION) != null) {
            entity.setCentroid(createPoint((Map) input.get(LOCATION)));
            isUpdated = true;
        }

        if (input.get(GEOMETRY) != null) {
            entity.setCentroid(createGeoJsonPoint((Map) input.get(GEOMETRY)));
            isUpdated = true;
        }
        return isUpdated;
    }

    private Point createGeoJsonPoint(Map map) {
        if (map.get("type") != null && map.get("coordinates") != null) {
            if ("Point".equals(map.get("type"))) {
                Coordinate[] coordinates = (Coordinate[]) map.get("coordinates");
                return geometryFactory.createPoint(coordinates[0]);
            }
        }
        return null;
    }

    private LineString createGeoJsonLineString(Map map) {
        if (map.get("type") != null && map.get("coordinates") != null) {
            if ("LineString".equals(map.get("type"))) {
                Coordinate[] coordinates = (Coordinate[]) map.get("coordinates");
                return geometryFactory.createLineString(coordinates);
            }
        }
        return null;
    }

    private EmbeddableMultilingualString getEmbeddableString(Map map) {
        return new EmbeddableMultilingualString((String) map.get(VALUE), (String) map.get(LANG));
    }

    private Point createPoint(Map map) {
        if (map.get(LONGITUDE) != null && map.get(LATITUDE) != null) {
            Double lon = (Double) map.get(LONGITUDE);
            Double lat = (Double) map.get(LATITUDE);
            return geometryFactory.createPoint(new Coordinate(lon, lat));
        }
        return null;
    }

}
