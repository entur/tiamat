package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TransportOperatorsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Organisation_VersionStructure> authorityOrOperator;

    public List<Organisation_VersionStructure> getAuthorityOrOperator() {
        if (authorityOrOperator == null) {
            authorityOrOperator = new ArrayList<Organisation_VersionStructure>();
        }
        return this.authorityOrOperator;
    }

}
