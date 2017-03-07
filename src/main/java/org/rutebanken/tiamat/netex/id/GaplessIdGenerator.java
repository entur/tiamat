package org.rutebanken.tiamat.netex.id;

import com.google.common.util.concurrent.Striped;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

import static java.util.stream.Collectors.toList;


/**
 * Generate gapless IDs for certain entities.
 * If matching incoming entity already have ID set, try to use it without genereration.
 * For other entities, fall back to sequence style generation.
 */
@Component
public class GaplessIdGenerator {

    private static final Logger logger = LoggerFactory.getLogger(GaplessIdGenerator.class);

    private static final int ID_FETCH_SIZE = GeneratedIdState.QUEUE_CAPACITY;
    private static final long START_LAST_ID = 1L;
    public static final int LOW_LEVEL_AVAILABLE_IDS = ID_FETCH_SIZE;

    private final Striped<Lock> locks = Striped.lock(1024);

    private Boolean isH2 = null;

    private EntityManagerFactory entityManagerFactory;

    private final ExecutorService executorService;

    private final GeneratedIdState generatedIdState;

    private final List<String> entityTypeNames = new ArrayList<>();

    @Autowired
    public GaplessIdGenerator(GeneratedIdState generatedIdState, EntityManagerFactory entityManagerFactory) {
        this.generatedIdState = generatedIdState;
        this.entityManagerFactory = entityManagerFactory;

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getMetamodel().getEntities().forEach(entityType -> {
            entityTypeNames.add(entityType.getBindableJavaType().getSimpleName());
        });
        logger.info("Found these types to generate IDs for: {}", entityTypeNames);

        executorService = Executors.newFixedThreadPool(entityTypeNames.size(), new ThreadFactoryBuilder().setNameFormat("id-generator-%d").build());

        entityTypeNames.forEach(entityTypeName -> {
            generatedIdState.registerEntityTypeName(entityTypeName, START_LAST_ID);
        });
    }


    @PostConstruct
    public void startExecutorService() {
        entityTypeNames.forEach(entityTypeName -> executorService.submit(() -> run(entityTypeName)));
    }


    public void run(String entityTypeName) {
        while (true) {
            try {
                ConcurrentLinkedQueue<Long> claimedIdQueueForEntity = generatedIdState.getClaimedIdQueueForEntity(entityTypeName);
                BlockingQueue<Long> availableIds = generatedIdState.getQueueForEntity(entityTypeName);

                if (!claimedIdQueueForEntity.isEmpty()) {
                    logger.debug("Found {} claimed IDs. Removing them from available IDs", claimedIdQueueForEntity.size());
                    // This should already have happened in NetexIdProvider.
                    availableIds.removeAll(claimedIdQueueForEntity);
                    insertClaimedIds(entityTypeName, claimedIdQueueForEntity);

                }

                if (availableIds.size() < LOW_LEVEL_AVAILABLE_IDS) {
                    logger.debug("I have these ids available for table {}: {}", entityTypeName, availableIds);
                    generateNewIds(entityTypeName, availableIds);
                }

                try {
                    // Sleep between queue size checks.
                    // It is a blocking queue and could use the blocking feature to add new IDs.
                    // But it might keep the transaction open longer than we want.
                    // And the transaction should be commited before adding generated IDs to the queue and releasing the lock.
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.warn("Interruped", e);
                    Thread.currentThread().interrupt();
                }


//            if (entityStructure.getGeneratedId() != null) {
//                logger.debug("Incoming object claims explicit entity ID {}. {}", entityStructure.getGeneratedId(), entityStructure);
//                availableIds.remove(entityStructure.getGeneratedId());
//                insertRetrievedIds(entityTypeName, Arrays.asList(entityStructure.getGeneratedId()), sessionImpl);
//                if(isH2(sessionImpl)) {
//                    usedH2Ids.putIfAbsent(tableName, new ConcurrentLinkedQueue<>());
//                    usedH2Ids.get(entityTypeName).add(entityStructure.getGeneratedId());
//                }
//                return entityStructure.getGeneratedId();
//            } else {
//                return generateId(entityTypeName, entityStructure, sessionImpl, availableIds);
//            }


            } catch (Throwable t) {
                logger.error("Caught throwable when generating IDs", t);
            }
        }

    }


