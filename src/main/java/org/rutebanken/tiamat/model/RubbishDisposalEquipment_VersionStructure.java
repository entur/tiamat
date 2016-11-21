

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class RubbishDisposalEquipment_VersionStructure
    extends PassengerEquipment_VersionStructure
{

    protected Boolean sharpsDispsal;
    protected Boolean recycling;

    public Boolean isSharpsDispsal() {
        return sharpsDispsal;
    }

    public void setSharpsDispsal(Boolean value) {
        this.sharpsDispsal = value;
    }

    public Boolean isRecycling() {
        return recycling;
    }

    public void setRecycling(Boolean value) {
        this.recycling = value;
    }

}
