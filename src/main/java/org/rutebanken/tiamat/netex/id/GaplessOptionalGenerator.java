package org.rutebanken.tiamat.netex.id;

import com.google.common.util.concurrent.Striped;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.SessionImpl;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.LongType;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;


/**
 * Generate gapless IDs for certain entities.
 * If matching incoming entity already have ID set, try to use it without genereration.
 * For other entities, fall back to sequence style generation.
 */
public class GaplessOptionalGenerator extends SequenceStyleGenerator {

    private static final Logger logger = LoggerFactory.getLogger(GaplessOptionalGenerator.class);

    private static final int ID_FETCH_SIZE = 500;
    private static final long START_LAST_ID = 1L;

    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> availableIdsPerTable = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> lastIdsPerTable = new ConcurrentHashMap<>();
    private static final Striped<Lock> locks = Striped.lock(1024);

    private static Boolean isH2 = null;

    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> usedH2Ids = new ConcurrentHashMap<>(0);

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {

        if (object == null) throw new HibernateException(new NullPointerException());

//        Optional<EntityStructure> optionalEntityStructure = entityStructure(object);
//        if (optionalEntityStructure.isPresent()) {
//
//            EntityStructure entityStructure = optionalEntityStructure.get();
//
//            SessionImpl sessionImpl = (SessionImpl) session;
//            String tableName = determineTableName(sessionImpl, object);
//            availableIdsPerTable.putIfAbsent(tableName, new ConcurrentLinkedQueue<>());
//            lastIdsPerTable.putIfAbsent(tableName, START_LAST_ID);
//            ConcurrentLinkedQueue<Long> availableIds = availableIdsPerTable.get(tableName);
//
//            logger.trace("I have these ids available for table {}: {}", tableName, availableIds);
//
//            if (entityStructure.getId() != null) {
//                logger.debug("Incoming object claims explicit entity ID {}. {}", entityStructure.getId(), entityStructure);
//                availableIds.remove(entityStructure.getId());
//                insertRetrievedIds(tableName, Arrays.asList(entityStructure.getId()), sessionImpl);
//                if(isH2(sessionImpl)) {
//                    usedH2Ids.putIfAbsent(tableName, new ConcurrentLinkedQueue<>());
//                    usedH2Ids.get(tableName).add(entityStructure.getId());
//                }
//                return entityStructure.getId();
//            } else {
//                return generateId(tableName, entityStructure, sessionImpl, availableIds);
//            }
//        }
        Serializable id = super.generate(session, object);
        logger.trace("Generated id {}", id);
        return id;
    }

