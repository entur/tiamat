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
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

import java.util.HashSet;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests that {@link Quay#hashCode()} and {@link Quay#equals(Object)} do not
 * have side effects that modify the entity's {@code keyValues} map.
 *
 * Both methods currently call {@code getOrCreateValues("imported-id")}, which
 * creates a new {@code Value} entity in the map when the key doesn't exist.
 * On Hibernate-managed entities this dirties the {@code keyValues} PersistentMap,
 * causing unexpected INSERTs on auto-flush and ultimately:
 *   ERROR: duplicate key value violates unique constraint "quay_key_values_pkey"
 *
 * These tests FAIL with the current code, proving the bug exists.
 * They should PASS once the side effect in hashCode/equals is fixed.
 *
 * See: Quay.hashCode() line 126, Quay.equals() line 119,
 *      DataManagedObjectStructure.getOrCreateValues() line 69
 */
public class GraphQLResourceBboxQueryDuplicateKeyTest extends AbstractGraphQLResourceIntegrationTest {

    /**
     * hashCode() must not modify the entity's keyValues map.
     * Currently FAILS because hashCode() calls getOrCreateValues("imported-id")
     * which inserts a new empty Value when the key is absent.
     */
    @Test
    public void quayHashCodeShouldNotModifyKeyValues() {
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("Quay Without ImportedId"));

        int sizeBefore = quay.getKeyValues().size();
        assertFalse("Precondition: quay should not have imported-id key",
                quay.getKeyValues().containsKey(NetexIdMapper.ORIGINAL_ID_KEY));

        quay.hashCode();

        assertFalse("hashCode() must not create an 'imported-id' entry in keyValues",
                quay.getKeyValues().containsKey(NetexIdMapper.ORIGINAL_ID_KEY));
        assertEquals("hashCode() must not change keyValues size",
                sizeBefore, quay.getKeyValues().size());
    }

    /**
     * equals() must not modify the keyValues map of either operand.
     * Currently FAILS because equals() calls getOrCreateValues("imported-id")
     * on both quays when the preceding fields all match.
     */
    @Test
    public void quayEqualsShouldNotModifyKeyValues() {
        Quay quay1 = new Quay();
        quay1.setName(new EmbeddableMultilingualString("Same Name"));

        Quay quay2 = new Quay();
        quay2.setName(new EmbeddableMultilingualString("Same Name"));

        assertFalse(quay1.getKeyValues().containsKey(NetexIdMapper.ORIGINAL_ID_KEY));
        assertFalse(quay2.getKeyValues().containsKey(NetexIdMapper.ORIGINAL_ID_KEY));

        quay1.equals(quay2);

        assertFalse("equals() must not create an 'imported-id' entry on quay1",
                quay1.getKeyValues().containsKey(NetexIdMapper.ORIGINAL_ID_KEY));
        assertFalse("equals() must not create an 'imported-id' entry on quay2",
                quay2.getKeyValues().containsKey(NetexIdMapper.ORIGINAL_ID_KEY));
    }

    /**
     * Calling hashCode() on a Hibernate-managed Quay must not dirty the
     * keyValues collection. Currently FAILS because the created Value
     * entry marks the PersistentMap dirty, scheduling an INSERT on auto-flush.
     */
    @Test
    public void hashCodeOnManagedQuayShouldNotDirtyKeyValuesCollection() {
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("Managed Quay"));

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test Stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(24.0, 61.5)));
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        saveStopPlaceTransactional(stopPlace);

        Long quayId = quay.getId();

        // Remove the spurious imported-id entry that hashCode() may have created during save
        var em = entityManagerFactory.createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        em.createNativeQuery(
                "DELETE FROM quay_key_values WHERE quay_id = :qid AND key_values_key = 'imported-id'")
                .setParameter("qid", quayId)
                .executeUpdate();
        em.createNativeQuery(
                "DELETE FROM value WHERE id NOT IN (SELECT key_values_id FROM quay_key_values) " +
                "AND id NOT IN (SELECT key_values_id FROM stop_place_key_values)")
                .executeUpdate();
        tx.commit();
        em.close();

        // Reload in a fresh EntityManager and call hashCode
        var em2 = entityManagerFactory.createEntityManager();
        var tx2 = em2.getTransaction();
        tx2.begin();

        Quay managedQuay = em2.find(Quay.class, quayId);
        assertFalse("Precondition: managed quay should not have imported-id after cleanup",
                managedQuay.getKeyValues().containsKey(NetexIdMapper.ORIGINAL_ID_KEY));

        managedQuay.hashCode();

        assertFalse("hashCode() must not dirty managed quay's keyValues by creating imported-id",
                managedQuay.getKeyValues().containsKey(NetexIdMapper.ORIGINAL_ID_KEY));

        tx2.rollback();
        em2.close();
    }

    /**
     * A stopPlaceBBox read-only query must not trigger any INSERT.
     * Exercises: StopPlaceFetcher → findStopPlacesWithin → EntityPermissionsFetcher.
     * The GraphQL response must not contain any errors.
     */
    @Test
    public void bboxQueryWithQuaysShouldNotCauseConstraintViolation() {
        Quay quay1 = new Quay();
        quay1.setName(new EmbeddableMultilingualString("Quay With ImportedId"));
        quay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("EXT:Quay:1");

        Quay quay2 = new Quay();
        quay2.setName(new EmbeddableMultilingualString("Quay Without ImportedId"));

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("BBox Test Stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(24.0, 61.5)));
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);
        saveStopPlaceTransactional(stopPlace);

        String bboxQuery = """
                {
                  stopPlaceBBox(
                    lonMin: 23.5, lonMax: 24.5, latMin: 61.0, latMax: 62.0,
                    includeExpired: false
                  ) {
                    id
                    version
                    name { value }
                    ... on StopPlace {
                      stopPlaceType
                      permissions { canEdit canDelete }
                    }
                  }
                }
                """;

        executeGraphqQLQueryOnly(bboxQuery)
                .body("data.stopPlaceBBox", notNullValue())
                .body("data.stopPlaceBBox", hasSize(1))
                .body("errors", org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.nullValue(), empty()));
    }

    /**
     * Bbox query with parent/child stops must not fail.
     * Exercises resolveParents() → findFirstByNetexIdAndVersion (auto-flush trigger).
     */
    @Test
    public void bboxQueryWithParentChildStopsShouldNotCauseConstraintViolation() {
        StopPlace parent = new StopPlace(new EmbeddableMultilingualString("Parent Stop"));
        parent.setParentStopPlace(true);
        parent.setVersion(1L);
        parent.setCentroid(geometryFactory.createPoint(new Coordinate(24.0, 61.5)));
        stopPlaceRepository.save(parent);

        Quay childQuay1 = new Quay();
        childQuay1.setName(new EmbeddableMultilingualString("Child Quay With ImportedId"));
        childQuay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("EXT:Quay:child1");

        Quay childQuay2 = new Quay();
        childQuay2.setName(new EmbeddableMultilingualString("Child Quay No ImportedId"));

        StopPlace child = new StopPlace(new EmbeddableMultilingualString("Child Stop"));
        child.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        child.setCentroid(geometryFactory.createPoint(new Coordinate(24.0, 61.5)));
        child.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));
        child.setQuays(new HashSet<>());
        child.getQuays().add(childQuay1);
        child.getQuays().add(childQuay2);
        child.setVersion(1L);
        stopPlaceRepository.save(child);

        parent.getChildren().add(child);
        stopPlaceRepository.save(parent);

        String bboxQuery = """
                {
                  stopPlaceBBox(
                    lonMin: 23.5, lonMax: 24.5, latMin: 61.0, latMax: 62.0,
                    includeExpired: false
                  ) {
                    id
                    version
                    name { value }
                    ... on StopPlace {
                      stopPlaceType
                      permissions { canEdit canDelete }
                    }
                    ... on ParentStopPlace {
                      permissions { canEdit canDelete }
                      children {
                        id version stopPlaceType
                        permissions { canEdit canDelete }
                      }
                    }
                  }
                }
                """;

        executeGraphqQLQueryOnly(bboxQuery)
                .body("data.stopPlaceBBox", notNullValue())
                .body("errors", org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.nullValue(), empty()));
    }

    /**
     * Bbox query after a mutation must not fail.
     * After mutation creates V2, the bbox query loads V2 while V1 rows still exist.
     */
    @Test
    public void bboxQueryAfterMutationShouldNotCauseConstraintViolation() {
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("Mutated Quay"));
        quay.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("EXT:Quay:mutated");

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Mutated Stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(24.0, 61.5)));
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        saveStopPlaceTransactional(stopPlace);

        String mutation = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {
                    id: "%s"
                    name: { value: "Mutated Stop V2" }
                  }) {
                    id version name { value }
                  }
                }
                """.formatted(stopPlace.getNetexId());

        executeGraphqQLQueryOnly(mutation)
                .body("data.stopPlace[0].name.value", org.hamcrest.Matchers.equalTo("Mutated Stop V2"));

        String bboxQuery = """
                {
                  stopPlaceBBox(
                    lonMin: 23.5, lonMax: 24.5, latMin: 61.0, latMax: 62.0,
                    includeExpired: false
                  ) {
                    id
                    version
                    name { value }
                    ... on StopPlace {
                      stopPlaceType
                      permissions { canEdit canDelete }
                    }
                  }
                }
                """;

        executeGraphqQLQueryOnly(bboxQuery)
                .body("data.stopPlaceBBox", notNullValue())
                .body("data.stopPlaceBBox[0].id", org.hamcrest.Matchers.equalTo(stopPlace.getNetexId()))
                .body("errors", org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.nullValue(), empty()));
    }
}
