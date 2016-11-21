

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "name",
    "shortName",
    "imageUri",
    "depictedObjectRef",
public class SchematicMap_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected String imageUri;
    protected VersionOfObjectRefStructure depictedObjectRef;
    protected SchematicMapMembers_RelStructure members;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String value) {
        this.imageUri = value;
    }

    public VersionOfObjectRefStructure getDepictedObjectRef() {
        return depictedObjectRef;
    }

    public void setDepictedObjectRef(VersionOfObjectRefStructure value) {
        this.depictedObjectRef = value;
    }

    public SchematicMapMembers_RelStructure getMembers() {
        return members;
    }

    public void setMembers(SchematicMapMembers_RelStructure value) {
        this.members = value;
    }

}
