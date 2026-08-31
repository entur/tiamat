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

package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

/**
 * Integration tests for duplicate key constraint violations during stop place mutations
 * and read queries.
 * These tests verify that:
 * - Mutating a stop place (with quay keyValues or accessibility assessments) and requesting
 *   the 'groups' field (which triggers Hibernate auto-flush) does NOT cause duplicate key
 *   violations on quay_key_values_pkey or accessibility_limitation constraints.
 *   VersionCreator (Orika) copies entities including their Hibernate-assigned ids;
 *   when the new version is flushed, cascaded re-inserts of already-persisted rows
 *   hit unique constraints.
 * - A read-only stopPlace query requesting quays { importedId } alongside groups { id }
 *   does NOT create phantom quay_key_values entries via getOriginalIds() side effects.
 *   getOriginalIds() calls getOrCreateValues() which inserts an empty Value into the
 *   keyValues map when the key is absent, dirtying the Hibernate session on a read path.
 * The 'groups' and 'permissions' field resolvers trigger native SQL queries that cause
 * Hibernate to auto-flush. Two distinct bugs can cause duplicate key violations:
 * 1. VersionCreator (Orika) copies retaining stale entity ids → duplicate inserts on flush
 * 2. getOriginalIds() → getOrCreateValues() mutating Hibernate-managed entities in
 *    read-only GraphQL fetchers → phantom entries flushed as unexpected INSERTs
 */
