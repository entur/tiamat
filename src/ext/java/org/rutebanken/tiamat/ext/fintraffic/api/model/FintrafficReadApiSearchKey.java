package org.rutebanken.tiamat.ext.fintraffic.api.model;

/**
 * Fintraffic Read API search key.
 * @param transportModes Transport modes
 * @param areaCodes Area codes
 */
public record FintrafficReadApiSearchKey(String[] transportModes, String[] areaCodes) implements ReadApiSearchKey {
    public static FintrafficReadApiSearchKey empty() {
        return new FintrafficReadApiSearchKey(new String[]{}, new String[]{});
    }
}
