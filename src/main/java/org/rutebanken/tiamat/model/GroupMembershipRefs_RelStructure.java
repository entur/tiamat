

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class GroupMembershipRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends GroupOfEntitiesRefStructure>> groupOfPointsRef_;

    public List<JAXBElement<? extends GroupOfEntitiesRefStructure>> getGroupOfPointsRef_() {
        if (groupOfPointsRef_ == null) {
            groupOfPointsRef_ = new ArrayList<JAXBElement<? extends GroupOfEntitiesRefStructure>>();
        }
        return this.groupOfPointsRef_;
    }

}
