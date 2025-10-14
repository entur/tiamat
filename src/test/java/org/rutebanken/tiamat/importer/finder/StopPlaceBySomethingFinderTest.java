package org.rutebanken.tiamat.importer.finder;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StopPlaceBySomethingFinderTest {

    private final StopPlaceBySomethingFinder finder = new StopPlaceBySomethingFinder();

    @Test
    public void testStopPlaceBySomethingFinder() {
        finder.updateCache("externalId", new SomethingWrapper("netexId", "ourOwnId"));
    }
}