package org.rutebanken.tiamat.service.stopplace;

import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.stereotype.Service;

@Service
public class ChildFromParentResolver {

    public StopPlace resolveChildFromParent(StopPlace parentStopPlace, String childNetexId, long childVersion) {
        return parentStopPlace.getChildren()
                .stream()
                .filter(child -> child.getNetexId().equals(childNetexId) && (childVersion == 0 || child.getVersion() == childVersion))
                .findFirst()
                .get();
    }
}
