

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ControlCentresInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<ControlCentre> controlCentre;

    public List<ControlCentre> getControlCentre() {
        if (controlCentre == null) {
            controlCentre = new ArrayList<ControlCentre>();
        }
        return this.controlCentre;
    }

}
