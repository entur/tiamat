package org.rutebanken.tiamat.importers;

import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public interface StopPlaceImporter {
    StopPlace importStopPlace(StopPlace stopPlace, SiteFrame siteFrame,
                              AtomicInteger topographicPlacesCreatedCounter, String correlationId) throws InterruptedException, ExecutionException;
}
