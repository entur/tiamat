package org.rutebanken.tiamat.versioning.save;

import org.rutebanken.tiamat.model.InfoSpot;
import org.rutebanken.tiamat.repository.InfoSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class InfoSpotVersionedSaverService {

    @Autowired
    private InfoSpotRepository infoSpotRepository;

    @Autowired
    private DefaultVersionedSaverService defaultVersionedSaverService;

    public InfoSpot saveNewVersion(InfoSpot infoSpot) {
        return defaultVersionedSaverService.saveNewVersion(infoSpot, infoSpotRepository);
    }
}
