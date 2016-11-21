

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class CodespacesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Codespace> codespace;

    public List<Codespace> getCodespace() {
        if (codespace == null) {
            codespace = new ArrayList<Codespace>();
        }
        return this.codespace;
    }

}
