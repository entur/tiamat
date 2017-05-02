package org.rutebanken.tiamat.versioning;


import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopographicPlaceVersionedSaverService extends VersionedSaverService<TopographicPlace> {

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Override
    public EntityInVersionRepository<TopographicPlace> getRepository() {
        return topographicPlaceRepository;
    }
}
