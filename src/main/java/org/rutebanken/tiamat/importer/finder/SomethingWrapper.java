package org.rutebanken.tiamat.importer.finder;

import java.util.Objects;

public class SomethingWrapper {

    private final String netexId;
    private final String ourOwnId;

    public SomethingWrapper(String netexId, String ourOwnId) {
        this.netexId = netexId;
        this.ourOwnId = ourOwnId;
    }

    public String getNetexId() {
        return netexId;
    }

    public String getOurOwnId() {
        return ourOwnId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SomethingWrapper that = (SomethingWrapper) o;
        return Objects.equals(netexId, that.netexId) && Objects.equals(ourOwnId, that.ourOwnId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(netexId, ourOwnId);
    }
}
