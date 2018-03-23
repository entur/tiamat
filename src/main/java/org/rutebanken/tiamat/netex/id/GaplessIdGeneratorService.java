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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISet;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import static java.util.stream.Collectors.toList;

@Service
public class GaplessIdGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(GaplessIdGeneratorService.class);

    public static final String REENTRANT_LOCK_PREFIX = "entity_lock_";

    public static final long INITIAL_LAST_ID = 0;
    public static final int LOW_LEVEL_AVAILABLE_IDS = 10;
    public static final int DEFAULT_FETCH_SIZE = 2000;

    public static final int INSERT_CLAIMED_ID_THRESHOLD = 1000;

    private static BasicFormatterImpl basicFormatter = new BasicFormatterImpl();

    private final int fetchSize;

    private final EntityManagerFactory entityManagerFactory;
    private final HazelcastInstance hazelcastInstance;
    private final GeneratedIdState generatedIdState;

    @Autowired
    public GaplessIdGeneratorService(EntityManagerFactory entityManagerFactory, HazelcastInstance hazelcastInstance, GeneratedIdState generatedIdState) {
        this.entityManagerFactory = entityManagerFactory;
        this.hazelcastInstance = hazelcastInstance;
        this.generatedIdState = generatedIdState;
        this.fetchSize = DEFAULT_FETCH_SIZE;
    }

    public GaplessIdGeneratorService(EntityManagerFactory entityManagerFactory, HazelcastInstance hazelcastInstance, GeneratedIdState generatedIdState, int fetchSize) {
        this.entityManagerFactory = entityManagerFactory;
        this.hazelcastInstance = hazelcastInstance;
        this.generatedIdState = generatedIdState;

        if (fetchSize < LOW_LEVEL_AVAILABLE_IDS) {
            logger.warn("Fetch size {} cannot be lower than LOW LEVEL AVAILABLE IDS {}. Setting fetch size to {}", fetchSize, LOW_LEVEL_AVAILABLE_IDS, LOW_LEVEL_AVAILABLE_IDS);
            fetchSize = LOW_LEVEL_AVAILABLE_IDS;
        }

        this.fetchSize = fetchSize;

    }

    public long getNextIdForEntity(String entityTypeName) {
        return getNextIdForEntity(entityTypeName, 0);
    }

    public long getNextIdForEntity(String entityTypeName, long claimedId) {
        final Lock lock = hazelcastInstance.getLock(entityLockString(entityTypeName));
        lock.lock();
        try {
            BlockingQueue<Long> availableIds = generatedIdState.getQueueForEntity(entityTypeName);
            ISet<Long> claimedIds = generatedIdState.getClaimedIdListForEntity(entityTypeName);

            if (claimedId > 0) {
                if (availableIds.remove(claimedId)) {
                    logger.trace("Removed claimed ID {} from list of available IDs for entity {}: {}", claimedId, entityTypeName, availableIds.stream().collect(toList()));
                }
                // Add it to make it persisted later
                claimedIds.add(claimedId);
            }

            boolean timeToGenerateAvailableIds = availableIds.size() < LOW_LEVEL_AVAILABLE_IDS;

            if (timeToGenerateAvailableIds || claimedIds.size() > INSERT_CLAIMED_ID_THRESHOLD) {
                writeClaimedIdsAndGenerateNew(entityTypeName, timeToGenerateAvailableIds);
            }

            if (claimedId > 0) {
                logger.trace("Returning claimed ID {}", claimedId);
                return claimedId;
            } else {
                Long remove = availableIds.remove();
                claimedIds.add(remove);
                logger.trace("Returning available ID for {}: {}", entityTypeName, remove);
                return remove;
            }
        } catch (Exception e) {
            throw new IdGeneratorException("Caught exception when generating IDs for entity " + entityTypeName, e);
        } finally {
            lock.unlock();
        }
    }

    private void writeClaimedIdsAndGenerateNew(String entityTypeName, boolean timeToGenerateAvailableIds) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        executeInTransaction(() -> {
            BlockingQueue<Long> availableIds = generatedIdState.getQueueForEntity(entityTypeName);
            ISet<Long> claimedIds = generatedIdState.getClaimedIdListForEntity(entityTypeName);

            if (!claimedIds.isEmpty()) {
                // Ignore duplicates because claimed ids could already have been inserted as available IDs previously
                logger.debug("Inserting {} claimed IDs for {}", claimedIds.size(), entityTypeName);
                insertIdsIgnoreDuplicates(entityTypeName, claimedIds, entityManager);
                claimedIds.destroy();
            }
            if (timeToGenerateAvailableIds) {
                logger.debug("Generating new available IDs for {}", entityTypeName);
                try {
                    generateNewIds(entityTypeName, availableIds, entityManager);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, entityManager);
    }

    /**
     * Generate new IDs for entity.
     *
     * @param entityTypeName table to writeClaimedIdsAndGenerateNew IDs for
     * @param availableIds   The (empty) queue of available IDs to fill
     * @param claimedId
     */
    private void generateNewIds(String entityTypeName, BlockingQueue<Long> availableIds, EntityManager entityManager) throws InterruptedException {
        List<Long> retrievedIds = new ArrayList<>();

        while (retrievedIds.isEmpty()) {
            retrievedIds.addAll(retrieveIds(entityTypeName, entityManager));
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

        List<Long> retrievedIds = selectNextAvailableIds(entityTypeName, lastId, entityManager);

        logger.trace("Generated for {}: {}", entityTypeName, retrievedIds);

        if (retrievedIds.isEmpty()) {
            generatedIdState.setLastIdForEntity(entityTypeName, lastId + fetchSize);
        } else {

            generatedIdState.setLastIdForEntity(entityTypeName, Collections.max(retrievedIds));
        }
        return retrievedIds;
    }

    private void insertIdsIgnoreDuplicates(String tableName, Set<Long> list, EntityManager entityManager) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No IDs to insert");
        }

        Iterator<Long> iterator = list.iterator();

        StringBuilder insertUsedIdsSql = new StringBuilder("insert into id_generator(table_name, id_value) ")
                .append("select id1.table_name, id1.id_value ")
                .append("from ( ")
                .append("select cast('")
                .append(tableName)
                .append("' as varchar) ")
                .append("as table_name, ")
                .append(iterator.next()) // First value
                .append(" as id_value ");

        while(iterator.hasNext()) {
            insertUsedIdsSql.append("union all ")
                    .append("select '")
                    .append(tableName)
                    .append("',")
                    .append(iterator.next())
                    .append(" ");
        }

        insertUsedIdsSql.append(" ) as id1 ")
                .append("where not exists (select 1 from id_generator id2 where id2.id_value = id1.id_value and id2.table_name = id1.table_name)");

        // Format the sql for logging
        String sql = basicFormatter.format(insertUsedIdsSql.toString());

        Query query = entityManager.createNativeQuery(sql);
        logger.trace(sql);
        int result = query.executeUpdate();
        logger.trace("Inserted {} ids for {}", result, tableName);
        entityManager.flush();
    }

    private List<Long> selectNextAvailableIds(String tableName, long lastId, EntityManager entityManager) {
        logger.debug("Will fetch new IDs from id_generator table for {}, lastId: {}", tableName, lastId);

        long lastIdPlusOne = lastId + 1;
        long upperBound = lastId + fetchSize;

        String sql = "SELECT * FROM generate_series(" + lastIdPlusOne + "," + upperBound + ")  " +
                "EXCEPT (SELECT id_value FROM id_generator WHERE table_name='" + tableName + "') ";

        Query sqlQuery = entityManager.createNativeQuery(sql);

        @SuppressWarnings("unchecked")
        List<BigInteger> results = sqlQuery.getResultList();

        logger.trace("Got generated values: {}", results);

        return results.stream()
                .map(bigInteger -> bigInteger.longValue())
                .sorted((v1, v2) -> Long.compare(v1, v2))
                .collect(toList());
    }

    public void persistClaimedIds() {

        logger.trace("Start to persist claimed IDs if any");
        AtomicInteger persisted = new AtomicInteger();
        generatedIdState.getRegisteredEntityNames().forEach(entityTypeName -> {
            final Lock lock = hazelcastInstance.getLock(entityLockString(entityTypeName));
            boolean gotLock;
            final int secondsToWait = 4;
            try {
                gotLock = lock.tryLock(secondsToWait, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.warn("Could not get lock for entity {} after {} seconds. Giving up.", secondsToWait, entityTypeName);
                gotLock = false;
            }

            if(gotLock) {
                try {
                    EntityManager entityManager = entityManagerFactory.createEntityManager();
                    executeInTransaction(() -> {
                        ISet<Long> claimedIds = generatedIdState.getClaimedIdListForEntity(entityTypeName);
                        if(!claimedIds.isEmpty()) {
                            logger.info("About to write {} claimed IDs to db for {}", claimedIds.size(), entityTypeName);
                            insertIdsIgnoreDuplicates(entityTypeName, claimedIds, entityManager);
                            persisted.addAndGet(claimedIds.size());
                            claimedIds.destroy();
                        } else {
                            logger.debug("No claimed IDs to insert for {}", entityTypeName);
                        }
                    }, entityManager);
                } catch (Exception e) {
                  logger.warn("Error writing claimed IDs for {} in transaction", entityTypeName, e);
                } finally {
                    lock.unlock();
                }
            }
        });
        if(persisted.get() > 0 ) {
            logger.info("Persisted {} claimed IDs", persisted);
        }
    }

    private void executeInTransaction(Runnable runnable, EntityManager entityManager) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            runnable.run();
            transaction.commit();

        } catch (RuntimeException e) {
            rollbackAndThrow(transaction, e);
        } catch (Exception e) {
            rollbackAndThrow(transaction, e);
        } finally {
            entityManager.close();
        }
    }

    private void rollbackAndThrow(EntityTransaction transaction, Exception e) {
        if (transaction != null && transaction.isActive()) transaction.rollback();
        throw new RuntimeException(e);
    }

    public static String entityLockString(String entityTypeName) {
        return REENTRANT_LOCK_PREFIX + entityTypeName;
    }

}
