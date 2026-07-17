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

package org.rutebanken.tiamat.model.factory;

import org.rutebanken.tiamat.model.Parking;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory for creating and configuring Parking entity instances.
 * <p>
 * Override with {@code @Primary} in an extension module to substitute a subclass
 * of {@link Parking}, enabling persistence of additional fields without modifying
 * the core model.
 */
@Component
public class ParkingEntityFactory {

    public Parking create() {
        return new Parking();
    }

    /**
     * Returns the entity class to instantiate and register Orika classmaps for.
     * <p>
     * Override in an extension module to substitute a subclass of {@link Parking}.
     * <p>
     * <strong>Hibernate inheritance:</strong> the core Flyway schema includes the
     * {@code dtype} discriminator column on the {@code parking} table (required because
     * any {@code @Entity} subclass on the classpath causes Hibernate to add discriminator
     * predicates to all queries, regardless of active Spring profiles).  Extension modules
     * that add further columns or auxiliary tables must provide their own Flyway migrations
     * for those additions.
     */
    public Class<? extends Parking> getEntityClass() {
        return Parking.class;
    }

    /**
     * Field names to exclude from the Orika NeTEx↔Tiamat classmap registration.
     * Fields listed here are not mapped during import or export.
     */
    public List<String> getMappingExclusions() {
        return List.of("paymentMethods", "cardsAccepted", "currenciesAccepted", "accessModes");
    }
}
