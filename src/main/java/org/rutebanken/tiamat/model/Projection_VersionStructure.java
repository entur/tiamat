package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;


public class Projection_VersionStructure
        extends DataManagedObjectStructure {

    protected TypeOfProjectionRefStructure typeOfProjectionRef;
    protected MultilingualStringEntity name;
    protected JAXBElement<? extends GroupOfPointsRefStructure> spatialFeatureRef;
    protected BigInteger order;

    public TypeOfProjectionRefStructure getTypeOfProjectionRef() {
        return typeOfProjectionRef;
    }

    public void setTypeOfProjectionRef(TypeOfProjectionRefStructure value) {
        this.typeOfProjectionRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public JAXBElement<? extends GroupOfPointsRefStructure> getSpatialFeatureRef() {
        return spatialFeatureRef;
    }

    public void setSpatialFeatureRef(JAXBElement<? extends GroupOfPointsRefStructure> value) {
        this.spatialFeatureRef = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
