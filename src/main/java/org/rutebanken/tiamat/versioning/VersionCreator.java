/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.versioning;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.CycleStorageEquipment;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.GeneralSign;
import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.SanitaryEquipment;
import org.rutebanken.tiamat.model.ShelterEquipment;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.WaitingRoomEquipment;
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

    private static final String VALID_BETWEEN = "validBetween";

    private static final String MODIFICATION_ENUMERATION ="modificationEnumeration";

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
                    public Instant convert(Instant instant, Type<? extends Instant> type, MappingContext mappingContext) {
                        return Instant.from(instant);
                    }
                });


        mapperFactory.classMap(PathLinkEnd.class, PathLinkEnd.class)
                .exclude(ID_FIELD)
                .byDefault()
                .register();

        List<Class<? extends EntityInVersionStructure>> excludeNetexIdAndVersionsForEntities =
                List.of(PlaceEquipment.class);

        excludeNetexIdAndVersionsForEntities.forEach(clazz -> {
            mapperFactory.classMap(clazz, clazz)
                    .exclude("netexId")
                    .exclude("version")
                    .byDefault()
                    .register();
        });

        mapperFactory.classMap(StopPlace.class, StopPlace.class)
                .fieldMap("topographicPlace").converter(stopPlacePassThroughId).add()
                .exclude(ID_FIELD)
                .exclude(VERSION_COMMENT_FIELD)
                .exclude(CHANGED_BY_FIELD)
                .exclude(VALID_BETWEEN)
                .exclude(MODIFICATION_ENUMERATION)
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
                .exclude(VALID_BETWEEN)
                .byDefault()
                .register());

        defaultMapperFacade = mapperFactory.getMapperFacade();
    }

    /**
     * Create next version of entity (copy), before changes are made.
     * Does not increment version or valid between:  Will be done by saver service
     *
     * @param entityInVersionStructure
     * @param type extends {@link EntityInVersionStructure}
     * @return a deep copied stop place with incremented version and valid between set.
     */
    public <T extends EntityInVersionStructure> T createCopy(EntityInVersionStructure entityInVersionStructure, Class<T> type) {
        logger.debug("Create new version for entity: {}", entityInVersionStructure);

        EntityInVersionStructure copy = defaultMapperFacade.map(entityInVersionStructure, type);
        logger.debug("Created copy of entity: {}", copy);

        return type.cast(copy);
    }
}
