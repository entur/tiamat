

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


    "objectRef",
    "changedAt",
    "changedBy",
    "description",
public class TraceStructure {

    protected VersionOfObjectRefStructure objectRef;
    protected XMLGregorianCalendar changedAt;
    protected String changedBy;
    protected String description;
    protected DeltaStructure delta;
    protected String id;
    protected XMLGregorianCalendar created;

    public VersionOfObjectRefStructure getObjectRef() {
        return objectRef;
    }

    public void setObjectRef(VersionOfObjectRefStructure value) {
        this.objectRef = value;
    }

    public XMLGregorianCalendar getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(XMLGregorianCalendar value) {
        this.changedAt = value;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String value) {
        this.changedBy = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public DeltaStructure getDelta() {
        return delta;
    }

    public void setDelta(DeltaStructure value) {
        this.delta = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public XMLGregorianCalendar getCreated() {
        return created;
    }

    public void setCreated(XMLGregorianCalendar value) {
        this.created = value;
    }

}
