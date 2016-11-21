package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class DummyPlaceRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends VersionOfObjectRefStructure>> placeRef_;

    public List<JAXBElement<? extends VersionOfObjectRefStructure>> getPlaceRef_() {
        if (placeRef_ == null) {
            placeRef_ = new ArrayList<JAXBElement<? extends VersionOfObjectRefStructure>>();
        }
        return this.placeRef_;
    }

}
