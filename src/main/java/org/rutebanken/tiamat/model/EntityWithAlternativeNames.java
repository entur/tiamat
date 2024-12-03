package org.rutebanken.tiamat.model;

import java.util.List;

public interface EntityWithAlternativeNames {
    // Used List implementation has to be mutable
    List<AlternativeName> getAlternativeNames();
}
