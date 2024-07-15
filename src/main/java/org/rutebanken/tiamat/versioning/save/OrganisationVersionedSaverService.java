package org.rutebanken.tiamat.versioning.save;

import org.rutebanken.tiamat.model.Organisation;
import org.rutebanken.tiamat.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class OrganisationVersionedSaverService {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private DefaultVersionedSaverService defaultVersionedSaverService;

    public Organisation saveNewVersion(Organisation newVersion) {
        return defaultVersionedSaverService.saveNewVersion(newVersion, organisationRepository);
    }
}
