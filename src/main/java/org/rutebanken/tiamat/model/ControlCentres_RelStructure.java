package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ControlCentres_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> controlCentreRefOrControlCentre;

    public List<Object> getControlCentreRefOrControlCentre() {
        if (controlCentreRefOrControlCentre == null) {
            controlCentreRefOrControlCentre = new ArrayList<Object>();
        }
        return this.controlCentreRefOrControlCentre;
    }

}
