package org.rutebanken.tiamat.netex.id;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;

import static java.util.stream.Collectors.toList;

public class GaplessIdGeneratorTask implements Runnable, Serializable, HazelcastInstanceAware {

    private static final Logger logger = LoggerFactory.getLogger(GaplessIdGeneratorTask.class);

    private static final String LOCK_PREFIX = "entity_lock_";

    public static final int LOW_LEVEL_AVAILABLE_IDS = 10;

    private final String entityTypeName;
    private final boolean isH2;
    private final int fetchSize;
    private EntityManagerFactory entityManagerFactory;


    private transient HazelcastInstance hazelcastInstance;
    private GeneratedIdState generatedIdState;

    public GaplessIdGeneratorTask(String entityTypeName, boolean isH2, EntityManagerFactory entityManagerFactory, int fetchSize) {
        this.entityTypeName = entityTypeName;
        this.isH2 = isH2;
        this.entityManagerFactory = entityManagerFactory;
        this.fetchSize = fetchSize;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        this.generatedIdState = new GeneratedIdState(hazelcastInstance);
    }

    @Override
    public void run() {
        String lockString = entityLockString(entityTypeName);
        final Lock lock = hazelcastInstance.getLock(lockString);
        if (lock.tryLock()) {
            try {
                generate();
            } catch (Exception e) {
                logger.error("Caught exception when generating IDs for entity {}", entityTypeName, e);
            } finally {
                lock.unlock();
            }
        } else {
            logger.debug("Could not get lock for generating IDs for {}. Exiting.", entityTypeName);
        }
    }

