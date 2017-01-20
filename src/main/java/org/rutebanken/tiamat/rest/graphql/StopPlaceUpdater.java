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
            if (field.getName().equals("addQuay")) {

                Preconditions.checkNotNull(environment.getArgument("stopPlaceId"), "stopPlaceId cannot be null");
                Preconditions.checkNotNull(environment.getArgument("latitude"), "latitude cannot be null");
                Preconditions.checkNotNull(environment.getArgument("longitude"), "longitude cannot be null");

                stopPlace = stopPlaceRepository.findOne((Long)environment.getArgument("stopPlaceId"));
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
            }
            if (field.getName().equals("updateQuay")) {

                Preconditions.checkNotNull(environment.getArgument("id"), "id cannot be null");

                Quay quay = quayRepository.findOne((Long)environment.getArgument("id"));
                if(quay != null) {
                    logger.info("Updating Quay {}", quay.getId());

                    quay.setChanged(ZonedDateTime.now());
                    if (environment.getArgument("latitude") != null) {

                        Preconditions.checkNotNull(environment.getArgument("latitude"), "latitude cannot be null");
                        Preconditions.checkNotNull(environment.getArgument("longitude"), "longitude cannot be null");

                        quay.setCentroid(createPoint(environment));
                    }

                    quayRepository.save(quay);
                }
            } else if (field.getName().equals("updateStopPlace")) {
                stopPlace = stopPlaceRepository.findOne((Long)environment.getArgument("id"));
                if(stopPlace != null) {
                    logger.info("Updating StopPlace {}", stopPlace.getId());

                    if (environment.getArgument("stopPlaceType") != null) {
                        stopPlace.setStopPlaceType(environment.getArgument("stopPlaceType"));
                    }
                    if (environment.getArgument("name") != null) {
                        EmbeddableMultilingualString name = stopPlace.getName();
                        name.setValue(environment.getArgument("name"));
                        stopPlace.setName(name);
                    }
                    stopPlace.setChanged(ZonedDateTime.now());
                    stopPlaceRepository.save(stopPlace);
                }
            }
        }

        return lazyFetchStopPlaces(stopPlace, environment);
    }

    private Point createPoint(DataFetchingEnvironment environment) {

        Preconditions.checkNotNull(environment.getArgument("latitude"), "latitude cannot be null");
        Preconditions.checkNotNull(environment.getArgument("longitude"), "longitude cannot be null");

        return geometryFactory.createPoint(new Coordinate(environment.getArgument("longitude"), environment.getArgument("latitude")));
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
                if ("quays".equals(selectedField.getName())) {
                    quaysRequested = true;
                }
            }
        }
        return quaysRequested;
    }
}
