package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ControlCentresInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ControlCentre> controlCentre;

    public List<ControlCentre> getControlCentre() {
        if (controlCentre == null) {
            controlCentre = new ArrayList<ControlCentre>();
        }
        return this.controlCentre;
    }

}
