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
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
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

        return lazyFetchStopPlaces(stopPlace, environment);
    }

    private StopPlace createStopPlace(DataFetchingEnvironment environment) {

        StopPlace stopPlace = new StopPlace();

        stopPlace.setName(new EmbeddableMultilingualString(environment.getArgument(NAME), "no"));
        stopPlace.setChanged(ZonedDateTime.now());
        stopPlace.setShortName(new EmbeddableMultilingualString(environment.getArgument(SHORT_NAME), "no"));
        stopPlace.setDescription(new EmbeddableMultilingualString(environment.getArgument(DESCRIPTION), "no"));
        stopPlace.setStopPlaceType(environment.getArgument(STOPPLACE_TYPE));

        stopPlace.setCentroid(createPoint(environment));

        stopPlace.setAllAreasWheelchairAccessible(environment.getArgument(WHEELCHAIR_ACCESSIBLE));

        // TODO: Create Quays

        StopPlace saved = stopPlaceRepository.save(stopPlace);
        return saved;
    }

    private StopPlace updateStopPlace(DataFetchingEnvironment environment) {
        StopPlace stopPlace;
        stopPlace = stopPlaceRepository.findOne(new Long(environment.getArgument(ID)));
        if(stopPlace != null) {
            logger.info("Updating StopPlace {}", stopPlace.getId());
            boolean hasValuesChanged = false;
            if (environment.getArgument(STOPPLACE_TYPE) != null &&
                    !stopPlace.getStopPlaceType().equals(environment.getArgument(STOPPLACE_TYPE))) {
                stopPlace.setStopPlaceType(environment.getArgument(STOPPLACE_TYPE));
                hasValuesChanged = true;
            }
            if (environment.getArgument(NAME) != null) {
                EmbeddableMultilingualString name = stopPlace.getName();

                String updatedName = environment.getArgument(NAME);
                if (!updatedName.equals(name.getValue())) {
                    name.setValue(updatedName);
                    stopPlace.setName(name);
                    hasValuesChanged = true;
                }
            }
            if (hasValuesChanged) {
                stopPlace.setChanged(ZonedDateTime.now());
                stopPlaceRepository.save(stopPlace);
            }
        }
        return stopPlace;
    }

    private StopPlace updateQuay(DataFetchingEnvironment environment) {
        Preconditions.checkNotNull(environment.getArgument(ID), ID + " cannot be null");
        Preconditions.checkNotNull(environment.getArgument(STOPPLACE_ID), STOPPLACE_ID +" cannot be null");

        Quay quay = quayRepository.findOne(new Long(environment.getArgument(ID)));
        if(quay != null) {
            logger.info("Updating Quay {}", quay.getId());

            quay.setChanged(ZonedDateTime.now());
            if (environment.getArgument(LATITUDE) != null) {

                Preconditions.checkNotNull(environment.getArgument(LATITUDE), LATITUDE+" cannot be null");
                Preconditions.checkNotNull(environment.getArgument(LONGITUDE), LONGITUDE+" cannot be null");

                quay.setCentroid(createPoint(environment));
            }

            quayRepository.save(quay);
        }
        return stopPlaceRepository.findOne(new Long(environment.getArgument(STOPPLACE_ID)));
    }

    private StopPlace createQuay(DataFetchingEnvironment environment) {
        StopPlace stopPlace;
        Preconditions.checkNotNull(environment.getArgument(STOPPLACE_ID), STOPPLACE_ID + " cannot be null");
        Preconditions.checkNotNull(environment.getArgument(LATITUDE), LATITUDE+" cannot be null");
        Preconditions.checkNotNull(environment.getArgument(LONGITUDE), LONGITUDE+" cannot be null");

        stopPlace = stopPlaceRepository.findOne(new Long(environment.getArgument(STOPPLACE_ID)));
        if(stopPlace != null) {
            logger.info("Adding quay to StopPlace {}", stopPlace.getId());
            if (stopPlace.getQuays() != null) {
                Quay newQuay = new Quay();
                newQuay.setCreated(ZonedDateTime.now());
                newQuay.setCentroid(createPoint(environment));
                stopPlace.getQuays().add(newQuay);
            }
            stopPlaceRepository.save(stopPlace);
            quayRepository.save(stopPlace.getQuays());
        }
        return stopPlace;
    }

    private Point createPoint(DataFetchingEnvironment environment) {
        if (environment.getArgument(LONGITUDE) != null && environment.getArgument(LATITUDE) != null) {
            return geometryFactory.createPoint(new Coordinate(environment.getArgument(LONGITUDE), environment.getArgument(LATITUDE)));
        }
        return null;
    }

    private Object lazyFetchStopPlaces(StopPlace stopPlace, DataFetchingEnvironment environment) {

        //TODO: Avoid this - i.e. fix @Transactional usage
        if (isQuaysRequested(environment)) {
            stopPlace.setQuays(new HashSet<>(stopPlace.getQuays()));
        }

        return Arrays.asList(stopPlace);
    }

    private boolean isQuaysRequested(DataFetchingEnvironment environment) {
        boolean quaysRequested = false;
        List<Field> fields = environment.getFields();
        for (Field field : fields) {
            SelectionSet selectionSet = field.getSelectionSet();
            List<Selection> selections = selectionSet.getSelections();
            for (Selection selection : selections) {
                Field selectedField = (Field) selection;
                if (QUAYS.equals(selectedField.getName())) {
                    quaysRequested = true;
                }
            }
        }
        return quaysRequested;
    }
}
