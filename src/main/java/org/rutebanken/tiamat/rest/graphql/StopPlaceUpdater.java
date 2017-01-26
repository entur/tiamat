package org.rutebanken.tiamat.rest.graphql;

import com.google.api.client.util.Preconditions;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import graphql.language.Field;
import graphql.language.Selection;
import graphql.language.SelectionSet;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
            if (field.getName().equals(CREATE_QUAY)) {

                stopPlace = createQuay(environment);
            } else if (field.getName().equals(UPDATE_QUAY)) {

                stopPlace = updateQuay(environment);
            } else if (field.getName().equals(CREATE_STOPPLACE)) {

                stopPlace = createStopPlace(environment);
            } else if (field.getName().equals(UPDATE_STOPPLACE)) {

                stopPlace = updateStopPlace(environment);
            }
        }
        if (stopPlace != null && isFieldRequested(environment, QUAYS)) {
            stopPlace.setQuays(new HashSet<>(stopPlace.getQuays()));
        }
        if (stopPlace != null && isFieldRequested(environment, IMPORTED_ID)) {
            List<String> originalIds = new ArrayList<>(stopPlace.getOriginalIds());
            stopPlace.getKeyValues().put(NetexIdMapper.ORIGINAL_ID_KEY, new Value(originalIds));
        }
        return Arrays.asList(stopPlace);
    }

    private boolean isFieldRequested(DataFetchingEnvironment environment, String fieldName) {
        boolean quaysRequested = false;
        List<Field> fields = environment.getFields();
        for (Field field : fields) {
            SelectionSet selectionSet = field.getSelectionSet();
            List<Selection> selections = selectionSet.getSelections();
            for (Selection selection : selections) {
                if (selection instanceof  Field) {
                    Field selectedField = (Field) selection;
                    if (fieldName.equals(selectedField.getName())) {
                        quaysRequested = true;
                    }
                }
            }
        }
        return quaysRequested;
    }

    private StopPlace createStopPlace(DataFetchingEnvironment environment) {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setCreated(ZonedDateTime.now());
        boolean hasValuesChanged = updateStopPlaceFromInput(environment, stopPlace);

        // TODO: Create Quays

        if (hasValuesChanged) {
            stopPlaceRepository.save(stopPlace);
        }
        return stopPlace;
    }

    private StopPlace updateStopPlace(DataFetchingEnvironment environment) {
        StopPlace stopPlace;
        String nsrId = environment.getArgument(ID);
        stopPlace = stopPlaceRepository.findOne(NetexIdMapper.getTiamatId(nsrId));
        if(stopPlace != null) {
            logger.info("Updating StopPlace {}", stopPlace.getId());

            boolean hasValuesChanged = updateStopPlaceFromInput(environment, stopPlace);

            if (hasValuesChanged) {
                stopPlace.setChanged(ZonedDateTime.now());
                stopPlaceRepository.save(stopPlace);
            }
        }
        return stopPlace;
    }

    private StopPlace updateQuay(DataFetchingEnvironment environment) {
        Preconditions.checkNotNull(environment.getArgument(ID), ID + " cannot be null");
        Preconditions.checkNotNull(environment.getArgument(STOPPLACE_ID), STOPPLACE_ID + " cannot be null");

        String nsrId = environment.getArgument(ID);
        Quay quay = quayRepository.findOne(NetexIdMapper.getTiamatId(nsrId));
        if(quay != null) {
            logger.info("Updating Quay {}", quay.getId());

            updateQuayFromInput(environment, quay);

            quayRepository.save(quay);
        }
        String nsrStopPlaceId = environment.getArgument(STOPPLACE_ID);
        return stopPlaceRepository.findOne(NetexIdMapper.getTiamatId(nsrStopPlaceId));
    }

    private StopPlace createQuay(DataFetchingEnvironment environment) {
        StopPlace stopPlace;
        Preconditions.checkNotNull(environment.getArgument(STOPPLACE_ID), STOPPLACE_ID + " cannot be null");
        Preconditions.checkNotNull(environment.getArgument(LATITUDE), LATITUDE+" cannot be null");
        Preconditions.checkNotNull(environment.getArgument(LONGITUDE), LONGITUDE+" cannot be null");

        String nsrId = environment.getArgument(STOPPLACE_ID);
        stopPlace = stopPlaceRepository.findOne(NetexIdMapper.getTiamatId(nsrId));
        if(stopPlace != null) {
            logger.info("Adding quay to StopPlace {}", stopPlace.getId());
            Quay newQuay = new Quay();
            newQuay.setCreated(ZonedDateTime.now());
            updateQuayFromInput(environment, newQuay);

            stopPlace.getQuays().add(newQuay);

            stopPlaceRepository.save(stopPlace);
            quayRepository.save(stopPlace.getQuays());
        }
        return stopPlace;
    }

    private boolean updateStopPlaceFromInput(DataFetchingEnvironment environment, StopPlace stopPlace) {
        boolean hasValuesChanged = setCommonFields(environment, stopPlace);
        if (environment.getArgument(STOPPLACE_TYPE) != null) {
            stopPlace.setStopPlaceType(environment.getArgument(STOPPLACE_TYPE));
            hasValuesChanged = true;
        }
        if (environment.getArgument(ALL_AREAS_WHEELCHAIR_ACCESSIBLE) != null) {
            stopPlace.setAllAreasWheelchairAccessible(environment.getArgument(ALL_AREAS_WHEELCHAIR_ACCESSIBLE));
            hasValuesChanged = true;
        }
        if (hasValuesChanged) {
            stopPlace.setChanged(ZonedDateTime.now());
        }
        return hasValuesChanged;
    }

    private boolean updateQuayFromInput(DataFetchingEnvironment environment, Quay quay) {
        boolean hasValuesChanged = setCommonFields(environment, quay);

        if (environment.getArgument(ALL_AREAS_WHEELCHAIR_ACCESSIBLE) != null) {
            quay.setAllAreasWheelchairAccessible(environment.getArgument(ALL_AREAS_WHEELCHAIR_ACCESSIBLE));
            hasValuesChanged = true;
        }
        if (environment.getArgument(COMPASS_BEARING) != null) {
            quay.setCompassBearing(((BigDecimal) environment.getArgument(COMPASS_BEARING)).floatValue());
            hasValuesChanged = true;
        }

        if (hasValuesChanged) {
            quay.setChanged(ZonedDateTime.now());
        }
        return hasValuesChanged;
    }

    private boolean setCommonFields(DataFetchingEnvironment environment, GroupOfEntities_VersionStructure entity) {
        boolean hasValuesChanged = false;
        if (environment.getArgument(NAME) != null) {
            EmbeddableMultilingualString name = getCurrentOrNew(entity.getName());

            String updatedName = environment.getArgument(NAME);
            if (!updatedName.equals(name.getValue())) {
                name.setValue(updatedName);
                entity.setName(name);
                hasValuesChanged = true;
            }
        }
        if (environment.getArgument(SHORT_NAME) != null) {
            EmbeddableMultilingualString shortName = getCurrentOrNew(entity.getShortName());

            String updatedName = environment.getArgument(SHORT_NAME);
            if (!updatedName.equals(shortName.getValue())) {
                shortName.setValue(updatedName);
                entity.setShortName(shortName);
                hasValuesChanged = true;
            }
        }
        if (environment.getArgument(DESCRIPTION) != null) {
            EmbeddableMultilingualString description = getCurrentOrNew(entity.getDescription());

            String updatedName = environment.getArgument(DESCRIPTION);
            if (!updatedName.equals(description.getValue())) {
                description.setValue(updatedName);
                entity.setDescription(description);
                hasValuesChanged = true;
            }
        }
        if (environment.getArgument(LONGITUDE) != null && environment.getArgument(LATITUDE) != null) {
            ((Zone_VersionStructure)entity).setCentroid(createPoint(environment));
            hasValuesChanged = true;
        }
        return hasValuesChanged;
    }

    private EmbeddableMultilingualString getCurrentOrNew(EmbeddableMultilingualString embeddableString) {
        if (embeddableString != null) {
            return embeddableString;
        }
        return new EmbeddableMultilingualString(null, "no");
    }

    private Point createPoint(DataFetchingEnvironment environment) {
        if (environment.getArgument(LONGITUDE) != null && environment.getArgument(LATITUDE) != null) {
            Double lon = ((BigDecimal) environment.getArgument(LONGITUDE)).doubleValue();
            Double lat = ((BigDecimal) environment.getArgument(LATITUDE)).doubleValue();
            return geometryFactory.createPoint(new Coordinate(lon, lat));
        }
        return null;
    }
}
