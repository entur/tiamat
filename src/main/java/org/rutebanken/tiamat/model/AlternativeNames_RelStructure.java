package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AlternativeNames_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<AlternativeName> alternativeName;

    public List<AlternativeName> getAlternativeName() {
        if (alternativeName == null) {
            alternativeName = new ArrayList<AlternativeName>();
        }
        return this.alternativeName;
    }

}
