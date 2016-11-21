

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "name",
    "description",
    "privateCode",
    "typeOfTimeDemandTypeRef",
    "presentation",
    "runTimes",
    "waitTimes",
    "layovers",
    "headways",
public class TimeDemandType_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity description;
    protected PrivateCodeStructure privateCode;
    protected TypeOfTimeDemandTypeRefStructure typeOfTimeDemandTypeRef;
    protected PresentationStructure presentation;
    protected JourneyRunTimes_RelStructure runTimes;
    protected JourneyWaitTimes_RelStructure waitTimes;
    protected JourneyLayovers_RelStructure layovers;
    protected JourneyHeadways_RelStructure headways;
    protected VehicleTypePreferences_RelStructure vehiclePreferences;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public TypeOfTimeDemandTypeRefStructure getTypeOfTimeDemandTypeRef() {
        return typeOfTimeDemandTypeRef;
    }

    public void setTypeOfTimeDemandTypeRef(TypeOfTimeDemandTypeRefStructure value) {
        this.typeOfTimeDemandTypeRef = value;
    }

    public PresentationStructure getPresentation() {
        return presentation;
    }

    public void setPresentation(PresentationStructure value) {
        this.presentation = value;
    }

    public JourneyRunTimes_RelStructure getRunTimes() {
        return runTimes;
    }

    public void setRunTimes(JourneyRunTimes_RelStructure value) {
        this.runTimes = value;
    }

    public JourneyWaitTimes_RelStructure getWaitTimes() {
        return waitTimes;
    }

    public void setWaitTimes(JourneyWaitTimes_RelStructure value) {
        this.waitTimes = value;
    }

    public JourneyLayovers_RelStructure getLayovers() {
        return layovers;
    }

    public void setLayovers(JourneyLayovers_RelStructure value) {
        this.layovers = value;
    }

    public JourneyHeadways_RelStructure getHeadways() {
        return headways;
    }

    public void setHeadways(JourneyHeadways_RelStructure value) {
        this.headways = value;
    }

    public VehicleTypePreferences_RelStructure getVehiclePreferences() {
        return vehiclePreferences;
    }

    public void setVehiclePreferences(VehicleTypePreferences_RelStructure value) {
        this.vehiclePreferences = value;
    }

}
