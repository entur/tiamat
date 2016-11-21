

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class DummyPlaceRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends VersionOfObjectRefStructure>> placeRef_;

    public List<JAXBElement<? extends VersionOfObjectRefStructure>> getPlaceRef_() {
        if (placeRef_ == null) {
            placeRef_ = new ArrayList<JAXBElement<? extends VersionOfObjectRefStructure>>();
        }
        return this.placeRef_;
    }

}
