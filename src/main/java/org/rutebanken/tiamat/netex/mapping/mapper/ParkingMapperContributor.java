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

import ma.glasnost.orika.MappingContext;

/**
 * Extension hook for the NeTEx ↔ Tiamat {@link ParkingMapper}.
 * <p>
 * Implementations registered as Spring beans (e.g. activated by a specific Spring profile)
 * are discovered automatically and called during both import ({@link #mapFromNetex}) and
 * export ({@link #mapToNetex}).
 * <p>
 * This allows extension modules to persist and emit fields that are {@code @Transient} in
 * Entur's core model (e.g. {@code paymentMethods}, {@code vehicleEntrances}) without
 * modifying core mapper code.
 */
public interface ParkingMapperContributor {

    /**
     * Called during NeTEx import after the default Orika mapping has run.
     * Implementations may copy additional fields from the NeTEx source onto the
     * Tiamat target (typically the {@code @Transient} fields that the core mapper
     * ignores).  The {@link org.rutebanken.tiamat.importer.merging.MergingParkingImporter}
     * then persists those values via its {@code mergeExtendedFields} hook.
     *
     * @param source  the NeTEx {@link org.rutebanken.netex.model.Parking} being imported
     * @param target  the Tiamat {@link org.rutebanken.tiamat.model.Parking} being populated
     * @param context the current Orika mapping context
     */
    void mapFromNetex(org.rutebanken.netex.model.Parking source,
                      org.rutebanken.tiamat.model.Parking target,
                      MappingContext context);

    /**
     * Called during NeTEx export after the default Orika mapping has run.
     * Implementations may write additional fields from the Tiamat source into the
     * NeTEx target so that extended fields survive the export roundtrip.
     *
     * @param source  the Tiamat {@link org.rutebanken.tiamat.model.Parking} being exported
     * @param target  the NeTEx {@link org.rutebanken.netex.model.Parking} being populated
     * @param context the current Orika mapping context
     */
    void mapToNetex(org.rutebanken.tiamat.model.Parking source,
                    org.rutebanken.netex.model.Parking target,
                    MappingContext context);
}
