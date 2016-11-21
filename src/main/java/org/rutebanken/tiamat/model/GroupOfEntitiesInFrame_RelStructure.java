

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class GroupOfEntitiesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<GeneralGroupOfEntities> generalGroupOfEntities;

    public List<GeneralGroupOfEntities> getGeneralGroupOfEntities() {
        if (generalGroupOfEntities == null) {
            generalGroupOfEntities = new ArrayList<GeneralGroupOfEntities>();
        }
        return this.generalGroupOfEntities;
    }

}
