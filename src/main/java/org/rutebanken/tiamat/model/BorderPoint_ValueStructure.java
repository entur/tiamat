

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "shortName",
    "description",
    "groupOfOperatorsRef",
public class BorderPoint_ValueStructure
    extends TimingPoint_VersionStructure
{

    protected MultilingualStringEntity shortName;
    protected MultilingualStringEntity description;
    protected GroupOfOperatorsRefStructure groupOfOperatorsRef;
    protected GroupOfOperators groupOfOperators;

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public GroupOfOperatorsRefStructure getGroupOfOperatorsRef() {
        return groupOfOperatorsRef;
    }

    public void setGroupOfOperatorsRef(GroupOfOperatorsRefStructure value) {
        this.groupOfOperatorsRef = value;
    }

    public GroupOfOperators getGroupOfOperators() {
        return groupOfOperators;
    }

    public void setGroupOfOperators(GroupOfOperators value) {
        this.groupOfOperators = value;
    }

}
