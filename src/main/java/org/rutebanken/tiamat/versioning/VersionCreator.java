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

        mapperFactory.classMap(ValidBetween.class, ValidBetween.class)
                .exclude(ID_FIELD)
                .byDefault()
                .register();

        defaultMapperFacade = mapperFactory.getMapperFacade();
    }

    public <T extends EntityInVersionStructure> T createCopy(EntityInVersionStructure entityInVersionStructure, Class<T> type) {
        logger.debug("Create new version for entity: {}", entityInVersionStructure);

        ZonedDateTime newVersionValidFrom = ZonedDateTime.now();

        EntityInVersionStructure copy = defaultMapperFacade.map(entityInVersionStructure, type);
        logger.debug("Created copy of entity: {}", copy);

        copy.getValidBetweens().clear();
        copy.getValidBetweens().add(new ValidBetween(newVersionValidFrom));

        return type.cast(copy);
    }

    /**
     * Create next version of stop place, before changes are made.
     * Does not increment version. Will be done when saving.
     * @param stopPlace
     * @return a deep copied stop place with incremented version and valid between set.
     */
    public StopPlace createCopy(StopPlace stopPlace) {
        return createCopy(stopPlace, StopPlace.class);
    }

    private <T extends EntityInVersionStructure> T initiateFirstVersion(EntityInVersionStructure entityInVersionStructure, Class<T> type) {
        logger.debug("Initiating first version for entity {}", entityInVersionStructure.getClass().getSimpleName());
        entityInVersionStructure.setVersion(VersionIncrementor.INITIAL_VERSION);
        return type.cast(entityInVersionStructure);
    }

    /**
     * Increment versions for stop place with children.
     * The object must have their netexId set, or else they will get an initial version
     * @param stopPlace with quays and accessibility assessment
     * @return modified StopPlace
     */
    public StopPlace initiateOrIncrementVersions(StopPlace stopPlace) {
        initiateOrIncrement(stopPlace);
        initiateOrIncrementVersionsForChildren(stopPlace);
        ZonedDateTime now = ZonedDateTime.now();
        stopPlace.setCreated(now);
        stopPlace.getValidBetweens().add(new ValidBetween(now));
        return stopPlace;
    }

    private void initiateOrIncrementVersionsForChildren(StopPlace stopPlaceToSave) {

        initiateOrIncrementAccessibilityAssesmentVersion(stopPlaceToSave);

        if (stopPlaceToSave.getQuays() != null) {
            logger.debug("Initiating first versions for {} quays, accessibility assessment and limitations", stopPlaceToSave.getQuays().size());
            stopPlaceToSave.getQuays().forEach(quay -> {
                initiateOrIncrement(quay);
                initiateOrIncrementAccessibilityAssesmentVersion(quay);
            });
        }
    }

    private void initiateOrIncrementAccessibilityAssesmentVersion(SiteElement siteElement) {
        AccessibilityAssessment accessibilityAssessment = siteElement.getAccessibilityAssessment();

        if (accessibilityAssessment != null) {
            initiateOrIncrement(accessibilityAssessment);

            if (accessibilityAssessment.getLimitations() != null && !accessibilityAssessment.getLimitations().isEmpty()) {
                AccessibilityLimitation limitation = accessibilityAssessment.getLimitations().get(0);
                initiateOrIncrement(limitation);
            }
        }
    }

    private void initiateOrIncrement(EntityInVersionStructure entityInVersionStructure) {
        if(entityInVersionStructure.getNetexId() == null) {
            initiateFirstVersion(entityInVersionStructure, EntityInVersionStructure.class);
        } else {
            versionIncrementor.incrementVersion(entityInVersionStructure);
        }
    }

    public <T extends EntityInVersionStructure> T terminateVersion(T entityInVersionStructure, ZonedDateTime newVersionValidFrom) {
        //TODO: Need to support "valid from" set explicitly

        if(entityInVersionStructure == null) {
            throw new IllegalArgumentException("Cannot terminate version for null object");
        }

        logger.debug("New version valid from {}", newVersionValidFrom);
        if (entityInVersionStructure.getValidBetweens() != null && !entityInVersionStructure.getValidBetweens().isEmpty()) {
            ValidBetween validBetween = entityInVersionStructure.getValidBetweens().get(0);
            validBetween.setToDate(newVersionValidFrom);
        }
        return entityInVersionStructure;
    }
}
