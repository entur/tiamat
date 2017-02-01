package org.rutebanken.tiamat.model;

public class DeltaValueStructure {

    protected String deltaRef;
    protected ModificationEnumeration modification;
    protected String valueName;
    protected Object oldValue;
    protected Object newValue;
    protected String id;

    public String getDeltaRef() {
        return deltaRef;
    }

    public void setDeltaRef(String value) {
        this.deltaRef = value;
    }

    public ModificationEnumeration getModification() {
        return modification;
    }

    public void setModification(ModificationEnumeration value) {
        this.modification = value;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String value) {
        this.valueName = value;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object value) {
        this.oldValue = value;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object value) {
        this.newValue = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

}