    private void insertClaimedIds(String entityTypeName, ConcurrentLinkedQueue<Long> claimedIdQueueForEntity) {
        logger.info("Inserting claimed IDs {}. Aquiring lock.", entityTypeName);
        final Lock lock = locks.get(entityTypeName);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            lock.lock();
            transaction.begin();
            insertRetrievedIds(entityTypeName, claimedIdQueueForEntity.stream().collect(toList()), entityManager);
            transaction.commit();

        } catch (RuntimeException e) {
            closeTransaction(transaction, e);
        } finally {
            entityManager.close();
            lock.unlock();
        }
    }


    /**
     * All previously fetched IDs are taken for this entity. Generate new IDs.
     * Will lock per entity type to avoid fetching and inserting IDs concurrently from the database.
     *
     * @param entityTypeName table to generate IDs for
     * @param availableIds   The (empty) queue of available IDs to fill
     */
    private void generateNewIds(String entityTypeName, BlockingQueue<Long> availableIds) {
        logger.info("Time to generate new IDs for {}. Aquiring lock.", entityTypeName);
        final Lock lock = locks.get(entityTypeName);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            lock.lock();
            transaction.begin();

            List<Long> retrievedIds = new ArrayList<>();

            while (retrievedIds.isEmpty()) {
                retrievedIds.addAll(retrieveIds(entityTypeName, entityManager));
            }

            insertRetrievedIds(entityTypeName, retrievedIds, entityManager);

            transaction.commit();

            for (long retrievedId : retrievedIds) {
                availableIds.put(retrievedId);
            }

        } catch (InterruptedException e) {
            logger.warn("Interrupted.", e);
            Thread.currentThread().interrupt();

        } catch (RuntimeException e) {
            closeTransaction(transaction, e);
        } finally {
            entityManager.close();
            lock.unlock();
        }
    }

    /**
     * Fetch new IDs when all previously fetched IDs taken.
     *
     * @return list of available IDs for table.
     */
    @SuppressWarnings(value = "unchecked")
    private List<Long> retrieveIds(String entityTypeName, EntityManager entityManager) {
        logger.info("About to retrieve new IDs for {}", entityTypeName);

        long lastId = generatedIdState.getLastIdForEntity(entityTypeName);

        List<Long> retrievedIds;
        if (isH2()) {
            // Because of issues using generate_series or query with system range with H2.
            retrievedIds = generateNextAvailableH2Ids(entityTypeName);
        } else {
            retrievedIds = selectNextAvailableIds(entityTypeName, lastId, entityManager);
        }

        if (retrievedIds.isEmpty()) {
            generatedIdState.setLastIdForEntity(entityTypeName, lastId + ID_FETCH_SIZE);
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
        Query query = entityManager.createNativeQuery(insertUsedIdsSql.toString());
        query.executeUpdate();
        entityManager.flush();
    }

    private List<Long> selectNextAvailableIds(String tableName, long lastId, EntityManager entityManager) {
        logger.debug("Will fetch new IDs from id_generator table for {}, lastId: {}", tableName, lastId);

        String sql = "SELECT generated FROM generate_series(" + lastId + "," + (lastId + ID_FETCH_SIZE - 1) + ") AS generated " +
                "EXCEPT (SELECT id_value FROM id_generator WHERE table_name='" + tableName + "') " +
                "ORDER BY generated";

        Query sqlQuery = entityManager.createNativeQuery(sql);

        @SuppressWarnings("unchecked")
        List<BigInteger> results = sqlQuery.getResultList();

        return results.stream()
                .map(bigInteger -> bigInteger.longValue())
                .collect(toList());
    }

    private boolean isH2() {
//        if (isH2 == null) {
//            try {
//                isH2 = entityManager.unwrap(SessionImpl.class).getPersistenceContext().getSession().connection().getMetaData().getDatabaseProductName().contains("H2");
//                logger.info("Detected H2: {}", isH2);
//            } catch (SQLException e) {
//                throw new RuntimeException("Could not determine database provider", e);
//            }
//        }
        return false;
    }

    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> usedH2Ids = new ConcurrentHashMap<>(0);


    /**
     * Generate new in-memory IDs for H2.
     */
    private List<Long> generateNextAvailableH2Ids(String entityTypeName) {

        logger.info("H2: About to retrieve new IDs for {}", entityTypeName);
        List<Long> availableIds = new ArrayList<>();

        usedH2Ids.putIfAbsent(entityTypeName, new ConcurrentLinkedQueue<>());


        Long id = generatedIdState.getLastIdForEntity(entityTypeName);
        Long counter = 0L;
        outer:
        while (counter < ID_FETCH_SIZE) {
            if (usedH2Ids.get(entityTypeName).contains(id)) {
                logger.debug("Looking for next available ID. {} is taken", id);
            } else {
                availableIds.add(id);
            }

            id++;
            counter++;
        }

        return availableIds;
    }

    private void closeTransaction(EntityTransaction transaction, RuntimeException e) {
        if (transaction != null && transaction.isActive()) transaction.rollback();
        throw e;
    }
}