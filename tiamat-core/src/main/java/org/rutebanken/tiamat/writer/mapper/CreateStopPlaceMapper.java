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
 * Copies a stop place submitted to the write API into one the register can store.
 * <p>
 * Identity is dropped throughout the whole graph, which is what separates this from copying an
 * existing stop place. A submitted payload describes a stop place that does not exist yet, so any
 * id or version on it describes nothing: ids are the register's to hand out, and honouring a
 * submitted one would let a caller pick its own identity or collide with something already stored.
 */
@Service
public class CreateStopPlaceMapper {

    private static final Logger logger = LoggerFactory.getLogger(CreateStopPlaceMapper.class);

    private final MapperFacade defaultMapperFacade;

    public CreateStopPlaceMapper() {
        MapperFactory mapperFactory = EntityCopyMappings.newMapperFactory();

        EntityCopyMappings.registerPathLinkEnd(mapperFactory);

        StopPlaceOwnedEntities.ALL.forEach(clazz -> mapperFactory.classMap(clazz, clazz)
                .exclude(EntityCopyMappings.NETEX_ID_FIELD)
                .exclude(EntityCopyMappings.VERSION_FIELD)
                .byDefault()
                .register());

        // The topographic place is resolved from the coordinates rather than submitted, so it is
        // carried by reference instead of being copied.
        mapperFactory.classMap(StopPlace.class, StopPlace.class)
                .fieldMap("topographicPlace").converter(EntityCopyMappings.TOPOGRAPHIC_PLACE_PASS_THROUGH).add()
                .exclude(EntityCopyMappings.NETEX_ID_FIELD)
                .exclude(EntityCopyMappings.VERSION_FIELD)
                .exclude(EntityCopyMappings.ID_FIELD)
                .exclude(EntityCopyMappings.VERSION_COMMENT_FIELD)
                .exclude(EntityCopyMappings.CHANGED_BY_FIELD)
                .exclude(EntityCopyMappings.VALID_BETWEEN_FIELD)
                .exclude(EntityCopyMappings.MODIFICATION_ENUMERATION_FIELD)
                .byDefault()
                .register();

        EntityCopyMappings.registerCommonEntities(mapperFactory);

        defaultMapperFacade = mapperFactory.getMapperFacade();
    }

    public <T extends EntityInVersionStructure> T createCopy(
        EntityInVersionStructure entityInVersionStructure,
        Class<T> type
    ) {
        logger.debug(
            "Create new version for entity: {}",
            entityInVersionStructure
        );

        EntityInVersionStructure copy = defaultMapperFacade.map(
            entityInVersionStructure,
            type
        );
        logger.debug("Created copy of entity: {}", copy);

        return type.cast(copy);
    }
}
