package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class LineShapeStructure
        extends DataManagedObjectStructure {

    protected String formula;
    protected MultilingualStringEntity name;
    protected JAXBElement<? extends LinkRefStructure> linkRef;
    protected String locatingSystemRef;

    public String getFormula() {
        return formula;
    }

    public void setFormula(String value) {
        this.formula = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public JAXBElement<? extends LinkRefStructure> getLinkRef() {
        return linkRef;
    }

    public void setLinkRef(JAXBElement<? extends LinkRefStructure> value) {
        this.linkRef = value;
    }

    public String getLocatingSystemRef() {
        return locatingSystemRef;
    }

    public void setLocatingSystemRef(String value) {
        this.locatingSystemRef = value;
    }

}
