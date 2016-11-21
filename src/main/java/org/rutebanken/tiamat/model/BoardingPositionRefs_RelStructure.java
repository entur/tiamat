

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class BoardingPositionRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<BoardingPositionRefStructure> boardingPositionRef;

    public List<BoardingPositionRefStructure> getBoardingPositionRef() {
        if (boardingPositionRef == null) {
            boardingPositionRef = new ArrayList<BoardingPositionRefStructure>();
        }
        return this.boardingPositionRef;
    }

}
