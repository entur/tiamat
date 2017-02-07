package org.rutebanken.tiamat.repository;

import com.google.common.util.concurrent.Striped;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.SessionImpl;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.type.LongType;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;


/**
 * Generate ID if not already set.
 */
public class OptionalIdGenerator extends SequenceStyleGenerator {

    private static final Logger logger = LoggerFactory.getLogger(OptionalIdGenerator.class);

    private static final int ID_FETCH_SIZE = 500;
    private static final long START_LAST_ID = 1L;

    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> availableIdsPerTable = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> lastIdsPerTable = new ConcurrentHashMap<>();
    private static final Striped<Lock> stripedSemaphores = Striped.lock(1024);

    private static Boolean isH2 = null;

    private static Set<Long> usedH2Ids = new HashSet<>();

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {

        if (object == null) throw new HibernateException(new NullPointerException());

        Optional<EntityStructure> optionalEntityStructure = entityStructure(object);
        if(optionalEntityStructure.isPresent()) {

            EntityStructure entityStructure = optionalEntityStructure.get();

            SessionImpl sessionImpl = (SessionImpl) session;
            String tableName = determineTableName(sessionImpl, object);
            availableIdsPerTable.putIfAbsent(tableName, new ConcurrentLinkedQueue<>());
            lastIdsPerTable.putIfAbsent(tableName, START_LAST_ID);
            ConcurrentLinkedQueue<Long> availableIds = availableIdsPerTable.get(tableName);

            logger.trace("I have these ids available for table {}: {}", tableName, availableIds);

            if (entityStructure.getId() != null) {
                logger.debug("Incoming object claims explicit entity ID {}. {}", entityStructure.getId(), entityStructure);
                availableIds.remove(entityStructure.getId());
                return entityStructure.getId();
            } else {
                return generateId(tableName, entityStructure, sessionImpl, availableIds);
            }
        }
        Serializable id = super.generate(session, object);
        logger.trace("Generated id {}", id);
        return id;
    }

    private long generateId(String tableName, EntityStructure entityStructure, SessionImpl sessionImpl, ConcurrentLinkedQueue<Long> availableIds) {

        if(availableIds.isEmpty()) {
            generateNewIds(tableName, sessionImpl, availableIds);
        }

        logger.trace("About to generate id for: {}", entityStructure);

        Long nextId = availableIds.poll();
        if (nextId != null) {
            logger.trace("Use previously fetched ID {}", nextId);
        } else {
            throw new HibernateException("Could not generate new ID for entity " + entityStructure);
        }

        lastIdsPerTable.put(tableName, nextId);

        return nextId.longValue();
    }

    private void generateNewIds(String tableName, SessionImpl sessionImpl, ConcurrentLinkedQueue<Long> availableIds) {
        final Lock lock = stripedSemaphores.get(tableName);

        try {
            lock.lock();

            while(availableIds.isEmpty()) {
                List<Long> retrievedIds = retrieveIds(tableName, sessionImpl);
                logger.trace("Inserting {} ids", retrievedIds);
                insertRetrievedIds(tableName, retrievedIds, sessionImpl);
                availableIds.addAll(retrievedIds);
            }
        } finally {
            lock.unlock();
        }
    }


    private String determineTableName(SessionImpl sessionImpl, Object object) {
        return ((AbstractEntityPersister) sessionImpl.getSessionFactory().getAllClassMetadata().get(object.getClass().getCanonicalName())).getTableName();
    }

    private Optional<EntityStructure> entityStructure(Object object) {
        if(object instanceof EntityStructure) {
            EntityStructure entityStructure = (EntityStructure) object;

            logger.trace("{} is instance of entity structre",entityStructure);

            if((entityStructure instanceof StopPlace||entityStructure instanceof Quay)){
                return Optional.of(entityStructure);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings(value = "unchecked")
    private List<Long> retrieveIds(String tableName, SessionImpl sessionImpl) {
        Long lastId = lastIdsPerTable.get(tableName);

        if(isH2(sessionImpl)) {
            // Because of issues using the query below with H2.
            return generateNextAvailableH2Ids(lastId, ID_FETCH_SIZE);
        }

        logger.trace("Will fetch new IDs from id_generator table for {}, lastId: {}", tableName, lastId);

        String sql = "SELECT generated FROM generate_series(" + lastId + "," + (lastId.longValue() + ID_FETCH_SIZE) + ") AS generated " +
                "EXCEPT (SELECT id_value FROM id_generator WHERE table_name='" + tableName + "') " +
                "ORDER BY generated";

        SQLQuery sqlQuery = sessionImpl.createSQLQuery(sql);
//        sqlQuery.setFlushMode(FlushMode.MANUAL);
        sqlQuery.addScalar("generated", LongType.INSTANCE);

        List list = sqlQuery.list();
        return list;
    }


    private void insertRetrievedIds(String tableName, List<Long> list, SessionImpl sessionImpl) {
        StringBuilder insertUsedIdsSql = new StringBuilder("INSERT INTO id_generator(table_name, id_value) VALUES");

        for (int i = 0; i < list.size(); i++) {
            insertUsedIdsSql.append("('").append(tableName).append("',").append(list.get(i)).append(")");
            if (i < list.size() - 1) {
                insertUsedIdsSql.append(',');
            }
        }
        SQLQuery query = sessionImpl.createSQLQuery(insertUsedIdsSql.toString());
//        query.setFlushMode(FlushMode.MANUAL);
        query.executeUpdate();
    }


    private boolean isH2(SessionImpl sessionImpl) {
        if(isH2 == null) {
            try {
                isH2 = sessionImpl.getPersistenceContext().getSession().connection().getMetaData().getDatabaseProductName().contains("H2");
                logger.info("Detected H2: {}", isH2);
            } catch (SQLException e) {
                throw new RuntimeException("Could not determine database provider", e);
            }
        }
        return isH2;
    }


    public static List<Long> generateNextAvailableH2Ids(long lastId, int max)  {
        List<Long> availableIds = new ArrayList<>();

        Long id = lastId;
        Long counter = 0L;
        while(counter < max) {
            while (usedH2Ids.contains(++id)) {
                logger.debug("Looking for next available ID. {} is taken", id);
            }
            usedH2Ids.add(id);
            availableIds.add(id);
            counter++;
        }

        return availableIds;
    }
}