    private void generate() throws InterruptedException {
        logger.debug("Generating new available IDs for {}", entityTypeName);
        List<Long> claimedIdQueueForEntity = generatedIdState.getClaimedIdQueueForEntity(entityTypeName);
        BlockingQueue<Long> availableIds = generatedIdState.getQueueForEntity(entityTypeName);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            handleClaimedIds(claimedIdQueueForEntity, availableIds, entityManager);

            if (availableIds.size() < LOW_LEVEL_AVAILABLE_IDS) {
                generateNewIds(entityTypeName, availableIds, entityManager);
            }

            transaction.commit();

        } catch (RuntimeException e) {
            rollbackAndThrow(transaction, e);
        } finally {
            entityManager.close();
        }
    }

    private void handleClaimedIds(List<Long> claimedIdListForEntity, BlockingQueue<Long> availableIds, EntityManager entityManager) {
        if (!claimedIdListForEntity.isEmpty()) {
            logger.debug("Found {} claimed IDs. Removing them from available IDs", claimedIdListForEntity.size());

            List<Long> insertClaimedIdList = new ArrayList<>();
            for (long claimedId : claimedIdListForEntity) {
                // Only insert claimed IDs which are not already in available id list, as they are already inserted.
                if (availableIds.contains(claimedId)) {
                    availableIds.remove(claimedId);
                } else {
                    insertClaimedIdList.add(claimedId);
                }
            }
            if (!isH2) {
                logger.info("Inserting {} claimed IDs {}.", insertClaimedIdList.size(), entityTypeName);
                insertRetrievedIds(entityTypeName, insertClaimedIdList, entityManager);
                claimedIdListForEntity.removeAll(insertClaimedIdList);
            }
        }
    }

    /**
     * Generate new IDs for entity.
     * Will lock per entity type to avoid fetching and inserting IDs concurrently from the database.
     *
     * @param entityTypeName table to generate IDs for
     * @param availableIds   The (empty) queue of available IDs to fill
     */
    private void generateNewIds(String entityTypeName, BlockingQueue<Long> availableIds, EntityManager entityManager) throws InterruptedException {
        List<Long> retrievedIds = new ArrayList<>();

        while (retrievedIds.isEmpty()) {
            retrievedIds.addAll(retrieveIds(entityTypeName, entityManager));
        }

        if (!isH2) {
            insertRetrievedIds(entityTypeName, retrievedIds, entityManager);
        }

        for (long retrievedId : retrievedIds) {
            availableIds.put(retrievedId);
        }
    }

    /**
     * Fetch new IDs when all previously fetched IDs taken.
     *
     * @return list of available IDs for table.
     */
    @SuppressWarnings(value = "unchecked")
    private List<Long> retrieveIds(String entityTypeName, EntityManager entityManager) {
        long lastId = generatedIdState.getLastIdForEntity(entityTypeName);

        List<Long> retrievedIds;
        if (isH2) {
            // Because of issues using generate_series or query with system range with H2.
            retrievedIds = generateNextAvailableH2Ids(entityTypeName);
        } else {
            retrievedIds = selectNextAvailableIds(entityTypeName, lastId, entityManager);
        }
        logger.info("Generated for {}: {}", entityTypeName, retrievedIds);

        if (retrievedIds.isEmpty()) {
            generatedIdState.setLastIdForEntity(entityTypeName, lastId + fetchSize);
        } else {
            generatedIdState.setLastIdForEntity(entityTypeName, retrievedIds.get(retrievedIds.size() - 1));
        }
        return retrievedIds;
    }

    private void insertRetrievedIds(String tableName, List<Long> list, EntityManager entityManager) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No IDs to insert");
        }

        StringBuilder insertUsedIdsSql = new StringBuilder("INSERT INTO id_generator(table_name, id_value) VALUES");

        for (int i = 0; i < list.size(); i++) {
            insertUsedIdsSql.append("('").append(tableName).append("',").append(list.get(i)).append(")");
            if (i < list.size() - 1) {
                insertUsedIdsSql.append(',');
            }
        }
        insertUsedIdsSql.append(" ON CONFLICT DO NOTHING");
        Query query = entityManager.createNativeQuery(insertUsedIdsSql.toString());
        query.executeUpdate();
        entityManager.flush();
    }


    private List<Long> selectNextAvailableIds(String tableName, long lastId, EntityManager entityManager) {
        logger.debug("Will fetch new IDs from id_generator table for {}, lastId: {}", tableName, lastId);

        String sql = "SELECT generated FROM generate_series(" + lastId + "," + (lastId + fetchSize - 1) + ") AS generated " +
                "EXCEPT (SELECT id_value FROM id_generator WHERE table_name='" + tableName + "') " +
                "ORDER BY generated";

        Query sqlQuery = entityManager.createNativeQuery(sql);

        @SuppressWarnings("unchecked")
        List<BigInteger> results = sqlQuery.getResultList();

        return results.stream()
                .map(bigInteger -> bigInteger.longValue())
                .collect(toList());
    }

    /**
     * Generate new in-memory IDs for H2.
     */
    private List<Long> generateNextAvailableH2Ids(String entityTypeName) {

        logger.debug("H2: About to retrieve new IDs for {}", entityTypeName);
        List<Long> retrievedIds = new ArrayList<>();

        List<Long> usedH2Ids = hazelcastInstance.getList("used-h2-ids-by-entity-" + entityTypeName);

        List<Long> claimedIdQueueForEntity = generatedIdState.getClaimedIdQueueForEntity(entityTypeName);
        usedH2Ids.addAll(claimedIdQueueForEntity);
        Long idCandidate = generatedIdState.getLastIdForEntity(entityTypeName);
        Long counter = 0L;

        while (counter < fetchSize) {
            if (usedH2Ids.contains(idCandidate) || claimedIdQueueForEntity.contains(idCandidate)) {
                logger.debug("Looking for next available ID for {}. {} is taken", entityTypeName, idCandidate);
            } else {
                retrievedIds.add(idCandidate);
                usedH2Ids.add(idCandidate);
            }

            idCandidate++;
            counter++;
        }

        logger.info("H2: Created {} Ids for {}", retrievedIds.size(), entityTypeName);
        return retrievedIds;
    }

    private void rollbackAndThrow(EntityTransaction transaction, RuntimeException e) {
        if (transaction != null && transaction.isActive()) transaction.rollback();
        throw e;
    }

    private String entityLockString(String entityTypeName) {
        return LOCK_PREFIX + entityTypeName;
    }


}
