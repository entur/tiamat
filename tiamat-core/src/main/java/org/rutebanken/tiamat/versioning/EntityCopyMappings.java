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
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.SanitaryEquipment;
import org.rutebanken.tiamat.model.ShelterEquipment;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.WaitingRoomEquipment;

import java.time.Instant;
import java.util.List;

/**
 * The Orika configuration shared by everything that deep copies a versioned entity.
 * <p>
 * Only the mechanical part lives here: the converters, and the entity classes whose copying rules
 * are the same wherever the copy is made. What differs between callers stays with the caller,
 * because that part carries meaning rather than boilerplate. A new version of a stop place keeps
 * its netexId because it is the same stop place; one built from a submitted payload must not,
 * because it is not. Pulling those decisions in here would hide them.
 * <p>
 * These are separate calls rather than one, because callers interleave their own registrations
 * between them. Note that the sets overlap: {@link PlaceEquipment} is configured both here and by
 * every caller. Registering a class twice appears to accumulate the exclusions rather than have
 * the later registration replace the earlier, which is why callers get the union of both. That was
 * established by reordering these calls and observing that VersionCreatorTest still passed, not
 * from Orika's documentation, so treat it as observed behaviour rather than a guarantee and keep
 * the existing call order when changing a caller.
 */
public final class EntityCopyMappings {

    public static final String ID_FIELD = "id";
    public static final String NETEX_ID_FIELD = "netexId";
    public static final String VERSION_FIELD = "version";
    public static final String VERSION_COMMENT_FIELD = "versionComment";
    public static final String CHANGED_BY_FIELD = "changedBy";
    public static final String VALID_BETWEEN_FIELD = "validBetween";
    public static final String MODIFICATION_ENUMERATION_FIELD = "modificationEnumeration";

    /**
     * Id of the converter that carries a topographic place over by reference instead of deep
     * copying it. Referenced from a field map, so registering it has no effect on its own.
     */
    public static final String TOPOGRAPHIC_PLACE_PASS_THROUGH = "stopPlacePassThroughId";

    /**
     * Entities that are copied the same way wherever they appear: they belong to whatever is being
     * copied, so they lose the audit trail and the primary key of the entity they came from.
     */
    private static final List<Class<? extends EntityInVersionStructure>> COMMON_ENTITIES = List.of(
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

    private EntityCopyMappings() {
    }

    /**
     * A factory with the converters every copy needs already registered. Converters have to be in
     * place before the field maps that reference them, which is why they are not a separate step.
     */
    public static MapperFactory newMapperFactory() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        mapperFactory.getConverterFactory()
                .registerConverter(TOPOGRAPHIC_PLACE_PASS_THROUGH, new PassThroughConverter(TopographicPlace.class));

        mapperFactory.getConverterFactory()
                .registerConverter(new PassThroughConverter(Point.class));

        // Geometry and instants are values, not entities: copying them field by field would build
        // an equal but distinct object where the original reference is what is wanted.
        mapperFactory.getConverterFactory()
                .registerConverter(new CustomConverter<Instant, Instant>() {
                    @Override
                    public Instant convert(Instant instant, Type<? extends Instant> type, MappingContext mappingContext) {
                        return Instant.from(instant);
                    }
                });

        return mapperFactory;
    }

    /**
     * A path link end refers to a place rather than owning it, so only the primary key is dropped.
     */
    public static void registerPathLinkEnd(MapperFactory mapperFactory) {
        mapperFactory.classMap(PathLinkEnd.class, PathLinkEnd.class)
                .exclude(ID_FIELD)
                .byDefault()
                .register();
    }

    /**
     * Registers {@link #COMMON_ENTITIES}. Call this last, as the existing callers do: it overlaps
     * with the entity lists they register themselves.
     */
    public static void registerCommonEntities(MapperFactory mapperFactory) {
        COMMON_ENTITIES.forEach(clazz -> mapperFactory.classMap(clazz, clazz)
                .exclude(VERSION_COMMENT_FIELD)
                .exclude(CHANGED_BY_FIELD)
                .exclude(ID_FIELD)
                .exclude(VALID_BETWEEN_FIELD)
                .byDefault()
                .register());
    }
}
