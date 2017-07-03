package org.rutebanken.tiamat.auth;

import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.tiamat.model.Site_VersionStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TiamatAuthorizationService extends ReflectionAuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(TiamatAuthorizationService.class);
    private final String administrativeZoneIdPrefix;
    private final TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    public TiamatAuthorizationService(RoleAssignmentExtractor roleAssignmentExtractor,
                                      @Value("${authorization.enabled:true}") boolean authorizationEnabled,
                                      @Value("${administrative.zone.id.prefix:KVE:TopographicPlace:}") String administrativeZoneIdPrefix,
                                      TopographicPlaceRepository topographicPlaceRepository) {
        super(roleAssignmentExtractor, authorizationEnabled);

        this.administrativeZoneIdPrefix = administrativeZoneIdPrefix;
        this.topographicPlaceRepository = topographicPlaceRepository;
    }


    @Override
    public boolean entityMatchesAdministrativeZone(RoleAssignment roleAssignment, Object entity) {

        if (roleAssignment.getAdministrativeZone() != null) {
            TopographicPlace topographicPlace = topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(administrativeZoneIdPrefix + roleAssignment.getAdministrativeZone());
            if (topographicPlace == null) {
                logger.warn("RoleAssignment contains unknown adminZone reference: {}. Will not allow authorization", roleAssignment.getAdministrativeZone());
                return false;
            }
            Polygon polygon = topographicPlace.getPolygon();

            if (entity instanceof Zone_VersionStructure) {
                Zone_VersionStructure zone = (Zone_VersionStructure) entity;

                if(zone.getCentroid() == null) {
                    logger.warn("Centroid is null for entity, cannot match polygon for topographic place {}-{}, Returning true for entity: {}", topographicPlace.getNetexId(), topographicPlace.getVersion(), zone);
                    return true;
                }

                if(polygon.contains(zone.getCentroid())) {
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

    @Override
    public boolean entityMatchesOrganisationRef(RoleAssignment roleAssignment, Object entity) {

        if(entity instanceof Site_VersionStructure) {

            Site_VersionStructure site = (Site_VersionStructure) entity;

            if (site.getOrganisationRef() != null) {
                String orgRef = site.getOrganisationRef().getValue().getRef();
                if (orgRef != null) {
                    logger.debug("Found org ref {} for entity. Returning true if it matches role assignment organisation :{}", orgRef, roleAssignment.getOrganisation());
                    return orgRef.endsWith(":" + roleAssignment.getOrganisation());
                }
            }
            logger.debug("Org ref is null for entity: {}", entity);
            return true;
        } else {
            logger.warn("Cannot check for organisation for entity {}", entity);
        }
        logger.debug("Entity not instance of version structure: {}", entity);
        return true;
    }
}
