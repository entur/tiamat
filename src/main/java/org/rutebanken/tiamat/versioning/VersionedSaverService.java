package org.rutebanken.tiamat.versioning;

import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Arrays;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

public abstract class VersionedSaverService<T extends EntityInVersionStructure> {

    private static final Logger logger = LoggerFactory.getLogger(VersionedSaverService.class);

    @Autowired
    protected UsernameFetcher usernameFetcher;

    @Autowired
    protected ValidityUpdater validityUpdater;

    @Autowired
    protected TiamatObjectDiffer tiamatObjectDiffer;

    @Autowired
    protected VersionCreator versionCreator;

    @Autowired
    protected ReflectionAuthorizationService authorizationService;

    public abstract EntityInVersionRepository<T> getRepository();

    public <T extends EntityInVersionStructure> T createCopy(T entity, Class<T> type) {
        return versionCreator.createCopy(entity, type);
    }

    public T saveNewVersion(T newVersion) {
        return saveNewVersion(null, newVersion);
    }

    protected T saveNewVersion(T existingVersion, T newVersion) {

        validate(existingVersion, newVersion);

        Instant now = Instant.now();
        Instant newVersionValidFrom = validityUpdater.updateValidBetween(newVersion, now);

        if(existingVersion == null) {
            if (newVersion.getNetexId() != null) {
                existingVersion = getRepository().findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());
                if (existingVersion != null) {
                    logger.debug("Found existing entity from netexId {}", existingVersion.getNetexId());
                }
            }
        }

        authorizeNewVersion(existingVersion, newVersion);

        if(existingVersion == null) {
            newVersion.setCreated(now);
            // If the new incoming version has the version attribute set, reset it.
            // For tiamat, this is the first time this entity with this ID is saved
            newVersion.setVersion(-1L);
        } else {
            newVersion.setVersion(existingVersion.getVersion());
            validityUpdater.terminateVersion(existingVersion, newVersionValidFrom);
            getRepository().save(existingVersion);
        }

        versionCreator.initiateOrIncrement(newVersion);

        String usernameForAuthenticatedUser = usernameFetcher.getUserNameForAuthenticatedUser();
        if(newVersion instanceof DataManagedObjectStructure) {
            ((DataManagedObjectStructure) newVersion).setChangedBy(usernameForAuthenticatedUser);
        }

        logger.info("Object {}, version {} changed by user {}", newVersion.getNetexId(), newVersion.getVersion(), usernameForAuthenticatedUser);

        newVersion = getRepository().save(newVersion);
        if(existingVersion != null) {
            tiamatObjectDiffer.logDifference(existingVersion, newVersion);
        }
        return newVersion;
    }

    protected void authorizeNewVersion(T existingVersion, T newVersion) {
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(existingVersion, newVersion));
    }

    protected void validate(T existingVersion, T newVersion) {

        if(newVersion == null) {
            throw new IllegalArgumentException("Cannot save new version if it's null");
        }

        if (existingVersion == newVersion) {
            throw new IllegalArgumentException("Existing and new version must be different objects");
        }

        if(existingVersion != null) {
            if (existingVersion.getNetexId() == null) {
                throw new IllegalArgumentException("Existing entity must have netexId set: " + existingVersion);
            }

            if (!existingVersion.getNetexId().equals(newVersion.getNetexId())) {
                throw new IllegalArgumentException("Existing and new entity do not match: " + existingVersion.getNetexId() + " != " + newVersion.getNetexId());
            }
        }
    }


}
