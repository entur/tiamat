package org.rutebanken.tiamat.netex.id;

import com.hazelcast.core.*;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.rutebanken.tiamat.netex.id.GaplessIdGeneratorTask.LOW_LEVEL_AVAILABLE_IDS;
import static org.rutebanken.tiamat.netex.id.GaplessIdGeneratorTask.USED_H2_IDS_BY_ENTITY;


/**
 * Generate gapless IDs for certain entities.
 * If matching incoming entity already have ID set, try to use it without genereration.
 * For other entities, fall back to sequence style generation.
 */
@Component
public class GaplessIdGenerator implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(GaplessIdGenerator.class);

    private static final String EXECUTOR_NAME = "id-generator";

    private static final long START_LAST_ID = 1L;

    private static final int INITIAL_FETCH_SIZE = 5;

    private static final int FETCH_SIZE = 20;

    private volatile Boolean isH2 = null;

    private final EntityManagerFactory entityManagerFactory;

    private ExecutorService executorService = null;

    private final GeneratedIdState generatedIdState;

    private final List<String> entityTypeNames = new ArrayList<>();

    private final HazelcastInstance hazelcastInstance;

    @Autowired
    public GaplessIdGenerator(GeneratedIdState generatedIdState, EntityManagerFactory entityManagerFactory, HazelcastInstance hazelcastInstance) {
        this.generatedIdState = generatedIdState;
        this.entityManagerFactory = entityManagerFactory;
        this.hazelcastInstance = hazelcastInstance;

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getMetamodel().getEntities().forEach(entityType -> {
            entityTypeNames.add(entityType.getBindableJavaType().getSimpleName());
        });
        logger.info("Found these types to generate IDs for: {}", entityTypeNames);

        resetAndShutdownPreexisingInstance(entityTypeNames);

        logger.info("Starting up {}", EXECUTOR_NAME);

        if (!Hazelcast.getAllHazelcastInstances().stream()
                .flatMap(hzInstance -> hzInstance.getDistributedObjects().stream())
                .peek(distributedObject -> logger.trace("{}", distributedObject))
                .filter(distributedObject -> distributedObject.getName().equals(EXECUTOR_NAME))
                .findAny().isPresent()) {
            this.executorService = hazelcastInstance.getExecutorService(EXECUTOR_NAME);
        } else {
            throw new IllegalStateException("ExecutorService {} seems to already be created " + EXECUTOR_NAME);
        }
    }

    /**
     * If there is an instance of the hazelcast executor already, it needs to be shutdown in case spring instansiated this bean again.
     * Also, the state of the generated IDs will be reset.
     * @param resetQueuesForEntities the entities to reset queues for
     */
    private void resetAndShutdownPreexisingInstance(List<String> resetQueuesForEntities) {

        logger.info("Resetting queues for entities: {}", resetQueuesForEntities);
        resetQueuesForEntities.forEach(entityTypeName -> {
            generatedIdState.getClaimedIdListForEntity(entityTypeName).clear();
            generatedIdState.getLastIdForEntityMap().put(entityTypeName, 1L);
            generatedIdState.getQueueForEntity(entityTypeName).clear();
            hazelcastInstance.getList(USED_H2_IDS_BY_ENTITY + entityTypeName).clear();
        });

        Optional<IExecutorService> optionalExecutorService = Hazelcast.getAllHazelcastInstances().stream()
                .flatMap(hzInstance -> hzInstance.getDistributedObjects().stream())
                .peek(distributedObject -> logger.trace("{}", distributedObject))
                .filter(distributedObject -> distributedObject.getName().equals(EXECUTOR_NAME))
                .filter(distributedObject -> distributedObject instanceof IExecutorService)
                .map(distributedObject -> (IExecutorService) distributedObject)
                .findAny();

        if(optionalExecutorService.isPresent()) {
            logger.warn("Found existing executor service from hazelcast. Destroying it.");
            IExecutorService preExistingExecutorSerivce = optionalExecutorService.get();
            shutDown(preExistingExecutorSerivce);
        }
    }

    @PostConstruct
    public void postConstruct() {
        entityTypeNames.forEach(entityTypeName -> {
            generatedIdState.registerEntityTypeName(entityTypeName, START_LAST_ID);

            final IQueue<Long> queue = generatedIdState.getQueueForEntity(entityTypeName);
            queue.addItemListener(new ItemListener<Long>() {
                @Override
                public void itemAdded(ItemEvent<Long> itemEvent) {
                }

                @Override
                public void itemRemoved(ItemEvent<Long> itemEvent) {
                    if (queue.size() < LOW_LEVEL_AVAILABLE_IDS) {
                        logger.debug("Low number of IDs for {}", entityTypeName);
                        if(!executorService.isShutdown()) {
                            executorService.submit(new GaplessIdGeneratorTask(entityTypeName, isH2(), entityManagerFactory, FETCH_SIZE));
                        } else {
                            logger.debug("Executor service is shut down. Not generating IDs");
                        }
                    }
                }
            }, true);

            // First time generation.
            executorService.submit(new GaplessIdGeneratorTask(entityTypeName, isH2(), entityManagerFactory, INITIAL_FETCH_SIZE));
        });
    }

    private boolean isH2() {
        if (isH2 == null) {
            try {
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                isH2 = entityManager.unwrap(SessionImpl.class).getPersistenceContext().getSession().connection().getMetaData().getDatabaseProductName().contains("H2");
                logger.info("Detected H2: {}", isH2);
            } catch (SQLException e) {
                throw new RuntimeException("Could not determine database provider", e);
            }
        }
        return isH2;
    }

    public List<String> getEntityTypeNames() {
        return entityTypeNames;
    }

    @PreDestroy
    public void preDestroy() {
        shutDown(executorService);
    }

    public void shutDown(ExecutorService executorService) {
        logger.info("Shutdown. Shutting down executor service");
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        hazelcastInstance.getExecutorService(EXECUTOR_NAME).destroy();
        logger.info("Destroyed");
    }

}