

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "name",
    "description",
    "groupOfLinesRef",
    "lineRef",
public class LineNetwork_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity description;
    protected JAXBElement<? extends GroupOfLinesRefStructure> groupOfLinesRef;
    protected JAXBElement<? extends LineRefStructure> lineRef;
    protected LineSections_RelStructure sections;

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

    public JAXBElement<? extends GroupOfLinesRefStructure> getGroupOfLinesRef() {
        return groupOfLinesRef;
    }

    public void setGroupOfLinesRef(JAXBElement<? extends GroupOfLinesRefStructure> value) {
        this.groupOfLinesRef = value;
    }

    public JAXBElement<? extends LineRefStructure> getLineRef() {
        return lineRef;
    }

    public void setLineRef(JAXBElement<? extends LineRefStructure> value) {
        this.lineRef = value;
    }

    public LineSections_RelStructure getSections() {
        return sections;
    }

    public void setSections(LineSections_RelStructure value) {
        this.sections = value;
    }

}
