package org.rutebanken.tiamat.netex.id;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hazelcast.core.HazelcastInstance;
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
import java.util.concurrent.*;


/**
 * Generate gapless IDs for certain entities.
 * If matching incoming entity already have ID set, try to use it without genereration.
 * For other entities, fall back to sequence style generation.
 */
@Component
public class GaplessIdGenerator implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(GaplessIdGenerator.class);

    private static final int ID_FETCH_SIZE = 10;
    private static final long START_LAST_ID = 1L;

    private volatile Boolean isH2 = null;

    private EntityManagerFactory entityManagerFactory;

    private final ExecutorService executorService;

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

        executorService = Executors.newFixedThreadPool(entityTypeNames.size(), new ThreadFactoryBuilder().setNameFormat("id-generator-%d").build());

//        executorService = hazelcastInstance.getExecutorService("id-generator");

        entityTypeNames.forEach(entityTypeName -> {
            generatedIdState.registerEntityTypeName(entityTypeName, START_LAST_ID);
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


    @PostConstruct
    public void startExecutorService() {
        logger.info("Starting {} ID generator threads", entityTypeNames.size());

        entityTypeNames.forEach(entityTypeName -> {
            GaplessIdGeneratorTask runnable = new GaplessIdGeneratorTask(generatedIdState, entityTypeName, isH2(), entityManagerFactory);
            runnable.setHazelcastInstance(hazelcastInstance);
            executorService.submit(runnable);
        });
    }

    @PreDestroy
    public void preDestroy() {
        logger.info("Pre destroy. Shutting down executor service");
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
           Thread.currentThread().interrupt();
        }
    }



}