

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "name",
    "shortName",
    "privateCode",
    "organisationPartRef",
    "vehicleMode",
public class OperationalContext_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected PrivateCodeStructure privateCode;
    protected JAXBElement<? extends OrganisationPartRefStructure> organisationPartRef;
    protected AllModesEnumeration vehicleMode;
    protected TransportSubmodeStructure transportSubmode;

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

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public JAXBElement<? extends OrganisationPartRefStructure> getOrganisationPartRef() {
        return organisationPartRef;
    }

    public void setOrganisationPartRef(JAXBElement<? extends OrganisationPartRefStructure> value) {
        this.organisationPartRef = value;
    }

    public AllModesEnumeration getVehicleMode() {
        return vehicleMode;
    }

    public void setVehicleMode(AllModesEnumeration value) {
        this.vehicleMode = value;
    }

    public TransportSubmodeStructure getTransportSubmode() {
        return transportSubmode;
    }

    public void setTransportSubmode(TransportSubmodeStructure value) {
        this.transportSubmode = value;
    }

}
