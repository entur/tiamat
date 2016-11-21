

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PathJunctionsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<PathJunction> pathJunction;

    public List<PathJunction> getPathJunction() {
        if (pathJunction == null) {
            pathJunction = new ArrayList<PathJunction>();
        }
        return this.pathJunction;
    }

}
