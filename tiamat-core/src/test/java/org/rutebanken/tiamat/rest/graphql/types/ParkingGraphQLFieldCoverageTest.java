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

package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLObjectType;
import org.junit.Test;
import org.rutebanken.tiamat.model.Parking;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards {@link CustomGraphQLTypes#createParkingObjectType}/{@code createParkingInputObjectType}
 * against silent field loss on the GraphQL API — the layer where {@code alternativeNames} was
 * actually missing before this test existed: the field had a real NeTEx representation and a
 * matching Tiamat model field, yet was absent from both Parking GraphQL types, so querying or
 * mutating it produced a schema validation error (not just an empty result), and nothing in the
 * existing suite exercised it.
 * <p>
 * Unlike {@link org.rutebanken.tiamat.netex.mapping.mapper.ParkingNetexMapperFieldCoverageTest},
 * this test inspects the actual built {@link graphql.schema.GraphQLSchema} field definitions
 * returned by the real factory methods, not a hand-maintained mirror of them — so it cannot
 * itself drift out of sync with the schema the API actually serves.
 */
public class ParkingGraphQLFieldCoverageTest {

    /**
     * Fields exposed on both the Parking output type and the Parking input type, each covered
     * by a {@code testMutateParkingWith<Field>*} test in
     * {@link org.rutebanken.tiamat.rest.graphql.GraphQLResourceParkingIntegrationTest}.
     */
    private static final Set<String> IN_GRAPHQL_SCHEMA = Set.of(
            "name",
            "parentSiteRef",
            "totalCapacity",
            "parkingType",
            "parkingVehicleTypes",
            "parkingLayout",
            "principalCapacity",
            "overnightParkingPermitted",
            "rechargingAvailable",
            "secure",
            "realTimeOccupancyAvailable",
            "parkingReservation",
            "bookingUrl",
            "freeParkingOutOfHours",
            "parkingPaymentProcess",
            "paymentMethods",
            "lighting",
            "parkingProperties",
            "parkingAreas",
            "vehicleEntrances",
            "infoLinks",
            "availabilityConditions",
            "placeEquipments",
            "alternativeNames",
            "centroid",
            "accessibilityAssessment"
    );

    /**
     * Identity, audit and versioning fields, exposed via {@code id}/{@code version}/
     * {@code validBetween} rather than under their model field name, or not exposed at all
     * because the GraphQL API is not a generic entity CRUD surface.
     */
    private static final Set<String> INFRASTRUCTURE = Set.of(
            "id",
            "netexId",
            "version",
            "created",
            "changed",
            "changedBy",
            "versionComment",
            "modification",
            "status",
            "validBetween",
            "dataSourceRef",
            "responsibilitySetRef",
            "derivedFromVersionRef",
            "derivedFromObjectRef",
            "compatibleWithVersionFrameVersionRef",
            "extensions",
            "keyValues"
    );

    /**
     * Fields deliberately not exposed via GraphQL: either {@code @Transient} on the Tiamat
     * model, out of scope for the park-and-ride use case this change covers, or
     * structural/internal fields with no meaningful GraphQL representation. A name belongs
     * here only with a reason recorded in the comment above its group.
     */
    private static final Set<String> KNOWINGLY_NOT_IN_GRAPHQL_SCHEMA = Set.of(
            // Capacity/layout and operational-flag fields with a real NeTEx representation
            // and a persisted Tiamat column, but never added to the GraphQL schema - a
            // pre-existing gap discovered while writing this test, out of scope for this
            // change. Matches the same fields already excluded from the merge path in
            // ParkingMergeFieldCoverageTest#KNOWINGLY_NOT_MERGED.
            "numberOfParkingLevels",
            "prohibitedForHazardousMaterials",
            // @Transient on Parking, never persisted, so never exposed
            "publicCode",
            "label",
            "defaultCurrency",
            "currenciesAccepted",
            "cardsAccepted",
            "paymentByMobile",
            "pathLinks",
            "pathJunctions",
            "navigationPaths",
            // Site — structure and ownership, out of scope for Parking's GraphQL API
            "organisationRef",
            "parentZoneRef",
            "adjacentSites",
            "topographicPlace",
            "siteType",
            "atCentre",
            "locale",
            "levels",
            "entrances",
            "equipmentPlaces",
            "localServices",
            "members",
            // SiteElement — descriptive and accessibility attributes not modelled for Parking
            "facilities",
            "nameSuffix",
            "crossRoad",
            "landmark",
            "publicUse",
            "covered",
            "gated",
            "allAreasWheelchairAccessible",
            "personCapacity",
            // Place / addressable place
            "url",
            "image",
            "placeTypes",
            // Zone geometry other than centroid — Parking exposes only "geometry" (centroid)
            "polygon",
            "multiSurface",
            "projections",
            // Group of entities
            "shortName",
            "description",
            "privateCode"
    );

    /**
     * Fields whose GraphQL name differs from the Java field name on {@code Parking}. Currently
     * only {@code centroid}, exposed as {@code geometry} (see {@code geometryFieldDefinition}
     * / {@code GEOMETRY} in {@link org.rutebanken.tiamat.rest.graphql.GraphQLNames}).
     */
    private static final java.util.Map<String, String> GRAPHQL_FIELD_NAME_OVERRIDES = java.util.Map.of(
            "centroid", "geometry"
    );

    @Test
    public void everyParkingFieldIsEitherInGraphQLSchemaOrKnowinglyNot() {
        GraphQLObjectType outputType = CustomGraphQLTypes.createParkingObjectType(dummyValidBetweenObjectType());
        GraphQLInputObjectType inputType = CustomGraphQLTypes.createParkingInputObjectType(dummyValidBetweenInputObjectType());

        Set<String> outputFieldNames = outputType.getFieldDefinitions().stream()
                .map(field -> field.getName())
                .collect(Collectors.toCollection(TreeSet::new));
        Set<String> inputFieldNames = inputType.getFields().stream()
                .map(field -> field.getName())
                .collect(Collectors.toCollection(TreeSet::new));

        Set<String> undeclared = new TreeSet<>(persistentFieldNames());
        undeclared.removeAll(IN_GRAPHQL_SCHEMA);
        undeclared.removeAll(INFRASTRUCTURE);
        undeclared.removeAll(KNOWINGLY_NOT_IN_GRAPHQL_SCHEMA);

        assertThat(undeclared)
                .as("""
                        Parking has field(s) that the GraphQL schema factory methods have not \
                        been told about: %s

                        A field absent from Parking's GraphQL object/input types cannot be read \
                        or written via the API at all, and unlike a mapping bug this fails loudly \
                        as a schema validation error only once someone actually tries to query or \
                        mutate it - nothing in the test suite forces that to happen.

                        Decide and record the decision by adding each name to one of the sets in \
                        this test:
                          IN_GRAPHQL_SCHEMA               - add the field to both
                        createParkingObjectType and createParkingInputObjectType in
                        CustomGraphQLTypes, handle it in ParkingUpdater, and add a
                        testMutateParkingWith<Field>* test to
                        GraphQLResourceParkingIntegrationTest
                          KNOWINGLY_NOT_IN_GRAPHQL_SCHEMA - if it is out of scope; say why in the
                        comment above the relevant group
                          INFRASTRUCTURE                  - if it is identity, audit or versioning
                        metadata\
                        """.formatted(undeclared))
                .isEmpty();

        // Every field this test expects in the schema must actually be present on both the
        // output and input types - not just declared as intended here.
        Set<String> missingFromOutput = new TreeSet<>(IN_GRAPHQL_SCHEMA);
        missingFromOutput.removeAll(outputFieldNames);
        missingFromOutput.removeIf(field -> {
            String override = GRAPHQL_FIELD_NAME_OVERRIDES.get(field);
            return override != null && outputFieldNames.contains(override);
        });
        assertThat(missingFromOutput)
                .as("Field(s) declared IN_GRAPHQL_SCHEMA but missing from the Parking output "
                        + "type actually built by createParkingObjectType: %s", missingFromOutput)
                .isEmpty();

        Set<String> missingFromInput = new TreeSet<>(IN_GRAPHQL_SCHEMA);
        missingFromInput.removeAll(inputFieldNames);
        missingFromInput.removeIf(field -> {
            String override = GRAPHQL_FIELD_NAME_OVERRIDES.get(field);
            return override != null && inputFieldNames.contains(override);
        });
        assertThat(missingFromInput)
                .as("Field(s) declared IN_GRAPHQL_SCHEMA but missing from the Parking input "
                        + "type actually built by createParkingInputObjectType: %s", missingFromInput)
                .isEmpty();
    }

    /**
     * Catches the opposite drift: a name left behind in one of the sets after the field it
     * referred to was renamed or removed, which would otherwise quietly weaken the guard.
     */
    @Test
    public void declaredFieldNamesAllExistOnParking() {
        Set<String> declared = new LinkedHashSet<>();
        declared.addAll(IN_GRAPHQL_SCHEMA);
        declared.addAll(INFRASTRUCTURE);
        declared.addAll(KNOWINGLY_NOT_IN_GRAPHQL_SCHEMA);

        Set<String> stale = new TreeSet<>(declared);
        stale.removeAll(persistentFieldNames());

        assertThat(stale)
                .as("Field name(s) declared in this test no longer exist on Parking or its "
                        + "supertypes: %s. Remove them, or correct them if the field was renamed.", stale)
                .isEmpty();
    }

    @Test
    public void theThreeSetsDoNotOverlap() {
        assertThat(intersection(IN_GRAPHQL_SCHEMA, INFRASTRUCTURE))
                .as("A field cannot be both in the schema and infrastructure").isEmpty();
        assertThat(intersection(IN_GRAPHQL_SCHEMA, KNOWINGLY_NOT_IN_GRAPHQL_SCHEMA))
                .as("A field cannot be both in the schema and knowingly not in it").isEmpty();
        assertThat(intersection(INFRASTRUCTURE, KNOWINGLY_NOT_IN_GRAPHQL_SCHEMA))
                .as("A field cannot be both infrastructure and knowingly not in the schema").isEmpty();
    }

    private static GraphQLObjectType dummyValidBetweenObjectType() {
        return newObject()
                .name("DummyValidBetween")
                .field(f -> f.name("fromDate").type(GraphQLString))
                .build();
    }

    private static GraphQLInputObjectType dummyValidBetweenInputObjectType() {
        return GraphQLInputObjectType.newInputObject()
                .name("DummyValidBetweenInput")
                .field(newInputObjectField().name("fromDate").type(GraphQLString))
                .build();
    }

    /**
     * Every instance field on {@code Parking} and its supertypes, by name.
     */
    private static Set<String> persistentFieldNames() {
        Set<String> names = new TreeSet<>();
        for (Class<?> type = Parking.class; type != null && !Object.class.equals(type); type = type.getSuperclass()) {
            Arrays.stream(type.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .filter(field -> !field.isSynthetic())
                    .map(Field::getName)
                    .forEach(names::add);
        }
        return names;
    }

    private static Set<String> intersection(Set<String> a, Set<String> b) {
        Set<String> result = new TreeSet<>(a);
        result.retainAll(b);
        return result;
    }
}
