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

package org.rutebanken.tiamat.service.stopplace;

import jakarta.transaction.Transactional;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class StopPlaceTopographicRefUpdater {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceTopographicRefUpdater.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private TopographicPlaceLookupService countyAndMunicipalityLookupService;

    public boolean update(StopPlace stopPlace) {
        if (stopPlace.getTopographicPlace() == null) {
            logger.info("Stop Place does not have reference to topographic place: {}", stopPlace);

            countyAndMunicipalityLookupService.populateTopographicPlaceRelation(stopPlace);
            stopPlaceRepository.save(stopPlace);
            return true;
        }
        return false;
    }


}
