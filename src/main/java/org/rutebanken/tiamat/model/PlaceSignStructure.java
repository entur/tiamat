

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "placeName",
public class PlaceSignStructure
    extends SignEquipment_VersionStructure
{

    protected MultilingualStringEntity placeName;
    protected PlaceRef placeRef;

    public MultilingualStringEntity getPlaceName() {
        return placeName;
    }

    public void setPlaceName(MultilingualStringEntity value) {
        this.placeName = value;
    }

    public PlaceRef getPlaceRef() {
        return placeRef;
    }

    public void setPlaceRef(PlaceRef value) {
        this.placeRef = value;
    }

}
