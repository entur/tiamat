

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class RoutingConstraintZone_VersionStructure
    extends Zone_VersionStructure
{

    protected ZoneUseEnumeration zoneUse;
    protected PointsInJourneyPattern_RelStructure pointsInPattern;
    protected LineRefs_RelStructure lines;
    protected JAXBElement<? extends GroupOfLinesRefStructure> groupOfLinesRef;

    public ZoneUseEnumeration getZoneUse() {
        return zoneUse;
    }

    public void setZoneUse(ZoneUseEnumeration value) {
        this.zoneUse = value;
    }

    public PointsInJourneyPattern_RelStructure getPointsInPattern() {
        return pointsInPattern;
    }

    public void setPointsInPattern(PointsInJourneyPattern_RelStructure value) {
        this.pointsInPattern = value;
    }

    public LineRefs_RelStructure getLines() {
        return lines;
    }

    public void setLines(LineRefs_RelStructure value) {
        this.lines = value;
    }

    public JAXBElement<? extends GroupOfLinesRefStructure> getGroupOfLinesRef() {
        return groupOfLinesRef;
    }

    public void setGroupOfLinesRef(JAXBElement<? extends GroupOfLinesRefStructure> value) {
        this.groupOfLinesRef = value;
    }

}
