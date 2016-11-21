

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


    "fareClass",
    "boardingPermisssion",
public class OnboardStay_VersionedChlldStructure
    extends VersionedChildStructure
{
    protected BoardingPermissionEnumeration boardingPermisssion;
    protected Duration period;

    public BoardingPermissionEnumeration getBoardingPermisssion() {
        return boardingPermisssion;
    }

    public void setBoardingPermisssion(BoardingPermissionEnumeration value) {
        this.boardingPermisssion = value;
    }

    public Duration getPeriod() {
        return period;
    }

    public void setPeriod(Duration value) {
        this.period = value;
    }

}
