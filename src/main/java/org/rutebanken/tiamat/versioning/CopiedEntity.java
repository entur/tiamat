package org.rutebanken.tiamat.versioning;


import org.rutebanken.tiamat.model.EntityInVersionStructure;

/**
 * Wrapper object for holding entity and copied version.
 * It could also hold a parent entity with copy, if such exist.
 * @param <T>
 */
public class CopiedEntity<T extends EntityInVersionStructure> {

    private T existingEntity;
    private T copiedEntity;
    private T existingParent;
    private T copiedParent;

    public CopiedEntity(T existingEntity, T copiedEntity, T existingParent, T copiedParent) {
        this.existingEntity = existingEntity;
        this.copiedEntity = copiedEntity;
        this.existingParent = existingParent;
        this.copiedParent = copiedParent;
    }

    public T getExistingEntity() {
        return existingEntity;
    }

    public T getCopiedEntity() {
        return copiedEntity;
    }

    public T getExistingParent() {
        return existingParent;
    }

    public T getCopiedParent() {
        return copiedParent;
    }

    public boolean hasParent() {
        return existingParent != null;
    }
}
