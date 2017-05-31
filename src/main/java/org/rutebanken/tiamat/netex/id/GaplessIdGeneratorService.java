package org.rutebanken.tiamat.netex.id;

import com.hazelcast.core.HazelcastInstance;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
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
    private static final int FETCH_SIZE = 2000;
    public static final String USED_H2_IDS_BY_ENTITY = "used-h2-ids-by-entity-";

    private static BasicFormatterImpl basicFormatter = new BasicFormatterImpl();

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
            List<Long> claimedIds = generatedIdState.getClaimedIdListForEntity(entityTypeName);

            if(claimedId > 0) {
                if (availableIds.remove(claimedId)) {
                    logger.trace("Removed claimed ID {} from list of available IDs for entity{}: {}", claimedId, entityTypeName, availableIds.stream().collect(toList()));
                } else if(! claimedIds.contains(claimedId)){
                    claimedIds.add(claimedId);
                }
            }
            if (availableIds.size() < LOW_LEVEL_AVAILABLE_IDS) {
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
        List<Long> claimedIds = generatedIdState.getClaimedIdListForEntity(entityTypeName);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            if(!claimedIds.isEmpty()) {
                insertIds(entityTypeName, claimedIds, entityManager);
                claimedIds.clear();
            }
            logger.debug("Generating new available IDs for {}", entityTypeName);
            generateNewIds(entityTypeName, availableIds, claimedId, entityManager);

            transaction.commit();

        } catch (RuntimeException e) {
            rollbackAndThrow(transaction, e);
        } finally {
            entityManager.close();
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

        logger.debug("Inserting retrieved IDs: {}", retrievedIds);
        insertIds(entityTypeName, retrievedIds, entityManager);

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

        List<Long> retrievedIds = selectNextAvailableIds(entityTypeName, lastId, entityManager);

        logger.debug("Generated for {}: {}", entityTypeName, retrievedIds);

        if (retrievedIds.isEmpty()) {
            generatedIdState.setLastIdForEntity(entityTypeName, lastId + FETCH_SIZE);
        } else {
            generatedIdState.setLastIdForEntity(entityTypeName, retrievedIds.get(retrievedIds.size() - 1));
        }
        return retrievedIds;
    }

    /**
     * Insert used IDs into the helper table.
     * @param tableName the entity name. Typically class.getSimpleName value
     * @param list list of long values to reserve
     * @param entityManager for the current transaction
     */
    private void insertIds(String tableName, List<Long> list, EntityManager entityManager) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No IDs to insert");
        }

        StringBuilder insertUsedIdsSql = new StringBuilder("insert into id_generator(table_name, id_value) ")
                .append("select id1.table_name, id1.id_value ")
                .append("from ( ")
                .append("select cast('")
                .append(tableName)
                .append("' as varchar) ")
                .append("as table_name, ")
                .append(list.get(0)) // First value
                .append(" as id_value ");

        for (int i = 1; i < list.size(); i++) {
            insertUsedIdsSql.append("union all ")
                    .append("select '")
                    .append(tableName)
                    .append("',")
                    .append(list.get(i))
                    .append(" ");
        }

        insertUsedIdsSql.append(" ) as id1 ")
                .append("where not exists (select 1 from id_generator id2 where id2.id_value = id1.id_value and id2.table_name = id1.table_name)");

        // Format the sql for logging
        String sql = basicFormatter.format(insertUsedIdsSql.toString());

        Query query = entityManager.createNativeQuery(sql);
        logger.trace(sql);
        int result = query.executeUpdate();
        logger.trace("Inserted {}", result);
        entityManager.flush();
    }

    private List<Long> selectNextAvailableIds(String tableName, long lastId, EntityManager entityManager) {
        logger.debug("Will fetch new IDs from id_generator table for {}, lastId: {}", tableName, lastId);

        String sql = "SELECT * FROM generate_series(" + lastId + "," + (lastId + FETCH_SIZE - 1) + ")  " +
                "EXCEPT (SELECT id_value FROM id_generator WHERE table_name='" + tableName + "') " ;

        Query sqlQuery = entityManager.createNativeQuery(sql);

        @SuppressWarnings("unchecked")
        List<BigInteger> results = sqlQuery.getResultList();

        return results.stream()
                .map(bigInteger -> bigInteger.longValue())
                .collect(toList());
    }


    private void rollbackAndThrow(EntityTransaction transaction, RuntimeException e) {
        if (transaction != null && transaction.isActive()) transaction.rollback();
        throw e;
    }

    public static String entityLockString(String entityTypeName) {
        return REENTRANT_LOCK_PREFIX + entityTypeName;
    }

}
