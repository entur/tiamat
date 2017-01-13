package org.rutebanken.tiamat.rest.graphql;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import graphql.language.Field;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.dtoassembling.dto.BoundingBoxDto;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
class StopPlaceFetcher implements DataFetcher {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;


    @Override
    public Object get(DataFetchingEnvironment environment) {
        StopPlaceSearch.Builder stopPlaceSearchBuilder = new StopPlaceSearch.Builder();
        stopPlaceSearchBuilder.setIdList(environment.getArgument("id"));
        stopPlaceSearchBuilder.setStopTypeEnumerations(environment.getArgument("stopPlaceType"));

        if (environment.getArgument("countyReference") != null) {
            stopPlaceSearchBuilder.setCountyIds(
                    Lists.transform(environment.getArgument("countyReference"), Functions.toStringFunction())
            );
        }

        if (environment.getArgument("municipalityReference") != null) {
            stopPlaceSearchBuilder.setMunicipalityIds(
                    Lists.transform(environment.getArgument("municipalityReference"), Functions.toStringFunction())
            );
        }

        stopPlaceSearchBuilder.setQuery(environment.getArgument("query"));

        PageRequest pageable = new PageRequest(environment.getArgument("page"), environment.getArgument("size"));
        stopPlaceSearchBuilder.setPageable(pageable);

        StopPlaceSearch stopPlaceSearch = stopPlaceSearchBuilder.build();


        Page<StopPlace> stopPlaces;
        if (environment.getArgument("xMin") != null) {
            BoundingBoxDto boundingBox = new BoundingBoxDto();

            try {
                boundingBox.xMin = ((BigDecimal) environment.getArgument("xMin")).doubleValue();
                boundingBox.yMin = ((BigDecimal) environment.getArgument("yMin")).doubleValue();
                boundingBox.xMax = ((BigDecimal) environment.getArgument("xMax")).doubleValue();
                boundingBox.yMax = ((BigDecimal) environment.getArgument("yMax")).doubleValue();
            } catch (NullPointerException npe) {
                RuntimeException rte = new RuntimeException("xMin, yMin, xMax and yMax must all be set when searching within bounding box");
                rte.setStackTrace(new StackTraceElement[0]);
                throw rte;
            }

            Long ignoreStopPlaceId = environment.getArgument("ignoreStopPlaceId");

            stopPlaces = stopPlaceRepository.findStopPlacesWithin(boundingBox.xMin, boundingBox.yMin, boundingBox.xMax,
                    boundingBox.yMax, ignoreStopPlaceId, pageable);
        } else {
            stopPlaces = stopPlaceRepository.findStopPlace(stopPlaceSearch);
        }

        if (isQuaysRequested(environment)) {
            stopPlaces.getContent().forEach(stopPlace -> stopPlace.setQuays(new HashSet<>(stopPlace.getQuays())));
        }

        return stopPlaces;

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
