package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class GroupMembershipRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends GroupOfEntitiesRefStructure>> groupOfPointsRef_;

    public List<JAXBElement<? extends GroupOfEntitiesRefStructure>> getGroupOfPointsRef_() {
        if (groupOfPointsRef_ == null) {
            groupOfPointsRef_ = new ArrayList<JAXBElement<? extends GroupOfEntitiesRefStructure>>();
        }
        return this.groupOfPointsRef_;
    }

}
