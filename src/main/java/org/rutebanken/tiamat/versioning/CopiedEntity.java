package org.rutebanken.tiamat.versioning;


import org.rutebanken.tiamat.model.EntityInVersionStructure;

/**
 * Wrapper object for holding entity and copied version.
 * It could also hold a parent entity with copy, if such exist.
 * @param <T>
 */
public class CopiedEntity<T extends EntityInVersionStructure> {

    private T entity;
    private T copiedEntity;
    private T parent;
    private T copiedParent;

    public CopiedEntity(T entity, T copiedEntity, T parent, T copiedParent) {
        this.entity = entity;
        this.copiedEntity = copiedEntity;
        this.parent = parent;
        this.copiedParent = copiedParent;
    }

    public T getEntity() {
        return entity;
    }

    public T getCopiedEntity() {
        return copiedEntity;
    }

    public T getParent() {
        return parent;
    }

    public T getCopiedParent() {
        return copiedParent;
    }

    public boolean hasParent() {
        return parent != null;
    }
}
