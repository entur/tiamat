package org.rutebanken.tiamat.rest.write.mapper;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.CycleStorageEquipment;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.GeneralSign;
import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.PostalAddress;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SanitaryEquipment;
import org.rutebanken.tiamat.model.ShelterEquipment;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.WaitingRoomEquipment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class UpdateStopPlaceMapper {

    private static final Logger logger = LoggerFactory.getLogger(
            UpdateStopPlaceMapper.class
    );

    private static final String ID_FIELD = "id";

    private static final String VERSION_COMMENT_FIELD = "versionComment";
    private static final String CHANGED_BY_FIELD = "changedBy";

    private static final String VALID_BETWEEN = "validBetween";

    private static final String MODIFICATION_ENUMERATION =
            "modificationEnumeration";

    private final MapperFacade defaultMapperFacade;

    public UpdateStopPlaceMapper() {
        MapperFactory mapperFactory =
                new DefaultMapperFactory.Builder().build();

        final String stopPlacePassThroughId = "stopPlacePassThroughId";

        mapperFactory
                .getConverterFactory()
                .registerConverter(
                        stopPlacePassThroughId,
                        new PassThroughConverter(TopographicPlace.class)
                );

        mapperFactory
                .getConverterFactory()
                .registerConverter(new PassThroughConverter(Point.class));

        mapperFactory
                .getConverterFactory()
                .registerConverter(
                        new CustomConverter<Instant, Instant>() {
                            @Override
                            public Instant convert(
                                    Instant instant,
                                    Type<? extends Instant> type,
                                    MappingContext mappingContext
                            ) {
                                return Instant.from(instant);
                            }
                        }
                );

        mapperFactory
                .classMap(PathLinkEnd.class, PathLinkEnd.class)
                .exclude(ID_FIELD)
                .byDefault()
                .register();

        List<
                Class<? extends EntityInVersionStructure>
                > excludeVersionsForEntities = Arrays.asList(
                PlaceEquipment.class,
                InstalledEquipment_VersionStructure.class,
                Quay.class,
                StopPlace.class,
                AccessibilityAssessment.class,
                AccessibilityLimitation.class,
                PostalAddress.class
        );

        excludeVersionsForEntities.forEach(clazz -> {
            mapperFactory
                    .classMap(clazz, clazz)
                    .exclude("version")
                    .byDefault()
                    .register();
        });

        mapperFactory
                .classMap(StopPlace.class, StopPlace.class)
                .fieldMap("topographicPlace")
                .converter(stopPlacePassThroughId)
                .add()
                .exclude(ID_FIELD)
                .exclude(VERSION_COMMENT_FIELD)
                .exclude(CHANGED_BY_FIELD)
                .exclude(VALID_BETWEEN)
                .exclude(MODIFICATION_ENUMERATION)
                .byDefault()
                .register();

        List<
                Class<? extends EntityInVersionStructure>
                > commonClassesToConfigure = Arrays.asList(
                TopographicPlace.class,
                PathLink.class,
                PlaceEquipment.class,
                WaitingRoomEquipment.class,
                SanitaryEquipment.class,
                TicketingEquipment.class,
                ShelterEquipment.class,
                CycleStorageEquipment.class,
                GeneralSign.class,
                AlternativeName.class
        );

        commonClassesToConfigure.forEach(clazz ->
                mapperFactory
                        .classMap(clazz, clazz)
                        .exclude(VERSION_COMMENT_FIELD)
                        .exclude(CHANGED_BY_FIELD)
                        .exclude(ID_FIELD)
                        .exclude(VALID_BETWEEN)
                        .byDefault()
                        .register()
        );

        defaultMapperFacade = mapperFactory.getMapperFacade();
    }

    public void update(
            EntityInVersionStructure before,
            EntityInVersionStructure after
    ) {
        logger.debug("Mapping from {} to {}. Before: {} After: {}", before.getClass(), after.getClass(), before, after);
        defaultMapperFacade.map(before, after);
    }
}
