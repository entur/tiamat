package org.rutebanken.tiamat.versioning;

import com.vividsolutions.jts.geom.Point;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.tiamat.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Creates new version of already existing objects, by mapping with Orika and ignore primary key "id".
 */
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

        final String pathLinkEndPassThroughId = "pathLinkEndPassThroughId";

        mapperFactory.getConverterFactory()
                .registerConverter(pathLinkEndPassThroughId, new PassThroughConverter(Quay.class, StopPlace.class));

        final String stopPlacePassThroughId = "stopPlacePassThroughId";

        mapperFactory.getConverterFactory()
                .registerConverter(stopPlacePassThroughId, new PassThroughConverter(TopographicPlace.class));

        mapperFactory.getConverterFactory()
                .registerConverter(new PassThroughConverter(Point.class));

        mapperFactory.getConverterFactory()
                .registerConverter(new CustomConverter<ZonedDateTime, ZonedDateTime>() {
                    @Override
                    public ZonedDateTime convert(ZonedDateTime zonedDateTime, Type<? extends ZonedDateTime> type) {
                        return ZonedDateTime.from(zonedDateTime);
                    }
                });

        mapperFactory.classMap(StopPlace.class, StopPlace.class)
                .fieldMap("topographicPlace").converter(stopPlacePassThroughId).add()
                .exclude(ID_FIELD)
                .byDefault()
                .register();

        mapperFactory.classMap(PathLinkEnd.class, PathLinkEnd.class)
                .exclude(ID_FIELD)
                // New version for path link does not mean new version for quay, stop place or entrance.
//                .fieldMap("quay").converter(pathLinkEndPassThroughId).add()
//                .fieldMap("stopPlace").converter(pathLinkEndPassThroughId).add()
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

    public <T extends EntityInVersionStructure> T createNextVersion(EntityInVersionStructure entityInVersionStructure, Class<T> type) {
        logger.debug("Create new version for entity: {}", entityInVersionStructure);

        ZonedDateTime newVersionValidFrom = ZonedDateTime.now();
        System.out.println("New version valid from : " + newVersionValidFrom);
        logger.debug("New version valid from {}", newVersionValidFrom);
        if (!entityInVersionStructure.getValidityConditions().isEmpty()) {
            AvailabilityCondition availabilityCondition = entityInVersionStructure.getValidityConditions().get(0);
            availabilityCondition.setToDate(newVersionValidFrom);
        }

        EntityInVersionStructure copy = defaultMapperFacade.map(entityInVersionStructure, type);
        logger.debug("Created copy of entity: {}", copy);
        versionIncrementor.incrementVersion(copy);

        copy.getValidityConditions().clear();
        copy.getValidityConditions().add(new AvailabilityCondition(newVersionValidFrom));

        return type.cast(copy);
    }

    public StopPlace createNextVersion(StopPlace stopPlace) {

        StopPlace newVersion = createNextVersion(stopPlace, StopPlace.class);
        if (newVersion.getQuays() != null) {
            newVersion.getQuays().forEach(quay -> versionIncrementor.incrementVersion(quay));
        }

        return newVersion;
    }

    public <T extends EntityInVersionStructure> T initiateFirstVersionWithAvailabilityCondition(EntityInVersionStructure entityInVersionStructure, Class<T> type) {
        logger.debug("Initiating new version for entity {}", entityInVersionStructure);
        entityInVersionStructure.setVersion(VersionIncrementor.INITIAL_VERSION);
        entityInVersionStructure.getValidityConditions().add(new AvailabilityCondition(ZonedDateTime.now()));
        return type.cast(entityInVersionStructure);
    }

    public StopPlace initiateFirstVersionWithAvailabilityCondition(StopPlace stopPlace) {
        stopPlace = initiateFirstVersionWithAvailabilityCondition(stopPlace, StopPlace.class);
        if(stopPlace.getQuays() != null) {
            stopPlace.getQuays().forEach(quay -> quay.setVersion(VersionIncrementor.INITIAL_VERSION));
        }
        return stopPlace;
    }

}
