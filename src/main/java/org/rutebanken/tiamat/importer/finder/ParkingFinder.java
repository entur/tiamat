package org.rutebanken.tiamat.importer.finder;

import org.rutebanken.tiamat.model.Parking;

public interface ParkingFinder {
    Parking find(Parking stopPlace);
}
