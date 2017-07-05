package org.rutebanken.tiamat.auth.check;

import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.helper.organisation.AdministrativeZoneChecker;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

            if (entity instanceof Zone_VersionStructure) {
                Zone_VersionStructure zone = (Zone_VersionStructure) entity;

                if (zone.getCentroid() == null) {
                    logger.warn("Centroid is null for entity, cannot match polygon for topographic place {}-{}, Returning true for entity: {}", topographicPlace.getNetexId(), topographicPlace.getVersion(), zone);
                    return true;
                }

                if (polygon.contains(zone.getCentroid())) {
                    logger.debug("Polygon for topographic place {}-{} contains centroid for {}", topographicPlace.getNetexId(), topographicPlace.getVersion(), zone);
                    return true;
                } else {
                    logger.warn("No polygon match for topographic place {}-{} and centroid for zone {}", topographicPlace.getNetexId(), topographicPlace.getVersion(), zone);
                    return false;
                }
            }

            logger.warn("Cannot check polygon match for entity as it's not instance of zone: {}", entity);
            return true;

        }
        logger.warn("Cannot look for matches in topographic place for entity {} ({})", entity, entity.getClass().getSimpleName());
        return true;
    }
}
