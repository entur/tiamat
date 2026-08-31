package org.rutebanken.tiamat.writer.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.versioning.EntityCopyMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Applies a submitted stop place onto the version already stored.
 * <p>
 * Unlike creating one, the entities keep their identity: this is the same stop place and the same
 * quays taking a new version. Only the version itself is dropped, so that the saver decides what
 * it becomes rather than the caller asserting it.
 * <p>
 * The topographic place is excluded outright rather than carried across, because it is derived
 * from the coordinates and is not the caller's to change.
 */
@Service
public class UpdateStopPlaceMapper {

    private static final Logger logger = LoggerFactory.getLogger(UpdateStopPlaceMapper.class);

    private final MapperFacade defaultMapperFacade;

    public UpdateStopPlaceMapper() {
        MapperFactory mapperFactory = EntityCopyMappings.newMapperFactory();

        EntityCopyMappings.registerPathLinkEnd(mapperFactory);

        StopPlaceOwnedEntities.ALL.forEach(clazz -> mapperFactory.classMap(clazz, clazz)
                .exclude(EntityCopyMappings.VERSION_FIELD)
                .byDefault()
                .register());

        mapperFactory.classMap(StopPlace.class, StopPlace.class)
                .exclude(EntityCopyMappings.ID_FIELD)
                .exclude(EntityCopyMappings.VERSION_FIELD)
                .exclude(EntityCopyMappings.NETEX_ID_FIELD)
                .exclude(EntityCopyMappings.VERSION_COMMENT_FIELD)
                .exclude(EntityCopyMappings.CHANGED_BY_FIELD)
                .exclude(EntityCopyMappings.VALID_BETWEEN_FIELD)
                .exclude(EntityCopyMappings.MODIFICATION_ENUMERATION_FIELD)
                .exclude("topographicPlace")
                .byDefault()
                .register();

        EntityCopyMappings.registerCommonEntities(mapperFactory);

        defaultMapperFacade = mapperFactory.getMapperFacade();
    }

    public void update(
            EntityInVersionStructure before,
            EntityInVersionStructure after
    ) {
        logger.debug("Mapping from {} to {}. Before: {} After: {}", before.getClass(), after.getClass(), before, after);
        defaultMapperFacade.map(after, before);
    }
}
