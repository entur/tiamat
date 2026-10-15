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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class GaplessIdGeneratorServiceTest extends TiamatIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private NetexIdHelper netexIdHelper;

    @Autowired
    private GaplessIdGeneratorService gaplessIdGeneratorService;

    @Autowired
    private DataSource dataSource;

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

        long wantedId2 = 10L;
        insertQuay(wantedId2, new Quay());

        assertThat(selectSingleInsertedId(Quay.class.getSimpleName(), wantedId1)).isEqualTo(wantedId1);
        assertThat(selectSingleInsertedId(Quay.class.getSimpleName(), wantedId2)).isEqualTo(wantedId2);
    }

    @Test
    public void generateIdAfterExplicitIDs() {

        for (long explicitId = 1; explicitId <= 30; explicitId++) {
            Quay quay = new Quay();
            quay.setNetexId(netexIdHelper.getNetexId(Quay.class.getSimpleName(), explicitId));
            quayRepository.save(quay);
        }

        Quay quay = new Quay();
        quayRepository.save(quay);
        assertThat(netexIdHelper.extractIdPostfixNumeric(quay.getNetexId())).isEqualTo(31);
    }

    @Test
    public void testIdGeneration() {

        final String testEntityName = "testEntityName";

        assertThat(gaplessIdGeneratorService.getNextIdForEntity(testEntityName)).as("first generated id").isEqualTo(1L);

        long explicitlyClaimed = gaplessIdGeneratorService.getNextIdForEntity(testEntityName, 3L);
        assertThat(explicitlyClaimed).isEqualTo(3L);

        assertThat(gaplessIdGeneratorService.getNextIdForEntity(testEntityName)).as("hole before the claimed id is filled").isEqualTo(2L);
        assertThat(gaplessIdGeneratorService.getNextIdForEntity(testEntityName)).as("claimed id is skipped").isEqualTo(4L);
    }

    @Test
    public void claimedIdsAreRecordedImmediately() {

        String entityName = "testEntityName2";

        for (long claimedId = 20; claimedId < 30; claimedId++) {
            assertThat(gaplessIdGeneratorService.getNextIdForEntity(entityName, claimedId)).isEqualTo(claimedId);
            assertThat(selectSingleInsertedId(entityName, claimedId)).as("Claimed ID found in the ID generator table").isEqualTo(claimedId);
        }
    }

    @Test
    public void ignoreDuplicateIdsOnInsert() {
        String entityName = "testEntityName2";

        assertThat(gaplessIdGeneratorService.getNextIdForEntity(entityName, 10L)).isEqualTo(10L);
        assertThat(gaplessIdGeneratorService.getNextIdForEntity(entityName, 10L)).isEqualTo(10L);

        assertThat(selectSingleInsertedId(entityName, 10L)).isEqualTo(10L);
    }

    @Test
    public void searchContinuesPastAFullWindow() throws SQLException {
        String entityName = "testEntityName3";
        long usedIds = GaplessIdGeneratorService.SEARCH_WINDOW + 500;
        executeSql("insert into id_generator (table_name, id_value) select '" + entityName + "', id from generate_series(1, " + usedIds + ") id");

        assertThat(gaplessIdGeneratorService.getNextIdForEntity(entityName)).isEqualTo(usedIds + 1);
    }

    @Test
    public void positionIsSharedBetweenGenerators() {
        String entityName = "testEntityName4";
        GaplessIdGeneratorService otherInstance = new GaplessIdGeneratorService(dataSource);

        assertThat(gaplessIdGeneratorService.getNextIdForEntity(entityName)).isEqualTo(1L);
        assertThat(otherInstance.getNextIdForEntity(entityName)).isEqualTo(2L);
        assertThat(gaplessIdGeneratorService.getNextIdForEntity(entityName)).isEqualTo(3L);
    }

    @Test
    public void concurrentGenerationGivesUniqueIdsWithoutGaps() throws Exception {
        String entityName = "testEntityName5";
        int threads = 8;
        int idsPerThread = 50;
        Set<Long> ids = ConcurrentHashMap.newKeySet();

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int thread = 0; thread < threads; thread++) {
                futures.add(executor.submit(() -> {
                    for (int i = 0; i < idsPerThread; i++) {
                        assertThat(ids.add(gaplessIdGeneratorService.getNextIdForEntity(entityName))).as("id is unique").isTrue();
                    }
                }));
            }
            for (Future<?> future : futures) {
                future.get();
            }
        } finally {
            executor.shutdownNow();
        }

        Set<Long> expected = LongStream.rangeClosed(1, (long) threads * idsPerThread).boxed().collect(Collectors.toSet());
        assertThat(ids).isEqualTo(expected);
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

    private long selectSingleInsertedId(String tableName, long expectedId) {

        Query query = entityManager.createNativeQuery("SELECT id_value FROM id_generator WHERE table_name = '" + tableName + "' AND id_value = '" + expectedId + "'");

        List list = query.getResultList();
        assertThat(list).hasSize(1);
        return (Long) list.getFirst();
    }

    private Quay insertQuay(long wantedId, Quay quay) {
        String wantedNetexIdId = netexIdHelper.getNetexId("Quay", wantedId);
        quay.setNetexId(wantedNetexIdId);
        quayRepository.save(quay);
        return quay;
    }

    private void executeSql(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
