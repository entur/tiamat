

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class CompoundTrain_VersionStructure
    extends VehicleType_VersionStructure
{

    protected TrainsInCompoundTrain_RelStructure components;

    public TrainsInCompoundTrain_RelStructure getComponents() {
        return components;
    }

    public void setComponents(TrainsInCompoundTrain_RelStructure value) {
        this.components = value;
    }

}
