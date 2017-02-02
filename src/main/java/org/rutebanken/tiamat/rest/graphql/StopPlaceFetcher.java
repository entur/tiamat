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
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service("stopPlaceFetcher")
@Transactional
class StopPlaceFetcher implements DataFetcher {


    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Override
    @Transactional
    public Object get(DataFetchingEnvironment environment) {
        StopPlaceSearch.Builder stopPlaceSearchBuilder = new StopPlaceSearch.Builder();

        Page<StopPlace> stopPlaces = null;
        if (environment.getArgument(ID) != null) {
            List<String> idList = environment.getArgument(ID);

            stopPlaceSearchBuilder.setIdList(idList
                    .stream()
                    .map(nsrId -> NetexIdMapper.getTiamatId(nsrId))
                    .collect(Collectors.<Long>toList()));

            stopPlaces = stopPlaceRepository.findStopPlace(stopPlaceSearchBuilder.build());
        } else if (environment.getArgument(IMPORTED_ID_QUERY) != null) {

            List<Long> stopPlaceId = stopPlaceRepository.searchByKeyValue(NetexIdMapper.ORIGINAL_ID_KEY, environment.getArgument(IMPORTED_ID_QUERY));

            if (stopPlaceId != null && !stopPlaceId.isEmpty()) {
                stopPlaceSearchBuilder.setIdList(stopPlaceId);
                stopPlaces = stopPlaceRepository.findStopPlace(stopPlaceSearchBuilder.build());
            }
        } else {
            stopPlaceSearchBuilder.setStopTypeEnumerations(environment.getArgument(STOPPLACE_TYPE));

            if (environment.getArgument(COUNTY_REF) != null) {
                stopPlaceSearchBuilder.setCountyIds(
                        Lists.transform(environment.getArgument(COUNTY_REF), Functions.toStringFunction())
                );
            }

            if (environment.getArgument(MUNICIPALITY_REF) != null) {
                stopPlaceSearchBuilder.setMunicipalityIds(
                        Lists.transform(environment.getArgument(MUNICIPALITY_REF), Functions.toStringFunction())
                );
            }

            stopPlaceSearchBuilder.setQuery(environment.getArgument(QUERY));

            PageRequest pageable = new PageRequest(environment.getArgument(PAGE), environment.getArgument(SIZE));
            stopPlaceSearchBuilder.setPageable(pageable);

            if (environment.getArgument(LONGITUDE_MIN) != null) {
                BoundingBoxDto boundingBox = new BoundingBoxDto();

                try {
                    boundingBox.xMin = ((BigDecimal) environment.getArgument(LONGITUDE_MIN)).doubleValue();
                    boundingBox.yMin = ((BigDecimal) environment.getArgument(LATITUDE_MIN)).doubleValue();
                    boundingBox.xMax = ((BigDecimal) environment.getArgument(LONGITUDE_MAX)).doubleValue();
                    boundingBox.yMax = ((BigDecimal) environment.getArgument(LATITUDE_MAX)).doubleValue();
                } catch (NullPointerException npe) {
                    RuntimeException rte = new RuntimeException(MessageFormat.format("{}, {}, {} and {} must all be set when searching within bounding box", LONGITUDE_MIN, LATITUDE_MIN, LONGITUDE_MAX, LATITUDE_MAX));
                    rte.setStackTrace(new StackTraceElement[0]);
                    throw rte;
                }

                Long ignoreStopPlaceId = null;
                if (environment.getArgument(IGNORE_STOPPLACE_ID) != null) {
                    ignoreStopPlaceId = new Long(environment.getArgument(IGNORE_STOPPLACE_ID));
                }

                stopPlaces = stopPlaceRepository.findStopPlacesWithin(boundingBox.xMin, boundingBox.yMin, boundingBox.xMax,
                        boundingBox.yMax, ignoreStopPlaceId, pageable);
            } else {
                stopPlaces = stopPlaceRepository.findStopPlace(stopPlaceSearchBuilder.build());
            }
        }
        if (stopPlaces != null && isFieldRequested(environment, QUAYS)) {
            stopPlaces.getContent().forEach(stopPlace -> stopPlace.setQuays(new HashSet<>(stopPlace.getQuays())));
        }
        if (stopPlaces != null && isFieldRequested(environment, TOPOGRAPHIC_PLACE)) {
            stopPlaces.getContent().forEach(stopPlace -> stopPlace.setTopographicPlace(stopPlace.getTopographicPlace()));

            if (isFieldRequested(environment, TOPOGRAPHIC_PLACE, PARENT_TOPOGRAPHIC_PLACE)) {
                stopPlaces.getContent().stream()
                        .filter(sp -> sp.getTopographicPlace() != null)
                        .forEach(stopPlace -> stopPlace.getTopographicPlace().setParentTopographicPlace(stopPlace.getTopographicPlace().getParentTopographicPlace()));
            }
        }
        if (stopPlaces != null && isFieldRequested(environment, IMPORTED_ID)) {
            stopPlaces.getContent().forEach(stopPlace -> {
                List<String> originalIds = new ArrayList<>(stopPlace.getOriginalIds());
                stopPlace.getKeyValues().put(NetexIdMapper.ORIGINAL_ID_KEY, new Value(originalIds));
            });
        }
        return stopPlaces;
    }

    /**
     * Returns <code>true</code> if fieldName is requested
     *
     * Example:
     * Given GraphQL-query:
     * <pre>
     * stopPlace {
     *     id
     *     name {
     *         value
     *     }
     *     topographicPlace {
     *         name {
     *             value
     *         }
     *         parentTopographicPlace {
     *             name {
     *                 value
     *             }
     *         }
     *     }
     * }
     * </pre>
     *
     * The following returns <code>true</code>:
     * - isFieldRequested(env, "id")
     * - isFieldRequested(env, "name")
     * - isFieldRequested(env, "name", "value")
     * - isFieldRequested(env, "topographicPlace", "name", "value")
     * - isFieldRequested(env, "topographicPlace", "parentTopographicPlace", "name", "value")
     *
     * The following returns <code>false</code>:
     * - isFieldRequested(env, "name", "lang")
     * - isFieldRequested(env, "value")
     *
     * @param environment
     * @param fieldNames
     * @return true if field is requested
     */
    private boolean isFieldRequested(DataFetchingEnvironment environment, String... fieldNames) {
        boolean fieldRequested = false;
        List<Field> fields = environment.getFields();
        for (Field field : fields) {
            SelectionSet selectionSet = field.getSelectionSet();

            Field currentField;
            for (int i = 0; i < fieldNames.length; i++) {
                currentField = getMatchingField(fieldNames[i], selectionSet);
                if (currentField != null) {
                    if ((i == fieldNames.length-1)) {
                        //last element in field-list - target-field has been found
                        fieldRequested = true;
                    } else {
                        // Prepare for next iteration
                        selectionSet = currentField.getSelectionSet();
                    }
                }
            }
        }
        return fieldRequested;
    }

    /**
     *
     * @param fieldName
     * @param selectionSet
     * @return Field with matching name - if it exists in selectionSet, otherwise <code>null</code>
     */
    private Field getMatchingField(String fieldName, SelectionSet selectionSet) {
        List<Selection> selections = selectionSet.getSelections();
        for (Selection selection : selections) {
            if (selection instanceof Field) {
                Field selectedField = (Field) selection;
                if (fieldName.equals(selectedField.getName())) {
                    return selectedField;
                }
            }
        }
        return null;
    }
}
