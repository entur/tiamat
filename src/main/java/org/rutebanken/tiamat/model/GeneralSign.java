package org.rutebanken.tiamat.model;

import javax.persistence.*;

@Entity
public class GeneralSign
        extends SignEquipment_VersionStructure {


    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "content_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "content_lang"))
    })
    @Embedded
    protected EmbeddableMultilingualString content;

    @Enumerated(value = EnumType.STRING)
    protected SignContentEnumeration signContentType;

    public EmbeddableMultilingualString getContent() {
        return content;
    }

    public void setContent(EmbeddableMultilingualString value) {
        this.content = value;
    }

    public SignContentEnumeration getSignContentType() {
        return signContentType;
    }

    public void setSignContentType(SignContentEnumeration value) {
        this.signContentType = value;
    }

}
