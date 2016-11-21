package org.rutebanken.tiamat.model;

import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@MappedSuperclass
public abstract class StopPlaceSpace_VersionStructure
        extends StopPlaceComponent_VersionStructure {

    @Embedded
    protected EmbeddableMultilingualString label;

    @Transient
    protected SiteEntrances_RelStructure entrances;

    public StopPlaceSpace_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public StopPlaceSpace_VersionStructure() {
    }
    
    public EmbeddableMultilingualString getLabel() {
        return label;
    }

    public void setLabel(EmbeddableMultilingualString value) {
        this.label = value;
    }

    public SiteEntrances_RelStructure getEntrances() {
        return entrances;
    }

    public void setEntrances(SiteEntrances_RelStructure value) {
        this.entrances = value;
    }

}