    private long generateId(String tableName, EntityStructure entityStructure, SessionImpl sessionImpl, ConcurrentLinkedQueue<Long> availableIds) {

        if (availableIds.isEmpty()) {
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

    /**
     * All previously fetched IDs are taken for this table. Generate new IDs.
     * Will lock per table to avoid fetching and inserting IDs concurrently from the database.
     * But there might be a potential issue with inserting reserved IDs during the transaction,
     * because another transaction would not see those IDs in the database until the first transaction commits/flushes.
     *
     * On the other hand, we have these available IDs in a a ConcurrentLinkedQueue. So IDs would be available to the next
     * transaction/thread when lock is unlocked, even though the transaction is not commited. But what about multiple instances?
     * One opportunity is to use Hazelcast to share memory fetched IDs amongst instances and use map locks.
     *
     * @param tableName table to generate IDs for
     * @param sessionImpl session to use for fetching and inserting reserved IDs
     * @param availableIds The (empty) queue of available IDs to fill
     */
    private void generateNewIds(String tableName, SessionImpl sessionImpl, ConcurrentLinkedQueue<Long> availableIds) {
        final Lock lock = locks.get(tableName);

        try {
            lock.lock();

            List<Long> retrievedIds = new ArrayList<>();
            while (retrievedIds.isEmpty()) {
                logger.trace("Retrieving new IDs");
                retrievedIds.addAll(retrieveIds(tableName, sessionImpl));
            }
            logger.trace("Inserting {} ids", retrievedIds);
            insertRetrievedIds(tableName, retrievedIds, sessionImpl);
            availableIds.addAll(retrievedIds);
        } finally {
            lock.unlock();
        }
    }

    private String determineTableName(SessionImpl sessionImpl, Object object) {
        return ((AbstractEntityPersister) sessionImpl.getSessionFactory().getAllClassMetadata().get(object.getClass().getCanonicalName())).getTableName();
    }

    private Optional<EntityStructure> entityStructure(Object object) {
        if (object instanceof EntityStructure) {
            EntityStructure entityStructure = (EntityStructure) object;

            logger.trace("{} is instance of entity structre", entityStructure);

            if ((entityStructure instanceof StopPlace || entityStructure instanceof Quay)) {
                return Optional.of(entityStructure);
            }
        }
        return Optional.empty();
    }

    /**
     * Fetch new IDs when all previously fetched IDs taken.
     * @return list of available IDs for table.
     */
    @SuppressWarnings(value = "unchecked")
    private List<Long> retrieveIds(String tableName, SessionImpl sessionImpl) {
        long lastId = lastIdsPerTable.get(tableName);

        List<Long> retrievedIds;
        if (isH2(sessionImpl)) {
            // Because of issues using the query below or query with system range with H2.
            retrievedIds = generateNextAvailableH2Ids(tableName, lastId, ID_FETCH_SIZE);
        } else {
            retrievedIds = selectNextAvailableIds(tableName, lastId, ID_FETCH_SIZE, sessionImpl);
        }

        if(retrievedIds.isEmpty()) {
            lastIdsPerTable.put(tableName, lastId + ID_FETCH_SIZE);
        } else {
            lastIdsPerTable.put(tableName,retrievedIds.get(retrievedIds.size()-1));
        }
        return retrievedIds;
    }


    private void insertRetrievedIds(String tableName, List<Long> list, SessionImpl sessionImpl) {
        if(list.isEmpty()) {
            throw new IllegalArgumentException("No IDs to insert");
        }

        StringBuilder insertUsedIdsSql = new StringBuilder("INSERT INTO id_generator(table_name, id_value) VALUES");

        for (int i = 0; i < list.size(); i++) {
            insertUsedIdsSql.append("('").append(tableName).append("',").append(list.get(i)).append(")");
            if (i < list.size() - 1) {
                insertUsedIdsSql.append(',');
            }
        }
        SQLQuery query = sessionImpl.createSQLQuery(insertUsedIdsSql.toString());
        query.setFlushMode(FlushMode.COMMIT);
        query.executeUpdate();
    }

    private List<Long> selectNextAvailableIds(String tableName, long lastId, int fetchSize, SessionImpl session) {
        logger.trace("Will fetch new IDs from id_generator table for {}, lastId: {}", tableName, lastId);

        String sql = "SELECT generated FROM generate_series(" + lastId + "," + (lastId + ID_FETCH_SIZE) + ") AS generated " +
                "EXCEPT (SELECT id_value FROM id_generator WHERE table_name='" + tableName + "') " +
                "ORDER BY generated";

        SQLQuery sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setFlushMode(FlushMode.COMMIT);
        sqlQuery.addScalar("generated", LongType.INSTANCE);

        List list = sqlQuery.list();
        return list;
    }

    private boolean isH2(SessionImpl sessionImpl) {
        if (isH2 == null) {
            try {
                isH2 = sessionImpl.getPersistenceContext().getSession().connection().getMetaData().getDatabaseProductName().contains("H2");
                logger.info("Detected H2: {}", isH2);
            } catch (SQLException e) {
                throw new RuntimeException("Could not determine database provider", e);
            }
        }
        return isH2;
    }

    /**
     * Generate new in-memory IDs for H2.
     */
    private List<Long> generateNextAvailableH2Ids(String tableName, long lastId, int fetchSize) {
        List<Long> availableIds = new ArrayList<>();

        usedH2Ids.putIfAbsent(tableName, new ConcurrentLinkedQueue<>());



        Long id = lastIdsPerTable.get(tableName);
        Long counter = 0L;
        outer : while (counter < fetchSize) {
            if (usedH2Ids.get(tableName).contains(id)) {
                logger.debug("Looking for next available ID. {} is taken", id);
            } else {
                availableIds.add(id);
            }

            id++;
            counter++;
        }

        return availableIds;
    }
}