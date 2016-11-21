package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class OrganisationalUnits_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> organisationalUnitRefOrOrganisationalUnit;

    public List<Object> getOrganisationalUnitRefOrOrganisationalUnit() {
        if (organisationalUnitRefOrOrganisationalUnit == null) {
            organisationalUnitRefOrOrganisationalUnit = new ArrayList<Object>();
        }
        return this.organisationalUnitRefOrOrganisationalUnit;
    }

}
