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

package org.rutebanken.tiamat.auth.check;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.helper.organisation.AdministrativeZoneChecker;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopographicPlaceChecker implements AdministrativeZoneChecker {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceChecker.class);

    private final TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    public TopographicPlaceChecker(TopographicPlaceRepository topographicPlaceRepository) {
        this.topographicPlaceRepository = topographicPlaceRepository;
    }

    @Override
    public boolean entityMatchesAdministrativeZone(RoleAssignment roleAssignment, Object entity) {

        if (roleAssignment.getAdministrativeZone() != null) {
            TopographicPlace topographicPlace = topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(roleAssignment.getAdministrativeZone());
            if (topographicPlace == null) {
                logger.warn("RoleAssignment contains unknown adminZone reference: {}. Will not allow authorization", roleAssignment.getAdministrativeZone());
                return false;
            }
            Polygon polygon = topographicPlace.getPolygon();

            if (entity instanceof Zone_VersionStructure zone) {
                if (zone.getCentroid() == null) {
                    logger.warn("Centroid is null for entity, cannot match polygon for topographic place {}-{}, Returning true for entity: {}", topographicPlace.getNetexId(), topographicPlace.getVersion(), zone);
                    return true;
                }

                if (polygon.contains(zone.getCentroid())) {
                    logger.debug("Polygon for topographic place {}-{} contains centroid for {}", topographicPlace.getNetexId(), topographicPlace.getVersion(), zone);
                    return true;
                } else {
                    return false;
                }
            }

            logger.warn("Cannot check polygon match for entity as it's not instance of zone: {}", entity);
            return true;

        }
        logger.warn("Cannot look for matches in topographic place for entity {} ({})", entity, entity.getClass().getSimpleName());
        return true;
    }

    public boolean pointMatchesAdministrativeZone(RoleAssignment roleAssignment, Point point) {
        if (roleAssignment.getAdministrativeZone() != null) {
            TopographicPlace topographicPlace = topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(roleAssignment.getAdministrativeZone());
            if (topographicPlace == null) {
                logger.warn("RoleAssignment contains unknown adminZone reference: {}. Will not allow authorization", roleAssignment.getAdministrativeZone());
                return false;
            }
            Polygon polygon = topographicPlace.getPolygon();

                if (polygon.contains(point)) {
                    logger.debug("Polygon for topographic place {}-{} contains point for {}", topographicPlace.getNetexId(), topographicPlace.getVersion(), point);
                    return true;
                } else {
                    logger.warn("No polygon match for topographic place {}-{} and point {}", topographicPlace.getNetexId(), topographicPlace.getVersion(), point);
                    return false;
                }

        }
        logger.warn("Cannot look for matches in topographic place for point {} ({})", point, point.getClass().getSimpleName());
        return true;
    }
}
