package org.rutebanken.tiamat.versioning;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VersionCreator {

    private static final Logger logger = LoggerFactory.getLogger(VersionCreator.class);

    private final VersionIncrementor versionIncrementor;

    private final MapperFacade mapperFacade;

    @Autowired
    public VersionCreator(VersionIncrementor versionIncrementor) {
        this.versionIncrementor = versionIncrementor;

        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        mapperFactory.classMap(StopPlace.class, StopPlace.class)
                .exclude("id")
                .byDefault()
                .register();

//        mapperFactory.classMap(PathLink.class, PathLink.class)
//                .exclude("id")
//                .byDefault()
//                .register();

        mapperFacade = mapperFactory.getMapperFacade();
    }

    public StopPlace createNewVersionFrom(StopPlace stopPlace) {
        logger.debug("Create new version for stop: {}", stopPlace);
        StopPlace copy = mapperFacade.map(stopPlace, StopPlace.class);
        logger.debug("Created copy of stop: {}", copy);
        versionIncrementor.incrementVersion(copy);
        return copy;
    }

}
