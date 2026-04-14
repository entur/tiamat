package org.rutebanken.tiamat.ext.fintraffic.api.model;

/**
 * Fintraffic Read API search key.
 * @param transportModes Transport modes
 * @param areaCodes Area codes
 * @param municipalityCodes Municipality codes (e.g. "091" for Helsinki)
 */
public record FintrafficReadApiSearchKey(String[] transportModes, String[] areaCodes, String[] municipalityCodes) implements ReadApiSearchKey {
    public static FintrafficReadApiSearchKey empty() {
        return new FintrafficReadApiSearchKey(new String[]{}, new String[]{}, new String[]{});
    }
}
