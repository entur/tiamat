

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public abstract class Layer_VersionStructure
    extends GroupOfEntities_VersionStructure
{

    protected String locationSystem;
    protected VersionFrameRefs_RelStructure versionFrames;
    protected ObjectRefs_RelStructure members;

    public String getLocationSystem() {
        return locationSystem;
    }

    public void setLocationSystem(String value) {
        this.locationSystem = value;
    }

    public VersionFrameRefs_RelStructure getVersionFrames() {
        return versionFrames;
    }

    public void setVersionFrames(VersionFrameRefs_RelStructure value) {
        this.versionFrames = value;
    }

    public ObjectRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(ObjectRefs_RelStructure value) {
        this.members = value;
    }

}
