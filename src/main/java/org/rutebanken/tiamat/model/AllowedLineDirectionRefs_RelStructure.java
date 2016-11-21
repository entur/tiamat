package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AllowedLineDirectionRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<AllowedLineDirectionRefStructure> allowedLineDirectionRef;

    public List<AllowedLineDirectionRefStructure> getAllowedLineDirectionRef() {
        if (allowedLineDirectionRef == null) {
            allowedLineDirectionRef = new ArrayList<AllowedLineDirectionRefStructure>();
        }
        return this.allowedLineDirectionRef;
    }

}
