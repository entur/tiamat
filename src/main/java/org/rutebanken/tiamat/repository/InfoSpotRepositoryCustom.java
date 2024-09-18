package org.rutebanken.tiamat.repository;

import java.util.List;
import org.rutebanken.tiamat.model.InfoSpot;

public interface InfoSpotRepositoryCustom extends DataManagedObjectStructureRepository<InfoSpot> {

    List<InfoSpot> findForAssociation(String netexId);
}
