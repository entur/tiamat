package org.rutebanken.tiamat.ext.fintraffic.api.repository;

import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityInRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityOutRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiSearchKey;

import java.util.Collection;
import java.util.stream.Stream;

public interface NetexRepository {
    void upsertEntities(Collection<ReadApiEntityInRecord> entityRecords);
    void checkDatabaseConsistency();
    Stream<ReadApiEntityOutRecord> streamStopPlaces(ReadApiSearchKey searchKey);
    int markAllEntitiesAsStale();
    int removeStaleEntities();
}
