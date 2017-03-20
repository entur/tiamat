package org.rutebanken.tiamat.versioning;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.rutebanken.tiamat.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VersionCreator {

    private static final Logger logger = LoggerFactory.getLogger(VersionCreator.class);

    private static final String ID_FIELD = "id";

    private final VersionIncrementor versionIncrementor;

    private final MapperFacade defaultMapperFacade;

    @Autowired
    public VersionCreator(VersionIncrementor versionIncrementor) {
        this.versionIncrementor = versionIncrementor;

        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        mapperFactory.classMap(StopPlace.class, StopPlace.class)
                .exclude(ID_FIELD)
                .byDefault()
                .register();

        final String pathLinkEndPassThroughId = "pathLinkEndPassThrough";

        mapperFactory.getConverterFactory()
                .registerConverter(pathLinkEndPassThroughId, new PassThroughConverter(Quay.class, StopPlace.class));

        mapperFactory.classMap(PathLinkEnd.class, PathLinkEnd.class)
                .exclude(ID_FIELD)
                .fieldMap("quay").converter(pathLinkEndPassThroughId).add()
                .fieldMap("stopPlace").converter(pathLinkEndPassThroughId).add()
                .fieldMap("entrance").converter(pathLinkEndPassThroughId).add()
                .byDefault()
                .register();

        mapperFactory.classMap(TopographicPlace.class, TopographicPlace.class)
                .exclude(ID_FIELD)
                .byDefault()
                .register();

        mapperFactory.classMap(PathLink.class, PathLink.class)
                .exclude(ID_FIELD)
                .byDefault()
                .register();

        defaultMapperFacade = mapperFactory.getMapperFacade();
    }

    public <T extends EntityInVersionStructure> T createNewVersionFrom(EntityInVersionStructure entityInVersionStructure, Class<T> type) {
        logger.debug("Create new version for entity: {}", entityInVersionStructure);
        EntityInVersionStructure copy = defaultMapperFacade.map(entityInVersionStructure, type);
        logger.debug("Created copy of entity: {}", copy);
        versionIncrementor.incrementVersion(copy);
        return type.cast(copy);
    }

}
