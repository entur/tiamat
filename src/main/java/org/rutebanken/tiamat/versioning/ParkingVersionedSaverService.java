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


import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Transactional
@Service
public class ParkingVersionedSaverService extends VersionedSaverService<Parking> {

    private static final Logger logger = LoggerFactory.getLogger(ParkingVersionedSaverService.class);

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Override
    public EntityInVersionRepository<Parking> getRepository() {
        return parkingRepository;
    }

    @Override
    public Parking saveNewVersion(Parking newVersion) {

        Parking existing = parkingRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());

        Parking result;
        if(existing != null) {
            logger.trace("existing: {}", existing);
            logger.trace("new: {}", newVersion);

            newVersion.setCreated(existing.getCreated());
            newVersion.setChanged(Instant.now());
            newVersion.setVersion(existing.getVersion());

            parkingRepository.delete(existing);
        } else {
            newVersion.setCreated(Instant.now());
        }
        newVersion.setValidBetween(null);
        versionIncrementor.incrementVersion(newVersion);
        newVersion.setChangedBy(usernameFetcher.getUserNameForAuthenticatedUser());
        result = parkingRepository.save(newVersion);

        logger.info("Saved parking {}, version {}, name {}", result.getNetexId(), result.getVersion(), result.getName());

        metricsService.registerEntitySaved(newVersion.getClass());
        return result;
    }
}
