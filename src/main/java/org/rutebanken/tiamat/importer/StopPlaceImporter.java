package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public interface StopPlaceImporter {
    org.rutebanken.netex.model.StopPlace importStopPlace(StopPlace stopPlace, SiteFrame siteFrame,
                                                         AtomicInteger topographicPlacesCreatedCounter) throws InterruptedException, ExecutionException;
}
