package org.rutebanken.tiamat.versioning.save;

import org.rutebanken.tiamat.model.Organisation;
import org.rutebanken.tiamat.repository.OrganisationRepository;
import org.rutebanken.tiamat.versioning.VersionIncrementor;
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

    @Autowired
    private VersionIncrementor versionIncrementor;

    public Organisation saveNewVersion(Organisation newVersion) {
        versionIncrementor.initiateOrIncrementVersions(newVersion);
        return defaultVersionedSaverService.saveNewVersion(newVersion, organisationRepository);
    }
}
