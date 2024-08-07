package org.rutebanken.tiamat.versioning.save;

import org.rutebanken.tiamat.model.InfoSpotPoster;
import org.rutebanken.tiamat.repository.InfoSpotPosterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class InfoSpotPosterVersionedSaverService {

    @Autowired
    private InfoSpotPosterRepository infoSpotPosterRepository;

    @Autowired
    private DefaultVersionedSaverService defaultVersionedSaverService;

    public InfoSpotPoster saveNewVersion(InfoSpotPoster infoSpotPoster) {
        return defaultVersionedSaverService.saveNewVersion(infoSpotPoster, infoSpotPosterRepository);
    }
}
