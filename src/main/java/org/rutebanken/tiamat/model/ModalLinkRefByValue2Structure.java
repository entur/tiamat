package org.rutebanken.tiamat.model;

import javax.xml.datatype.XMLGregorianCalendar;


public class ModalLinkRefByValue2Structure {

    protected PointRefStructure fromPointRef;
    protected PointRefStructure toPointRef;
    protected TypeOfLinkRefStructure typeOfLinkRef;
    protected AllModesEnumeration vehicleMode;
    protected String nameOfClass;
    protected XMLGregorianCalendar created;
    protected XMLGregorianCalendar changed;
    protected String version;

    public PointRefStructure getFromPointRef() {
        return fromPointRef;
    }

    public void setFromPointRef(PointRefStructure value) {
        this.fromPointRef = value;
    }

    public PointRefStructure getToPointRef() {
        return toPointRef;
    }

    public void setToPointRef(PointRefStructure value) {
        this.toPointRef = value;
    }

    public TypeOfLinkRefStructure getTypeOfLinkRef() {
        return typeOfLinkRef;
    }

    public void setTypeOfLinkRef(TypeOfLinkRefStructure value) {
        this.typeOfLinkRef = value;
    }

    public AllModesEnumeration getVehicleMode() {
        return vehicleMode;
    }

    public void setVehicleMode(AllModesEnumeration value) {
        this.vehicleMode = value;
    }

    public String getNameOfClass() {
        return nameOfClass;
    }

    public void setNameOfClass(String value) {
        this.nameOfClass = value;
    }

    public XMLGregorianCalendar getCreated() {
        return created;
    }

    public void setCreated(XMLGregorianCalendar value) {
        this.created = value;
    }

    public XMLGregorianCalendar getChanged() {
        return changed;
    }

    public void setChanged(XMLGregorianCalendar value) {
        this.changed = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String value) {
        this.version = value;
    }

}
