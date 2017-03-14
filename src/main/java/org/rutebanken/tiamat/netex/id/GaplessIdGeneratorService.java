package org.rutebanken.tiamat.netex.id;

import com.hazelcast.core.HazelcastInstance;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import static java.util.stream.Collectors.toList;

@Service
public class GaplessIdGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(GaplessIdGeneratorService.class);

    public static final String REENTRANT_LOCK_PREFIX = "entity_lock_";

    public static final long INITIAL_LAST_ID = 1;
    public static final int LOW_LEVEL_AVAILABLE_IDS = 10;
    private static final int FETCH_SIZE = 20;
    public static final String USED_H2_IDS_BY_ENTITY = "used-h2-ids-by-entity-";

    private static AtomicBoolean isH2 = null;
    private final EntityManagerFactory entityManagerFactory;
    private final HazelcastInstance hazelcastInstance;
    private final GeneratedIdState generatedIdState;

    public GaplessIdGeneratorService(EntityManagerFactory entityManagerFactory, HazelcastInstance hazelcastInstance, GeneratedIdState generatedIdState) {
        this.entityManagerFactory = entityManagerFactory;
        this.hazelcastInstance = hazelcastInstance;
        this.generatedIdState = generatedIdState;
    }

    public long getNextIdForEntity(String entityTypeName) {
        return getNextIdForEntity(entityTypeName, 0);
    }

    public long getNextIdForEntity(String entityTypeName, long claimedId) {
        String lockString = entityLockString(entityTypeName);


        final Lock lock = hazelcastInstance.getLock(lockString);
        lock.lock();
        try {
            BlockingQueue<Long> availableIds = generatedIdState.getQueueForEntity(entityTypeName);
            if (availableIds.size() < LOW_LEVEL_AVAILABLE_IDS || claimedId > 0) {
                generateInTransaction(entityTypeName, claimedId);
            }

            if(claimedId > 0) {
                return claimedId;
            } else {
                return availableIds.remove();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while generating ID. This is not expected", e);
        } catch (Exception e) {
            throw new IdGeneratorException("Caught exception when generating IDs for entity " + entityTypeName, e);
        } finally {
            lock.unlock();
        }
    }

    private void generateInTransaction(String entityTypeName, long claimedId) throws InterruptedException {
        BlockingQueue<Long> availableIds = generatedIdState.getQueueForEntity(entityTypeName);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            handleClaimedId(claimedId, availableIds, entityManager, entityTypeName);

            if (availableIds.size() < LOW_LEVEL_AVAILABLE_IDS) {
                logger.debug("Generating new available IDs for {}", entityTypeName);
                generateNewIds(entityTypeName, availableIds, claimedId, entityManager);
            }

            transaction.commit();

        } catch (RuntimeException e) {
            rollbackAndThrow(transaction, e);
        } finally {
            entityManager.close();
        }
    }

    private void handleClaimedId(long claimedId, BlockingQueue<Long> availableIds, EntityManager entityManager, String entityTypeName) {
        if (claimedId > 0) {
            // Only insert claimed IDs which are not already in available id list, as they are already inserted.

            if (availableIds.remove(claimedId)) {
                logger.debug("Removed claimed ID {} from list of available IDs: {}", claimedId, availableIds.stream().collect(toList()));
            } else if (!isH2()) {
                logger.debug("Inserting claimed ID {} {}. list {}", claimedId, entityTypeName, availableIds);
                insertRetrievedIds(entityTypeName, Arrays.asList(claimedId), entityManager);
            }
        }
    }

    /**
     * Generate new IDs for entity.
     *
     * @param entityTypeName table to generateInTransaction IDs for
     * @param availableIds   The (empty) queue of available IDs to fill
     * @param claimedId
     */
    private void generateNewIds(String entityTypeName, BlockingQueue<Long> availableIds, long claimedId, EntityManager entityManager) throws InterruptedException {
        List<Long> retrievedIds = new ArrayList<>();

        while (retrievedIds.isEmpty()) {
            retrievedIds.addAll(retrieveIds(entityTypeName, entityManager, claimedId));
        }

        if (!isH2()) {
            insertRetrievedIds(entityTypeName, retrievedIds, entityManager);
        }

        for (long retrievedId : retrievedIds) {
            if (retrievedId != claimedId) {
                availableIds.put(retrievedId);
            }
        }
    }

    /**
     * Fetch new IDs when all previously fetched IDs taken.
     *
     * @return list of available IDs for table.
     */
    @SuppressWarnings(value = "unchecked")
    private List<Long> retrieveIds(String entityTypeName, EntityManager entityManager, long claimedId) {
        long lastId = generatedIdState.getLastIdForEntity(entityTypeName);

        List<Long> retrievedIds;
        if (isH2()) {
            // Because of issues using generate_series or query with system range with H2.
            retrievedIds = generateNextAvailableH2Ids(entityTypeName, claimedId);
        } else {
            retrievedIds = selectNextAvailableIds(entityTypeName, lastId, entityManager);
        }
        logger.debug("Generated for {}: {}", entityTypeName, retrievedIds);

        if (retrievedIds.isEmpty()) {
            generatedIdState.setLastIdForEntity(entityTypeName, lastId + FETCH_SIZE);
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
//        insertUsedIdsSql.append(" ON CONFLICT DO NOTHING"); // psql version in carbon does not support this.
        Query query = entityManager.createNativeQuery(insertUsedIdsSql.toString());
        query.executeUpdate();
        entityManager.flush();
    }


    private List<Long> selectNextAvailableIds(String tableName, long lastId, EntityManager entityManager) {
        logger.debug("Will fetch new IDs from id_generator table for {}, lastId: {}", tableName, lastId);

        String sql = "SELECT generated FROM generate_series(" + lastId + "," + (lastId + FETCH_SIZE - 1) + ") AS generated " +
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
    private List<Long> generateNextAvailableH2Ids(String entityTypeName, long claimedId) {

        logger.debug("H2: About to retrieve new IDs for {}", entityTypeName);
        List<Long> retrievedIds = new ArrayList<>();

        List<Long> usedH2Ids = hazelcastInstance.getList(USED_H2_IDS_BY_ENTITY + entityTypeName);

        Long idCandidate = generatedIdState.getLastIdForEntity(entityTypeName);
        Long counter = 0L;

        while (counter < FETCH_SIZE) {
            if (usedH2Ids.contains(idCandidate)) {
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

    public static String entityLockString(String entityTypeName) {
        return REENTRANT_LOCK_PREFIX + entityTypeName;
    }

    private boolean isH2() {
        if (isH2 == null) {
            try {
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                isH2 = new AtomicBoolean(entityManager.unwrap(SessionImpl.class).getPersistenceContext().getSession().connection().getMetaData().getDatabaseProductName().contains("H2"));
                logger.info("Detected H2: {}", isH2);
            } catch (SQLException e) {
                throw new RuntimeException("Could not determine database provider", e);
            }
        }
        return isH2.get();
    }


}
