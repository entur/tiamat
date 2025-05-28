/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.dtoassembling.dto.BoundingBoxDto;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.stopplace.ParentStopPlacesFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.rutebanken.tiamat.exporter.params.ExportParams.newExportParamsBuilder;
import static org.rutebanken.tiamat.exporter.params.StopPlaceSearch.newStopPlaceSearchBuilder;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ALL_VERSIONS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.COUNTRY_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.COUNTY_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.HAS_PARKING;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.IGNORE_STOPPLACE_ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.IMPORTED_ID_QUERY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.INCLUDE_EXPIRED;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.KEY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LATITUDE_MAX;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LATITUDE_MIN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LONGITUDE_MAX;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LONGITUDE_MIN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUNICIPALITY_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ONLY_MONOMODAL_STOPPLACES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PAGE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POINT_IN_TIME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.QUERY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SEARCH_WITH_CODE_SPACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SIZE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_PLACE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAGS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALUES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION_VALIDITY_ARG;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WITHOUT_LOCATION_ONLY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WITHOUT_QUAYS_ONLY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WITH_DUPLICATED_QUAY_IMPORTED_IDS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WITH_NEARBY_SIMILAR_DUPLICATES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WITH_TAGS;

@Service("stopPlaceFetcher")
@Transactional
class StopPlaceFetcher implements DataFetcher {


    private static final Logger logger = LoggerFactory.getLogger(StopPlaceFetcher.class);

    private static final Page<StopPlace> EMPTY_STOPS_RESULT = new PageImpl<>(new ArrayList<>());

    /**
     * Whether to keep children when resolving parent stop places. False, because with graphql it's possible to fetch children from parent.
     */
    private static final boolean KEEP_CHILDREN = false;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ParentStopPlacesFetcher parentStopPlacesFetcher;

