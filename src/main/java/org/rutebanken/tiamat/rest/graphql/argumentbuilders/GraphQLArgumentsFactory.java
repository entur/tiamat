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

package org.rutebanken.tiamat.rest.graphql.argumentbuilders;

import graphql.language.BooleanValue;
import graphql.language.IntValue;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import org.rutebanken.tiamat.rest.graphql.registry.GraphQLServicesRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.scalars.ExtendedScalars.GraphQLBigDecimal;
import static graphql.schema.GraphQLArgument.newArgument;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

/**
 * Factory for creating GraphQL argument lists for queries and mutations.
 * Consolidates all argument building logic to eliminate duplication and improve maintainability.
 */
@Component
public class GraphQLArgumentsFactory {

    public static final int DEFAULT_PAGE_VALUE = 0;
    public static final int DEFAULT_SIZE_VALUE = 20;

    @Autowired
    private GraphQLServicesRegistry servicesRegistry;

    /**
     * Creates common pagination arguments (page and size).
     * Used across most query operations.
     *
     * @return List containing page and size arguments with default values
     */
    public List<GraphQLArgument> createPageAndSizeArguments() {
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.add(GraphQLArgument.newArgument()
                .name(PAGE)
                .type(GraphQLInt)
                .defaultValueLiteral(IntValue.of(DEFAULT_PAGE_VALUE))
                .description(PAGE_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(SIZE)
                .type(GraphQLInt)
                .defaultValueLiteral(IntValue.of(DEFAULT_SIZE_VALUE))
                .description(SIZE_ARG_DESCRIPTION)
                .build());
        return arguments;
    }

    /**
     * Creates location arguments for geographic queries (longitude and latitude).
     *
     * @return List containing required longitude and latitude arguments
     */
    public List<GraphQLArgument> createLocationArguments() {
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.add(GraphQLArgument.newArgument()
                .name(LONGITUDE)
                .description("longitude")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(LATITUDE)
                .description("latitude")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());
        return arguments;
    }

    /**
     * Creates bounding box arguments for spatial queries.
     * Includes pagination, coordinate bounds, and filtering options.
     *
     * @return List of arguments for bounding box queries
     */
    public List<GraphQLArgument> createBboxArguments() {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();

        // Bounding box coordinates
        arguments.add(GraphQLArgument.newArgument()
                .name(LONGITUDE_MIN)
                .description("Bottom left longitude (xMin).")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(LATITUDE_MIN)
                .description("Bottom left latitude (yMin).")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(LONGITUDE_MAX)
                .description("Top right longitude (xMax).")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(LATITUDE_MAX)
                .description("Top right longitude (yMax).")
                .type(new GraphQLNonNull(GraphQLBigDecimal))
                .build());

        // Filtering options
        arguments.add(GraphQLArgument.newArgument()
                .name(IGNORE_STOPPLACE_ID)
                .type(GraphQLString)
                .description("ID of StopPlace to excluded from result.")
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(INCLUDE_EXPIRED)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description("Set to true if expired StopPlaces should be returned, default is 'false'.")
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(POINT_IN_TIME)
                .type(servicesRegistry.getDateScalar().getGraphQLDateScalar())
                .description(POINT_IN_TIME_ARG_DESCRIPTION)
                .build());

        return arguments;
    }

    /**
     * Creates arguments for finding StopPlaces.
     * Includes pagination, versioning, filtering by type/location/tags, and search options.
     *
     * @param allVersionsArgument Argument for including all versions
     * @return Comprehensive list of StopPlace query arguments
     */
    public List<GraphQLArgument> createFindStopPlaceArguments(GraphQLArgument allVersionsArgument) {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(allVersionsArgument);

        // Identity and versioning
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .description(ID_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(VERSION)
                .type(GraphQLInt)
                .description(VERSION_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .type(versionValidityEnumType)
                .name(VERSION_VALIDITY_ARG)
                .description(VERSION_ARG_DESCRIPTION)
                .build());

        // Type and location filtering
        arguments.add(GraphQLArgument.newArgument()
                .name(STOP_PLACE_TYPE)
                .type(new GraphQLList(stopPlaceTypeEnum))
                .description(STOP_PLACE_TYPE_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(COUNTY_REF)
                .type(new GraphQLList(GraphQLString))
                .description(COUNTY_REF_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(COUNTRY_REF)
                .type(new GraphQLList(GraphQLString))
                .description(COUNTRY_REF_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(TAGS)
                .type(new GraphQLList(GraphQLString))
                .description(TAGS_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(MUNICIPALITY_REF)
                .type(new GraphQLList(GraphQLString))
                .description(MUNICIPALITY_REF_ARG_DESCRIPTION)
                .build());

        // Search and query options
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .description(QUERY_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(IMPORTED_ID_QUERY)
                .type(GraphQLString)
                .description(IMPORTED_ID_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(POINT_IN_TIME)
                .type(servicesRegistry.getDateScalar().getGraphQLDateScalar())
                .description(POINT_IN_TIME_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(KEY)
                .type(GraphQLString)
                .description(KEY_ARG_DESCRIPTION)
                .build());

        // Boolean filters
        arguments.add(GraphQLArgument.newArgument()
                .name(WITHOUT_LOCATION_ONLY)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(WITHOUT_LOCATION_ONLY_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITHOUT_QUAYS_ONLY)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(WITHOUT_QUAYS_ONLY_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITH_DUPLICATED_QUAY_IMPORTED_IDS)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(WITH_DUPLICATED_QUAY_IMPORTED_IDS_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITH_NEARBY_SIMILAR_DUPLICATES)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(WITH_NEARBY_SIMILAR_DUPLICATES_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(HAS_PARKING)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(HAS_PARKING)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(ONLY_MONOMODAL_STOPPLACES)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(ONLY_MONOMODAL_STOPPLACES_DESCRIPTION)
                .build());

        // Additional filters
        arguments.add(GraphQLArgument.newArgument()
                .name(VALUES)
                .type(new GraphQLList(GraphQLString))
                .description(VALUES_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(WITH_TAGS)
                .type(GraphQLBoolean)
                .defaultValueLiteral(BooleanValue.of(false))
                .description(WITH_TAGS_ARG_DESCRIPTION)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(SEARCH_WITH_CODE_SPACE)
                .type(GraphQLString)
                .description(SEARCH_WITH_CODE_SPACE_ARG_DESCRIPTION)
                .build());

        return arguments;
    }

    /**
     * Creates arguments for finding TopographicPlaces.
     *
     * @param allVersionsArgument Argument for including all versions
     * @return List of TopographicPlace query arguments
     */
    public List<GraphQLArgument> createFindTopographicPlaceArguments(GraphQLArgument allVersionsArgument) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(allVersionsArgument);
        arguments.add(GraphQLArgument.newArgument()
                .name(TOPOGRAPHIC_PLACE_TYPE)
                .type(topographicPlaceTypeEnum)
                .description("Limits results to specified placeType.")
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .description("Searches for TopographicPlaces by name.")
                .build());
        return arguments;
    }

    /**
     * Creates arguments for finding PathLinks.
     *
     * @param allVersionsArgument Argument for including all versions
     * @return List of PathLink query arguments
     */
    public List<GraphQLArgument> createFindPathLinkArguments(GraphQLArgument allVersionsArgument) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(allVersionsArgument);
        arguments.add(GraphQLArgument.newArgument()
                .name(FIND_BY_STOP_PLACE_ID)
                .type(GraphQLString)
                .build());
        return arguments;
    }

    /**
     * Creates arguments for finding Parking.
     *
     * @param allVersionsArgument Argument for including all versions
     * @return List of Parking query arguments
     */
    public List<GraphQLArgument> createFindParkingArguments(GraphQLArgument allVersionsArgument) {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(VERSION)
                .type(GraphQLInt)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(FIND_BY_STOP_PLACE_ID)
                .type(GraphQLString)
                .build());
        arguments.add(allVersionsArgument);
        return arguments;
    }

    /**
     * Creates arguments for finding GroupOfStopPlaces.
     *
     * @return List of GroupOfStopPlaces query arguments
     */
    public List<GraphQLArgument> createFindGroupOfStopPlacesArguments() {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(FIND_BY_STOP_PLACE_ID)
                .type(GraphQLString)
                .build());
        return arguments;
    }

    /**
     * Creates arguments for finding PurposeOfGrouping.
     *
     * @return List of PurposeOfGrouping query arguments
     */
    public List<GraphQLArgument> createFindPurposeOfGroupingArguments() {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .build());
        return arguments;
    }

    /**
     * Creates arguments for finding GroupOfTariffZones.
     *
     * @return List of GroupOfTariffZones query arguments
     */
    public List<GraphQLArgument> createFindGroupOfTariffZonesArguments() {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(ID)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(FIND_BY_TARIFF_ZONE_ID)
                .type(GraphQLString)
                .build());
        return arguments;
    }

    /**
     * Creates arguments for finding TariffZones.
     *
     * @return List of TariffZone query arguments
     */
    public List<GraphQLArgument> createFindTariffZonesArguments() {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(IDS)
                .type(new GraphQLList(GraphQLString))
                .build());
        return arguments;
    }

    /**
     * Creates arguments for finding FareZones.
     *
     * @return List of FareZone query arguments
     */
    public List<GraphQLArgument> createFindFareZonesArguments() {
        List<GraphQLArgument> arguments = createPageAndSizeArguments();
        arguments.add(GraphQLArgument.newArgument()
                .name(QUERY)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(IDS)
                .type(new GraphQLList(GraphQLString))
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(FARE_ZONES_AUTHORITY_REF)
                .type(GraphQLString)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(FARE_ZONES_SCOPING_METHOD)
                .type(scopingMethodEnumType)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name(FARE_ZONES_ZONE_TOPOLOGY)
                .type(zoneTopologyEnumType)
                .build());
        return arguments;
    }
}
