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


import com.google.api.client.util.Preconditions;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

@Transactional
@Service
public class ParkingVersionedSaverService extends VersionedSaverService<Parking> {

    private static final Logger logger = LoggerFactory.getLogger(ParkingVersionedSaverService.class);

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private ReferenceResolver referenceResolver;

    @Override
    public EntityInVersionRepository<Parking> getRepository() {
        return parkingRepository;
    }

    @Override
    public Parking saveNewVersion(Parking newVersion) {

        Preconditions.checkArgument(newVersion.getParentSiteRef() != null, "Parent site ref cannot be null for parking");

        Parking existing = parkingRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());

        resolveAndAuthorizeParkingSiteRef(newVersion);

        Parking result;
        if(existing != null) {
            logger.trace("existing: {}", existing);
            logger.trace("new: {}", newVersion);

            resolveAndAuthorizeParkingSiteRef(existing);
            newVersion.setCreated(existing.getCreated());
            newVersion.setChanged(Instant.now());
            newVersion.setVersion(existing.getVersion());

            parkingRepository.delete(existing);
        } else {
            newVersion.setCreated(Instant.now());
        }


        newVersion.setValidBetween(null);
        versionIncrementor.initiateOrIncrement(newVersion);
        newVersion.setChangedBy(usernameFetcher.getUserNameForAuthenticatedUser());
        result = parkingRepository.save(newVersion);

        logger.info("Saved parking {}, version {}, name {}", result.getNetexId(), result.getVersion(), result.getName());

        metricsService.registerEntitySaved(newVersion.getClass());
        return result;
    }

    /**
     * A parking must refer to a stop place.
     * And the user must be authorized to edit this stop place.
     * In NeTEx, a parking can refer to any site. But this implementation is for now limited to stop place.
     *
     * @param parking
     */
    private void resolveAndAuthorizeParkingSiteRef(Parking parking) {
        DataManagedObjectStructure parentSite = referenceResolver.resolve(parking.getParentSiteRef());
        if(parentSite == null) {
            throw new IllegalArgumentException("Cannot save parking without resolvable parent site ref: " + parking.toString());
        }
        if(!(parentSite instanceof StopPlace)) {
            throw new IllegalArgumentException("Parking must have a parentSiteRef pointing to stop place. Parking: " + parking.toString() + " Parent site: " + parentSite);
        }
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(parentSite));
    }
}
