package org.rutebanken.tiamat.versioning;

import org.keycloak.KeycloakPrincipal;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Arrays;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

public abstract class VersionedSaverService<T extends EntityInVersionStructure> {

    private static final Logger logger = LoggerFactory.getLogger(VersionedSaverService.class);

    @Autowired
    private VersionCreator versionCreator;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    public abstract EntityInVersionRepository<T> getRepository();

    public <T extends EntityInVersionStructure> T createCopy(T entity, Class<T> type) {
        return versionCreator.createCopy(entity, type);
    }

    public T saveNewVersion(T newVersion) {
        return saveNewVersion(null, newVersion);
    }

    protected T saveNewVersion(T existingVersion, T newVersion) {

        validate(existingVersion, newVersion);

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
            newVersion.setCreated(Instant.now());
            // If the new incoming version has the version attribute set, reset it.
            // For tiamat, this is the first time this entity with this ID is saved
            newVersion.setVersion(-1L);
        } else {
            newVersion.setVersion(existingVersion.getVersion());
            existingVersion = versionCreator.terminateVersion(existingVersion, Instant.now());
            getRepository().save(existingVersion);
        }

        versionCreator.initiateOrIncrement(newVersion);

        String usernameForAuthenticatedUser = getUserNameForAuthenticatedUser();
        if(newVersion instanceof DataManagedObjectStructure) {
            ((DataManagedObjectStructure) newVersion).setChangedBy(usernameForAuthenticatedUser);
        }

        logger.info("Object {}, version {} changed by user {}", newVersion.getNetexId(), newVersion.getVersion(), usernameForAuthenticatedUser);

        return getRepository().save(newVersion);
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

    /*
     * Gets username from Spring Security
     *
     * Expects property keycloak.principal-attribute=preferred_username
     *
     */
    protected String getUserNameForAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() != null &&
                    authentication.getPrincipal() instanceof KeycloakPrincipal) {
                return ((KeycloakPrincipal)authentication.getPrincipal()).getName();
            }
        }
        return null;
    }
}
