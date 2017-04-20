package org.rutebanken.tiamat.versioning;

import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class VersionedSaverService<T extends EntityInVersionStructure> {

    @Autowired
    private VersionCreator versionCreator;

    public <T extends EntityInVersionStructure> T createCopy(T entity, Class<T> type) {
        return versionCreator.createCopy(entity, type);
    }

    public T saveNewVersion(T newVersion) {
        return saveNewVersion(null, newVersion);
    }

    protected abstract T saveNewVersion(T existingVersion, T newVersion);

    protected void validate(T existingVersion, T newVersion) {

        if(newVersion == null) {
            throw new IllegalArgumentException("Cannot save new version if it's null");
        }

        if (existingVersion == newVersion) {
            throw new IllegalArgumentException("Existing and new version must be different objects");
        }

        if(existingVersion != null) {
            if (existingVersion.getNetexId() == null) {
                throw new IllegalArgumentException("Existing stop place must have netexId set: " + existingVersion);
            }

            if (!existingVersion.getNetexId().equals(newVersion.getNetexId())) {
                throw new IllegalArgumentException("Existing and new StopPlace do not match: " + existingVersion.getNetexId() + " != " + newVersion.getNetexId());
            }
        }
    }
}
