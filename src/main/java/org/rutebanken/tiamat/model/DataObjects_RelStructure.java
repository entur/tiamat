

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class DataObjects_RelStructure {

    protected List<Common_VersionFrameStructure> compositeFrameOrCommonFrame;

    public List<Common_VersionFrameStructure> getCompositeFrameOrCommonFrame() {
        if (compositeFrameOrCommonFrame == null) {
            compositeFrameOrCommonFrame = new ArrayList<Common_VersionFrameStructure>();
        }
        return this.compositeFrameOrCommonFrame;
    }

}
