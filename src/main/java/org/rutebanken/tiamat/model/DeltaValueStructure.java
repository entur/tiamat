

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
