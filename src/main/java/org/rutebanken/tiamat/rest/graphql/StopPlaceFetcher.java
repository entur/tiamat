package org.rutebanken.tiamat.rest.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.dtoassembling.dto.BoundingBoxDto;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service("stopPlaceFetcher")
@Transactional
class StopPlaceFetcher implements DataFetcher {


    private static final Logger logger = LoggerFactory.getLogger(StopPlaceFetcher.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Override
    @Transactional
    public Object get(DataFetchingEnvironment environment) {
        StopPlaceSearch.Builder stopPlaceSearchBuilder = new StopPlaceSearch.Builder();

        logger.info("Searching for StopPlaces with arguments {}", environment.getArguments());

        Page<StopPlace> stopPlaces = new PageImpl<>(new ArrayList<>());

        PageRequest pageable = new PageRequest(environment.getArgument(PAGE), environment.getArgument(SIZE));
        stopPlaceSearchBuilder.setPageable(pageable);

        String netexId = environment.getArgument(ID);
        String importedId = environment.getArgument(IMPORTED_ID_QUERY);
        if (netexId != null && !netexId.isEmpty()) {

            try {
                stopPlaceSearchBuilder.setNetexIdList(Arrays.asList(netexId));

                stopPlaces = stopPlaceRepository.findStopPlace(stopPlaceSearchBuilder.build());
            } catch (NumberFormatException nfe) {
                logger.info("Attempted to find stopPlace with invalid id [{}]", netexId);
            }
        } else if (importedId != null && !importedId.isEmpty()) {

            List<String> stopPlaceNetexId = stopPlaceRepository.searchByKeyValue(NetexIdMapper.ORIGINAL_ID_KEY, environment.getArgument(IMPORTED_ID_QUERY));

            if (stopPlaceNetexId != null && !stopPlaceNetexId.isEmpty()) {
                stopPlaceSearchBuilder.setNetexIdList(stopPlaceNetexId);
                stopPlaces = stopPlaceRepository.findStopPlace(stopPlaceSearchBuilder.build());
            }
        } else {
            List<StopTypeEnumeration> stopTypes = environment.getArgument(STOP_PLACE_TYPE);
            if (stopTypes != null && !stopTypes.isEmpty()) {
                stopPlaceSearchBuilder.setStopTypeEnumerations(stopTypes.stream()
                                .filter(type -> type != null)
                                .collect(Collectors.toList())
                );
            }

            List<String> countyRef = environment.getArgument(COUNTY_REF);
            if (countyRef != null && !countyRef.isEmpty()) {
                stopPlaceSearchBuilder.setCountyIds(
                        countyRef.stream()
                            .filter(countyRefValue -> countyRefValue != null && !countyRefValue.isEmpty())
                            .collect(Collectors.toList())
                );
            }

            List<String> municipalityRef = environment.getArgument(MUNICIPALITY_REF);
            if (municipalityRef != null && !municipalityRef.isEmpty()) {
                stopPlaceSearchBuilder.setMunicipalityIds(
                        municipalityRef.stream()
                                .filter(municipalityRefValue -> municipalityRefValue != null && !municipalityRefValue.isEmpty())
                                .collect(Collectors.toList())
                );
            }

            stopPlaceSearchBuilder.setQuery(environment.getArgument(QUERY));

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

                stopPlaces = stopPlaceRepository.findStopPlacesWithin(boundingBox.xMin, boundingBox.yMin, boundingBox.xMax,
                        boundingBox.yMax, ignoreStopPlaceId, pageable);
            } else {
                    stopPlaces = stopPlaceRepository.findStopPlace(stopPlaceSearchBuilder.build());
            }
        }
        return stopPlaces;
    }
}
