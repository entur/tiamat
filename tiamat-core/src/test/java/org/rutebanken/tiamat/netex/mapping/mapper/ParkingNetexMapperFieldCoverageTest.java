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

package org.rutebanken.tiamat.netex.mapping.mapper;

import org.junit.Test;
import org.rutebanken.tiamat.model.Parking;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards {@link ParkingMapper} and the generic Orika {@code byDefault()} mapping registered
 * for {@code Parking} in {@code NetexMapper} against silent field loss on the plain NeTEx
 * import/export path (as distinct from {@code MergingParkingImporter}'s re-import path, which
 * {@link org.rutebanken.tiamat.importer.merging.ParkingMergeFieldCoverageTest} guards
 * separately).
 * <p>
 * {@code alternativeNames} is the motivating example: it has a real NeTEx representation
 * (inherited from {@code SiteElement_VersionStructure}), Tiamat's {@code Parking} model has a
 * matching field, but {@code ParkingMapper} never mapped it and no test exercised the gap —
 * because there was no test whose absence-of-failure could have been misleading, there was
 * simply no test to fail. A field added to {@code Parking} without either a mapping decision
 * or a test now fails this test instead of silently round-tripping to null.
 */
public class ParkingNetexMapperFieldCoverageTest {

    /**
     * Fields mapped between {@code org.rutebanken.netex.model.Parking} and
     * {@code org.rutebanken.tiamat.model.Parking}, each covered by an explicit
     * {@code mapNetexParking<Field>*}/{@code mapInternalParking<Field>*} test pair in
     * {@link org.rutebanken.tiamat.netex.mapping.NetexMapperTest}. Some are mapped by an
     * explicit method in {@link ParkingMapper} (paymentMethods, vehicleEntrances, infoLinks,
     * availabilityConditions, alternativeNames, parkingAreas); the rest are mapped generically
     * by Orika's {@code byDefault()} field-name matching because the NeTEx and Tiamat models
     * both declare a same-named, same-shaped field.
     */
    private static final Set<String> MAPPED_TO_NETEX = Set.of(
            "name",
            "centroid",
            "keyValues",
            "parkingType",
            "parkingVehicleTypes",
            "parkingLayout",
            "numberOfParkingLevels",
            "principalCapacity",
            "totalCapacity",
            "overnightParkingPermitted",
            "prohibitedForHazardousMaterials",
            "rechargingAvailable",
            "secure",
            "realTimeOccupancyAvailable",
            "parkingReservation",
            "bookingUrl",
            "freeParkingOutOfHours",
            "parkingPaymentProcess",
            "paymentMethods",
            "lighting",
            "parkingProperties",
            "parkingAreas",
            "vehicleEntrances",
            "infoLinks",
            "availabilityConditions",
            "alternativeNames",
            "placeEquipments",
            "accessibilityAssessment"
    );

    /**
     * Identity, audit and versioning fields, owned by the versioning machinery rather than
     * describing the place itself.
     */
    private static final Set<String> INFRASTRUCTURE = Set.of(
            "id",
            "netexId",
            "version",
            "created",
            "changed",
            "changedBy",
            "versionComment",
            "modification",
            "status",
            "validBetween",
            "dataSourceRef",
            "responsibilitySetRef",
            "derivedFromVersionRef",
            "derivedFromObjectRef",
            "compatibleWithVersionFrameVersionRef",
            "extensions"
    );

    /**
     * Fields deliberately not mapped to/from NeTEx: either {@code @Transient} on the Tiamat
     * model (so a mapped value would not survive a save/reload anyway), out of the scope
     * Fintraffic's Parking extension actually needs, or genuinely without a NeTEx counterpart.
     * A name belongs here only with a reason recorded in the comment above its group.
     */
    private static final Set<String> KNOWINGLY_NOT_MAPPED_TO_NETEX = Set.of(
            // @Transient on Parking - excluded from Orika's byDefault() in NetexMapper, or
            // simply never persisted, so a mapped value goes nowhere.
            "publicCode",
            "label",
            "defaultCurrency",
            "currenciesAccepted",
            "cardsAccepted",
            "paymentByMobile",
            "pathLinks",
            "pathJunctions",
            "navigationPaths",
            // Site — structure and ownership, out of scope for Parking's NeTEx mapping
            "organisationRef",
            "parentSiteRef",
            "parentZoneRef",
            "adjacentSites",
            "topographicPlace",
            "siteType",
            "atCentre",
            "locale",
            "levels",
            "entrances",
            "equipmentPlaces",
            "localServices",
            "members",
            // SiteElement — descriptive and accessibility attributes not modelled for Parking
            "facilities",
            "nameSuffix",
            "crossRoad",
            "landmark",
            "publicUse",
            "covered",
            "gated",
            "allAreasWheelchairAccessible",
            "personCapacity",
            // Place / addressable place
            "url",
            "image",
            "placeTypes",
            // Zone geometry other than centroid
            "polygon",
            "multiSurface",
            "projections",
            // Group of entities
            "shortName",
            "description",
            "privateCode"
    );

    @Test
    public void everyParkingFieldIsEitherMappedOrKnowinglyNotMapped() {
        Set<String> undeclared = new TreeSet<>(persistentFieldNames());
        undeclared.removeAll(MAPPED_TO_NETEX);
        undeclared.removeAll(INFRASTRUCTURE);
        undeclared.removeAll(KNOWINGLY_NOT_MAPPED_TO_NETEX);

        assertThat(undeclared)
                .as("""
                        Parking has field(s) that ParkingMapper/NetexMapper's byDefault() mapping \
                        has not been told about: %s

                        A field neither mapped explicitly in ParkingMapper nor covered by Orika's \
                        generic byDefault() field-name matching is silently dropped on NeTEx \
                        import/export, with no compile error, no failing test and no log line to \
                        reveal it.

                        Decide and record the decision by adding each name to one of the sets in \
                        this test:
                          MAPPED_TO_NETEX               - and add a mapNetexParking<Field>* /
                        mapInternalParking<Field>* test pair to NetexMapperTest
                          KNOWINGLY_NOT_MAPPED_TO_NETEX - if it is out of scope; say why in the
                        comment above the relevant group
                          INFRASTRUCTURE                - if it is identity, audit or versioning
                        metadata\
                        """.formatted(undeclared))
                .isEmpty();
    }

    /**
     * Catches the opposite drift: a name left behind in one of the sets after the field it
     * referred to was renamed or removed, which would otherwise quietly weaken the guard.
     */
    @Test
    public void declaredFieldNamesAllExistOnParking() {
        Set<String> declared = new LinkedHashSet<>();
        declared.addAll(MAPPED_TO_NETEX);
        declared.addAll(INFRASTRUCTURE);
        declared.addAll(KNOWINGLY_NOT_MAPPED_TO_NETEX);

        Set<String> stale = new TreeSet<>(declared);
        stale.removeAll(persistentFieldNames());

        assertThat(stale)
                .as("Field name(s) declared in this test no longer exist on Parking or its "
                        + "supertypes: %s. Remove them, or correct them if the field was renamed.", stale)
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
     * Every instance field on {@code Parking} and its supertypes, by name.
     */
    private static Set<String> persistentFieldNames() {
        Set<String> names = new TreeSet<>();
        for (Class<?> type = Parking.class; type != null && !Object.class.equals(type); type = type.getSuperclass()) {
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
