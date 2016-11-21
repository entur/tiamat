

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class Composite_VersionFrameStructure
    extends Common_VersionFrameStructure
{

    protected Frames_RelStructure frames;

    public Frames_RelStructure getFrames() {
        return frames;
    }

    public void setFrames(Frames_RelStructure value) {
        this.frames = value;
    }

}
