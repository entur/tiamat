/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.versioning;

import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.service.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Arrays;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

public abstract class VersionedSaverService<T extends EntityInVersionStructure> {

    private static final Logger logger = LoggerFactory.getLogger(VersionedSaverService.class);

    public static final int MILLIS_BETWEEN_VERSIONS = 1;

    @Autowired
    protected UsernameFetcher usernameFetcher;

    @Autowired
    protected ValidityUpdater validityUpdater;

    @Autowired
    protected TiamatObjectDiffer tiamatObjectDiffer;

    @Autowired
    protected VersionCreator versionCreator;

    @Autowired
    protected VersionIncrementor versionIncrementor;

    @Autowired
    protected MetricsService metricsService;

    @Autowired
    protected ReflectionAuthorizationService authorizationService;

    @Autowired
    private VersionValidator versionValidator;

    public abstract EntityInVersionRepository<T> getRepository();

    public <T extends EntityInVersionStructure> T createCopy(T entity, Class<T> type) {
        return versionCreator.createCopy(entity, type);
    }

    public T saveNewVersion(T newVersion) {
        return saveNewVersion(null, newVersion, Instant.now());
    }

    public T saveNewVersion(T existingVersion, T newVersion) {
        return saveNewVersion(existingVersion, newVersion, Instant.now());
    }

    protected T saveNewVersion(T existingVersion, T newVersion, Instant defaultValidFrom) {

        versionValidator.validate(existingVersion, newVersion);

        Instant newVersionValidFrom = validityUpdater.updateValidBetween(existingVersion, newVersion, defaultValidFrom);

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
            newVersion.setCreated(defaultValidFrom);
            // If the new incoming version has the version attribute set, reset it.
            // For tiamat, this is the first time this entity with this ID is saved
            newVersion.setVersion(-1L);
        } else {
            newVersion.setVersion(existingVersion.getVersion());
            newVersion.setChanged(defaultValidFrom);
            validityUpdater.terminateVersion(existingVersion, newVersionValidFrom.minusMillis(MILLIS_BETWEEN_VERSIONS));
            getRepository().save(existingVersion);
        }

        versionIncrementor.initiateOrIncrement(newVersion);

        String usernameForAuthenticatedUser = usernameFetcher.getUserNameForAuthenticatedUser();
        if(newVersion instanceof DataManagedObjectStructure) {
            ((DataManagedObjectStructure) newVersion).setChangedBy(usernameForAuthenticatedUser);
        }

        logger.info("Object {}, version {} changed by user {}", newVersion.getNetexId(), newVersion.getVersion(), usernameForAuthenticatedUser);

        newVersion = getRepository().save(newVersion);
        if(existingVersion != null) {
            tiamatObjectDiffer.logDifference(existingVersion, newVersion);
        }
        metricsService.registerEntitySaved(newVersion.getClass());
        return newVersion;
    }

    protected void authorizeNewVersion(T existingVersion, T newVersion) {
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(existingVersion, newVersion));
    }



}
