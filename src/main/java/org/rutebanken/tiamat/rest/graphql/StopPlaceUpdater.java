package org.rutebanken.tiamat.rest.graphql;

import com.google.api.client.util.Preconditions;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.*;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service("stopPlaceUpdater")
@Transactional
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
        if (environment.getArgument(STOPPLACE) != null) {
            Map input = environment.getArgument(STOPPLACE);

            String nsrId = (String) input.get(ID);
            if (nsrId != null) {
                stopPlace = stopPlaceRepository.findOne(NetexIdMapper.getTiamatId(nsrId));
            } else {
                stopPlace = new StopPlace();
                stopPlace.setCreated(ZonedDateTime.now());
            }

            if (stopPlace != null) {
                if (stopPlace.getId() != null) {
                    logger.info("Updating StopPlace {}", stopPlace.getId());
                } else {
                    logger.info("Creating new StopPlace");
                }

                Pair<Boolean, Boolean> hasValuesChanged = populateStopPlaceFromInput(input, stopPlace);

                if (hasValuesChanged.getLeft()) {
                    stopPlace.setChanged(ZonedDateTime.now());
                    stopPlaceRepository.save(stopPlace);
                }

                if (hasValuesChanged.getRight() && stopPlace.getQuays() != null) {
                    quayRepository.save(stopPlace.getQuays());
                }
            }
        }
        return stopPlace;
    }

    /**
     *
     * @param input
     * @param stopPlace
     * @return Pair- Left: StopPlace is updated, Right: Quays are updated
     */
    private Pair<Boolean, Boolean> populateStopPlaceFromInput(Map input, StopPlace stopPlace) {
        boolean isUpdated = populate(input, stopPlace);

        if (input.get(STOP_TYPE) != null) {
            stopPlace.setStopPlaceType((StopTypeEnumeration) input.get(STOP_TYPE));
            isUpdated = true;
        }

        boolean isQuaysUpdated = false;
        if (input.get(QUAYS) != null) {
            List quays = (List) input.get(QUAYS);
            for (Object quayObject : quays) {

                Map quayInputMap = (Map) quayObject;
                if (populateQuayFromInput(stopPlace, quayInputMap)) {
                    isQuaysUpdated = true;
                } else {
                    logger.info("Quay not changed");
                }
            }
        }
        if (isUpdated) {
            stopPlace.setChanged(ZonedDateTime.now());
        }

        return Pair.of(isUpdated, isQuaysUpdated);
    }

    private boolean populateQuayFromInput(StopPlace stopPlace, Map quayInputMap) {
        Quay quay;
        if (quayInputMap.get(ID) != null) {
            Optional<Quay> existingQuay = stopPlace.getQuays().stream()
                    .filter(q -> q.getId() != null)
                    .filter(q -> q.getId().equals(NetexIdMapper.getTiamatId((String) quayInputMap.get(ID)))).findFirst();

            Preconditions.checkArgument(existingQuay.isPresent(), "Attempting to update Quay (id:{}) on StopPlace (id:{}) , but Quay does not exist on StopPlace", quayInputMap.get(ID), stopPlace.getId());

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

            //If Quay already exists it is not added - ref Quay#equals()
            return stopPlace.getQuays().add(quay);
        }
        return false;
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
        }
        return isUpdated;
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
