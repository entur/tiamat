package org.rutebanken.tiamat.versioning.save;

import com.google.common.collect.Sets;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.repository.PurposeOfGroupingRepository;
import org.rutebanken.tiamat.versioning.VersionIncrementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.time.Instant.now;

@Service
@Transactional
public class PurposeOfGroupingSaverService {
    private static final Logger logger = LoggerFactory.getLogger(PurposeOfGroupingSaverService.class);

    @Autowired
    PurposeOfGroupingRepository purposeOfGroupingRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private VersionIncrementor versionIncrementor;

    public PurposeOfGrouping saveNewVersion(PurposeOfGrouping newVersion) {
        final PurposeOfGrouping existing = purposeOfGroupingRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());

        if (existing != null) {
            logger.trace("existing: {}", existing);
            logger.trace("new: {}", newVersion);

            authorizationService.verifyCanEditEntities( Sets.newHashSet(existing));

            newVersion.setCreated(existing.getCreated());
            newVersion.setChanged(now());
            newVersion.setVersion(existing.getVersion());

            purposeOfGroupingRepository.delete(existing);
        } else {
            newVersion.setCreated(now());
        }

        newVersion.setValidBetween(null);
        versionIncrementor.initiateOrIncrement(newVersion);
        newVersion.setChangedBy(usernameFetcher.getUserNameForAuthenticatedUser());

        return purposeOfGroupingRepository.save(newVersion);


    }
}