public class GraphQLResourceStopPlaceQuayKeyValuesDuplicateTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private VersionCreator versionCreator;

    /**
     * Verifies Orika copy behavior for VersionCreator:
     * - id is NOT copied (private setter) → null for all entities
     * - netexId IS copied (public setter) → retains original value
     * - version IS copied → retains original value
     * - keyValues map is deep-copied (different HashMap, different Value objects)
     */
    @Test
    public void versionCreatorCopyShouldNotRetainDatabaseIds() {
        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(LimitationStatusEnumeration.TRUE);
        limitation.setStepFreeAccess(LimitationStatusEnumeration.TRUE);
        limitation.setLiftFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setEscalatorFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setAudibleSignalsAvailable(LimitationStatusEnumeration.UNKNOWN);
        limitation.setVisualSignsAvailable(LimitationStatusEnumeration.UNKNOWN);

        AccessibilityAssessment assessment = new AccessibilityAssessment();
        assessment.setLimitations(new ArrayList<>(List.of(limitation)));

        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("Diag Quay"));
        quay.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("EXTERNAL:Quay:diag");

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Diag Stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAccessibilityAssessment(assessment);
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);

        StopPlace saved = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        Quay savedQuay = saved.getQuays().iterator().next();

        // Create copy and verify ids are null (not shared with original)
        StopPlace copy = versionCreator.createCopy(saved, StopPlace.class);
        assertNull("Copy StopPlace id should be null", copy.getId());
        assertNotNull("Copy StopPlace netexId should be retained", copy.getNetexId());

        Quay copyQuay = copy.getQuays().iterator().next();
        assertNull("Copy Quay id should be null", copyQuay.getId());
        assertNotNull("Copy Quay netexId should be retained", copyQuay.getNetexId());

        // Verify keyValues map is NOT shared
        assertNotSame("Copy keyValues map should be a different object",
                savedQuay.getKeyValues(), copyQuay.getKeyValues());
    }

    /**
     * Reproduces production error: duplicate key violation on quay_key_values_pkey
     * when mutating a stop place with quay keyValues and requesting 'groups'.
     * The mutation sets the same imported-id that was already persisted. The
     * GroupOfEntitiesMapper clears then re-adds the keyValues entry, producing a
     * new Value object with the same content. When 'groups' triggers Hibernate
     * auto-flush, the re-added Value is inserted alongside the existing row,
     * hitting the (quay_id, key_values_key) primary key constraint.
     */
    @Test
    public void mutateQuayKeyValuesWithGroupsShouldNotCauseDuplicateKeyValues() {

        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("Test Quay"));
        quay.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("EXTERNAL:Quay:1234");

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test Stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        // Re-set the same imported-id — GroupOfEntitiesMapper clear()s then put()s
        String graphQlJsonQuery = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {
                    id: "%s"
                    quays: [{
                      id: "%s"
                      keyValues: [{ key: "imported-id", values: ["EXTERNAL:Quay:1234"] }]
                    }]
                  }) {
                    id
                    name { value }
                    ... on StopPlace {
                      quays { id keyValues { key values } }
                      groups { id name { value } }
                    }
                  }
                }
                """.formatted(stopPlace.getNetexId(), quay.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()))
                .body("quays[0].id", notNullValue());
    }

    /**
     * Reproduces production error: duplicate key violation on
     * accessibility_limitation_netex_id_version_constraint during mutateStopPlace.
     * VersionCreator (Orika) copies the AccessibilityLimitation including its
     * netexId and version. When the new stop place version is saved and 'groups'
     * triggers auto-flush, the copied limitation is inserted as a new row while
     * the original row with the same (netex_id, version) still exists.
     */
    @Test
    public void mutateStopPlaceWithAccessibilityAndGroupsShouldNotCauseDuplicateConstraintViolation() {

        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(LimitationStatusEnumeration.TRUE);
        limitation.setStepFreeAccess(LimitationStatusEnumeration.TRUE);
        limitation.setLiftFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setEscalatorFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setAudibleSignalsAvailable(LimitationStatusEnumeration.UNKNOWN);
        limitation.setVisualSignsAvailable(LimitationStatusEnumeration.UNKNOWN);

        AccessibilityAssessment assessment = new AccessibilityAssessment();
        assessment.setLimitations(new ArrayList<>(List.of(limitation)));

        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("Accessible Quay"));

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Accessible Stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAccessibilityAssessment(assessment);
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        // Mutate (change name only) and request 'groups' to trigger auto-flush
        String graphQlJsonQuery = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {
                    id: "%s"
                    name: { value: "Updated Accessible Stop" }
                  }) {
                    id
                    name { value }
                    ... on StopPlace {
                      quays { id }
                      groups { id }
                    }
                  }
                }
                """.formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()))
                .body("name.value", equalTo("Updated Accessible Stop"));
    }

    /**
     * More complex scenario: mutate accessibility AND keyValues, then
     * mutate AGAIN. The second mutation may trigger the duplicate when
     * the copied entities from the second version conflict.
     */
    @Test
    public void doubleMutationWithAccessibilityAndKeyValuesShouldNotCauseDuplicates() {

        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(LimitationStatusEnumeration.TRUE);
        limitation.setStepFreeAccess(LimitationStatusEnumeration.TRUE);
        limitation.setLiftFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setEscalatorFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setAudibleSignalsAvailable(LimitationStatusEnumeration.UNKNOWN);
        limitation.setVisualSignsAvailable(LimitationStatusEnumeration.UNKNOWN);

        AccessibilityAssessment assessment = new AccessibilityAssessment();
        assessment.setLimitations(new ArrayList<>(List.of(limitation)));

        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("Double Quay"));
        quay.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("EXTERNAL:Quay:double");

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Double Stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAccessibilityAssessment(assessment);
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        // First mutation: change accessibility and request groups
        String firstMutation = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {
                    id: "%s"
                    accessibilityAssessment: {
                      limitations: {
                        wheelchairAccess: PARTIAL
                        stepFreeAccess: TRUE
                        liftFreeAccess: UNKNOWN
                        escalatorFreeAccess: UNKNOWN
                        audibleSignalsAvailable: UNKNOWN
                        visualSignsAvailable: UNKNOWN
                      }
                    }
                  }) {
                    id
                    name { value }
                    ... on StopPlace {
                      quays { id }
                      groups { id }
                    }
                  }
                }
                """.formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(firstMutation)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()));

        // Second mutation: change name + keyValues and request groups
        String secondMutation = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {
                    id: "%s"
                    name: { value: "Double Stop V3" }
                    quays: [{
                      id: "%s"
                      keyValues: [{ key: "imported-id", values: ["EXTERNAL:Quay:double-v3"] }]
                    }]
                  }) {
                    id
                    name { value }
                    ... on StopPlace {
                      quays { id keyValues { key values } }
                      groups { id }
                    }
                  }
                }
                """.formatted(stopPlace.getNetexId(), quay.getNetexId());

        executeGraphqQLQueryOnly(secondMutation)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()))
                .body("name.value", equalTo("Double Stop V3"));
    }

    /**
     * Reproduces production error: duplicate key violation on
     * accessibility_limitation_netex_id_version_constraint when the 'permissions'
     * field triggers Hibernate auto-flush.
     * Scenario: stop place with stop-level accessibility is mutated
     * with unchanged accessibility. The 'permissions' field triggers
     * EntityPermissionsFetcher which calls findFirstByNetexIdOrderByVersionDesc,
     * causing an auto-flush that may expose duplicate AccessibilityLimitation inserts.
     */
    @Test
    public void mutateStopPlaceWithPermissionsFieldShouldNotCauseDuplicateAccessibilityLimitation() {

        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(LimitationStatusEnumeration.TRUE);
        limitation.setStepFreeAccess(LimitationStatusEnumeration.TRUE);
        limitation.setLiftFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setEscalatorFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setAudibleSignalsAvailable(LimitationStatusEnumeration.UNKNOWN);
        limitation.setVisualSignsAvailable(LimitationStatusEnumeration.UNKNOWN);

        AccessibilityAssessment assessment = new AccessibilityAssessment();
        assessment.setLimitations(new ArrayList<>(List.of(limitation)));

        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("Permissions Quay"));

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Permissions Stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAccessibilityAssessment(assessment);
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        // Mutate name only, request permissions field (triggers EntityPermissionsFetcher auto-flush)
        String graphQlJsonQuery = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {
                    id: "%s"
                    name: { value: "Permissions Stop Updated" }
                  }) {
                    id
                    name { value }
                    ... on StopPlace {
                      quays { id }
                      permissions { canEdit canDelete }
                    }
                  }
                }
                """.formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()))
                .body("name.value", equalTo("Permissions Stop Updated"));
    }

    /**
     * Tests the scenario where quays have different accessibility assessments
     * (kept at quay level by optimizer), then a mutation makes them equal
     * (optimizer consolidates to stop level). The permissions field triggers
     * auto-flush which may expose duplicate AccessibilityLimitation inserts
     * from the orphaned Orika-copied quay limitations.
     */
    @Test
    public void mutateQuayAccessibilityToEqualThenPermissionsShouldNotCauseDuplicate() {

        // v1: two quays with DIFFERENT accessibility → stay on quay level
        Quay quay1 = new Quay();
        quay1.setName(new EmbeddableMultilingualString("EqQuay1"));
        AccessibilityLimitation lim1 = new AccessibilityLimitation();
        lim1.setWheelchairAccess(LimitationStatusEnumeration.TRUE);
        lim1.setStepFreeAccess(LimitationStatusEnumeration.TRUE);
        lim1.setLiftFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        lim1.setEscalatorFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        lim1.setAudibleSignalsAvailable(LimitationStatusEnumeration.UNKNOWN);
        lim1.setVisualSignsAvailable(LimitationStatusEnumeration.UNKNOWN);
        AccessibilityAssessment aa1 = new AccessibilityAssessment();
        aa1.setLimitations(new ArrayList<>(List.of(lim1)));
        quay1.setAccessibilityAssessment(aa1);

        Quay quay2 = new Quay();
        quay2.setName(new EmbeddableMultilingualString("EqQuay2"));
        AccessibilityLimitation lim2 = new AccessibilityLimitation();
        lim2.setWheelchairAccess(LimitationStatusEnumeration.FALSE);
        lim2.setStepFreeAccess(LimitationStatusEnumeration.FALSE);
        lim2.setLiftFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        lim2.setEscalatorFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        lim2.setAudibleSignalsAvailable(LimitationStatusEnumeration.UNKNOWN);
        lim2.setVisualSignsAvailable(LimitationStatusEnumeration.UNKNOWN);
        AccessibilityAssessment aa2 = new AccessibilityAssessment();
        aa2.setLimitations(new ArrayList<>(List.of(lim2)));
        quay2.setAccessibilityAssessment(aa2);

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("EqStop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        // v2: set both quays to same accessibility via mutation → optimizer consolidates
        String graphQlJsonQuery = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {
                    id: "%s"
                    quays: [
                      {
                        id: "%s"
                        accessibilityAssessment: {
                          limitations: {
                            wheelchairAccess: TRUE
                            stepFreeAccess: TRUE
                            liftFreeAccess: UNKNOWN
                            escalatorFreeAccess: UNKNOWN
                            audibleSignalsAvailable: UNKNOWN
                            visualSignsAvailable: UNKNOWN
                          }
                        }
                      },
                      {
                        id: "%s"
                        accessibilityAssessment: {
                          limitations: {
                            wheelchairAccess: TRUE
                            stepFreeAccess: TRUE
                            liftFreeAccess: UNKNOWN
                            escalatorFreeAccess: UNKNOWN
                            audibleSignalsAvailable: UNKNOWN
                            visualSignsAvailable: UNKNOWN
                          }
                        }
                      }
                    ]
                  }) {
                    id
                    ... on StopPlace {
                      quays { id }
                      permissions { canEdit canDelete }
                    }
                  }
                }
                """.formatted(stopPlace.getNetexId(), quay1.getNetexId(), quay2.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()));
    }

    /**
     * Tests three consecutive mutations on a stop place with accessibility.
     * Each mutation goes through the full Orika copy + optimizer + version
     * increment + save flow. The third mutation is most likely to trigger
     * duplicate limitations because multiple previous versions exist in the DB.
     */
    @Test
    public void threeConsecutiveMutationsWithAccessibilityShouldNotCauseDuplicate() {

        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setStepFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setLiftFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setEscalatorFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setAudibleSignalsAvailable(LimitationStatusEnumeration.UNKNOWN);
        limitation.setVisualSignsAvailable(LimitationStatusEnumeration.UNKNOWN);

        AccessibilityAssessment assessment = new AccessibilityAssessment();
        assessment.setLimitations(new ArrayList<>(List.of(limitation)));

        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("Triple Quay"));

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Triple Stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAccessibilityAssessment(assessment);
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        // Mutation 1: change accessibility
        String mutation1 = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {
                    id: "%s"
                    accessibilityAssessment: {
                      limitations: {
                        wheelchairAccess: TRUE
                        stepFreeAccess: TRUE
                        liftFreeAccess: TRUE
                        escalatorFreeAccess: UNKNOWN
                        audibleSignalsAvailable: UNKNOWN
                        visualSignsAvailable: UNKNOWN
                      }
                    }
                  }) {
                    id
                    ... on StopPlace { permissions { canEdit } }
                  }
                }
                """.formatted(stopPlace.getNetexId());
        executeGraphqQLQueryOnly(mutation1)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()));

        // Mutation 2: change name (accessibility unchanged)
        String mutation2 = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {
                    id: "%s"
                    name: { value: "Triple Stop v3" }
                  }) {
                    id
                    name { value }
                    ... on StopPlace { permissions { canEdit } }
                  }
                }
                """.formatted(stopPlace.getNetexId());
        executeGraphqQLQueryOnly(mutation2)
                .rootPath("data.stopPlace[0]")
                .body("name.value", equalTo("Triple Stop v3"));

        // Mutation 3: change accessibility again
        String mutation3 = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {
                    id: "%s"
                    accessibilityAssessment: {
                      limitations: {
                        wheelchairAccess: FALSE
                        stepFreeAccess: FALSE
                        liftFreeAccess: FALSE
                        escalatorFreeAccess: FALSE
                        audibleSignalsAvailable: FALSE
                        visualSignsAvailable: FALSE
                      }
                    }
                  }) {
                    id
                    ... on StopPlace { permissions { canEdit } }
                  }
                }
                """.formatted(stopPlace.getNetexId());
        executeGraphqQLQueryOnly(mutation3)
                .rootPath("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()));
    }

    /**
     * A read-only stopPlace query requesting quays { importedId } alongside groups { id }
     * must not create phantom quay_key_values rows.
     * Historical root cause in this GraphQL path: getOriginalIds() →
     * getOrCreateValues("imported-id") created a phantom empty Value entry on any
     * Hibernate-managed Quay that had no imported-id in the DB, dirtying the session.
     * When StopPlaceGroupsFetcher executed its native SQL query for 'groups', Hibernate
     * auto-flushed and inserted the phantom quay_key_values row.
     * Note: the stop places do NOT need to belong to any group. Requesting 'groups { id }'
     * in the query is sufficient — StopPlaceGroupsFetcher always executes a native SQL query
     * (which triggers the auto-flush) even when the result set is empty.
     * The fix: keep getOriginalIds() unchanged and make GraphQL importedId fetchers
     * read via getValues(NetexIdMapper.ORIGINAL_ID_KEY).
     */
    @Test
    public void readQueryWithImportedIdAndGroupsShouldNotCreatePhantomQuayKeyValues() {
        // Stop place 1: quay WITH an imported-id in the DB
        Quay quayWithId = new Quay();
        quayWithId.setName(new EmbeddableMultilingualString("Quay With ImportedId"));
        quayWithId.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("EXT:Quay:existing");

        StopPlace stopPlace1 = new StopPlace(new EmbeddableMultilingualString("Stop With ImportedId Quay"));
        stopPlace1.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace1.setCentroid(geometryFactory.createPoint(new Coordinate(10.0, 59.0)));
        stopPlace1.setQuays(new HashSet<>());
        stopPlace1.getQuays().add(quayWithId);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace1);

        // Stop place 2: quay WITHOUT any imported-id — this is the trigger
        Quay quayWithoutId = new Quay();
        quayWithoutId.setName(new EmbeddableMultilingualString("Quay Without ImportedId"));

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("Stop Without ImportedId Quay"));
        stopPlace2.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace2.setCentroid(geometryFactory.createPoint(new Coordinate(10.1, 59.0)));
        stopPlace2.setQuays(new HashSet<>());
        stopPlace2.getQuays().add(quayWithoutId);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace2);

        Long quayWithoutIdDbId = quayWithoutId.getId();
        deleteQuayImportedIdRows(quayWithoutIdDbId);

        // Regression context: importedId fetchers used to call getOriginalIds()
        // (which mutates via getOrCreateValues). They now call getValues().
        // groups still triggers StopPlaceGroupsFetcher native SQL → Hibernate auto-flush.
        // Neither stop place belongs to any group — that is not required to trigger the bug.
        String query = """
                {
                  stopPlace1: stopPlace(id: "%s") {
                    id
                    ... on StopPlace {
                      quays { id importedId }
                      groups { id name { value } }
                    }
                  }
                  stopPlace2: stopPlace(id: "%s") {
                    id
                    ... on StopPlace {
                      quays { id importedId }
                      groups { id name { value } }
                    }
                  }
                }
                """.formatted(stopPlace1.getNetexId(), stopPlace2.getNetexId());

        executeGraphqQLQueryOnly(query)
                .body("errors", anyOf(nullValue(), empty()));

        // A pure read must not have created a phantom imported-id entry for quayWithoutId.
        long phantomCount = countQuayImportedIdRows(quayWithoutIdDbId);
        assertEquals(
                "getOriginalIds() must not create a phantom imported-id entry in quay_key_values " +
                "for a quay that had no imported-id. Fix: GraphQL importedId fetchers must use getValues() instead of getOrCreateValues().",
                0, phantomCount);
    }

    /**
     * Regression check: requesting importedId for a persisted Quay that has no imported-id
     * must not write anything to quay_key_values.
     * Historically this path used getOriginalIds() → getOrCreateValues() and dirtied the
     * session. GraphQL importedId fetchers now use getValues().
     */
    @Test
    public void getOriginalIdsOnQuayWithoutImportedIdShouldNotCreateDbEntry() {
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("No ImportedId Quay"));

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("No ImportedId Stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.0, 59.0)));
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        Long quayId = quay.getId();
        deleteQuayImportedIdRows(quayId);

        // Request importedId together with groups (triggers StopPlaceGroupsFetcher
        // native SQL → Hibernate auto-flush). This guards against regressions where
        // importedId fetchers call the mutating getOriginalIds() path again.
        // The stop place need not belong to any group — requesting the field is enough.
        String query = """
                {
                  stopPlace(id: "%s") {
                    id
                    ... on StopPlace {
                      quays { id importedId }
                      groups { id }
                    }
                  }
                }
                """.formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(query)
                .body("errors", anyOf(nullValue(), empty()))
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()));

        long phantomCount = countQuayImportedIdRows(quayId);
        assertEquals(
                "getOriginalIds() must not insert a phantom quay_key_values row. " +
                "Fix: GraphQL importedId fetchers must use getValues() instead of getOrCreateValues().",
                0, phantomCount);
    }

    /**
     * Read-only query requesting importedId at the stop-place level alongside groups.
     * getOriginalIdsFetcher() is registered for StopPlace (not only Quay). Historically,
     * requesting importedId on a stop place without imported-id inserted a phantom empty Value
     * into the StopPlace keyValues map. This now guards the non-mutating fetcher behavior.
     */
    @Test
    public void readQueryWithStopPlaceImportedIdAndGroupsShouldNotCreatePhantomKeyValues() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop Without ImportedId"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.0, 59.0)));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        Long stopPlaceDbId = stopPlace.getId();

        String query = """
                {
                  stopPlace(id: "%s") {
                    id
                    ... on StopPlace {
                      importedId
                      groups { id }
                    }
                  }
                }
                """.formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(query)
                .body("errors", anyOf(nullValue(), empty()))
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()));

        long phantomCount = countStopPlaceImportedIdRows(stopPlaceDbId);
        assertEquals(
                "getOriginalIds() must not create a phantom imported-id entry in stop_place_key_values " +
                "for a stop place that had no imported-id. Fix: GraphQL importedId fetchers must use getValues() instead of getOrCreateValues().",
                0, phantomCount);
    }

    /**
     * Read-only query using the 'permissions' field as the flush trigger instead of 'groups'.
     * EntityPermissionsFetcher calls findFirstByNetexIdOrderByVersionDesc() — a JPQL derived
     * query — which triggers Hibernate auto-flush under FlushMode.AUTO just as native SQL does.
     * This guards against regressions where importedId fetchers reintroduce the mutating
     * getOriginalIds() path before the flush.
     */
    @Test
    public void readQueryWithImportedIdAndPermissionsShouldNotCreatePhantomQuayKeyValues() {
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("No ImportedId Quay"));

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("No ImportedId Stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.0, 59.0)));
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        Long quayId = quay.getId();
        deleteQuayImportedIdRows(quayId);

        String query = """
                {
                  stopPlace(id: "%s") {
                    id
                    ... on StopPlace {
                      quays { id importedId }
                      permissions { canEdit canDelete }
                    }
                  }
                }
                """.formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(query)
                .body("errors", anyOf(nullValue(), empty()))
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()));

        long phantomCount = countQuayImportedIdRows(quayId);
        assertEquals(
                "getOriginalIds() must not insert a phantom quay_key_values row when " +
                "'permissions' triggers the auto-flush. Fix: GraphQL importedId fetchers must use getValues() instead of getOrCreateValues().",
                0, phantomCount);
    }

    private long countQuayImportedIdRows(long quayId) {
        var em = entityManagerFactory.createEntityManager();
        try {
            return ((Number) em.createNativeQuery(
                    "SELECT COUNT(*) FROM quay_key_values WHERE quay_id = ?1 AND key_values_key = 'imported-id'")
                    .setParameter(1, quayId)
                    .getSingleResult()).longValue();
        } finally {
            em.close();
        }
    }

    private void deleteQuayImportedIdRows(long quayId) {
        var em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM quay_key_values WHERE quay_id = ?1 AND key_values_key = 'imported-id'")
                    .setParameter(1, quayId)
                    .executeUpdate();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    private long countStopPlaceImportedIdRows(long stopPlaceId) {
        var em = entityManagerFactory.createEntityManager();
        try {
            return ((Number) em.createNativeQuery(
                    "SELECT COUNT(*) FROM stop_place_key_values WHERE stop_place_id = ?1 AND key_values_key = 'imported-id'")
                    .setParameter(1, stopPlaceId)
                    .getSingleResult()).longValue();
        } finally {
            em.close();
        }
    }
}
