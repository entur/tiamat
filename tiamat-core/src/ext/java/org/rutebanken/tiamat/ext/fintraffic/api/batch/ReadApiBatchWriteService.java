package org.rutebanken.tiamat.ext.fintraffic.api.batch;

import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityInRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service that handles write operations to the Read API database.
 * Uses REQUIRES_NEW propagation to ensure writes happen in separate transactions
 * from the read operations, avoiding transaction isolation level conflicts.
 */
public class ReadApiBatchWriteService {

    private final NetexRepository netexRepository;
    private final Logger logger = LoggerFactory.getLogger(ReadApiBatchWriteService.class);

    public ReadApiBatchWriteService(NetexRepository netexRepository) {
        this.netexRepository = netexRepository;
    }

    /**
     * Upsert entities in a new transaction, independent of any ongoing read transaction.
     * This allows the repository to set its own transaction isolation level without conflicts.
     *
     * @param batch The batch of entities to upsert
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void upsertBatch(List<ReadApiEntityInRecord> batch) {
        logger.info("Upserting batch of {} entities", batch.size());
        netexRepository.upsertEntities(batch);
    }
}
