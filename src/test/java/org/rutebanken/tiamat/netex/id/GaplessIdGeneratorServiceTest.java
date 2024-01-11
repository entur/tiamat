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

package org.rutebanken.tiamat.netex.id;

import com.hazelcast.collection.IQueue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netex.id.GaplessIdGeneratorService.LOW_LEVEL_AVAILABLE_IDS;

public class GaplessIdGeneratorServiceTest extends TiamatIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private NetexIdHelper netexIdHelper;

    @Test
    public void verifyNetexIdAssignedToStop() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);
        assertThat(stopPlace.getNetexId()).isNotNull();
    }

    @Test
    public void explicitIdMustBeInsertedIntoHelperTable() {

        long wantedId = 11L;
        insertQuay(wantedId, new Quay());

        long actual = selectSingleInsertedId(Quay.class.getSimpleName(), wantedId);

        assertThat(actual).describedAs("Expecting to find the ID in the id_generator table").isEqualTo(wantedId);
    }

    @Test
    public void multipleExplicitIdMustBeInsertedIntoHelperTable() {

        long wantedId1 = 12L;
        insertQuay(wantedId1, new Quay());

        // first one will be inserted as level is low

        long wantedId2 = 10L;
        insertQuay(wantedId2, new Quay());

        // second with not be inserted because level is not low - insertion

        long actualWantedId1 = selectSingleInsertedId(Quay.class.getSimpleName(), wantedId1);
        assertThat(actualWantedId1).describedAs("Expecting to find the ID in the id_generator table").isEqualTo(wantedId1);

        // We cannot check that the second value was insterted, because the first call will, because of the low level create new available IDs.
        // But, we can check that the second claimed ID is not available anymore
        assertThat(generatedIdState.getQueueForEntity(Quay.class.getSimpleName())).doesNotContain(wantedId2);
    }

    private long selectSingleInsertedId(String tableName, long expectedId) {

        Query query = entityManager.createNativeQuery("SELECT id_value FROM id_generator WHERE table_name = '" + tableName + "' AND id_value = '" + expectedId + "'");

        List list = query.getResultList();
        assertThat(list).hasSize(1);
        BigInteger actual = (BigInteger) list.get(0);
        return actual.longValue();
    }

    private Quay insertQuay(long wantedId, Quay quay) {
        String wantedNetexIdId = netexIdHelper.getNetexId("Quay", wantedId);
        quay.setNetexId(wantedNetexIdId);
        quayRepository.save(quay);
        return quay;
    }

    @Test
    public void generateIdAfterExplicitIDs() throws InterruptedException {

        // Use first 500 IDs
        for (long explicitId = 1; explicitId <= 30; explicitId++) {
            Quay quay = new Quay();
            quay.setNetexId(netexIdHelper.getNetexId(Quay.class.getSimpleName(), explicitId));
            quayRepository.save(quay);
            System.out.println("Saved quay: " + quay.getNetexId());
        }

        Quay quay = new Quay();
        quayRepository.save(quay);
        assertThat(netexIdHelper.extractIdPostfixNumeric(quay.getNetexId())).isEqualTo(31);
    }

    @Test
    public void testIdGeneration() {

        final String testEntityName = "testEntityName";
        int fetchSize = LOW_LEVEL_AVAILABLE_IDS;
        GaplessIdGeneratorService gaplessIdGeneratorService = new GaplessIdGeneratorService(entityManagerFactory, hazelcastInstance, generatedIdState, fetchSize);
        long actual = gaplessIdGeneratorService.getNextIdForEntity(testEntityName);

        assertThat(actual).as("generated id is last id plus one").isEqualTo(1L);

        IQueue lastIds = generatedIdState.getQueueForEntity(testEntityName);
        assertThat(lastIds).as("Last ids for " + testEntityName + " is fetch size minus one used").hasSize(fetchSize - 1);

        long lastId = generatedIdState.getLastIdForEntity(testEntityName);
        assertThat(lastId).as("last id for entity after generation same as max value in last ids").isEqualTo(Collections.max(lastIds));

        // Verify claiming
        long explicityClaimed = gaplessIdGeneratorService.getNextIdForEntity(testEntityName, 3L);
        assertThat(explicityClaimed).isEqualTo(3L);

        // Verify hole filled
        long nextGenerated = gaplessIdGeneratorService.getNextIdForEntity(testEntityName);
        assertThat(nextGenerated).isEqualTo(2L);
    }

    /**
     * Was implemented under the supsicion that {@link GaplessIdGeneratorService} caused a bug.
     * But it was instead a matter of keeping the attached returned entity from save (in case the entity was merged)
     * See NRP-1171
     */
    @Test
    public void testUpdatingStopPlace() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("test"));
        stopPlace = stopPlaceRepository.save(stopPlace);

        assertThat(stopPlace.getNetexId()).isNotNull();
        String id = stopPlace.getNetexId();

        Quay quay = new Quay(new EmbeddableMultilingualString("quayTest"));
        quay = quayRepository.save(quay);

        //Add Quay, and save StopPlace
        stopPlace.getQuays().add(quay);
        stopPlace = stopPlaceRepository.save(stopPlace);

        Quay quay2 = new Quay(new EmbeddableMultilingualString("quay2Test"));
        quay2 = quayRepository.save(quay2);

        //Add another Quay and save StopPlace
        stopPlace.getQuays().add(quay2);
        stopPlace = stopPlaceRepository.save(stopPlace);

        assertEquals(id, stopPlace.getNetexId());
    }

    @Test
    public void claimIds() {

        String entityName = "testEntityName2";

        int fetchSize = LOW_LEVEL_AVAILABLE_IDS;
        GaplessIdGeneratorService gaplessIdGeneratorService = new GaplessIdGeneratorService(entityManagerFactory, hazelcastInstance, generatedIdState, fetchSize);

        /**
         * Offset to not claim ids that will be present in the available ids queue.
         */
        int offset = LOW_LEVEL_AVAILABLE_IDS + fetchSize;

        Set<Long> claimedIds = new HashSet<>();

        for (int excplicityClaimedId = offset; excplicityClaimedId < offset + 10; excplicityClaimedId++) {
            System.out.println("adding explicity claimed id " + excplicityClaimedId);
            claimedIds.add(gaplessIdGeneratorService.getNextIdForEntity(entityName, excplicityClaimedId));
        }

        assertThat(generatedIdState.getQueueForEntity(entityName)).as("available ids for entity after claiming").doesNotContainAnyElementsOf(claimedIds);

        gaplessIdGeneratorService.persistClaimedIds();

        assertThat(generatedIdState.getQueueForEntity(entityName)).as("available ids for entity after writing claimed ids").doesNotContainAnyElementsOf(claimedIds);

        for (long excpectedId : claimedIds) {
            long actual = selectSingleInsertedId(entityName, excpectedId);

            assertThat(actual).as("Claimed ID found in the ID generator table+").isEqualTo(excpectedId);
        }

        assertThat(generatedIdState.getClaimedIdListForEntity(entityName)).as("claimed ids for entity after writing").doesNotContainAnyElementsOf(claimedIds);
    }

    @Test
    public void ignoreDuplicateIdsOnInsert() {
        String entityName = "testEntityName2";
        int fetchSize = LOW_LEVEL_AVAILABLE_IDS;
        GaplessIdGeneratorService gaplessIdGeneratorService = new GaplessIdGeneratorService(entityManagerFactory, hazelcastInstance, generatedIdState, fetchSize);
        Set<Long> claimedIds = new HashSet<>();
        claimedIds.add(gaplessIdGeneratorService.getNextIdForEntity(entityName, 10L));
        gaplessIdGeneratorService.persistClaimedIds();
        claimedIds.add(gaplessIdGeneratorService.getNextIdForEntity(entityName, 10L));
        gaplessIdGeneratorService.persistClaimedIds();
        assertThat(claimedIds.size()).isEqualTo(1);

    }

}
