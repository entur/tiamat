package org.rutebanken.tiamat.model;

import javax.xml.datatype.XMLGregorianCalendar;


public class DeltaStructure {

    protected SimpleObjectRefStructure simpleObjectRef;
    protected FromVersionRef fromVersionRef;
    protected ToVersionRef toVersionRef;
    protected ModificationEnumeration modification;
    protected DeltaValues_RelStructure deltaValues;
    protected String id;
    protected XMLGregorianCalendar created;

    public SimpleObjectRefStructure getSimpleObjectRef() {
        return simpleObjectRef;
    }

    public void setSimpleObjectRef(SimpleObjectRefStructure value) {
        this.simpleObjectRef = value;
    }

    public FromVersionRef getFromVersionRef() {
        return fromVersionRef;
    }

    public void setFromVersionRef(FromVersionRef value) {
        this.fromVersionRef = value;
    }

    public ToVersionRef getToVersionRef() {
        return toVersionRef;
    }

    public void setToVersionRef(ToVersionRef value) {
        this.toVersionRef = value;
    }

    public ModificationEnumeration getModification() {
        return modification;
    }

    public void setModification(ModificationEnumeration value) {
        this.modification = value;
    }

    public DeltaValues_RelStructure getDeltaValues() {
        return deltaValues;
    }

    public void setDeltaValues(DeltaValues_RelStructure value) {
        this.deltaValues = value;
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
