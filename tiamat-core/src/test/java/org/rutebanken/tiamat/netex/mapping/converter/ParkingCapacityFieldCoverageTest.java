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

package org.rutebanken.tiamat.netex.mapping.converter;

import org.junit.Test;
import org.rutebanken.tiamat.model.ParkingCapacity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards {@link ParkingPropertiesListConverter} against silent field loss on a parking
 * capacity group.
 * <p>
 * {@code ParkingNetexMapperFieldCoverageTest} guards the same drift one level higher, on
 * {@code Parking}. That guard lists {@code parkingProperties} as mapped and stops there. It
 * never looks inside a capacity group.
 * <p>
 * Orika does not map a capacity group generically. {@link ParkingPropertiesListConverter}
 * names every field, so the converter loses a new field until somebody adds a line to it.
 */
public class ParkingCapacityFieldCoverageTest {

    /**
     * Fields that {@link ParkingPropertiesListConverter} copies in both directions.
     */
    private static final Set<String> MAPPED_TO_NETEX = Set.of(
            "parkingUserType",
            "parkingVehicleType",
            "parkingStayType",
            "numberOfSpaces",
            "numberOfSpacesWithRechargePoint"
    );

    /**
     * Identity, audit and versioning fields. The converter copies the NeTEx id and the
     * version, and the versioning machinery owns the rest.
     */
    private static final Set<String> INFRASTRUCTURE = Set.of(
            "id",
            "netexId",
            "version",
            "created",
            "changed",
            "modification",
            "status",
            "validBetween",
            "dataSourceRef",
            "derivedFromVersionRef",
            "derivedFromObjectRef",
            "compatibleWithVersionFrameVersionRef",
            "extensions"
    );

    /**
     * Fields deliberately not mapped. A name belongs here only with a reason.
     */
    private static final Set<String> KNOWINGLY_NOT_MAPPED_TO_NETEX = Set.of(
            // @Transient on ParkingCapacity, so a mapped value would not survive a save.
            // The parent relation is expressed by the owning ParkingProperties instead.
            "parentRef"
    );

    @Test
    public void everyParkingCapacityFieldIsEitherMappedOrKnowinglyNotMapped() {
        Set<String> undeclared = new TreeSet<>(persistentFieldNames());
        undeclared.removeAll(MAPPED_TO_NETEX);
        undeclared.removeAll(INFRASTRUCTURE);
        undeclared.removeAll(KNOWINGLY_NOT_MAPPED_TO_NETEX);

        assertThat(undeclared)
                .as("""
                        ParkingCapacity has field(s) that ParkingPropertiesListConverter has not \
                        been told about: %s

                        The converter names every field it copies. A field it does not name is \
                        dropped on NeTEx import and on NeTEx export, with no compile error and no \
                        log line.

                        Decide and record the decision by adding each name to one of the sets in \
                        this test:
                          MAPPED_TO_NETEX               - and copy the field in BOTH convertTo and
                        convertFrom, then assert it in ParkingPropertiesListConverterTest
                          KNOWINGLY_NOT_MAPPED_TO_NETEX - if it is out of scope; say why
                          INFRASTRUCTURE                - if it is identity, audit or versioning
                        metadata\
                        """.formatted(undeclared))
                .isEmpty();
    }

    @Test
    public void declaredFieldNamesAllExistOnParkingCapacity() {
        Set<String> declared = new LinkedHashSet<>();
        declared.addAll(MAPPED_TO_NETEX);
        declared.addAll(INFRASTRUCTURE);
        declared.addAll(KNOWINGLY_NOT_MAPPED_TO_NETEX);

        Set<String> stale = new TreeSet<>(declared);
        stale.removeAll(persistentFieldNames());

        assertThat(stale)
                .as("Field name(s) declared in this test no longer exist on ParkingCapacity or "
                        + "its supertypes: %s. Remove them, or correct them after a rename.", stale)
                .isEmpty();
    }

    @Test
    public void theThreeSetsDoNotOverlap() {
        assertThat(intersection(MAPPED_TO_NETEX, INFRASTRUCTURE))
                .as("A field cannot be both mapped and infrastructure").isEmpty();
        assertThat(intersection(MAPPED_TO_NETEX, KNOWINGLY_NOT_MAPPED_TO_NETEX))
                .as("A field cannot be both mapped and knowingly not mapped").isEmpty();
        assertThat(intersection(INFRASTRUCTURE, KNOWINGLY_NOT_MAPPED_TO_NETEX))
                .as("A field cannot be both infrastructure and knowingly not mapped").isEmpty();
    }

    /**
     * Every instance field on {@code ParkingCapacity} and its supertypes, by name.
     */
    private static Set<String> persistentFieldNames() {
        Set<String> names = new TreeSet<>();
        for (Class<?> type = ParkingCapacity.class; type != null && !Object.class.equals(type); type = type.getSuperclass()) {
            Arrays.stream(type.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .filter(field -> !field.isSynthetic())
                    .map(Field::getName)
                    .forEach(names::add);
        }
        return names;
    }

    private static Set<String> intersection(Set<String> a, Set<String> b) {
        Set<String> result = new TreeSet<>(a);
        result.retainAll(b);
        return result;
    }
}