    @Override
    @Transactional
    public Object get(DataFetchingEnvironment environment) {
        ExportParams.Builder exportParamsBuilder = newExportParamsBuilder();
        StopPlaceSearch.Builder stopPlaceSearchBuilder = newStopPlaceSearchBuilder();

        logger.info("Searching for StopPlaces with arguments {}", environment.getArguments());

        Page<StopPlace> stopPlacesPage = new PageImpl<>(new ArrayList<>());

        stopPlaceSearchBuilder.setPage(environment.getArgument(PAGE)).setSize(environment.getArgument(SIZE));

        String netexId = environment.getArgument(ID);
        String importedId = environment.getArgument(IMPORTED_ID_QUERY);
        Integer version = environment.getArgument(VERSION);

        String key = environment.getArgument(KEY);
        List<String> values = environment.getArgument(VALUES);

        Boolean allVersions = setIfNonNull(environment, ALL_VERSIONS, stopPlaceSearchBuilder::setAllVersions);
        setIfNonNull(environment, WITHOUT_LOCATION_ONLY, stopPlaceSearchBuilder::setWithoutLocationOnly);
        setIfNonNull(environment, WITHOUT_QUAYS_ONLY, stopPlaceSearchBuilder::setWithoutQuaysOnly);
        setIfNonNull(environment, WITH_DUPLICATED_QUAY_IMPORTED_IDS, stopPlaceSearchBuilder::setWithDuplicatedQuayImportedIds);
        setIfNonNull(environment, WITH_NEARBY_SIMILAR_DUPLICATES, stopPlaceSearchBuilder::setWithNearbySimilarDuplicates);
        setIfNonNull(environment, HAS_PARKING, stopPlaceSearchBuilder::setHasParking);
        setIfNonNull(environment, WITH_TAGS, stopPlaceSearchBuilder::setWithTags);

        Instant pointInTime ;
        if (environment.getArgument(POINT_IN_TIME) != null) {
            pointInTime = environment.getArgument(POINT_IN_TIME);
        } else {
            pointInTime = null;
        }

        if(environment.getArgument(VERSION_VALIDITY_ARG) != null) {
            ExportParams.VersionValidity versionValidity = ExportParams.VersionValidity.valueOf(ExportParams.VersionValidity.class, environment.getArgument(VERSION_VALIDITY_ARG));
            stopPlaceSearchBuilder.setVersionValidity(versionValidity);
        }

        if (netexId != null && !netexId.isEmpty()) {

            try {
                List<StopPlace> stopPlace;
                if(version != null && version > 0) {
                    stopPlace = Arrays.asList(stopPlaceRepository.findFirstByNetexIdAndVersion(netexId, version));
                    stopPlacesPage = getStopPlaces(environment, stopPlace, 1L);
                } else {
                    stopPlaceSearchBuilder.setNetexIdList(Arrays.asList(netexId));
                    stopPlacesPage = stopPlaceRepository.findStopPlace(exportParamsBuilder.setStopPlaceSearch(stopPlaceSearchBuilder.build()).build());
                }

            } catch (NumberFormatException nfe) {
                logger.info("Attempted to find stopPlace with invalid id [{}]", netexId);
            }
        } else if (importedId != null && !importedId.isEmpty()) {

            List<String> stopPlaceNetexId = stopPlaceRepository.searchByKeyValue(NetexIdMapper.ORIGINAL_ID_KEY, environment.getArgument(IMPORTED_ID_QUERY));

            if (stopPlaceNetexId != null && !stopPlaceNetexId.isEmpty()) {
                stopPlaceSearchBuilder.setNetexIdList(stopPlaceNetexId);
                stopPlacesPage = stopPlaceRepository.findStopPlace(exportParamsBuilder.setStopPlaceSearch(stopPlaceSearchBuilder.build()).build());
            }
        } else {

            if (key != null && values != null) {
                Set<String> valueSet = new HashSet<>();
                valueSet.addAll(values);

                Set<String> stopPlaceNetexId = stopPlaceRepository.findByKeyValues(key, valueSet, true);
                if (stopPlaceNetexId != null && !stopPlaceNetexId.isEmpty()) {
                    List<String> idList = new ArrayList<>();
                    idList.addAll(stopPlaceNetexId);
                    stopPlaceSearchBuilder.setNetexIdList(idList);
                } else {
                    //Search for key/values returned no results
                    return EMPTY_STOPS_RESULT;
                }
            } else {

                if (allVersions == null || !allVersions) {
                    //If requesting all versions - POINT_IN_TIME is irrelevant
                    stopPlaceSearchBuilder.setPointInTime(pointInTime);
                }

                List<StopTypeEnumeration> stopTypes = environment.getArgument(STOP_PLACE_TYPE);
                if (stopTypes != null && !stopTypes.isEmpty()) {
                    stopPlaceSearchBuilder.setStopTypeEnumerations(stopTypes.stream()
                            .filter(type -> type != null)
                            .toList()
                    );
                }

                List<String> countryRef = environment.getArgument(COUNTRY_REF);
                if (countryRef != null && !countryRef.isEmpty()) {
                    exportParamsBuilder.setCountryReferences(
                            countryRef.stream()
                                    .filter(countryRefValue -> countryRefValue != null && !countryRefValue.isEmpty())
                                    .toList()
                    );
                }

                List<String> countyRef = environment.getArgument(COUNTY_REF);
                if (countyRef != null && !countyRef.isEmpty()) {
                    exportParamsBuilder.setCountyReferences(
                            countyRef.stream()
                                    .filter(countyRefValue -> countyRefValue != null && !countyRefValue.isEmpty())
                                    .toList()
                    );
                }

                List<String> municipalityRef = environment.getArgument(MUNICIPALITY_REF);
                if (municipalityRef != null && !municipalityRef.isEmpty()) {
                    exportParamsBuilder.setMunicipalityReferences(
                            municipalityRef.stream()
                                    .filter(municipalityRefValue -> municipalityRefValue != null && !municipalityRefValue.isEmpty())
                                    .toList()
                    );
                }

                if (environment.getArgument(SEARCH_WITH_CODE_SPACE) != null) {
                    String code = environment.getArgument(SEARCH_WITH_CODE_SPACE);
                    exportParamsBuilder.setCodeSpace(code.toLowerCase());
                }

                setIfNonNull(environment, TAGS, stopPlaceSearchBuilder::setTags);

                stopPlaceSearchBuilder.setQuery(environment.getArgument(QUERY));
            }

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

                String ignoreStopPlaceId = null;
                if (environment.getArgument(IGNORE_STOPPLACE_ID) != null) {
                    ignoreStopPlaceId = environment.getArgument(IGNORE_STOPPLACE_ID);
                }

                if (environment.getArgument(INCLUDE_EXPIRED)) {
                    pointInTime = null;
                }
                stopPlacesPage = stopPlaceRepository.findStopPlacesWithin(boundingBox.xMin, boundingBox.yMin, boundingBox.xMax,
                        boundingBox.yMax, ignoreStopPlaceId, pointInTime, PageRequest.of(environment.getArgument(PAGE), environment.getArgument(SIZE)));
            } else {
                stopPlacesPage = stopPlaceRepository.findStopPlace(exportParamsBuilder.setStopPlaceSearch(stopPlaceSearchBuilder.build()).build());
            }
        }

        final List<StopPlace> stopPlaces = stopPlacesPage.getContent();
        boolean onlyMonomodalStopplaces= false;
        if (environment.getArgument(ONLY_MONOMODAL_STOPPLACES) != null) {
            onlyMonomodalStopplaces = environment.getArgument(ONLY_MONOMODAL_STOPPLACES);
        }
        //By default, stop should resolve parent stops
        if (onlyMonomodalStopplaces) {
            return getStopPlaces(environment, stopPlaces, stopPlaces.size());
        } else {
            List<StopPlace> parentsResolved = parentStopPlacesFetcher.resolveParents(stopPlaces, KEEP_CHILDREN);
            return getStopPlaces(environment,parentsResolved,parentsResolved.size());
        }
    }

    private PageImpl<StopPlace> getStopPlaces(DataFetchingEnvironment environment, List<StopPlace> stopPlaces, long size) {
        return new PageImpl<>(stopPlaces, PageRequest.of(environment.getArgument(PAGE), environment.getArgument(SIZE)), size);
    }

    private <T> T setIfNonNull(DataFetchingEnvironment environment, String argumentName, Consumer<T> consumer) {
        if(environment.getArgument(argumentName) != null) {
            T value = environment.getArgument(argumentName);
            consumer.accept(value);
            return value;
        }
        return null;
    }
}
