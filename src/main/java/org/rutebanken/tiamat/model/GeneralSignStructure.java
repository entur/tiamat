package org.rutebanken.tiamat.model;

public class GeneralSignStructure
        extends SignEquipment_VersionStructure {

    protected MultilingualStringEntity content;
    protected SignContentEnumeration signContentType;

    public MultilingualStringEntity getContent() {
        return content;
    }

    public void setContent(MultilingualStringEntity value) {
        this.content = value;
    }

    public SignContentEnumeration getSignContentType() {
        return signContentType;
    }

    public void setSignContentType(SignContentEnumeration value) {
        this.signContentType = value;
    }

}
