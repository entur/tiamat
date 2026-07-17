package org.rutebanken.tiamat.netex.mapping;

import org.rutebanken.tiamat.model.Parking;

/**
 * Plain (non-persistent) Parking subclass used in {@link NetexMapperTest} to verify
 * that Orika registers and applies classmaps for both a subclass factory type and the
 * base {@link Parking} type.  Not annotated with {@code @Entity} — this class is only
 * used for Orika mapping tests and must never be registered with Hibernate.
 */
public class ParkingSubclassForTest extends Parking {
}
