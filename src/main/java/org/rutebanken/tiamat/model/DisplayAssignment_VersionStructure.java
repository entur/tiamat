

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class DisplayAssignment_VersionStructure
    extends Assignment_VersionStructure
{

    protected LogicalDisplayRefStructure logicalDisplayRef;
    protected JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPointRef;
    protected AllModesEnumeration vehicleMode;
    protected JAXBElement<? extends LineRefStructure> lineRef;
    protected DirectionRefStructure directionRef;
    protected JAXBElement<? extends JourneyPatternRefStructure> journeyPatternRef;
    protected DisplayAssignmentTypeEnumeration displayAssignmentType;
    protected BigInteger numberOfJourneysToShow;
    protected BigInteger displayPriority;

    public LogicalDisplayRefStructure getLogicalDisplayRef() {
        return logicalDisplayRef;
    }

    public void setLogicalDisplayRef(LogicalDisplayRefStructure value) {
        this.logicalDisplayRef = value;
    }

    public JAXBElement<? extends ScheduledStopPointRefStructure> getScheduledStopPointRef() {
        return scheduledStopPointRef;
    }

    public void setScheduledStopPointRef(JAXBElement<? extends ScheduledStopPointRefStructure> value) {
        this.scheduledStopPointRef = value;
    }

    public AllModesEnumeration getVehicleMode() {
        return vehicleMode;
    }

    public void setVehicleMode(AllModesEnumeration value) {
        this.vehicleMode = value;
    }

    public JAXBElement<? extends LineRefStructure> getLineRef() {
        return lineRef;
    }

    public void setLineRef(JAXBElement<? extends LineRefStructure> value) {
        this.lineRef = value;
    }

    public DirectionRefStructure getDirectionRef() {
        return directionRef;
    }

    public void setDirectionRef(DirectionRefStructure value) {
        this.directionRef = value;
    }

    public JAXBElement<? extends JourneyPatternRefStructure> getJourneyPatternRef() {
        return journeyPatternRef;
    }

    public void setJourneyPatternRef(JAXBElement<? extends JourneyPatternRefStructure> value) {
        this.journeyPatternRef = value;
    }

    public DisplayAssignmentTypeEnumeration getDisplayAssignmentType() {
        return displayAssignmentType;
    }

    public void setDisplayAssignmentType(DisplayAssignmentTypeEnumeration value) {
        this.displayAssignmentType = value;
    }

    public BigInteger getNumberOfJourneysToShow() {
        return numberOfJourneysToShow;
    }

    public void setNumberOfJourneysToShow(BigInteger value) {
        this.numberOfJourneysToShow = value;
    }

    public BigInteger getDisplayPriority() {
        return displayPriority;
    }

    public void setDisplayPriority(BigInteger value) {
        this.displayPriority = value;
    }

}
