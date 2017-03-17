package org.rutebanken.tiamat.versioning;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.rutebanken.tiamat.model.SiteElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopPlaceVersionIncrementor {

    private final VersionIncrementor versionIncrementor;

    @Autowired
    public StopPlaceVersionIncrementor(VersionIncrementor versionIncrementor) {
        this.versionIncrementor = versionIncrementor;
    }

    public void incrementVersion(SiteElement existingPersistedEntity, SiteElement changedUnpersistedEntity) {

        if(existingPersistedEntity.getVersion() != changedUnpersistedEntity.getVersion()) {
            throw new IllegalStateException("Existing and changed entity must have the same version to be able to increment version: "
                    + existingPersistedEntity.getVersion() + "!=" + changedUnpersistedEntity.getVersion());
        }

        boolean shouldIncrement = nameHasChanged(existingPersistedEntity, changedUnpersistedEntity);


        if(shouldIncrement) {
            // Becomes new version
            versionIncrementor.incrementVersion(changedUnpersistedEntity);
        }
    }

    public boolean nameHasChanged(SiteElement existingPersistedEntity, SiteElement changedUnpersistedEntity) {

        if(existingPersistedEntity.getName() != null) {
            if (existingPersistedEntity.getName().equals(changedUnpersistedEntity.getName())) {
                return false;
            }
        }

        return Strings.commonPrefix(
                existingPersistedEntity.getName().getValue(),
                changedUnpersistedEntity.getName().getValue())
                .isEmpty();
    }
}
