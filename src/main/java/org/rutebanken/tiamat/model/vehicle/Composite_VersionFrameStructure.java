package org.rutebanken.tiamat.model.vehicle;

import lombok.Getter;
import lombok.Setter;
import org.rutebanken.tiamat.model.Common_VersionFrameStructure;

@Getter
@Setter
public class Composite_VersionFrameStructure extends Common_VersionFrameStructure {
    private Frames_RelStructure frames;

}
