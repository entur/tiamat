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

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Creates new version of already existing objects, by mapping with Orika and ignore primary key "id".
 */
@Service
public class VersionCreator {

    private static final Logger logger = LoggerFactory.getLogger(VersionCreator.class);

    private static final String ID_FIELD = "id";

    private static final String VERSION_COMMENT_FIELD = "versionComment";
    private static final String CHANGED_BY_FIELD = "changedBy";

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
                .registerConverter(new CustomConverter<Instant, Instant>() {
                    @Override
                    public Instant convert(Instant instant, Type<? extends Instant> type) {
                        return Instant.from(instant);
                    }
                });


        mapperFactory.classMap(PathLinkEnd.class, PathLinkEnd.class)
                .exclude(ID_FIELD)
                .byDefault()
                .register();

        mapperFactory.classMap(StopPlace.class, StopPlace.class)
                .fieldMap("topographicPlace").converter(stopPlacePassThroughId).add()
                .exclude(ID_FIELD)
                .exclude(VERSION_COMMENT_FIELD)
                .exclude(CHANGED_BY_FIELD)
                .byDefault()
                .register();

        List<Class<? extends EntityInVersionStructure>> commonClassesToConfigure =
                Arrays.asList(TopographicPlace.class,
                        PathLink.class, PlaceEquipment.class,
                        WaitingRoomEquipment.class, SanitaryEquipment.class,
                        TicketingEquipment.class, ShelterEquipment.class,
                        CycleStorageEquipment.class, GeneralSign.class,
                        AlternativeName.class);


        commonClassesToConfigure.forEach(clazz -> mapperFactory.classMap(clazz, clazz)
                .exclude(VERSION_COMMENT_FIELD)
                .exclude(CHANGED_BY_FIELD)
                .exclude(ID_FIELD)
                .byDefault()
                .register());

        defaultMapperFacade = mapperFactory.getMapperFacade();
    }

    /**
     * Create next version of entity (copy), before changes are made.
     * Does not increment version. Will be done when saving.
     * Clears valid betweens
     *
     * @param entityInVersionStructure
     * @param type extends {@link EntityInVersionStructure}
     * @return a deep copied stop place with incremented version and valid between set.
     */
    public <T extends EntityInVersionStructure> T createCopy(EntityInVersionStructure entityInVersionStructure, Class<T> type) {
        logger.debug("Create new version for entity: {}", entityInVersionStructure);

        Instant newVersionValidFrom = Instant.now();

        EntityInVersionStructure copy = defaultMapperFacade.map(entityInVersionStructure, type);
        logger.debug("Created copy of entity: {}", copy);

        copy.setValidBetween(new ValidBetween(newVersionValidFrom));

        return type.cast(copy);
    }

    private <T extends EntityInVersionStructure> T initiateFirstVersion(EntityInVersionStructure entityInVersionStructure, Class<T> type) {
        logger.debug("Initiating first version for entity {}", entityInVersionStructure.getClass().getSimpleName());
        entityInVersionStructure.setVersion(VersionIncrementor.INITIAL_VERSION);
        return type.cast(entityInVersionStructure);
    }

    public void initiateOrIncrementAccessibilityAssesmentVersion(SiteElement siteElement) {
        AccessibilityAssessment accessibilityAssessment = siteElement.getAccessibilityAssessment();

        if (accessibilityAssessment != null) {
            initiateOrIncrement(accessibilityAssessment);

            if (accessibilityAssessment.getLimitations() != null && !accessibilityAssessment.getLimitations().isEmpty()) {
                AccessibilityLimitation limitation = accessibilityAssessment.getLimitations().get(0);
                initiateOrIncrement(limitation);
            }
        }
    }

    public void initiateOrIncrementAlternativeNamesVersion(List<AlternativeName> alternativeNames) {

        if (alternativeNames != null) {
            alternativeNames.forEach(alternativeName -> initiateOrIncrement(alternativeName));
        }
    }

    public void initiateOrIncrement(EntityInVersionStructure entityInVersionStructure) {
        if(entityInVersionStructure.getNetexId() == null) {
            initiateFirstVersion(entityInVersionStructure, EntityInVersionStructure.class);
        } else {
            versionIncrementor.incrementVersion(entityInVersionStructure);
        }
    }

    public <T extends EntityInVersionStructure> T terminateVersion(T entityInVersionStructure, Instant newVersionValidFrom) {
        //TODO: Need to support "valid from" set explicitly

        if(entityInVersionStructure == null) {
            throw new IllegalArgumentException("Cannot terminate version for null object");
        }

        logger.debug("New version valid from {}", newVersionValidFrom);
        if (entityInVersionStructure.getValidBetween() != null ) {
            ValidBetween validBetween = entityInVersionStructure.getValidBetween();
            validBetween.setToDate(newVersionValidFrom);
        }
        return entityInVersionStructure;
    }
}
