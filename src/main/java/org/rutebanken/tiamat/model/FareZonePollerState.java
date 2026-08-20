package org.rutebanken.tiamat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Single-row record of what {@code FareZonePoller} last imported, shared by every replica.
 *
 * <p>Kept in the database rather than in a Hazelcast map: the replicas do not form a Hazelcast
 * cluster (see {@link org.rutebanken.tiamat.lock.PostgresAdvisoryLock}), so a map would be per-pod
 * and every pod would re-import each snapshot once, and it would be lost on restart. Written in the
 * same transaction as the snapshot it describes, so the record and the data cannot diverge.
 */
@Entity
@Table(name = "fare_zone_poller_state")
public class FareZonePollerState {

    /** The table holds exactly one row, under this id. */
    public static final short SINGLETON_ID = 1;

    @Id
    private short id = SINGLETON_ID;

    private String lastImportedHash;

    private Instant lastImportedAt;

    public short getId() {
        return id;
    }

    public String getLastImportedHash() {
        return lastImportedHash;
    }

    public void setLastImportedHash(String lastImportedHash) {
        this.lastImportedHash = lastImportedHash;
    }

    public Instant getLastImportedAt() {
        return lastImportedAt;
    }

    public void setLastImportedAt(Instant lastImportedAt) {
        this.lastImportedAt = lastImportedAt;
    }
}
