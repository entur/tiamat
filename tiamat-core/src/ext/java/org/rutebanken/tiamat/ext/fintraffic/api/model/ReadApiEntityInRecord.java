package org.rutebanken.tiamat.ext.fintraffic.api.model;

/**
 * Record representing a Read Api entity stored in the database.
 *
 * @param id Unique identifier of the entity
 * @param type Type of the entity
 * @param searchKey Search key associated with the entity
 * @param xml XML content of the NeTEx-entity
 * @param version Version number of the entity
 * @param changed Timestamp of the last change to the entity
 * @param status Status of the entity
 * @param parentRefs Array of parent NeTEx ids associated with the entity
 */
public record ReadApiEntityInRecord(
    String id,
    String type,
    String searchKey,
    String xml,
    long version,
    long changed,
    ReadApiEntityStatus status,
    String[] parentRefs
) {
}
