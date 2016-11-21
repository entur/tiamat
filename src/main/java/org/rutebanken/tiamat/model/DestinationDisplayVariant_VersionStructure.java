

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class DestinationDisplayVariant_VersionStructure
    extends DataManagedObjectStructure
{

    protected DestinationDisplayRefStructure destinationDisplayRef;
    protected DeliveryVariantTypeEnumeration destinationDisplayVariantMediaType;
    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected MultilingualStringEntity sideText;
    protected MultilingualStringEntity frontText;
    protected MultilingualStringEntity driverDisplayText;
    protected Vias_RelStructure vias;

    public DestinationDisplayRefStructure getDestinationDisplayRef() {
        return destinationDisplayRef;
    }

    public void setDestinationDisplayRef(DestinationDisplayRefStructure value) {
        this.destinationDisplayRef = value;
    }

    public DeliveryVariantTypeEnumeration getDestinationDisplayVariantMediaType() {
        return destinationDisplayVariantMediaType;
    }

    public void setDestinationDisplayVariantMediaType(DeliveryVariantTypeEnumeration value) {
        this.destinationDisplayVariantMediaType = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public MultilingualStringEntity getSideText() {
        return sideText;
    }

    public void setSideText(MultilingualStringEntity value) {
        this.sideText = value;
    }

    public MultilingualStringEntity getFrontText() {
        return frontText;
    }

    public void setFrontText(MultilingualStringEntity value) {
        this.frontText = value;
    }

    public MultilingualStringEntity getDriverDisplayText() {
        return driverDisplayText;
    }

    public void setDriverDisplayText(MultilingualStringEntity value) {
        this.driverDisplayText = value;
    }

    public Vias_RelStructure getVias() {
        return vias;
    }

    public void setVias(Vias_RelStructure value) {
        this.vias = value;
    }

}
